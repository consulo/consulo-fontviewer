/*
 * Copyright 2013-2026 consulo.io
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
package consulo.fontviewer.impl.editor;

import consulo.ui.font.Font;

import java.util.stream.IntStream;

/**
 * @author VISTALL
 * @since 2026-09-30
 */
record FontSample(String fontName, String text) {
    private static final int MAX_GLYPHS = 250;

    static FontSample of(Font font, String fileName, String loremText) {
        String fontName = font.getFontName().isEmpty() ? fileName : font.getFontName();
        return new FontSample(fontName, findDisplayableText(font, loremText));
    }

    private static String findDisplayableText(Font font, String loremText) {
        if (loremText.codePoints().filter(FontSample::isVisible).allMatch(font::canDisplay)) {
            return loremText;
        }

        StringBuilder text = new StringBuilder();
        IntStream.rangeClosed(0, Character.MAX_CODE_POINT)
            .filter(FontSample::isVisible)
            .filter(font::canDisplay)
            .limit(MAX_GLYPHS)
            .forEach(text::appendCodePoint);
        return text.toString();
    }

    private static boolean isVisible(int codePoint) {
        return !Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint);
    }
}
