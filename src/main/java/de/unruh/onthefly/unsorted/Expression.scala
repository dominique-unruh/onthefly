package de.unruh.onthefly.unsorted

object Expression {

  case class Var(variable: String) extends Expression {
    override def toString: String = variable
    override def toLatex: String =
      if (variable.length == 1) variable
      else "\\mathit{" + variable + "}"

    override def substitute(environment: Map[String, Expression]): Expression =
      environment.get(variable) match {
        case Some(expr) => expr
        case None => this
      }
  }

  case class Num(num: BigInt) extends Expression {
    override def toString: String = num.toString
    override def toLatex: String = num.toString

    override def substitute(environment: Map[String, Expression]): Num = this
  }
  object Num {
    def apply(num: String): Num = Num(BigInt(num))
  }

  case class App(op: String, args: Expression*) extends Expression {
    override def toString: String = {
      val sb = new StringBuilder(op)
      sb.append('(')
      var first = true
      for (a <- args) {
        if (!first) sb.append(", ")
        first = false
        sb.append(a)
      }
      sb.append(')')
      sb.toString
    }

    override def toLatex: String = op match {
      case "+" =>
        assert(args.length == 2)
        "(" + args(0).toLatex + "+" + args(1).toLatex + ")"
      case "*" =>
        assert(args.length == 2)
        "(" + args(0).toLatex + "\\cdot " + args(1).toLatex + ")"
      case "/" =>
        assert(args.length == 2)
        "\\frac{" + args(0).toLatex + "}{" + args(1).toLatex + "}"
      case "sqrt" =>
        assert(args.length == 1)
        "\\sqrt{" + args(0).toLatex + "}"
      case "-" =>
        assert(args.length == 1 || args.length == 2)
        if (args.length == 1) "-" + args(0).toLatex
        else "(" + args(0).toLatex + "-" + args(1).toLatex + ")"
      case _ =>
        System.out.println("Unknown op: " + op)
        "\\mathbf{invalid}"
    }

    override def substitute(environment: Map[String, Expression]): App =
      App(op, args map (_.substitute(environment)) : _*)
  }
}

trait Expression {
  def toLatex: String
  def substitute(environment: Map[String, Expression]) : Expression
}
