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
import consulo.application.Application;
import consulo.application.dumb.DumbAware;
import consulo.fileEditor.FileEditor;
import consulo.fileEditor.FileEditorPolicy;
import consulo.fileEditor.FileEditorProvider;
import consulo.fontviewer.internal.FontEditorFactory;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Inject;

import jakarta.annotation.Nonnull;

@ExtensionImpl
public class FontEditorProvider implements FileEditorProvider, DumbAware
{
	private static final String ID = "fontseditor";

	private static final String LOREM_TEXT = new LoremGenerator().generate(50, true);

	private final Application myApplication;

	@Inject
	public FontEditorProvider(Application application)
	{
		myApplication = application;
	}

	@Override
	public boolean accept(@Nonnull Project project, @Nonnull VirtualFile file)
	{
		for(FontEditorFactory factory : myApplication.getExtensionList(FontEditorFactory.class))
		{
			if(factory.accept(project, file))
			{
				return true;
			}
		}
		return false;
	}

	@RequiredUIAccess
	@Nonnull
	@Override
	public FileEditor createEditor(@Nonnull Project project, @Nonnull VirtualFile file)
	{
		for(FontEditorFactory factory : myApplication.getExtensionList(FontEditorFactory.class))
		{
			if(factory.accept(project, file))
			{
				return factory.createEditor(project, file, LOREM_TEXT);
			}
		}

		throw new UnsupportedOperationException("Can't find font editor implementation");
	}

	@Nonnull
	@Override
	public String getEditorTypeId()
	{
		return ID;
	}

	@Nonnull
	@Override
	public FileEditorPolicy getPolicy()
	{
		return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
	}
}
