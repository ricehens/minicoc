package coc;

public class CocBloc extends RuntimeException {

    public final int line;
    public final int index;
    public final String message;

    public CocBloc(int index, String message) {
        this(0, index, message);
    }

    public CocBloc(int line, int index, String message) {
        this.line = line;
        this.index = index;
        this.message = message;
    }

}
