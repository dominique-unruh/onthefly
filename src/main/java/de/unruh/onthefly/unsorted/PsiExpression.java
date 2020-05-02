package de.unruh.onthefly.unsorted;

import com.intellij.psi.PsiElement;

public interface PsiExpression extends PsiElement {
    Expression getExpression();
}
