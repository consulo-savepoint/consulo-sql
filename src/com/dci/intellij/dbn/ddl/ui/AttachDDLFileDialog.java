/*
 * Copyright 2012-2014 Dan Cioca
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dci.intellij.dbn.ddl.ui;

import com.dci.intellij.dbn.common.ui.dialog.DBNDialog;
import com.dci.intellij.dbn.ddl.DDLFileAttachmentManager;
import com.dci.intellij.dbn.object.common.DBSchemaObject;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import java.awt.event.ActionEvent;
import java.util.List;

public class AttachDDLFileDialog extends DBNDialog {
    private SelectDDLFileForm fileForm;
    private DBSchemaObject object;

    public AttachDDLFileDialog(List<VirtualFile> virtualFiles, DBSchemaObject object) {
        super(object.getProject(), "Attach DDL File", true);
        this.object = object;
        String hint =
            "Following DDL files were found matching the name of the selected " + object.getTypeName() + ".\n" +
            "Select files to attach to this object.\n\n" +
            "NOTE: \nBound DDL files will become readonly and their content will change automatically when the database object is edited.";
        fileForm = new SelectDDLFileForm(object, virtualFiles, hint);
        init();
    }

    protected String getDimensionServiceKey() {
        return "DBNavigator.DDLFileBinding";
    }

    @NotNull
    protected final Action[] createActions() {
        return new Action[]{
                getOKAction(),
                new SelectAllAction(),
                new SelectNoneAction(),
                getCancelAction()
        };
    }

    private class SelectAllAction extends AbstractAction {
        private SelectAllAction() {
            super("Select all");
        }

        public void actionPerformed(ActionEvent e) {
            fileForm.selectAll();
            doOKAction();
        }
    }

    private class SelectNoneAction extends AbstractAction {
        private SelectNoneAction() {
            super("Select none");
        }

        public void actionPerformed(ActionEvent e) {
            fileForm.selectNone();
            close(2);
        }
    }

    protected void doOKAction() {
        DDLFileAttachmentManager fileAttachmentManager = DDLFileAttachmentManager.getInstance(object.getProject());
        Object[] selectedPsiFiles = getSelection();
        for (Object selectedPsiFile : selectedPsiFiles) {
            VirtualFile virtualFile = (VirtualFile) selectedPsiFile;
            fileAttachmentManager.bindDDLFile(object, virtualFile);
        }
        super.doOKAction();
    }

    @Nullable
    protected JComponent createCenterPanel() {
        return fileForm.getComponent();
    }

    public Object[] getSelection() {
        return fileForm.getSelection();
    }
}
