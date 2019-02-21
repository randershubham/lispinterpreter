import java.io.*;

import java.util.function.Supplier;


/**
 * Created by shubham on 1/11/2019.
 */
public class LexicalAnalyzer {

    private static Token currentToken = null;

    private static final String INPUT_PATH = "C:\\Users\\shubham\\StudioProjects\\lispinterpreter\\src\\main\\resources\\input.txt";

    private static final Supplier<BufferedReader> FILE_READER_SUPPLIER = () -> new BufferedReader(new InputStreamReader(System.in));

    /*private static final Supplier<BufferedReader> FILE_READER_SUPPLIER = () -> {
        try {
            return new BufferedReader(new FileReader(new File(INPUT_PATH)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    };*/

    private static final BufferedReader BUFFERED_READER = FILE_READER_SUPPLIER.get();

    static {
        try {
            currentToken = getNextToken(BUFFERED_READER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Token getCurrentToken() {
        return currentToken;
    }

    public static void moveToNext() {
        try {
            currentToken = getNextToken(BUFFERED_READER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Token getNextToken(Reader reader) throws IOException {
        while(true) {
            int c = reader.read();
            reader.mark(0);

            if(c == -1) {
                return new Token(Tokens.EOF, null, null);
            }

            char tokenChar = (char)c;
            switch (tokenChar) {
                case '(':
                    return new Token(Tokens.OPEN_PARENTHESES, "(", null);
                case ')':
                    return new Token(Tokens.CLOSING_PARENTHESES, ")", null);
                case ' ':
                    break;
                case '\n':
                    break;
                case '\r':
                    break;
                case '\t':
                    break;
                default: {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(tokenChar);
                    if(Character.isDigit(tokenChar)) {
                        return getDigitToken(reader, stringBuilder);
                    } else {
                        return getCharacterToken(reader, stringBuilder);
                    }
                }
            }
        }
    }

    private static Token getDigitToken(Reader reader, StringBuilder stringBuilder) throws IOException {

        boolean isError = false;

        while(true) {
            char currentChar = (char) reader.read();

            if(currentChar != '(' && currentChar != ')') {
                reader.mark(0);
            } else {
                reader.reset();
                break;
            }

            if(isEmpty(currentChar)) {
                break;
            }

            if(!Character.isDigit(currentChar)) {
                isError = true;
            }
            stringBuilder.append(currentChar);
        }

        if(isError) {
            return new Token(Tokens.ERROR, stringBuilder.toString(), null);
        } else {
            return new Token(Tokens.NUMERIC_ATOMS, null, Integer.parseInt(stringBuilder.toString()));
        }
    }

    private static Token getCharacterToken(Reader reader, StringBuilder stringBuilder) throws IOException {

        while(true) {
            char currentChar = (char) reader.read();

            if(currentChar != '(' && currentChar != ')') {
                reader.mark(0);
            } else {
                reader.reset();
                break;
            }

            if(isEmpty(currentChar)) {
                break;
            }

            stringBuilder.append(currentChar);
        }

        return new Token(Tokens.LITERAL_ATOMS, stringBuilder.toString(), null);
    }

    private static boolean isEmpty(char currentChar) {
        return !((int) currentChar != Character.MAX_VALUE &&
                (int) currentChar != -1 &&
                currentChar != ' ' &&
                currentChar != '\n' &&
                currentChar != '\r' &&
                currentChar != '\t');
    }
}

