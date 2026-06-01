package coc;

import java.util.*;

public class Lexer {

    enum TokenKind {
        ID,
        COLON, LPAREN, RPAREN, DOT,
        ARROW,
        LAM, PI,
        PROP, TYPE,
    };

    record Token(TokenKind kind, int index, String content) {}

    private char[] c;
    private int index;

    public Lexer(String str) {
        c = str.toCharArray();
        index = 0;
        frozen = new Stack<>();
    }

    public Token next() {
        if (!hasNext()) throw new CocBloc(c.length, "no more tokens");

        return switch (c[index]) {
            case ':' -> new Token(TokenKind.COLON, index, "" + c[index++]);
            case '(' -> new Token(TokenKind.LPAREN, index, "" + c[index++]);
            case ')' -> new Token(TokenKind.RPAREN, index, "" + c[index++]);
            case '.' -> new Token(TokenKind.DOT, index, "" + c[index++]);
            case 'λ' -> new Token(TokenKind.LAM, index, "" + c[index++]);
            case 'Π' -> new Token(TokenKind.PI, index, "" + c[index++]);
            case '→', '⇒' -> new Token(TokenKind.ARROW, index, "" + c[index++]);
            case '-', '=' -> {
                if (index + 1 >= c.length || c[index + 1] != '>')
                    throw new CocBloc(index, "unexpected character `" + c[index] + "`");
                yield new Token(TokenKind.ARROW, index, "" + c[index++] + c[index++]);
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
                    case "Prop" -> new Token(TokenKind.PROP, start, s);
                    case "Type" -> new Token(TokenKind.TYPE, start, s);
                    case "Lam" -> new Token(TokenKind.LAM, start, s);
                    case "Pi" -> new Token(TokenKind.PI, start, s);
                    default -> new Token(TokenKind.ID, start, s);
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

}
