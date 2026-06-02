package coc;

import java.util.*;

public class Lexer {

    enum TokenKind {
        ID,
        COLON, LPAREN, RPAREN, DOT,
        ARROW,
        LAM, PI,
        PROP, TYPE,
        EQUAL,
    };

    record Token(TokenKind kind, int index, String content) {}

    private final char[] c;
    private int index;
    private final int offset;

    public Lexer(Environment env, String str) {
        if (env == null) env = new Environment();
        c = env.wrap(str).toCharArray();
System.out.println("==========\n" + env.wrap(str));
        index = 0;
        // offset = env.offset();
offset = 0;
        frozen = new Stack<>();
    }

    public Token next() {
        if (!hasNext()) throw new CocBloc(c.length, "no more tokens");

        return switch (c[index]) {
            case ':' -> new Token(TokenKind.COLON, index - offset, "" + c[index++]);
            case '(' -> new Token(TokenKind.LPAREN, index - offset, "" + c[index++]);
            case ')' -> new Token(TokenKind.RPAREN, index - offset, "" + c[index++]);
            case '.' -> new Token(TokenKind.DOT, index - offset, "" + c[index++]);
            case 'λ' -> new Token(TokenKind.LAM, index - offset, "" + c[index++]);
            case 'Π' -> new Token(TokenKind.PI, index - offset, "" + c[index++]);
            case '→', '⇒' -> new Token(TokenKind.ARROW, index - offset, "" + c[index++]);
            case '-' -> {
                if (index + 1 >= c.length || c[index + 1] != '>')
                    throw new CocBloc(index, "unexpected character `-`");
                yield new Token(TokenKind.ARROW, index - offset, "" + c[index++] + c[index++]);
            }
            case '=' -> {
                if (index + 1 >= c.length || c[index + 1] != '>')
                    yield new Token(TokenKind.EQUAL, index - offset, "" + c[index++]);
                yield new Token(TokenKind.ARROW, index - offset, "" + c[index++] + c[index++]);
            }
            default -> {
                if (!Character.isJavaIdentifierStart(c[index])) {
                    throw new CocBloc(index, "unexpected character `" + c[index] + "`");
                }

                int start = index + 1;
                StringBuilder sb = new StringBuilder();
                while (index < c.length
                        && Character.isJavaIdentifierPart(c[index]))
                    sb.append(c[index++]);
                String s = sb.toString();

                yield switch (s) {
                    case "Prop" -> new Token(TokenKind.PROP, start - offset, s);
                    case "Type" -> new Token(TokenKind.TYPE, start - offset, s);
                    case "Lam" -> new Token(TokenKind.LAM, start - offset, s);
                    case "Pi" -> new Token(TokenKind.PI, start - offset, s);
                    default -> new Token(TokenKind.ID, start - offset, s);
                };
            }
        };
    }

    public boolean hasNext() {
        while (true) {
            while (index < c.length
                    && Character.isWhitespace(c[index]))
                index++;
            if (index + 1 < c.length
                    && c[index] == '-'
                    && c[index + 1] == '-')
                while (index < c.length && c[index++] != '\n') {}
            else break;
        }

        return index < c.length;
    }

    public Token expect(TokenKind kind) {
        Token tk = next();
        if (tk.kind() != kind)
            throw new CocBloc(tk.index(),
                    "unexpected token `" + tk.content()
                    + "`, expected " + kind);
        return tk;
    }

    private Stack<Integer> frozen;

    public void freeze() {
        frozen.push(index);
    }

    public void unfreezeAndRevert() {
        index = frozen.pop();
    }

    public void unfreezeAndIgnore() {
        frozen.pop();
    }

    public int getIndex() {
        return index;
    }

}
