package coc;

import java.util.*;

public class Runtime {

    private Environment env;
    private Map<String, String> boundTypes;

    public Runtime() {
        env = new Environment();
        boundTypes = new HashMap<>();
    }

    public void process(String s) {
        Lexer temp = new Lexer(null, s);
        if (!temp.hasNext()) return;

        Lexer.Token id = temp.expect(Lexer.TokenKind.ID);
        String name = id.content();

        Lexer.Token token = temp.next();
        switch (token.kind()) {
            case COLON -> {
                String rhs = s.substring(temp.getIndex());
                if (boundTypes.containsKey(name))
                    throw new CocBloc(id.index(),
                            "identifier " + name + " already bound to type");
                boundTypes.put(name, rhs);
            }
            case EQUAL -> {
                String rhs = s.substring(temp.getIndex());
                Term term = new Parser(new Lexer(env, rhs)).parse();
                Term type = new TypeChecker().infer(term);

                String bound = boundTypes.computeIfAbsent(name, _ -> type.print());
                Term boundType = new Parser(new Lexer(env, bound)).parse();

                if (!BetaReducer.normalize(type).equals(
                            BetaReducer.normalize(boundType)))
                    throw new CocBloc(0, String.format(
                                "type checking unsuccessful; failed to match type:%n%s%n",
                                boundType));

                env.bind(name, rhs, bound);
            }
            default -> throw new CocBloc(token.index(),
                    "unexpected token, expected `:` or `=`");
        }
    }

}
