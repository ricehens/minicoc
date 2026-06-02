package coc;

import java.util.*;

public class TypeChecker {

    private List<Term> ctx;

    public TypeChecker() {
        ctx = new ArrayList<>();
    }

    public Term infer(Term term) {
        return switch (term) {
            case Term.Sort(int index, Term.SortKind kind) -> {
                if (kind == Term.SortKind.PROP)
                    yield new Term.Sort(-1, Term.SortKind.TYPE);
                else throw new CocBloc(index,
                        "cannot infer type of TYPE");
            }

            case Term.Var(int _, int bruijn)
                -> BetaReducer.shift(bruijn(bruijn), bruijn + 1, 0);

            case Term.Pi(int index, Term type, Term body) -> {
                throw new UnsupportedOperationException();
            }

            case Term.Lam(int index, Term type, Term body) -> {
                throw new UnsupportedOperationException();
            }

            case Term.App(int index, Term left, Term right) -> {
                Term tLeft = BetaReducer.normalize(infer(left));
                if (!(tLeft instanceof Term.Pi(int _, Term domain, Term codomain)))
                    throw new CocBloc(index,
                            "expected type Pi for left side of function application");

                Term tRight = BetaReducer.normalize(infer(right));
                if (!domain.equals(tRight))
                    throw new CocBloc(index,
                            "type mismatch for function application");

                yield BetaReducer.subst(codomain, 0, right);
            }
        };
    }

    private void push(Term type) {
        ctx.add(type);
    }

    private void pop() {
        ctx.remove(ctx.size() - 1);
    }

    private Term bruijn(int index) {
        return ctx.get(ctx.size() - 1 - index);
    }

}
