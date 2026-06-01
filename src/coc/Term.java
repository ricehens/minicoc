package coc;

public sealed interface Term {

    enum Sort implements Term {
        PROP, TYPE;
    }

    // https://en.wikipedia.org/wiki/De_Bruijn_index
    record Var(int index) implements Term {
        @Override
        public String toString() {
            return "" + index;
        }
    }

    record Pi(Term type, Term body) implements Term {
        @Override
        public String toString() {
            return String.format("(Π %s %s)", type, body);
        }
    }

    record Lam(Term type, Term body) implements Term {
        @Override
        public String toString() {
            return String.format("(λ %s %s)", type, body);
        }
    }

    record App(Term left, Term right) implements Term {
        @Override
        public String toString() {
            return String.format("(%s %s)", left, right);
        }
    }

    default String print() {
        return new PrettyPrinter().print(this);
    }

    public class PrettyPrinter {

        private int cnt;

        public PrettyPrinter() {
            cnt = 1;
        }

        public String print(Term term) {
            return switch (term) {
                case Sort s -> s == Sort.PROP ? "Prop" : "Type";
                case Var(int index) -> "v" + (cnt - 1 - index);
                case Pi(Term type, Term body) -> {
                    String sType = print(type);
                    cnt++;
                    String sBody = print(body);
                    cnt--;
                    yield String.format("Π(v%d: (%s)). (%s)", cnt, sType, sBody);
                }
                case Lam(Term type, Term body) -> {
                    String sType = print(type);
                    cnt++;
                    String sBody = print(body);
                    cnt--;
                    yield String.format("λ(v%d: (%s)). (%s)", cnt, sType, sBody);
                }
                case App(Term left, Term right) ->
                    String.format("(%s) (%s)", print(left), print(right));
            };
        }

    }

}
