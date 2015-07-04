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

package com.dci.intellij.dbn.common.environment.options;


import com.dci.intellij.dbn.common.options.PersistentConfiguration;
import com.dci.intellij.dbn.common.options.setting.BooleanSetting;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

public class EnvironmentVisibilitySettings implements PersistentConfiguration {
    private BooleanSetting connectionTabs = new BooleanSetting("connection-tabs", true);
    private BooleanSetting objectEditorTabs = new BooleanSetting("object-editor-tabs", true);
    private BooleanSetting scriptEditorTabs = new BooleanSetting("script-editor-tabs", false);
    private BooleanSetting dialogHeaders = new BooleanSetting("dialog-headers", true);
    private BooleanSetting executionResultTabs = new BooleanSetting("execution-result-tabs", true);

    public BooleanSetting getConnectionTabs() {
        return connectionTabs;
    }

    public BooleanSetting getObjectEditorTabs() {
        return objectEditorTabs;
    }

    public BooleanSetting getScriptEditorTabs() {
        return scriptEditorTabs;
    }

    public BooleanSetting getDialogHeaders() {
        return dialogHeaders;
    }

    public BooleanSetting getExecutionResultTabs() {
        return executionResultTabs;
    }

    @Override
    public void readConfiguration(Element element) throws InvalidDataException {
        connectionTabs.readConfiguration(element);
        dialogHeaders.readConfiguration(element);
        objectEditorTabs.readConfiguration(element);
        scriptEditorTabs.readConfiguration(element);
        executionResultTabs.readConfiguration(element);
    }

    @Override
    public void writeConfiguration(Element element) throws WriteExternalException {
        connectionTabs.writeConfiguration(element);
        dialogHeaders.writeConfiguration(element);
        objectEditorTabs.writeConfiguration(element);
        scriptEditorTabs.writeConfiguration(element);
        executionResultTabs.writeConfiguration(element);
    }
}
