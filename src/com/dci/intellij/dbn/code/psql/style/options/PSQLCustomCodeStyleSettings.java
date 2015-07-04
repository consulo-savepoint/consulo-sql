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

package com.dci.intellij.dbn.code.psql.style.options;

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class PSQLCustomCodeStyleSettings extends CustomCodeStyleSettings {
    private PSQLCodeStyleSettings codeStyleSettings;
    protected PSQLCustomCodeStyleSettings(CodeStyleSettings container) {
        super("PSQLCodeStyleSettings", container);
        codeStyleSettings = new PSQLCodeStyleSettings();
    }

    public PSQLCodeStyleSettings getCodeStyleSettings() {
        return codeStyleSettings;
    }

    public void readExternal(Element parentElement) throws InvalidDataException {
        codeStyleSettings.readConfiguration(parentElement);
    }

    public void writeExternal(Element parentElement, @NotNull CustomCodeStyleSettings parentSettings) throws WriteExternalException {
        codeStyleSettings.writeConfiguration(parentElement);
    }
}