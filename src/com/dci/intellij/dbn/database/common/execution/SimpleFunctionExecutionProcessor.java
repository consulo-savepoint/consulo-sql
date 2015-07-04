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

package com.dci.intellij.dbn.database.common.execution;

import com.dci.intellij.dbn.execution.method.MethodExecutionInput;
import com.dci.intellij.dbn.object.DBFunction;

public class SimpleFunctionExecutionProcessor extends MethodExecutionProcessorImpl<DBFunction> {
    public SimpleFunctionExecutionProcessor(DBFunction function) {
        super(function);
    }

    @Override
    public String buildExecutionCommand(MethodExecutionInput executionInput) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{? = call ");
        buffer.append(getMethod().getQualifiedName());
        buffer.append("(");
        for (int i=1; i<getParametersCount(); i++) {
            if (i>1) buffer.append(",");
            buffer.append("?");
        }
        buffer.append(")}");
        return buffer.toString();
    }
}