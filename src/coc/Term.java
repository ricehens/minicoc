package coc;

public sealed interface Term {

    enum SortKind {
        PROP, TYPE
    }

    record Sort(int index, SortKind kind) implements Term {

        @Override
        public String toString() {
            return kind.toString();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Sort(int _, SortKind kind2)
                && kind == kind2;
        }

    }

    // https://en.wikipedia.org/wiki/De_Bruijn_index
    record Var(int index, int bruijn) implements Term {

        @Override
        public String toString() {
            return "" + bruijn;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Var(int _, int bruijn2)
                && bruijn == bruijn2;
        }

    }

    record Pi(int index, Term type, Term body) implements Term {

        @Override
        public String toString() {
            return String.format("(Π %s %s)", type, body);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Pi(int _, Term type2, Term body2)
                && type.equals(type2) && body.equals(body2);
        }

    }

    record Lam(int index, Term type, Term body) implements Term {

        @Override
        public String toString() {
            return String.format("(λ %s %s)", type, body);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Lam(int _, Term type2, Term body2)
                && type.equals(type2) && body.equals(body2);
        }

    }

    record App(int index, Term left, Term right) implements Term {

        @Override
        public String toString() {
            return String.format("(%s %s)", left, right);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof App(int _, Term left2, Term right2)
                && left.equals(left2) && right.equals(right2);
        }

    }

    int index();

    default String print() {
        return new PrettyPrinter().print(this);
    }

    class PrettyPrinter {

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
