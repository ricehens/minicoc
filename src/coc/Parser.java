package coc;

import java.util.*;

public class Parser {

    private Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        reverseStack = new HashMap<>();
        stackSize = 0;
    }

    private Map<String, Integer> reverseStack;
    private int stackSize;

    // TODO split by colon?
    public Term parse() {
        Term term = parseApplication();
        lexer.freeze();
        try {
            lexer.expect(Lexer.TokenKind.ARROW);
            push("_");
            Term right;
            try {
                right = parse();
            } finally {
                pop("_");
                lexer.unfreezeAndIgnore();
            }
            return new Term.Pi(term.index(), term, right);
        } catch (CocBloc e) {
            lexer.unfreezeAndRevert();
            return term;
        }
    }

    private Term parseApplication() {
        Term term = parseSimple();
        lexer.freeze();
        while (true) {
            try {
                Term right = parseSimple();
                term = new Term.App(term.index(), term, right);
                lexer.unfreezeAndIgnore();
                lexer.freeze();
            } catch (CocBloc e) {
                lexer.unfreezeAndRevert();
                return term;
            }
        }
    }

    private Term parseSimple() {
        if (!lexer.hasNext()) 
            throw new CocBloc(-1, "no tokens");

        var tk = lexer.next();
        switch (tk.kind()) {
            case ID -> {
                if (!reverseStack.containsKey(tk.content()))
                    throw new CocBloc(tk.index(),
                            "cannot resolve variable " + tk.content());
                return new Term.Var(tk.index(), bruijn(tk.content()));
            }
            case PI, LAM -> {
                lexer.expect(Lexer.TokenKind.LPAREN);
                return parsePiLam(tk.index(), tk.kind() == Lexer.TokenKind.PI);
            }
            case LPAREN -> {
                Term term = parse();
                lexer.expect(Lexer.TokenKind.RPAREN);
                return term;
            }
            case PROP -> {
                return new Term.Sort(tk.index(), Term.SortKind.PROP);
            }
            case TYPE -> {
                return new Term.Sort(tk.index(), Term.SortKind.TYPE);
            }
            default -> throw new CocBloc(tk.index(),
                    "unexpected token " + tk.content());
        }
    }

    // after first LPAREN
    private Term parsePiLam(int index, boolean isPi) {
        String id = lexer.expect(Lexer.TokenKind.ID).content();
        lexer.expect(Lexer.TokenKind.COLON);
        Term type = parse();
        lexer.expect(Lexer.TokenKind.RPAREN);

        Lexer.Token dot = lexer.next();
        push(id);
        Term body;
        try {
            body = switch (dot.kind()) {
                case DOT -> parse();
                case LPAREN -> parsePiLam(dot.index(), isPi);
                default -> throw new CocBloc(dot.index(),
                        "expected `.`, got `" + dot.content() + "`");
            };
        } finally {
            pop(id);
        }
        return isPi
            ? new Term.Pi(index, type, body)
            : new Term.Lam(index, type, body);
    }

    private void push(String id) {
        reverseStack.put(id, stackSize++);
    }

    private void pop(String id) {
        reverseStack.remove(id);
        stackSize--;
    }

    private int bruijn(String id) {
        return stackSize - 1 - reverseStack.get(id);
    }

}
