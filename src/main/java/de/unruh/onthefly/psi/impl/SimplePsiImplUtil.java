package de.unruh.onthefly.psi.impl;

import de.unruh.onthefly.Expression;
import de.unruh.onthefly.FakeComputationInlay;
import de.unruh.onthefly.PsiExpression;
import de.unruh.onthefly.inlays.Inlay;
import de.unruh.onthefly.psi.*;

import java.util.List;

public class SimplePsiImplUtil {
  public static String getVariable(SimpleAssignment element) {
    return element.getNode().findChildByType(SimpleTypes.IDENTIFIER).getText();
  }

    public static Expression getExpression(SimplePrefixExpression element) {
    return new Expression.App(
            element.getPrefixOp().getText(),
            element.getAtom().getExpression()
    );
  }

  public static Expression getExpression(SimpleInfixExpression element) {
    List<SimpleAtom> atoms = element.getAtomList();
    assert atoms.size() == 2;
    return new Expression.App(
            element.getInfixOp().getText(),
            atoms.get(0).getExpression(), atoms.get(1).getExpression());
  }

  public static Expression getExpression(SimpleExpression element) {
    PsiExpression e = element.getInfixExpression();
    if (e != null) return e.getExpression();
    e = element.getPrefixExpression();
    assert e != null;
    return e.getExpression();
  }

  public static Expression getExpression(SimpleAtom element) {
    PsiExpression e = element.getVariable();
    if (e != null) return e.getExpression();
    e = element.getNumeral();
    assert e != null;
    return e.getExpression();
  }

  public static Expression getExpression(SimpleNumeral element) {
    return new Expression.Num(element.getText());
  }

  public static Expression getExpression(SimpleVariable element) {
    return new Expression.Var(element.getText());
  }

  public static Inlay getInlay(SimpleAssignment element) {
    return new FakeComputationInlay(element.getVariable(), element.getExpression().getExpression());
  }
}