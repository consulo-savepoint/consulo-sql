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

package com.dci.intellij.dbn.common.option;


import com.dci.intellij.dbn.common.Constants;
import com.dci.intellij.dbn.common.Icons;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;

import java.text.MessageFormat;

public class InteractiveOptionHandler implements DialogWrapper.DoNotAskOption{
    private String title;
    private String message;
    private Integer selectedOption;
    private String[] options;
    private int maxPersistableOption;

    public InteractiveOptionHandler(String title, String message, int maxPersistableOption, String... options) {
        this.title = title;
        this.message = message;
        this.options = options;
        this.maxPersistableOption = maxPersistableOption;
    }

    @Override
    public boolean isToBeShown() {
        return true;
    }

    @Override
    public void setToBeShown(boolean keepAsking, int selectedOption) {
        if (keepAsking || selectedOption > maxPersistableOption) {
            this.selectedOption = null;
        } else {
            this.selectedOption = selectedOption;
        }
    }

    @Override
    public boolean canBeHidden() {
        return true;
    }

    @Override
    public boolean shouldSaveOptionsOnCancel() {
        return false;
    }

    @Override
    public String getDoNotShowMessage() {
        return "Remember option";
    }

    public int resolve(String ... messageArgs) {
        if (selectedOption != null) {
            return selectedOption;
        } else {
            return Messages.showDialog(
                    MessageFormat.format(message, messageArgs),
                    Constants.DBN_TITLE_PREFIX + title, options, 0, Icons.DIALOG_WARNING, this);
        }
    }
}
