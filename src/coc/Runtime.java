package coc;

import java.util.*;

public class Runtime {

    private Map<String, Term> env;
    private Queue<Check> promises;

    private record Check(int line, Lexer lexer) {}

    public Runtime() {
        env = new HashMap<>();
        promises = new LinkedList<>();
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_WHITE_BOLD = "\u001B[1;37m";
    private static final String ANSI_GREEN_BOLD = "\u001B[1;32m";

    public void process(int line, String s) {
        Lexer lexer = new Lexer(s);
        if (!lexer.hasNext()) return;

        lexer.freeze();
        Lexer.Token id;
        try {
            id = lexer.expect(Lexer.TokenKind.ID);
            lexer.expect(Lexer.TokenKind.EQUAL);
        } catch (CocBloc e) {
            lexer.unfreezeAndRevert();
            promises.offer(new Check(line, lexer));
            return;
        }

        Term term = new Parser(lexer, env).parse();
        if (lexer.hasNext()) {
            Lexer.Token tk = lexer.next();
            throw new CocBloc(line, tk.index(),
                    "unexpected token `" + tk.content() + "`; expected end of line");
        }
        if (env.containsKey(id.content()))
            throw new CocBloc(line, 0,
                    "name `" + id.content() + "` already bound");
        env.put(id.content(), term);
    }

    public void flush(String file) {
        while (!promises.isEmpty()) {
            Check promise = promises.poll();
            try {
                Lexer lexer = promise.lexer();
                Term value = new Parser(lexer, env).parse();
                lexer.expect(Lexer.TokenKind.COLON);
                Term type = new Parser(lexer, env).parse();

                Term actualType = BetaReducer.normalize(
                        new TypeChecker().infer(value));
                Term expectedType = BetaReducer.normalize(type);

                if (!actualType.equals(expectedType))
                    throw new CocBloc(0, "type checking failed");
                System.err.printf("%s%s:%d %ssuccess:%s type checking succeeded!%n",
                        ANSI_WHITE_BOLD, file, promise.line(), ANSI_GREEN_BOLD, ANSI_RESET);

            } catch (CocBloc e) {
                throw new CocBloc(promise.line(), e.index, e.message);
            }
        }
    }

}
