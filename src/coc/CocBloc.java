package coc;

public class CocBloc extends RuntimeException {

    public final int index;
    public final String message;

    public CocBloc(int index, String message) {
        this.index = index;
        this.message = message;
    }

}
