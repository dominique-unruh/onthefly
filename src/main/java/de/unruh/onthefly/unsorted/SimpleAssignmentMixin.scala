package de.unruh.onthefly.unsorted

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import de.unruh.onthefly.inlays.ObservableInlay
import de.unruh.onthefly.unsorted.psi.{SimpleAssignment, SimpleTypes}
import groovy.transform.Memoized
import SimpleAssignmentMixin.Memoize
import io.reactivex.rxjava3.core.Observable
import org.omg.CORBA.Environment

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

abstract class SimpleAssignmentMixin(node: ASTNode) extends ASTWrapperPsiElement(node) with SimpleAssignment {
  implicit private val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  type Env = Map[String, Expression]

  private def getPrevious : Option[SimpleAssignment] = {
    var sibling: PsiElement = this
    while (true) {
      sibling = sibling.getPrevSibling
      if (sibling == null)
        return None
      sibling match {
        case assignment: SimpleAssignment => return Some(assignment)
        case _ =>
      }
    }
    unreachableCode
  }

  def getLine : Int = {
    val doc = getContainingFile.getViewProvider.getDocument
    doc.getLineNumber(getTextOffset) + 1
  }

  private val getEverything: Long => CachedComputation.O[(Expression, Env)] = new Memoize((id: Long) => {
    import Hasher.dummyHasher // all hashing disabled

    val expr = getExpression.getExpression;

    val emptyEnv: CachedComputation.O[Env] = Observable.just(Map.empty: Env)
    val variable = this.getVariable

    val previousEnv: CachedComputation.O[Env] = getPrevious match {
      case Some(previous) =>
        emptyEnv.concatWith(
          previous.asInstanceOf[SimpleAssignmentMixin].getEnvironment(id))
      case None => emptyEnv
    }

    previousEnv.map { env =>
      val evaledExpr = expr.substitute(env)
      val myEnv = env.updated(variable, evaledExpr)
      (evaledExpr, myEnv)
    }.delay(1, TimeUnit.SECONDS)

  })
    /*    for (previousEnv: Env <- getPrevious match {
      case Some(previous) => previous.asInstanceOf[SimpleAssignmentMixin].getEnvironment(id)
      case None => Future.successful(Map.empty: Env)
    };
         _ = System.out.println("Substituting in " + this);
         _ <- SimpleAssignmentMixin.wait(1000);
         expr = getExpression.getExpression;
         evaledExpr = expr.substitute(previousEnv);
         env: Env = previousEnv.updated(getVariable, evaledExpr)
         )
      yield (evaledExpr, env)*/

  def getEnvironment(id: Long): CachedComputation.O[Env] =
    getEverything(id).map(_._2)

  def getEvaluatedExpression(id: Long) : CachedComputation.O[Expression] =
    getEverything(id).map(_._1)

  def unreachableCode : Nothing = throw new RuntimeException("Supposedly unreachable code reached")

  def getVariable: String = getNode.findChildByType(SimpleTypes.IDENTIFIER).getText

  override def getInlay(id: Long) = new FakeComputationInlay(getVariable, getEvaluatedExpression(id))
  /*override def getInlay(id: Long): ObservableInlay = {
    val offset = getTextOffset
    val prevLine = getPrevious.map(_.asInstanceOf[SimpleAssignmentMixin].getLine)
    val latex = getEvaluatedExpression(id).toLatex
    ObservableInlay("Offset "+offset+", line "+getLine+" following line "+prevLine+" "+latex)
  }*/
}

object SimpleAssignmentMixin {
  implicit private val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  class Memoize[U](f: Long => U) extends (Long => U) {
    private var id: Long = -1
    private var output: U = _

    override def apply(id: Long): U = synchronized {
      if (id == this.id) this.output
      else {
        val output = f(id)
        this.output = output
        this.id = id
        output
      }
    }
  }

  def wait(millis: Int) = Future {
    scala.concurrent.blocking {
      Thread.sleep(millis)
    }
  }
}
