package org.asciidoc.intellij.editor.notification;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import org.asciidoc.intellij.editor.AsciiDocSplitEditor;
import org.asciidoc.intellij.file.AsciiDocFileType;
import org.asciidoc.intellij.psi.AsciiDocUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Notify user that Antora support is available.
 * Triggers notification if the AsciiDoc document is part of an Antora module.
 * The condition needs to be fulfilled when opening the editor.
 * It will not be re-checked during typing or generating files.
 */
public class AntoraNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> implements DumbAware {
  private static final Key<EditorNotificationPanel> KEY = Key.create("Antora Support available");

  private static final String ANTORA_AVAILABLE = "asciidoc.antora.available";

  @NotNull
  @Override
  public Key<EditorNotificationPanel> getKey() {
    return KEY;
  }

  @Nullable
  @Override
  public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull final FileEditor fileEditor) {
    // only in AsciiDoc files
    if (file.getFileType() != AsciiDocFileType.INSTANCE) {
      return null;
    }

    // only if not previously disabled
    if (PropertiesComponent.getInstance().getBoolean(ANTORA_AVAILABLE)) {
      return null;
    }

    // find about the current project
    Project project;
    if (fileEditor instanceof AsciiDocSplitEditor) {
      project = ((AsciiDocSplitEditor) fileEditor).getEditor().getProject();
      if (project == null) {
        return null;
      }
    } else {
      return null;
    }

    // find out if we're in an Antora module
    VirtualFile antoraModuleDir = AsciiDocUtil.findAntoraModuleDir(project.getBaseDir(), file.getParent());
    if (antoraModuleDir == null) {
      return null;
    }

    final EditorNotificationPanel panel = new EditorNotificationPanel();
    panel.setText("It seems you are editing a document that is part of an Antora module. Do you want to learn more how this plugin can support you?");
    panel.createActionLabel("Yes, tell me more!", ()
      -> BrowserUtil.browse("https://github.com/asciidoctor/asciidoctor-intellij-plugin/wiki/Antora-support"));
    panel.createActionLabel("Do not show again", () -> {
      PropertiesComponent.getInstance().setValue(ANTORA_AVAILABLE, true);
      EditorNotifications.updateAll();
    });
    return panel;
  }
}
