package de.unruh.onthefly.inlays;

import com.intellij.psi.PsiElement;

import java.awt.*;

public interface PsiWithInlay extends PsiElement {
    Inlay getInlay();
}
