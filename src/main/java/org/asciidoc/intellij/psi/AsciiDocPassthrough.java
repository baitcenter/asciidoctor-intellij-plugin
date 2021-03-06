package org.asciidoc.intellij.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import icons.AsciiDocIcons;
import org.asciidoc.intellij.inspections.AsciiDocVisitor;
import org.asciidoc.intellij.lexer.AsciiDocTokenTypes;
import org.asciidoc.intellij.settings.AsciiDocApplicationSettings;
import org.asciidoc.intellij.settings.AsciiDocPreviewSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AsciiDocPassthrough extends AbstractAsciiDocCodeBlock {

  AsciiDocPassthrough(IElementType type) {
    super(type);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AsciiDocVisitor) {
      ((AsciiDocVisitor) visitor).visitBlocks(this);
      return;
    }

    super.accept(visitor);
  }

  @Override
  public Type getType() {
    return Type.PASSTHROUGH;
  }

  @Override
  public String getDefaultTitle() {
    return "Passthrough";
  }

  @Override
  public Icon getIcon(int ignored) {
    return AsciiDocIcons.Structure.BLOCK;
  }

  @Override
  public TextRange getContentTextRange() {
    return getContentTextRange(AsciiDocTokenTypes.PASSTRHOUGH_BLOCK_DELIMITER);
  }

  @Override
  public String getFenceLanguage() {
    AsciiDocPreviewSettings settings = AsciiDocApplicationSettings.getInstance().getAsciiDocPreviewSettings();
    if (!settings.isEnabledInjections() || StringUtil.isEmpty(settings.getLanguageForPassthrough())) {
      return null;
    } else {
      return "source-" + settings.getLanguageForPassthrough();
    }
  }

  public static class Manipulator extends AbstractManipulator<AsciiDocPassthrough> {

    @Override
    protected AsciiDocPassthrough createElement(AsciiDocPassthrough element, String content) {
      return AsciiDocPsiElementFactory.createPassthrough(element.getProject(), content);
    }
  }

}
