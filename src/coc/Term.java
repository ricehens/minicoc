package coc;

public sealed interface Term {

    enum SortKind {
        PROP, TYPE
    }

    record Sort(int index, SortKind kind) implements Term {}

    // https://en.wikipedia.org/wiki/De_Bruijn_index
    record Var(int index, int bruijn) implements Term {
        @Override
        public String toString() {
            return "" + bruijn;
        }
    }

    record Pi(int index, Term type, Term body) implements Term {
        @Override
        public String toString() {
            return String.format("(Π %s %s)", type, body);
        }
    }

    record Lam(int index, Term type, Term body) implements Term {
        @Override
        public String toString() {
            return String.format("(λ %s %s)", type, body);
        }
    }

    record App(int index, Term left, Term right) implements Term {
        @Override
        public String toString() {
            return String.format("(%s %s)", left, right);
        }
    }

    int index();

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
                case Sort(int _, SortKind kind)
                    -> kind == SortKind.PROP ? "Prop" : "Type";
                case Var(int _, int bruijn) -> "v" + (cnt - 1 - bruijn);
                case Pi(int _, Term type, Term body) -> {
                    String sType = print(type);
                    cnt++;
                    String sBody = print(body);
                    cnt--;
                    yield String.format("Π(v%d: (%s)). (%s)", cnt, sType, sBody);
                }
                case Lam(int _, Term type, Term body) -> {
                    String sType = print(type);
                    cnt++;
                    String sBody = print(body);
                    cnt--;
                    yield String.format("λ(v%d: (%s)). (%s)", cnt, sType, sBody);
                }
                case App(int _, Term left, Term right) ->
                    String.format("(%s) (%s)", print(left), print(right));
            };
        }

    }

}
