package coc;

import java.util.*;

import coc.Lexer.Token;

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
            expect(Lexer.TokenKind.ARROW);
            push("_");
            Term right;
            try {
                right = parse();
            } finally {
                pop("_");
                lexer.unfreezeAndIgnore();
            }
            return new Term.Pi(term, right);
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
                term = new Term.App(term, right);
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
                return new Term.Var(stackSize - 1 - reverseStack.get(tk.content()));
            }
            case PI, LAM -> {
                expect(Lexer.TokenKind.LPAREN);
                return parsePiLam(tk.kind() == Lexer.TokenKind.PI);
            }
            case LPAREN -> {
                Term term = parse();
                expect(Lexer.TokenKind.RPAREN);
                return term;
            }
            case PROP -> {
                return Term.Sort.PROP;
            }
            case TYPE -> {
                return Term.Sort.TYPE;
            }
            default -> throw new CocBloc(tk.index(),
                    "unexpected token " + tk.content());
        }
    }

    // after first LPAREN
    private Term parsePiLam(boolean isPi) {
        String id = expect(Lexer.TokenKind.ID).content();
        expect(Lexer.TokenKind.COLON);
        Term type = parse();
        expect(Lexer.TokenKind.RPAREN);

        Token dot = lexer.next();
        push(id);
        Term body;
        try {
            body = switch (dot.kind()) {
                case DOT -> parse();
                case LPAREN -> parsePiLam(isPi);
                default -> throw new CocBloc(dot.index(),
                        "expected `.`, got `" + dot.content() + "`");
            };
        } finally {
            pop(id);
        }
        return isPi
            ? new Term.Pi(type, body)
            : new Term.Lam(type, body);
    }

    private void push(String id) {
        reverseStack.put(id, stackSize++);
    }

    private void pop(String id) {
        reverseStack.remove(id);
        stackSize--;
    }

    private Token expect(Lexer.TokenKind kind) {
        Token tk = lexer.next();
        if (tk.kind() != kind)
            throw new CocBloc(tk.index(),
                    "unexpected token " + tk.content());
        return tk;
    }

}
