package coc;

import java.util.*;

import static coc.BetaReducer.*;

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
                -> shift(bruijn(bruijn), bruijn + 1, 0);

            case Term.Pi(int _, Term type, Term body) -> {
                Term tType = normalize(infer(type));
                if (!(tType instanceof Term.Sort))
                    throw new CocBloc(type.index(),
                            "expected type Sort for domain of Pi");

                push(type);
                Term tBody = normalize(infer(body));
                if (!(tBody instanceof Term.Sort))
                    throw new CocBloc(body.index(),
                            "expected type Sort for body of Pi");
                pop();
                yield tBody;
            }

            case Term.Lam(int _, Term type, Term body) -> {
                Term tType = normalize(infer(type));
                if (!(tType instanceof Term.Sort))
                    throw new CocBloc(type.index(),
                            "expected type Sort for domain of Lambda");

                push(type);
                Term tBody = normalize(infer(body));
                pop();
                yield new Term.Pi(-1, type, tBody);
            }

            case Term.App(int index, Term left, Term right) -> {
                Term tLeft = normalize(infer(left));
                if (!(tLeft instanceof Term.Pi(int _, Term domain, Term codomain)))
                    throw new CocBloc(left.index(),
                            "expected type Pi for left side of function application");

                Term tRight = normalize(infer(right));
                if (!tRight.equals(normalize(domain)))
                    throw new CocBloc(index,
                            "type mismatch for function application");

                yield subst(codomain, 0, right);
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
