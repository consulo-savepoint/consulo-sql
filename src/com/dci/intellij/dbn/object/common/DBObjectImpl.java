package com.dci.intellij.dbn.object.common;

import com.dci.intellij.dbn.browser.DatabaseBrowserManager;
import com.dci.intellij.dbn.browser.DatabaseBrowserUtils;
import com.dci.intellij.dbn.browser.model.BrowserTreeElement;
import com.dci.intellij.dbn.browser.model.LoadInProgressTreeElement;
import com.dci.intellij.dbn.browser.ui.HtmlToolTipBuilder;
import com.dci.intellij.dbn.browser.ui.ToolTipProvider;
import com.dci.intellij.dbn.code.common.lookup.DBObjectLookupItemFactory;
import com.dci.intellij.dbn.code.common.lookup.LookupItemFactory;
import com.dci.intellij.dbn.code.sql.color.SQLTextAttributesKeys;
import com.dci.intellij.dbn.common.content.DynamicContent;
import com.dci.intellij.dbn.common.content.DynamicContentType;
import com.dci.intellij.dbn.common.dispose.DisposeUtil;
import com.dci.intellij.dbn.common.environment.EnvironmentType;
import com.dci.intellij.dbn.common.filter.Filter;
import com.dci.intellij.dbn.common.thread.BackgroundTask;
import com.dci.intellij.dbn.common.thread.ConditionalLaterInvocator;
import com.dci.intellij.dbn.common.ui.tree.TreeUtil;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.connection.ConnectionUtil;
import com.dci.intellij.dbn.database.DatabaseCompatibilityInterface;
import com.dci.intellij.dbn.language.common.DBLanguage;
import com.dci.intellij.dbn.language.common.DBLanguageDialect;
import com.dci.intellij.dbn.language.psql.PSQLLanguage;
import com.dci.intellij.dbn.language.sql.SQLLanguage;
import com.dci.intellij.dbn.navigation.psi.NavigationPsiCache;
import com.dci.intellij.dbn.object.DBUser;
import com.dci.intellij.dbn.object.common.list.DBObjectList;
import com.dci.intellij.dbn.object.common.list.DBObjectListContainer;
import com.dci.intellij.dbn.object.common.list.DBObjectNavigationList;
import com.dci.intellij.dbn.object.common.list.DBObjectRelationListContainer;
import com.dci.intellij.dbn.object.common.operation.DBOperationExecutor;
import com.dci.intellij.dbn.object.common.operation.DBOperationNotSupportedException;
import com.dci.intellij.dbn.object.common.operation.DBOperationType;
import com.dci.intellij.dbn.object.common.property.DBObjectProperties;
import com.dci.intellij.dbn.object.properties.ConnectionPresentableProperty;
import com.dci.intellij.dbn.object.properties.DBObjectPresentableProperty;
import com.dci.intellij.dbn.object.properties.PresentableProperty;
import com.dci.intellij.dbn.vfs.DatabaseObjectFile;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public abstract class DBObjectImpl extends DBObjectPsiAbstraction implements DBObject, ToolTipProvider {
    private List<BrowserTreeElement> allPossibleTreeChildren;
    private List<BrowserTreeElement> visibleTreeChildren;
    private BrowserTreeElement treeParent;
    private int treeDepth;
    private boolean treeChildrenLoaded;
    private boolean isDisposed = false;

    protected String name;
    protected String qualifiedName;
    protected DBUser owner;
    private ConnectionHandler connectionHandler;
    private DBObjectListContainer childObjects;
    private DBObjectRelationListContainer childObjectRelations;
    private DBObject parentObject;
    private DBObjectProperties properties;

    private LookupItemFactory sqlLookupItemFactory;
    private LookupItemFactory psqlLookupItemFactory;
    private DynamicContent ownerContent;

    protected DatabaseObjectFile virtualFile;

    private static final DBOperationExecutor NULL_OPERATION_EXECUTOR = new DBOperationExecutor() {
        public void executeOperation(DBOperationType operationType) throws SQLException, DBOperationNotSupportedException {
            throw new DBOperationNotSupportedException(operationType);
        }
    };

    protected Logger getLogger() {return Logger.getInstance(getClass().getName());}

    @Override
    public PsiElement getParent() {
        PsiFile containingFile = getContainingFile();
        if (containingFile != null) {
            return containingFile.getParent();
        }
        return null;
    }

    public DBObjectImpl(DBObject parentObject){
        this.parentObject = parentObject;
        owner = parentObject.getOwner();
        connectionHandler = parentObject.getConnectionHandler();
        updateTreeInfo();
        updateProperties();
    }

    public DBObjectImpl(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        updateTreeInfo();
        updateProperties();
    }

    protected void initPossibleTreeChildren(BrowserTreeElement ... treeElements) {
        allPossibleTreeChildren = DatabaseBrowserUtils.createList(treeElements);
    }

    public void updateProperties() {}

    private void updateTreeInfo() {
        DBObject parentObject = getParentObject();
        treeParent = parentObject == null ?
                connectionHandler.getObjectBundle().getObjectListContainer().getObjectList(getObjectType()) :
                parentObject.getChildObjects().getObjectList(getObjectType());
        treeDepth = treeParent.getTreeDepth() + 1;
    }

    public DBObjectProperties getProperties() {
        if (properties == null) {
            properties = new DBObjectProperties();
        }
        return properties;
    }

    public DBOperationExecutor getOperationExecutor() {
        return NULL_OPERATION_EXECUTOR;
    }

    public DBObjectImpl(ConnectionHandler connectionHandler, String name) {
        this.connectionHandler = connectionHandler;
        this.name = name;
    }

    public DBObject getParentObject() {
        if (parentObject != null) {
            parentObject = (DBObject) parentObject.getUndisposedElement();
        }
        return parentObject;
    }

    public DBObject getDefaultNavigationObject() {
        return null;
    }

    public boolean isOfType(DBObjectType objectType) {
        return getObjectType().matches(objectType);
    }

    public String getTypeName() {
        return getObjectType().getName();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public String getQuotedName(boolean quoteAlways) {
        if (quoteAlways ||
                name.indexOf('-') > 0 ||
                name.indexOf('.') > 0 ||
                name.indexOf('#') > 0 ||
                getLanguageDialect(SQLLanguage.INSTANCE).isReservedWord(name)) {
            char quoteChar = DatabaseCompatibilityInterface.getInstance(this).getIdentifierQuotes();
            return quoteChar + name + quoteChar;
        } else {
            return name;
        }
    }

    public Icon getIcon() {
        return getObjectType().getIcon();
    }

    public String getQualifiedName() {
        if (qualifiedName == null) {
            if (getParentObject() == null) {
                qualifiedName = getName();
            } else {
                StringBuilder buffer = new StringBuilder(getName());
                DBObject parentObject = getParentObject();
                while (parentObject != null) {
                    buffer.insert(0, '.');
                    buffer.insert(0, parentObject.getName());
                    parentObject = parentObject.getParentObject();
                }
                qualifiedName = buffer.toString();
            }
        }
        return qualifiedName;
    }

    public String getQualifiedNameWithType() {
        return getTypeName() + " " + getQualifiedName();
    }

    public String getQualifiedNameWithConnectionId() {
        return "[" + getConnectionHandler().getId() + "]" + getQualifiedName();
    }

    public DBUser getOwner() {
        if (owner != null) {
            owner = (DBUser) owner.getUndisposedElement();
        }
        return owner;
    }

    public Icon getOriginalIcon() {
        return getIcon();
    }

    public String getNavigationTooltipText() {
        DBObject parentObject = getParentObject();
        if (parentObject == null) {
            return getTypeName();
        } else {
            return getTypeName() + " (" +
                    parentObject.getTypeName() + " " +
                    parentObject.getName() + ")";
        }
    }


    public String getToolTip() {
        if (isDisposed) {
            return null;
        }
        return new HtmlToolTipBuilder() {
            public void buildToolTip() {
                DBObjectImpl.this.buildToolTip(this);
            }
        }.getToolTip();
    }

    public void buildToolTip(HtmlToolTipBuilder ttb) {
        ttb.append(true, getQualifiedName(), false);
        ttb.append(true, "Connection: ", "-2", null, false );
        ttb.append(false, getConnectionHandler().getPresentableText(), false);
    }

    public DBObjectAttribute[] getObjectAttributes(){return null;}
    public DBObjectAttribute getNameAttribute(){return null;}

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    @Override
    public EnvironmentType getEnvironmentType() {
        return connectionHandler.getEnvironmentType();
    }

    public DBLanguageDialect getLanguageDialect(DBLanguage language) {
        return getConnectionHandler().getLanguageDialect(language);
    }

    public DBObjectListContainer getChildObjects() {
        if (childObjects == null) {
            childObjects = new DBObjectListContainer(this, connectionHandler);
        }
        return childObjects;
    }

    public DBObjectRelationListContainer getChildObjectRelations() {
        if (childObjectRelations == null) {
            childObjectRelations = new DBObjectRelationListContainer(connectionHandler);
        }
        return childObjectRelations;

    }

    public static DBObject getObjectByName(List<? extends DBObject> objects, String name) {
        if (objects != null) {
            for (DBObject object : objects) {
                if (object.getName().equals(name)) {
                    return object;
                }
            }
        }
        return null;
    }

    public DBObject getChildObject(DBObjectType objectType, String name, boolean lookupHidden) {
        if (childObjects == null) {
            return null;
        } else {
            DBObject object = childObjects.getObject(objectType, name);
            if (object == null && lookupHidden) {
                object = childObjects.getHiddenObject(objectType, name);
            }
            return object;
        }
    }

    public DBObject getChildObject(String name, boolean lookupHidden) {
        return childObjects == null ? null :
                childObjects.getObjectForParentType(this.getObjectType(), name, lookupHidden);
    }

    public DBObject getChildObjectNoLoad(String name) {
        return childObjects == null ? null : childObjects.getObjectNoLoad(name);
    }

    public List<DBObject> getChildObjects(DBObjectType objectType) {
        if (objectType.getFamilyTypes().size() > 1) {
            List<DBObject> list = new ArrayList<DBObject>();
            for (DBObjectType childObjectType : objectType.getFamilyTypes()) {
                if (objectType != childObjectType) {
                    List<DBObject> childObjects = getChildObjects(childObjectType);
                    list.addAll(childObjects);
                } else {
                    DBObjectList<DBObject> objectList = childObjects == null ? null : childObjects.getObjectList(objectType);
                    if (objectList != null) {
                        list.addAll(objectList.getObjects());
                    }
                }
            }
            return list;
        } else {
            DBObjectList<DBObject> objectList = childObjects == null ? null : childObjects.getObjectList(objectType);
            return objectList == null ? DBObject.EMPTY_LIST : objectList.getObjects();
        }
    }

    public List<DBObjectNavigationList> getNavigationLists() {
        // todo consider caching;
        return createNavigationLists();
    }

    protected List<DBObjectNavigationList> createNavigationLists() {
        return null;
    }

    public LookupItemFactory getLookupItemFactory(DBLanguage language) {
        if (language == SQLLanguage.INSTANCE) {
            if (sqlLookupItemFactory == null) {
                sqlLookupItemFactory = new DBObjectLookupItemFactory(this, language);
            }
            return sqlLookupItemFactory;
        }
        if (language == PSQLLanguage.INSTANCE) {
            if (psqlLookupItemFactory == null) {
                psqlLookupItemFactory = new DBObjectLookupItemFactory(this, language);
            }
            return psqlLookupItemFactory;
        }
        return null;
    }

    public String extractDDL() throws SQLException {
        String ddl = null;
        CallableStatement statement = null;
        Connection connection = null;

        try {
            connection = getConnectionHandler().getPoolConnection();
            statement = connection.prepareCall("{? = call DBMS_METADATA.GET_DDL(?, ?, ?)}");
            statement.registerOutParameter(1, Types.VARCHAR);
            statement.setString(2, getTypeName().toUpperCase());
            statement.setString(3, getName());
            statement.setString(4, getParentObject().getName());

            statement.execute();
            ddl = statement.getString(1);
            ddl = ddl == null ? null : ddl.trim();
            statement.close();
        } finally{
            ConnectionUtil.closeStatement(statement);
            getConnectionHandler().freePoolConnection(connection);
        }
        return ddl;
    }

    public DBObject getUndisposedElement() {
        if (isDisposed()) {
            DBObject parentObject = getParentObject();

            DBObject undisposedObject = parentObject == null ?
                    getConnectionHandler().getObjectBundle().getObject(getObjectType(), getName()) :
                    parentObject.getUndisposedElement().getChildObject(getObjectType(), getName(), true);
            if (undisposedObject != null){
                return undisposedObject;
            }
        }
        return this;
    }

    public DynamicContent getDynamicContent(DynamicContentType dynamicContentType) {
        if(dynamicContentType instanceof DBObjectType) {
            DBObjectType objectType = (DBObjectType) dynamicContentType;
            DBObjectListContainer objectLists = getChildObjects();
            DynamicContent dynamicContent = objectLists.getObjectList(objectType);
            if (dynamicContent == null) dynamicContent = objectLists.getHiddenObjectList(objectType);
            return dynamicContent;
        }

        if (dynamicContentType instanceof DBObjectRelationType) {
            DBObjectRelationType objectRelationType = (DBObjectRelationType) dynamicContentType;
            return getChildObjectRelations().getObjectRelationList(objectRelationType);
        }

        return null;
    }

    public void reload() {

    }

    @NotNull
    public DatabaseObjectFile getVirtualFile() {
        if (virtualFile == null) {
            virtualFile = new DatabaseObjectFile(this);
        }
        return virtualFile;
    }

    /*********************************************************
     *                   NavigationItem                      *
     *********************************************************/
    public FileStatus getFileStatus() {
        return FileStatus.UNKNOWN;
    }

    public ItemPresentation getPresentation() {
        return this;
    }

    public TextAttributesKey getTextAttributesKey() {
        return SQLTextAttributesKeys.IDENTIFIER;
    }

    public String getLocationString() {
        return null;
    }

    public Icon getIcon(boolean open) {
        return getIcon();
    }

    /*********************************************************
     *                  BrowserTreeElement                   *
     *********************************************************/
    public void initTreeElement() {}

    public boolean isTreeStructureLoaded() {
        return treeChildrenLoaded;
    }

    public boolean canExpand() {
        return !isLeafTreeElement() && isTreeStructureLoaded() && getTreeChild(0).isTreeStructureLoaded();
    }

    public Icon getIcon(int flags) {
        return getIcon();
    }

    public String getPresentableText() {
        return getName();
    }

    public String getPresentableTextDetails() {
        return null;
    }

    public String getPresentableTextConditionalDetails() {
        return null;
    }

    public BrowserTreeElement getTreeParent() {
        if (treeParent != null && treeParent.isDisposed()) {
            updateTreeInfo();
        }
        return treeParent;
    }

    public int getTreeDepth() {
        return treeDepth;
    }

    @NotNull
    public synchronized List<BrowserTreeElement> getAllPossibleTreeChildren() {
        if (allPossibleTreeChildren == null) {
            allPossibleTreeChildren = buildAllPossibleTreeChildren();
        }
        return allPossibleTreeChildren;
    }

    public List<? extends BrowserTreeElement> getTreeChildren() {
        if (visibleTreeChildren == null) {
            visibleTreeChildren = new ArrayList<BrowserTreeElement>();
            visibleTreeChildren.add(new LoadInProgressTreeElement(this));

            new BackgroundTask(getProject(), "Loading data dictionary", true) {
                public void execute(@NotNull ProgressIndicator progressIndicator) {
                    if (!isDisposed()) buildTreeChildren();
                }
            }.start();

        }
        return visibleTreeChildren;
    }

    private void buildTreeChildren() {
        Filter<BrowserTreeElement> filter = connectionHandler.getObjectFilter();
        List<BrowserTreeElement> allPossibleTreeChildren = getAllPossibleTreeChildren();
        List<BrowserTreeElement> newTreeChildren = allPossibleTreeChildren;
        if (allPossibleTreeChildren.size() > 0) {
            if (!filter.acceptsAll(allPossibleTreeChildren)) {
                newTreeChildren = new ArrayList<BrowserTreeElement>();
                for (BrowserTreeElement treeElement : allPossibleTreeChildren) {
                    if (treeElement != null && filter.accepts(treeElement)) {
                        DBObjectList objectList = (DBObjectList) treeElement;
                        newTreeChildren.add(objectList);
                    }
                }
            }

            for (BrowserTreeElement treeElement : newTreeChildren) {
                DBObjectList objectList = (DBObjectList) treeElement;
                objectList.initTreeElement();
            }

            if (visibleTreeChildren.size() == 1 && visibleTreeChildren.get(0) instanceof LoadInProgressTreeElement) {
                visibleTreeChildren.get(0).dispose();
            }
        }
        visibleTreeChildren = newTreeChildren;
        treeChildrenLoaded = true;

        new ConditionalLaterInvocator() {
            public void run() {
                if (!isDisposed()) {
                    DatabaseBrowserManager.updateTree(DBObjectImpl.this, TreeUtil.STRUCTURE_CHANGED);
                    DatabaseBrowserManager.scrollToSelectedElement(getConnectionHandler());
                }
            }
        }.start();
    }

    public void rebuildTreeChildren() {
        Filter<BrowserTreeElement> filter = connectionHandler.getObjectFilter();
        if (visibleTreeChildren != null && DatabaseBrowserUtils.treeVisibilityChanged(getAllPossibleTreeChildren(), visibleTreeChildren, filter)) {
            buildTreeChildren();
        }
        if (visibleTreeChildren != null) {
            for (BrowserTreeElement treeElement : visibleTreeChildren) {
                treeElement.rebuildTreeChildren();
            }
        }
    }

    @NotNull
    public abstract List<BrowserTreeElement> buildAllPossibleTreeChildren();

    public boolean isLeafTreeElement() {
        Filter<BrowserTreeElement> filter = connectionHandler.getObjectFilter();
        for (BrowserTreeElement treeElement : getAllPossibleTreeChildren() ) {
            if (treeElement != null && filter.accepts(treeElement)) {
                return false;
            }
        }
        return true;
    }

    public BrowserTreeElement getTreeChild(int index) {
        return getTreeChildren().get(index);
    }

    public int getTreeChildCount() {
        return getTreeChildren().size();
    }

    public int getIndexOfTreeChild(BrowserTreeElement child) {
        return getTreeChildren().indexOf(child);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof DBObject) {
            DBObject remote = (DBObject) obj;
            DBObject local = this;

            while (remote != null && local != null) {
                if (remote.getConnectionHandler() != local.getConnectionHandler()) return false;
                if (remote.getObjectType() != local.getObjectType()) return false;
                if (!remote.getName().equals(local.getName())) return false;
                remote = remote.getParentObject();
                local = local.getParentObject();
            }

            return remote == null && local == null;
        }
        return false;
    }


    @NotNull
    public Project getProject() throws PsiInvalidElementAccessException {
        return getConnectionHandler().getProject();
    }

    public String toString() {
        return getName();
    }

    public int compareTo(Object o) {
        DBObject remote = (DBObject) o;
        if (getObjectType() == remote.getObjectType()) {
            DBObject localParentObject = getParentObject();
            DBObject remoteParentObject = remote.getParentObject();
            if (localParentObject != null && remoteParentObject != null) {
                int result = localParentObject.compareTo(remoteParentObject);
                if (result != 0) {
                    return result;
                }
            }
            DBObjectType localObjectType = getObjectType();
            DBObjectType remoteObjectType = remote.getObjectType();
            if (localObjectType == remoteObjectType) {
                return getName().compareTo(remote.getName());
            } else {
                return getObjectType().getName().compareTo(remoteObjectType.getName());
            }
        } else {
            return getObjectType().getTypeId().ordinal() - remote.getObjectType().getTypeId().ordinal();
        }
    }

    public List<PresentableProperty> getPresentableProperties() {
        List<PresentableProperty> properties = new ArrayList<PresentableProperty>();
        DBObject parent = getParentObject();
        while (parent != null) {
            properties.add(new DBObjectPresentableProperty(parent));
            parent = parent.getParentObject();
        }
        properties.add(new ConnectionPresentableProperty(connectionHandler));

        return properties;
    }

    public int hashCode() {
        return getParentObject() == null ? getName().hashCode() : getParentObject().getName().hashCode() + getName().hashCode();
    }

    public boolean isValid() {
        return !isDisposed();
    }

    /*********************************************************
    *               DynamicContentElement                    *
    *********************************************************/
    public DynamicContent getOwnerContent() {
        return ownerContent;
    }

    public void setOwnerContent(DynamicContent ownerContent) {
        this.ownerContent = ownerContent;
    }

    public void dispose() {
        if (!isDisposed()) {
            isDisposed = true;
            DisposeUtil.dispose(childObjects);
            DisposeUtil.dispose(childObjectRelations);
            childObjects = null;
            childObjectRelations = null;
            visibleTreeChildren = null;
            treeParent = null;
            owner = null;
            //connectionHandler = null;
            //parentObject = null;
            sqlLookupItemFactory = null;
            psqlLookupItemFactory = null;
        }
    }


    public boolean isDisposed() {
        return isDisposed;
    }

    public String getDescription() {
        return getQualifiedName();
    }

    /*********************************************************
    *                      Navigatable                      *
    *********************************************************/
    public void navigate(boolean requestFocus) {
        DatabaseBrowserManager browserManager = DatabaseBrowserManager.getInstance(getProject());
        browserManager.navigateToElement(this, requestFocus);
    }

    public boolean canNavigate() {
        return true;
    }

    /*********************************************************
     *                   PsiElement                          *
     *********************************************************/

    @Override
    public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
        NavigationPsiCache psiCache = getConnectionHandler().getPsiCache();
        return psiCache.getPsiFile(this);
    }
}
