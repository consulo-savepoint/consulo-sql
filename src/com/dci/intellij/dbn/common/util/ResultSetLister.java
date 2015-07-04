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

package com.dci.intellij.dbn.common.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetLister {

    public static String list(String name, ResultSet resultSet) {
        StringBuilder buffer = new StringBuilder(name);
        try {
            buffer.append("\n--------------------------------\n");
            if (resultSet == null) {
                buffer.append("NO RESULT SET");
                return buffer.toString();
            }
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i=0; i<columnCount; i++) {
                buffer.append(metaData.getColumnName(i+1)).append('\t');
            }
            while(resultSet.next()) {
                buffer.append("\n");
                for (int i=0; i<columnCount; i++) {
                    buffer.append(resultSet.getString(i+1)).append('\t');
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
