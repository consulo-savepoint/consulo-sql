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

package com.dci.intellij.dbn.editor.code.action;

import com.dci.intellij.dbn.code.common.completion.options.CodeCompletionSettings;
import com.dci.intellij.dbn.common.Icons;
import com.dci.intellij.dbn.common.util.ActionUtil;
import com.dci.intellij.dbn.options.ui.GlobalProjectSettingsDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;

public class OpenSettingsAction extends DumbAwareAction {

    public void actionPerformed(AnActionEvent e) {
        Project project = ActionUtil.getProject(e);
        GlobalProjectSettingsDialog globalSettingsDialog = new GlobalProjectSettingsDialog(project);
        CodeCompletionSettings settings = CodeCompletionSettings.getInstance(project);
        globalSettingsDialog.focusSettings(settings);
        globalSettingsDialog.show();
    }

    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setIcon(Icons.ACTION_SETTINGS);
        presentation.setText("Settings");
    }
}
