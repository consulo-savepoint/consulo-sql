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

package com.dci.intellij.dbn.editor.data.filter.action;

import com.dci.intellij.dbn.common.Icons;
import com.dci.intellij.dbn.editor.data.filter.DatasetFilter;
import com.dci.intellij.dbn.editor.data.filter.ui.DatasetFilterList;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class CreateBasicFilterAction extends AbstractFilterListAction {

    public CreateBasicFilterAction(DatasetFilterList filterList) {
        super(filterList,  "Basic filter", Icons.DATASET_FILTER_BASIC);
    }

    public void actionPerformed(AnActionEvent e) {
        DatasetFilter filter = getFilterGroup().createBasicFilter(true);
        getFilterList().setSelectedValue(filter, true);
    }
}