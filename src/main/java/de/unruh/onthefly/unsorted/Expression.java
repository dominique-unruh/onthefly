package de.unruh.onthefly.unsorted;

public interface Expression {
    public static class Var implements Expression {
        private final String var;
        public Var(String var) {
            this.var = var;
        }

        @Override
        public String toString() {
            return var;
        }

        @Override
        public String toLatex() {
            if (var.length()==1)
                return var;
            else
                return "\\mathit{" + var + "}";
        }
    }

    public static class Num implements Expression {
        private final int num;

        public Num(String num) {
            this.num = Integer.parseInt(num);
        }

        @Override
        public String toString() {
            return String.valueOf(num);
        }

        @Override
        public String toLatex() {
            return String.valueOf(num);
        }
    }

    public static class App implements Expression {
        private final Expression[] args;
        private final String op;

        public App(String op, Expression ... args) {
            this.op = op;
            this.args = args;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(op);
            sb.append('(');
            boolean first = true;
            for (Expression a : args) {
                if (!first) sb.append(", ");
                first = false;
                sb.append(a);
            }
            sb.append(')');
            return sb.toString();
        }

        @Override
        public String toLatex() {
            switch (op) {
                case "+":
                    assert args.length == 2;
                    return "(" + args[0].toLatex() + "+" + args[1].toLatex() + ")";
                case "*":
                    assert args.length == 2;
                    return "(" + args[0].toLatex() + "\\cdot " + args[1].toLatex() + ")";
                case "/":
                    assert args.length == 2;
                    return "\\frac{" + args[0].toLatex() + "}{" + args[1].toLatex() + "}";
                case "sqrt":
                    assert args.length == 1;
                    return "\\sqrt{" + args[0].toLatex() + "}";
                case "-":
                    assert args.length == 1 || args.length == 2;
                    if (args.length == 1)
                        return "-" + args[0].toLatex();
                    else
                        return "(" + args[0].toLatex() + "-" + args[1].toLatex() + ")";
                default:
                    System.out.println("Unknown op: " + op);
                    return "\\mathbf{invalid}";
            }
        }
    }

    String toLatex();
}
