package de.unruh.onthefly.inlays;

import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("UnstableApiUsage")
public class Collector implements InlayHintsCollector {
    static AtomicLong counter = new AtomicLong();
    static long id = counter.getAndIncrement();

    @Override
    public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (!(element instanceof PsiWithInlay)) return true;

        PsiWithInlay psiWithInlay = (PsiWithInlay) element;

        InlayPresentation inlayPresentation = new Presentation(editor, psiWithInlay.getInlay(id));
        inlayHintsSink.addBlockElement(element.getTextRange().getEndOffset(), true, false, 0, inlayPresentation);
        return true;
    }

}
