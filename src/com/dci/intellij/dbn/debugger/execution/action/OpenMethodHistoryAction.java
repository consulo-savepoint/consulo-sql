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

package com.dci.intellij.dbn.debugger.execution.action;

import com.dci.intellij.dbn.common.Icons;
import com.dci.intellij.dbn.common.util.ActionUtil;
import com.dci.intellij.dbn.debugger.execution.DBProgramRunConfiguration;
import com.dci.intellij.dbn.execution.method.MethodExecutionInput;
import com.dci.intellij.dbn.execution.method.MethodExecutionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class OpenMethodHistoryAction extends AbstractSelectMethodAction {
    public OpenMethodHistoryAction(DBProgramRunConfiguration configuration) {
        super("Execution History", Icons.METHOD_EXECUTION_HISTORY, configuration);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = ActionUtil.getProject(e);
        MethodExecutionManager methodExecutionManager = MethodExecutionManager.getInstance(project);
        MethodExecutionInput currentInput = getConfiguration().getExecutionInput();
        MethodExecutionInput methodExecutionInput = methodExecutionManager.selectHistoryMethodExecutionInput(currentInput);
        if (methodExecutionInput != null) {
            getConfiguration().setExecutionInput(methodExecutionInput);
        }
    }
}
