package de.unruh.onthefly;

import com.intellij.psi.PsiElement;

public interface PsiExpression extends PsiElement {
    Expression getExpression();
}
