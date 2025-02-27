/*
 * Copyright 2013-2022 consulo.io
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

package consulo.fontviewer.desktop.awt.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.fileEditor.FileEditor;
import consulo.fontviewer.FontFileType;
import consulo.fontviewer.internal.FontEditorFactory;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 19-Sep-22
 */
@ExtensionImpl
public class DesktopAWTFontEditorFactory implements FontEditorFactory
{
	@Override
	public boolean accept(@Nonnull Project project, @Nonnull VirtualFile file)
	{
		return file.getFileType() == FontFileType.INSTANCE && !FontFileType.WOFF_EXTENSION.equals(file.getExtension());
	}

	@Nonnull
	@Override
	public FileEditor createEditor(@Nonnull Project project, @Nonnull VirtualFile file, @Nonnull String exampleText)
	{
		return new FontEditor(file, exampleText);
	}
}
