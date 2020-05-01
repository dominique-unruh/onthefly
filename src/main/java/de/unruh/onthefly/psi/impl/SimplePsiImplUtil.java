package de.unruh.onthefly.psi.impl;

import com.intellij.lang.ASTNode;
import de.unruh.onthefly.psi.SimpleProperty;
import de.unruh.onthefly.psi.SimpleTypes;

public class SimplePsiImplUtil {
  public static String getKey(SimpleProperty element) {
    ASTNode keyNode = element.getNode().findChildByType(SimpleTypes.KEY);
    if (keyNode != null) {
      // IMPORTANT: Convert embedded escaped spaces to simple spaces
      return keyNode.getText().replaceAll("\\\\ ", " ");
    } else {
      return null;
    }
  }
  
  public static String getValue(SimpleProperty element) {
    ASTNode valueNode = element.getNode().findChildByType(SimpleTypes.VALUE);
    if (valueNode != null) {
      return valueNode.getText();
    } else {
      return null;
    }
  }
}