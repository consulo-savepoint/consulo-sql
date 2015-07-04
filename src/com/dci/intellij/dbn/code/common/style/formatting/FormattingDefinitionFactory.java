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

package com.dci.intellij.dbn.code.common.style.formatting;

import org.jdom.Element;

public class FormattingDefinitionFactory {
    public static FormattingDefinition cloneDefinition(FormattingDefinition attributes) {
        if (attributes != null && !attributes.isEmpty()) {
            return new FormattingDefinition(attributes);
        }
        return null;
    }

    public static FormattingDefinition loadDefinition(Element element) {
        if (element != null) {
            FormattingDefinition attributes = new FormattingDefinition(element);
            if (!attributes.isEmpty()) {
                return attributes;
            }
        }
        return null;
    }

    public static FormattingDefinition mergeDefinitions(FormattingDefinition definition, FormattingDefinition defaultDefinition) {
        if (definition == null) {
            if (defaultDefinition != null && !defaultDefinition.isEmpty()) {
                definition = new FormattingDefinition(defaultDefinition);
            }
        } else if (defaultDefinition != null){
            definition.merge(defaultDefinition);
        }
        return definition;
    }

}
