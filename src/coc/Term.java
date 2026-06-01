package coc;

public sealed interface Term {

    enum Sort implements Term {
        PROP, TYPE
    }

    // https://en.wikipedia.org/wiki/De_Bruijn_index
    record Var(int index) implements Term {}

    record Pi(Term type, Term body) implements Term {}

    record Lam(Term type, Term body) implements Term {}

    record App(Term left, Term right) implements Term {}

}
