package coc;

import coc.Lexer.Token;

public class Main {
    public static void main(String[] args) {
        String and = """
            λ(A: Prop) (B: Prop). Π(P: Prop).
                (A -> B -> P) -> P
                """;
        // String and = "Lam (A: Prop). A A A";

        try {
            Lexer lex = new Lexer(and);
            /*
               while (lex.hasNext()) {
               System.out.println(lex.next());
               }
               */

            Parser p = new Parser(lex);
            System.out.println(p.parse());
            if (lex.hasNext()) {
                Token tk = lex.next();
                throw new CocBloc(tk.index(),
                        "unexpected token " + tk.content());
            }

        } catch (CocBloc e) {
            System.err.printf("Error at index %d: %s%n",
                    e.index, e.message);
            throw e;
            // System.exit(1);
        }

    }
}

