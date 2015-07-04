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

package com.dci.intellij.dbn.generator;

import com.dci.intellij.dbn.common.message.MessageBundle;
import com.dci.intellij.dbn.object.DBColumn;
import com.dci.intellij.dbn.object.DBTable;

import java.util.Iterator;

public class InsertStatementGenerator extends StatementGenerator {
    private DBTable table;

    public InsertStatementGenerator(DBTable table) {
        this.table = table;
    }

    @Override
    public StatementGeneratorResult generateStatement() {
        StatementGeneratorResult result = new StatementGeneratorResult();
        MessageBundle messages = result.getMessages();

        StringBuilder statement = new StringBuilder();
        statement.append("insert into ");
        statement.append(table.getName());
        statement.append(" (\n");

        Iterator<DBColumn> columnIterator = table.getColumns().iterator();
        while (columnIterator.hasNext()) {
            DBColumn column = columnIterator.next();
            statement.append("    ");
            statement.append(column.getName());
            if (columnIterator.hasNext()) {
                statement.append(",\n");
            } else {
                statement.append(")\n");
            }
        }
        statement.append("values (\n");

        columnIterator = table.getColumns().iterator();
        while (columnIterator.hasNext()) {
            DBColumn column = columnIterator.next();
            statement.append("    :");
            statement.append(column.getName().toLowerCase());
            if (columnIterator.hasNext()) {
                statement.append(",\n");
            } else {
                statement.append(")\n");
            }
        }
        statement.append(";");

        result.setStatement(statement.toString());
        return result;
    }
}
