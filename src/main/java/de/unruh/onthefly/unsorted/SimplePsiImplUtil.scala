package de.unruh.onthefly.unsorted

import de.unruh.onthefly.unsorted.Expression
import de.unruh.onthefly.unsorted.PsiExpression
import de.unruh.onthefly.inlays.Inlay
import de.unruh.onthefly.unsorted.psi._
import de.unruh.onthefly.unsorted.FakeComputationInlay
import java.util
import java.util.List

import com.intellij.psi.PsiElement

import scala.runtime.Nothing$


object SimplePsiImplUtil {

  def getExpression(element: SimplePrefixExpression) = new Expression.App(element.getPrefixOp.getText, element.getAtom.getExpression)

  def getExpression(element: SimpleInfixExpression): Expression = {
    val atoms = element.getAtomList
    assert(atoms.size == 2)
    new Expression.App(element.getInfixOp.getText, atoms.get(0).getExpression, atoms.get(1).getExpression)
  }

  def getExpression(element: SimpleExpression): Expression = {
    var e = element.getInfixExpression : PsiExpression
    if (e != null) return e.getExpression
    e = element.getPrefixExpression
    assert(e != null)
    e.getExpression
  }

  def getExpression(element: SimpleAtom): Expression = {
    var e = element.getVariable : PsiExpression
    if (e != null) return e.getExpression
    e = element.getNumeral
    assert(e != null)
    e.getExpression
  }

  def getExpression(element: SimpleNumeral): Expression.Num = Expression.Num(element.getText)

  def getExpression(element: SimpleVariable): Expression.Var = Expression.Var(element.getText)

}