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
package com.android.tools.idea.fileTypes;

import com.intellij.openapi.fileTypes.FileType;
import consulo.fontviewer.icon.FontViewerIconGroup;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class FontFileType implements FileType
{
	public static final FontFileType INSTANCE = new FontFileType();
	@NonNls
	private static final String TTF_EXTENSION = "ttf";

	private FontFileType()
	{
	}

	@NotNull
	@Override
	public String getId()
	{
		return "Font";
	}

	@NotNull
	@Override
	public LocalizeValue getDescription()
	{
		return LocalizeValue.localizeTODO("Font");
	}

	@NotNull
	@Override
	public String getDefaultExtension()
	{
		return TTF_EXTENSION;
	}

	@Override
	public Image getIcon()
	{
		return FontViewerIconGroup.font_file();
	}

	@Override
	public boolean isBinary()
	{
		return true;
	}
}
