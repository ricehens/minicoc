package coc;

public class Main {

    static String[] tests = {
        // : PROP -> PROP -> PROP
        """
            λ(A: Prop) (B: Prop). Π(P: Prop).
            (A -> B -> P) -> P
            """,

        "Lam (A: Prop). A A A",

        "(λ(x: Prop). x) Type",

        "(λ(x: Prop). (λ(y: Prop). y) x) Type",

        "(λ(x: Prop) (y: Prop). x) Type",

        // Nat : Prop
        "Π(A: Prop). (A -> A) -> (A -> A)",

        // 1 + 1 : Nat, i.e. Π(A: Prop). (A -> A) -> (A -> A)
        """
            (
             λ(m: Π(A: Prop). (A -> A) -> (A -> A))
             (n: Π(A: Prop). (A -> A) -> (A -> A))
             (A: Prop)
             (f: A -> A)
             (x: A).
             m A f (n A f x)
            )
            (λ(A: Prop) (f: A -> A) (x: A). f x)
            (λ(A: Prop) (f: A -> A) (x: A). f x)
            """,

        // 0 : Nat, i.e. (Π PROP (Π (Π 0 1) (Π 1 2)))
        "λ(A: Prop) (f: A -> A) (x: A). x",

        // K : Π(A: Prop). Π(B: Prop). A -> B -> A
        "λ(A: Prop) (B: Prop) (x: A) (y: B). x",

        // modus ponens : Π(A: Prop). Π(B: Prop). (A -> B) -> A -> B
        "λ(A: Prop) (B: Prop) (f: A -> B) (a: A). f a",

        // bot : TYPE
        "Π(P: Prop). P",

        // id : Π(P: Prop). P -> P
        "λ(P: Prop). (λ(A: Prop) (x: A). x) P",

        // succ : Nat -> Nat
        """
            λ(n: Π(A: Prop). (A -> A) -> (A -> A)).
            λ(A: Prop) (f: A -> A) (x: A).
            f (n A f x)
            """,
    };

    public static void main(String[] args) {
        String s =  tests[12];

        try {
            Environment env = new Environment();
            env.bind("Nat", tests[5], "Prop");
            Lexer lex = new Lexer(env, s);
            /*
               while (lex.hasNext()) {
               System.out.println(lex.next());
               }
               */

            Parser p = new Parser(lex);
            Term t = p.parse();
            System.out.println(t.print());
            System.out.println(BetaReducer.normalize(t));
            System.out.println(new TypeChecker().infer(t));

            System.out.println(BetaReducer.normalize(t).equals(BetaReducer.normalize(new Parser(new Lexer(env, t.print())).parse())));

            /*
            if (lex.hasNext()) {
                Token tk = lex.next();
                throw new CocBloc(tk.index(),
                        "unexpected token " + tk.content());
            }
            */

            String n2n = "Nat -> Nat";
            Term tn2n = new Parser(new Lexer(env, n2n)).parse();
            System.out.println(BetaReducer.normalize(new TypeChecker().infer(t))
                    .equals(BetaReducer.normalize(tn2n)));

        } catch (CocBloc e) {
            System.err.printf("Error at index %d: %s%n",
                    e.index, e.message);
            throw e;
            // System.exit(1);
        }

    }
}

