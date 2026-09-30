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

import consulo.application.concurrent.coroutine.DisposableCoroutineScope;
import consulo.fileEditor.FileEditor;
import consulo.fontviewer.localize.FontViewerLocalize;
import consulo.localize.LocalizeValue;
import consulo.logging.Logger;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.*;
import consulo.ui.Button;
import consulo.ui.Component;
import consulo.ui.Label;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.font.Font;
import consulo.ui.font.FontManager;
import consulo.ui.image.Image;
import consulo.ui.layout.DockLayout;
import consulo.ui.layout.HorizontalLayout;
import consulo.ui.layout.LoadingLayout;
import consulo.util.concurrent.coroutine.Coroutine;
import consulo.util.concurrent.coroutine.step.CodeExecution;
import consulo.util.concurrent.coroutine.step.CompletableFutureStep;
import consulo.util.dataholder.UserDataHolderBase;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import kava.beans.PropertyChangeListener;
import org.jspecify.annotations.Nullable;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * @author VISTALL
 * @since 2026-09-30
 */
public class FontEditor extends UserDataHolderBase implements FileEditor {
    private record LoadedFont(Font font, FontSample sample) {
    }

    private static final Logger LOG = Logger.getInstance(FontEditor.class);

    private static final int DEFAULT_FONT_SIZE = 24;
    private static final int MIN_FONT_SIZE = 8;
    private static final int MAX_FONT_SIZE = 96;
    private static final int FONT_SIZE_STEP = 2;

    private final Project myProject;
    private final VirtualFile myFile;
    private final String myLoremText;

    private @Nullable LoadingLayout<DockLayout> myComponent;
    private @Nullable TextArea myTextArea;
    private @Nullable Font myPreviewFont;
    private int myFontSize = DEFAULT_FONT_SIZE;

    public FontEditor(Project project, VirtualFile file, String loremText) {
        myProject = project;
        myFile = file;
        myLoremText = loremText;
    }

    @RequiredUIAccess
    @Override
    public Component getUIComponent() {
        LoadingLayout<DockLayout> component = myComponent;
        if (component != null) {
            return component;
        }

        component = LoadingLayout.create(DockLayout.create(Space.NONE), this);
        component.startLoading(FontViewerLocalize.fontLoadingText());
        myComponent = component;

        load(component);
        return component;
    }

    @RequiredUIAccess
    private void load(LoadingLayout<DockLayout> component) {
        UIAccess uiAccess = myProject.getUIAccess();

        DisposableCoroutineScope.launchAsync(myProject.coroutineContext(), this, () -> Coroutine
            .<Object, @Nullable Font>first(CompletableFutureStep.await(ignored -> registerFont(uiAccess)))
            .then(CodeExecution.<@Nullable Font, @Nullable LoadedFont>apply(font -> font == null
                ? null
                : new LoadedFont(font, FontSample.of(font, myFile.getName(), myLoremText))))
            .then(UIAction.<@Nullable LoadedFont, Object>apply(loaded -> {
                component.stopLoading(content -> content.center(buildContent(loaded)));
                return null;
            })));
    }

    private CompletableFuture<@Nullable Font> registerFont(UIAccess uiAccess) {
        URL url = VirtualFileUtil.convertToURL(myFile.getUrl());
        if (url == null) {
            LOG.warn("Unable to open font " + myFile.getUrl() + ": no url");
            return CompletableFuture.completedFuture(null);
        }

        return FontManager.get().registerFontAsync(url, uiAccess, this).handle((font, error) -> {
            if (error != null) {
                LOG.warn("Unable to open font " + myFile.getUrl(), error);
                return null;
            }
            return font;
        });
    }

    @RequiredUIAccess
    private Component buildContent(@Nullable LoadedFont loaded) {
        if (loaded == null) {
            return Label.create(FontViewerLocalize.fontCannotOpen(myFile.getName()));
        }

        Font font = loaded.font();
        TextArea textArea = TextArea.create();
        String text = loaded.sample().text();
        if (text.isEmpty()) {
            textArea.setValue(FontViewerLocalize.fontNoDisplayableGlyphs().get());
            textArea.setEditable(false);
        }
        else {
            myPreviewFont = font;
            textArea.setValue(text);
            textArea.setFont(font.buildNewFont(myFontSize));
        }
        myTextArea = textArea;

        HorizontalLayout zoomButtons = HorizontalLayout.create(Space.X_SMALL)
            .add(createZoomButton(PlatformIconGroup.graphZoomout(), FontViewerLocalize.fontZoomOut(), -FONT_SIZE_STEP))
            .add(createZoomButton(PlatformIconGroup.graphActualzoom(), FontViewerLocalize.fontZoomActualSize(), 0))
            .add(createZoomButton(PlatformIconGroup.graphZoomin(), FontViewerLocalize.fontZoomIn(), FONT_SIZE_STEP));

        DockLayout header = DockLayout.create(Space.NONE);
        header.left(Label.create(LocalizeValue.of(loaded.sample().fontName())));
        header.right(zoomButtons);

        DockLayout content = DockLayout.create(Space.MEDIUM);
        content.top(header);
        content.center(textArea);
        content.paddingBuilder().allSet(Space.LARGE).apply();
        return content;
    }

    @RequiredUIAccess
    private Button createZoomButton(Image icon, LocalizeValue tooltip, int delta) {
        Button button = Button.create(LocalizeValue.empty());
        button.setIcon(icon);
        button.addStyle(ButtonStyle.BORDERLESS);
        button.setToolTipText(tooltip);
        button.addClickListener(event -> zoom(delta));
        return button;
    }

    @RequiredUIAccess
    private void zoom(int delta) {
        myFontSize = delta == 0 ? DEFAULT_FONT_SIZE : Math.clamp(myFontSize + delta, MIN_FONT_SIZE, MAX_FONT_SIZE);

        TextArea textArea = myTextArea;
        Font font = myPreviewFont;
        if (textArea != null && font != null) {
            textArea.setFont(font.buildNewFont(myFontSize));
        }
    }

    @Override
    public @Nullable Component getPreferredFocusedUIComponent() {
        return myTextArea;
    }

    @Override
    public String getName() {
        return FontViewerLocalize.fontEditorName().get();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return myFile.isValid();
    }

    @Override
    public VirtualFile getFile() {
        return myFile;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void dispose() {
    }
}
