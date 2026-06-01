package coc;

public class BetaReducer {

    public static Term normalize(Term term) {
        return switch (term) {
            case Term.Sort _, Term.Var _ -> term;

            case Term.Lam(int index, Term type, Term body)
                -> new Term.Lam(index, normalize(type), normalize(body));

            case Term.Pi(int index, Term type, Term body)
                -> new Term.Pi(index, normalize(type), normalize(body));

            case Term.App(int index, Term left, Term right) -> {
                Term nl = normalize(left);
                Term nr = normalize(right);

                if (nl instanceof Term.Lam lam)
                    yield normalize(subst(lam.body(), 0, nr));
                else yield new Term.App(index, nl, nr);
            }
        };
    }

    public static Term shift(Term term, int amount, int cutoff) {
        return switch (term) {
            case Term.Sort _ -> term;

            case Term.Var(int index, int bruijn) -> {
                if (bruijn >= cutoff)
                    yield new Term.Var(index, bruijn + amount);
                else yield term;
            }

            case Term.App(int index, Term left, Term right)
                -> new Term.App(
                        index,
                        shift(left, amount, cutoff),
                        shift(right, amount, cutoff));

            case Term.Lam(int index, Term type, Term body)
                -> new Term.Lam(
                        index,
                        shift(type, amount, cutoff),
                        shift(body, amount, cutoff + 1));

            case Term.Pi(int index, Term type, Term body)
                -> new Term.Pi(
                        index,
                        shift(type, amount, cutoff),
                        shift(body, amount, cutoff + 1));
        };
    }

    public static Term subst(Term term, int depth, Term replacement) {
        return switch (term) {
            case Term.Sort _ -> term;

            case Term.Var(int index, int bruijn) -> {
                if (bruijn == depth)
                    yield shift(replacement, depth, 0);
                else if (bruijn > depth)
                    yield new Term.Var(index, bruijn - 1);
                else yield term;
            }

            case Term.App(int index, Term left, Term right)
                -> new Term.App(
                        index,
                        subst(left, depth, replacement),
                        subst(right, depth, replacement));

            case Term.Lam(int index, Term type, Term body)
                -> new Term.Lam(
                        index,
                        subst(type, depth, replacement),
                        subst(body, depth + 1, replacement));

            case Term.Pi(int index, Term type, Term body)
                -> new Term.Pi(
                        index,
                        subst(type, depth, replacement),
                        subst(body, depth + 1, replacement));
        };
    }

}
