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

package com.dci.intellij.dbn.vfs;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class DBFileIconProvider /*implements FileIconProvider*/{
   // @Override
    public Icon getIcon(@NotNull VirtualFile file, int flags, @Nullable Project project) {
        if (file instanceof DBVirtualFile) {
            DBVirtualFile virtualFile = (DBVirtualFile) file;
            return virtualFile.getIcon();
        }
        return null;
    }
}
