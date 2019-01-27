package com.rander.lisp.interpreter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by shubham on 1/11/2019.
 */
public class Scanner {

    private static Token currentToken = null;

    private static final String INPUT_PATH = "C:\\Users\\shubham\\StudioProjects\\lispinterpreter\\src\\main\\java\\com\\rander\\lisp\\interpreter\\input.txt";

    private static final Supplier<BufferedReader> FILE_READER_SUPPLIER = () -> new BufferedReader(new InputStreamReader(System.in));

   /* private static final Supplier<BufferedReader> FILE_READER_SUPPLIER = () -> {
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

    /*public static void main(String[] args) throws IOException {

        int openParenthesisCount = 0;
        int closedParenthesisCount = 0;

        List<String> literalAtoms = new ArrayList<>();
        Long numericAtomSum = 0L;
        Long numericAtomCount = 0L;

        while(true) {
            Token currentToken = getNextToken(BUFFERED_READER);
            if(currentToken.getTokenType() == Tokens.EOF) {
                break;
            } else if(currentToken.getTokenType() == Tokens.ERROR) {
                System.out.println(Tokens.ERROR.getTokenName() + ":  Invalid token " + currentToken.getStringTokenValue());
                System.exit(1);
            }
            switch (currentToken.getTokenType()) {
                case CLOSING_PARENTHESES:
                    closedParenthesisCount++;
                    break;
                case OPEN_PARENTHESES:
                    openParenthesisCount++;
                    break;
                case LITERAL_ATOMS:
                    literalAtoms.add(currentToken.getStringTokenValue());
                    break;
                case NUMERIC_ATOMS:
                    numericAtomSum += currentToken.getIntegerTokenValue();
                    numericAtomCount++;
                    break;
            }
        }

        if(literalAtoms.size() > 0) {
            System.out.println(Tokens.LITERAL_ATOMS.getTokenName() + ": " + literalAtoms.size() + ", " + literalAtoms.stream().collect(Collectors.joining( ", " ) ));
        } else {
            System.out.println(Tokens.LITERAL_ATOMS.getTokenName() + ": " + literalAtoms.size());
        }

        if(numericAtomCount > 0) {
            System.out.println(Tokens.NUMERIC_ATOMS.getTokenName() + ": " + numericAtomCount + ", " + numericAtomSum);
        } else {
           System.out.println(Tokens.NUMERIC_ATOMS.getTokenName() + ": " + numericAtomCount);
        }

        System.out.println(Tokens.OPEN_PARENTHESES.getTokenName() + ": " + openParenthesisCount);
        System.out.println(Tokens.CLOSING_PARENTHESES.getTokenName() + ": " + closedParenthesisCount);
    }*/

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

