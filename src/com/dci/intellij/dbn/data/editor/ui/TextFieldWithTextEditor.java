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

package com.dci.intellij.dbn.data.editor.ui;

import com.dci.intellij.dbn.common.Icons;
import com.dci.intellij.dbn.common.ui.KeyUtil;
import com.dci.intellij.dbn.data.editor.text.TextEditorAdapter;
import com.dci.intellij.dbn.data.editor.text.ui.TextEditorDialog;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TextFieldWithTextEditor extends JPanel implements DataEditorComponent, TextEditorAdapter {
    private JTextField textField;
    private JButton button;

    private UserValueHolder userValueHolder;
    private Project project;

    public TextFieldWithTextEditor(Project project) {
        super(new BorderLayout(2, 0));
        this.project = project;
        setBounds(0, 0, 0, 0);

        textField = new JTextField();
        textField.setMargin(new Insets(1, 3, 1, 1));
        add(textField, BorderLayout.CENTER);

        button = new JButton(Icons.DATA_EDITOR_BROWSE);
        button.setFocusable(false);
        button.addActionListener(actionListener);
        button.setMargin(new Insets(0, 4, 0, 4));
        button.setVisible(true);
        button.setBackground(getBackground());
        Shortcut[] shortcuts = KeyUtil.getShortcuts(IdeActions.ACTION_SHOW_INTENTION_ACTIONS);
        String shortcutText = KeymapUtil.getShortcutsText(shortcuts);

        button.setToolTipText("Open editor (" + shortcutText +")");
        add(button, BorderLayout.EAST);
        textField.setPreferredSize(new Dimension(150, -1));
        textField.addKeyListener(keyListener);
        textField.setEditable(false);

        customizeButton(button);
        customizeTextField(textField);
    }

    public void setEditable(boolean editable){
        textField.setEditable(editable);
    }

    public void setUserValueHolder(UserValueHolder userValueHolder) {
        this.userValueHolder = userValueHolder;
    }


    public void customizeTextField(JTextField textField) {}
    public void customizeButton(JButton button) {}

    public boolean isSelected() {
        Document document = textField.getDocument();
        return document.getLength() > 0 &&
               textField.getSelectionStart() == 0 &&
               textField.getSelectionEnd() == document.getLength();
    }

    public void clearSelection() {
        if (isSelected()) {
            textField.setSelectionStart(0);
            textField.setSelectionEnd(0);
            textField.setCaretPosition(0);
        }
    }

    public JTextField getTextField() {
        return textField;
    }

    @Override
    public String getText() {
        return textField.getText();
    }

    @Override
    public void setText(String text) {
        textField.setText(text);
    }

    public JButton getButton() {
        return button;
    }

    @Override
    public void setEnabled(boolean enabled) {
        textField.setEditable(enabled);
    }

    public void openEditor() {
        TextEditorDialog.show(project, this);
    }

    /********************************************************
     *                      KeyListener                     *
     ********************************************************/
    private KeyListener keyListener = new KeyAdapter() {
        public void keyPressed(KeyEvent keyEvent) {
            Shortcut[] shortcuts = KeyUtil.getShortcuts(IdeActions.ACTION_SHOW_INTENTION_ACTIONS);
            if (KeyUtil.match(shortcuts, keyEvent)) {
                openEditor();
            }
        }
    };
    /********************************************************
     *                    ActionListener                    *
     ********************************************************/
    private ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            openEditor();
        }
    };

    public UserValueHolder getUserValueHolder() {
        return userValueHolder;
    }

    /********************************************************
     *                 TextEditorListener                   *
     ********************************************************/
    public void afterUpdate() {
        if (userValueHolder.getUserValue() instanceof String) {
            String text = (String) userValueHolder.getUserValue();
            setEditable(text == null || (text.length() < 1000 && text.indexOf('\n') == -1));
            setText(text);
        }
    }
}
