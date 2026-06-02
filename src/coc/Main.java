package coc;

import java.io.*;
import java.util.*;

public class Main {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_WHITE_BOLD = "\u001B[1;37m";
    private static final String ANSI_RED_BOLD = "\u001B[1;31m";

    public static void main(String[] args) {
        if (args.length != 1 || args[0].equals("-h") || args[0].equals("--help")) {
            System.err.printf("%serror:%s no arguments given, expected file path%n",
                ANSI_RED_BOLD, ANSI_RESET);
            System.exit(1);
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
            System.err.printf("%s%s: %serror%s unable to open file",
                    ANSI_WHITE_BOLD, args[0], ANSI_RED_BOLD, ANSI_RESET);
            System.exit(1);
        } catch (CocBloc e) {
            System.err.printf("%s%s:%d:%d: %serror:%s %s%n",
                ANSI_WHITE_BOLD, args[0], e.line, e.index, ANSI_RED_BOLD, ANSI_RESET,
                e.message);
            System.exit(1);
        }
    }

}

