package de.unruh.onthefly.inlays;

import com.intellij.psi.PsiElement;

import java.awt.*;

public interface PsiWithInlay extends PsiElement {
    /** Return an inlay for this element
     * @param id A unique id. If two calls with the same id (even in different {@link PsiWithInlay} objects)
     *           are made, it is guaranteed that the psi did not change in between. (This is useful for caching results.)
     */
    de.unruh.onthefly.inlays.Inlay getInlay(long id);
}
