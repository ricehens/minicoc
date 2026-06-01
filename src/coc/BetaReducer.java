package coc;

public class BetaReducer {

    public Term normalize(Term term) {
        return switch (term) {
            case Term.Sort s -> s;

            case Term.Var v -> v;

            case Term.Lam(Term type, Term body) -> new Term.Lam(normalize(type), normalize(body));

            case Term.Pi(Term type, Term body) -> new Term.Pi(normalize(type), normalize(body));

            case Term.App(Term left, Term right) -> {
                Term nl = normalize(left);
                Term nr = normalize(right);

                if (nl instanceof Term.Lam lam)
                    yield normalize(subst(lam.body(), 0, nr));
                else yield new Term.App(nl, nr);
            }
        };
    }

    private Term shift(Term term, int amount, int cutoff) {
        return switch (term) {
            case Term.Sort s -> s;

            case Term.Var(int index) -> {
                if (index >= cutoff)
                    yield new Term.Var(index + amount);
                else yield term;
            }

            case Term.App(Term left, Term right) -> new Term.App(
                    shift(left, amount, cutoff),
                    shift(right, amount, cutoff));

            case Term.Lam(Term type, Term body) -> new Term.Lam(
                    shift(type, amount, cutoff),
                    shift(body, amount, cutoff + 1));

            case Term.Pi(Term type, Term body) -> new Term.Pi(
                    shift(type, amount, cutoff),
                    shift(body, amount, cutoff + 1));
        };
    }

    Term subst(Term term, int depth, Term replacement) {
        return switch (term) {
            case Term.Sort s -> s;

            case Term.Var(int index) -> {
                if (index == depth)
                    yield shift(replacement, depth, 0);
                else if (index > depth)
                    yield new Term.Var(index - 1);
                else yield term;
            }

            case Term.App(Term left, Term right) -> new Term.App(
                    subst(left, depth, replacement),
                    subst(right, depth, replacement));

            case Term.Lam(Term type, Term body) -> new Term.Lam(
                    subst(type, depth, replacement),
                    subst(body, depth + 1, replacement));

            case Term.Pi(Term type, Term body) -> new Term.Pi(
                    subst(type, depth, replacement),
                    subst(body, depth + 1, replacement));
        };
    }

}
