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
package consulo.fontviewer;

import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.fileType.FileType;

import jakarta.annotation.Nonnull;

public class FontFileType implements FileType
{
	public static final FontFileType INSTANCE = new FontFileType();

	public static final String WOFF_EXTENSION = "woff";
	private static final String TTF_EXTENSION = "ttf";

	private FontFileType()
	{
	}

	@Nonnull
	@Override
	public String getId()
	{
		return "Font";
	}

	@Nonnull
	@Override
	public LocalizeValue getDescription()
	{
		return LocalizeValue.localizeTODO("Font");
	}

	@Nonnull
	@Override
	public String getDefaultExtension()
	{
		return TTF_EXTENSION;
	}

	@Nonnull
	@Override
	public Image getIcon()
	{
		return PlatformIconGroup.filetypesFont();
	}

	@Override
	public boolean isBinary()
	{
		return true;
	}
}
