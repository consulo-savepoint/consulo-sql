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

package com.dci.intellij.dbn.data.find.action;

import com.dci.intellij.dbn.data.find.DataSearchComponent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.project.DumbAware;
import com.intellij.util.containers.ContainerUtil;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class PrevOccurrenceAction extends DataSearchHeaderAction implements DumbAware {

    public PrevOccurrenceAction(DataSearchComponent searchComponent, JComponent component, boolean isSearchComponent) {
        super(searchComponent);

        copyFrom(ActionManager.getInstance().getAction(IdeActions.ACTION_PREVIOUS_OCCURENCE));
        Set<Shortcut> shortcuts = new HashSet<Shortcut>();
        ContainerUtil.addAll(shortcuts, ActionManager.getInstance().getAction(IdeActions.ACTION_FIND_PREVIOUS).getShortcutSet().getShortcuts());

        if (isSearchComponent) {
            ContainerUtil.addAll(shortcuts, ActionManager.getInstance().getAction(IdeActions.ACTION_EDITOR_MOVE_CARET_UP).getShortcutSet().getShortcuts());
            shortcuts.add(new KeyboardShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK), null));
        }
        registerShortcutsToComponent(shortcuts, this, component);
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        getSearchComponent().searchBackward();
    }

    @Override
    public void update(final AnActionEvent e) {
        e.getPresentation().setEnabled(getSearchComponent().hasMatches());
    }
}
