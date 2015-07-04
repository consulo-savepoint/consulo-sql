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

package com.dci.intellij.dbn.object.properties;

import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.intellij.pom.Navigatable;

import javax.swing.*;

public class ConnectionPresentableProperty extends PresentableProperty{
    private ConnectionHandler connectionHandler;

    public ConnectionPresentableProperty(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public String getName() {
        return "Connection";
    }

    public String getValue() {
        return connectionHandler.getName();
    }

    public Icon getIcon() {
        return connectionHandler.getIcon();
    }

    @Override
    public Navigatable getNavigatable() {
        return connectionHandler.getObjectBundle();
    }
}
