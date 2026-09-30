/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.fontviewer.impl.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.dumb.DumbAware;
import consulo.fileEditor.FileEditor;
import consulo.fileEditor.FileEditorPolicy;
import consulo.fileEditor.FileEditorProvider;
import consulo.fontviewer.FontFileType;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.virtualFileSystem.VirtualFile;

@ExtensionImpl
public class FontEditorProvider implements FileEditorProvider, DumbAware {
    private static final String ID = "fontseditor";

    private static final String LOREM_TEXT = new LoremGenerator().generate(50, true);

    @Override
    public boolean accept(Project project, VirtualFile file) {
        return file.getFileType() == FontFileType.INSTANCE;
    }

    @RequiredUIAccess
    @Override
    public FileEditor createEditor(Project project, VirtualFile file) {
        return new FontEditor(project, file, LOREM_TEXT);
    }

    @Override
    public String getEditorTypeId() {
        return ID;
    }

    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
