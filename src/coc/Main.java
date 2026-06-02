package coc;

import java.io.*;
import java.util.*;

public class Main {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_WHITE_BOLD = "\u001B[1;37m";
    private static final String ANSI_RED_BOLD = "\u001B[1;31m";

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
        if (args.length != 1 || args[0].equals("-h") || args[0].equals("--help")) {
            // TODO print usage
            tmp();
            return;
        }

        int i = -1;
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            List<String> lines = br.readAllLines();
            Runtime runtime = new Runtime();

            outer:
            for (i = 0; i < lines.size();) {
                while (lines.get(i).length() == 0)
                    if (++i >= lines.size())
                        break outer;

                int line = i + 1;

                if (Character.isWhitespace(lines.get(i).charAt(0)))
                    throw new CocBloc(line, 0, "unexpected whitespace");

                StringBuilder sb = new StringBuilder();
                do {
                    sb.append(String.format("%s%n", lines.get(i++)));
                } while (i < lines.size()
                        && (lines.get(i).length() == 0
                            || Character.isWhitespace(lines.get(i).charAt(0))));

                runtime.process(line, sb.toString());
            }

            runtime.flush(args[0]);
        } catch (IOException e) {
            System.err.printf("%s%s: %serror%s could not open file",
                    ANSI_WHITE_BOLD, args[0], ANSI_RED_BOLD, ANSI_RESET);
            System.exit(1);
        } catch (CocBloc e) {
            System.err.printf("%s%s:%d:%d: %serror:%s %s%n",
                ANSI_WHITE_BOLD, args[0], e.line, e.index, ANSI_RED_BOLD, ANSI_RESET,
                e.message);
            System.exit(1);
        }
    }

    private static void tmp() {
        String s =  tests[12];

        try {
            Lexer lex = new Lexer(s);

            Parser p = new Parser(lex, null);
            Term t = p.parse();
            System.out.println(t.print());
            System.out.println(BetaReducer.normalize(t));
            System.out.println(new TypeChecker().infer(t));

            System.out.println(BetaReducer.normalize(t).equals(BetaReducer.normalize(new Parser(new Lexer(t.print()), null).parse())));
        } catch (CocBloc e) {
            System.err.printf("Error at index %d: %s%n",
                    e.index, e.message);
            throw e;
            // System.exit(1);
        }

    }
}

