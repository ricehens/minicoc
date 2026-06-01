package coc;

import coc.Lexer.Token;

public class Main {

    static String[] tests = {
        """
            λ(A: Prop) (B: Prop). Π(P: Prop).
            (A -> B -> P) -> P
        """,

        "Lam (A: Prop). A A A",

        "(λ(x: Prop). x) Type",

        "(λ(x: Prop). (λ(y: Prop). y) x) Type",

        "(λ(x: Prop) (y: Prop). x) Type",
    };

    public static void main(String[] args) {
        String s =  tests[3];

        try {
            Lexer lex = new Lexer(s);
            /*
               while (lex.hasNext()) {
               System.out.println(lex.next());
               }
               */

            Parser p = new Parser(lex);
            Term t = p.parse();
            System.out.println(t);
            System.out.println(new BetaReducer().normalize(t));

            /*
            if (lex.hasNext()) {
                Token tk = lex.next();
                throw new CocBloc(tk.index(),
                        "unexpected token " + tk.content());
            }
            */

        } catch (CocBloc e) {
            System.err.printf("Error at index %d: %s%n",
                    e.index, e.message);
            throw e;
            // System.exit(1);
        }

    }
}

