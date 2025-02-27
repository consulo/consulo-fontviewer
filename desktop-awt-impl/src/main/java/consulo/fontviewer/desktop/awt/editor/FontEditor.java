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

package consulo.fontviewer.desktop.awt.editor;

import consulo.fileEditor.FileEditor;
import consulo.logging.Logger;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.UIUtil;
import consulo.util.dataholder.UserDataHolderBase;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

/**
 * {@link FileEditor} that providers preview for TTF fonts.
 */
class FontEditor extends UserDataHolderBase implements FileEditor
{
	private static final Logger LOG = Logger.getInstance(FontEditor.class);

	private static final String NAME = "Font";

	private static final float MAX_FONT_SIZE = UIUtil.getFontSize(UIUtil.FontSize.NORMAL) + JBUI.scale(30f);
	private static final float MIN_FONT_SIZE = UIUtil.getFontSize(UIUtil.FontSize.MINI);
	private static final Border BORDER = JBUI.Borders.empty(50);
	private static final Font DEFAULT_FONT = UIUtil.getLabelFont();

	private final JTextArea myTextArea;
	private final VirtualFile myFile;

	private final JPanel myRootPanel;
	private float myCurrentFontSize;
	private final JTextArea myFontNameArea;

	@NotNull
	private static JTextArea createTextArea()
	{
		JTextArea area = new JTextArea();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);

		return area;
	}

	/**
	 * Find the text that we can display using the given font. If the font can render lorem ipsum, that will be used. Otherwise
	 * we find visible glyphs that we can render.
	 * If there is nothing that can be displayed using this font, an empty string is returned.
	 */
	@NotNull
	private static String findDisplayableText(@NotNull Font font, @NotNull String exampleText)
	{
		if(font.canDisplayUpTo(exampleText) == -1)
		{
			// Everything can be displayed
			return exampleText;
		}

		StringBuilder displayableString = new StringBuilder(50);
		// Display a maximum of 250 glyphs
		int numGlyphs = Math.min(font.getNumGlyphs(), 250);
		int displayedGlyphs = 0;
		for(int i = Character.MIN_CODE_POINT; i < Character.MAX_CODE_POINT; i++)
		{
			if(!Character.isValidCodePoint(i))
			{
				continue;
			}
			if(displayedGlyphs >= numGlyphs)
			{
				return displayableString.toString();
			}
			if(font.canDisplay(i))
			{
				displayedGlyphs++;
				displayableString.appendCodePoint(i);
			}
		}

		return "";
	}

	public FontEditor(@NotNull VirtualFile file, @NotNull String exampleText)
	{
		myFile = file;
		myRootPanel = new JPanel(new BorderLayout());
		myRootPanel.setBackground(UIUtil.getTextFieldBackground());
		myRootPanel.setBorder(BORDER);
		myFontNameArea = createTextArea();
		myTextArea = createTextArea();

		myCurrentFontSize = UIUtil.getFontSize(UIUtil.FontSize.NORMAL) + JBUI.scale(15f);

		try
		{
			// Derive the font and set it to large
			Font font = Font.createFont(Font.TRUETYPE_FONT, file.getInputStream()).deriveFont(myCurrentFontSize);

			myFontNameArea.setText(font.getFontName());
			if(font.canDisplayUpTo(font.getFontName()) == -1)
			{
				myFontNameArea.setFont(font);
			}
			else
			{
				myFontNameArea.setFont(DEFAULT_FONT.deriveFont(myCurrentFontSize));
			}

			String displayableText = findDisplayableText(font, exampleText);
			if(!displayableText.isEmpty())
			{
				myTextArea.setFont(font);
				myTextArea.setText(displayableText);
			}
			else
			{
				// We can not display anything using these font so just show a message
				myTextArea.setFont(DEFAULT_FONT);
				myTextArea.setEditable(false);
				myTextArea.setText("This font does not contain any glyphs that can be previewed");
			}
			myRootPanel.addMouseWheelListener(this::onMouseWheelEvent);
		}
		catch(FontFormatException | IOException e)
		{
			String message = "Unable to open font " + file.getName();

			myTextArea.setFont(UIUtil.getLabelFont());
			myTextArea.setEditable(false);
			myTextArea.setText(message);
			LOG.warn(message, e);
		}
		myRootPanel.add(myFontNameArea, BorderLayout.NORTH);
		myRootPanel.add(myTextArea, BorderLayout.CENTER);
	}

	private void onMouseWheelEvent(MouseWheelEvent e)
	{
		float increment = (e.getWheelRotation() < 0) ? -1f : 1f;

		float newFontSize = Math.min(Math.max(MIN_FONT_SIZE, myCurrentFontSize + increment), MAX_FONT_SIZE);
		if(newFontSize != myCurrentFontSize)
		{
			myCurrentFontSize = newFontSize;
			myTextArea.setFont(myTextArea.getFont().deriveFont(myCurrentFontSize));
			myFontNameArea.setFont(myFontNameArea.getFont().deriveFont(myCurrentFontSize));
		}
	}

	@NotNull
	@Override
	public JComponent getComponent()
	{
		return myRootPanel;
	}

	@Nullable
	@Override
	public JComponent getPreferredFocusedComponent()
	{
		return myTextArea;
	}

	@NotNull
	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public boolean isModified()
	{
		return false;
	}

	@Override
	public boolean isValid()
	{
		return myFile.isValid();
	}

	@Override
	public void selectNotify()
	{
	}

	@Override
	public void deselectNotify()
	{
	}

	@Override
	public void addPropertyChangeListener(@Nonnull kava.beans.PropertyChangeListener propertyChangeListener)
	{

	}

	@Override
	public void removePropertyChangeListener(@Nonnull kava.beans.PropertyChangeListener propertyChangeListener)
	{

	}

	@Override
	public void dispose()
	{
	}
}
