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

package com.dci.intellij.dbn.editor.data.options;

import com.dci.intellij.dbn.common.options.Configuration;
import com.dci.intellij.dbn.common.options.setting.SettingsUtil;
import com.dci.intellij.dbn.editor.data.options.ui.DatatEditorValueListPopupSettingsForm;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

public class DataEditorValueListPopupSettings extends Configuration<DatatEditorValueListPopupSettingsForm> {
    private boolean activeForPrimaryKeyColumns = false;
    private int elementCountThreshold = 1000;
    private int dataLengthThreshold = 250;

    public String getDisplayName() {
        return "Data editor filters settings";
    }

    public String getHelpTopic() {
        return "dataEditor";
    }

    /*********************************************************
    *                       Settings                        *
    *********************************************************/

    public boolean isActiveForPrimaryKeyColumns() {
        return activeForPrimaryKeyColumns;
    }

    public void setActiveForPrimaryKeyColumns(boolean activeForPrimaryKeyColumns) {
        this.activeForPrimaryKeyColumns = activeForPrimaryKeyColumns;
    }

    public int getElementCountThreshold() {
        return elementCountThreshold;
    }

    public void setElementCountThreshold(int elementCountThreshold) {
        this.elementCountThreshold = elementCountThreshold;
    }

    public int getDataLengthThreshold() {
        return dataLengthThreshold;
    }

    public void setDataLengthThreshold(int dataLengthThreshold) {
        this.dataLengthThreshold = dataLengthThreshold;
    }

    /****************************************************
     *                   Configuration                  *
     ****************************************************/
    public DatatEditorValueListPopupSettingsForm createConfigurationEditor() {
        return new DatatEditorValueListPopupSettingsForm(this);
    }

    @Override
    public String getConfigElementName() {
        return "values-list-popup";
    }

    public void readConfiguration(Element element) throws InvalidDataException {
        activeForPrimaryKeyColumns = SettingsUtil.getBoolean(element, "active-for-primary-keys", activeForPrimaryKeyColumns);
        elementCountThreshold = SettingsUtil.getInteger(element, "element-count-threshold", elementCountThreshold);
        dataLengthThreshold = SettingsUtil.getInteger(element, "data-length-threshold", dataLengthThreshold);
    }

    public void writeConfiguration(Element element) throws WriteExternalException {
        SettingsUtil.setBoolean(element, "active-for-primary-keys", activeForPrimaryKeyColumns);
        SettingsUtil.setInteger(element, "element-count-threshold", elementCountThreshold);
        SettingsUtil.setInteger(element, "data-length-threshold", dataLengthThreshold);
    }
}
