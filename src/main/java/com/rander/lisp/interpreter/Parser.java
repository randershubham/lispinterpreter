package com.rander.lisp.interpreter;

/**
 * Created by Shubham on 1/23/2019.
 */
public class Parser {

    private void parseStart() {
        while (true) {
            Token currentToken = LexicalAnalyzer.getCurrentToken();
            if (currentToken.getTokenType() == Tokens.EOF) {
                break;
            } else if (currentToken.getTokenType() == Tokens.ERROR) {
                System.out.println(Tokens.ERROR.getTokenName() + ":  Invalid token " + currentToken.getStringTokenValue());
                System.exit(1);
            }

            ExpressionTree root = new ExpressionTree();
            try {
                parse(root);
            } catch (ParseException | InvalidTokenException e) {
                System.out.println("ERROR: " + e.getLocalizedMessage());
                System.exit(1);
            }
            prettyPrint(root);
            System.out.println();
        }
    }

    private ExpressionTree parse(ExpressionTree root) throws ParseException, InvalidTokenException {
        ExpressionTree temp = root;
        if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.OPEN_PARENTHESES)) {
            LexicalAnalyzer.moveToNext();
            while (!LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.CLOSING_PARENTHESES)) {
                ExpressionTree expressionTree = parse(new ExpressionTree());
                temp.setLeft(expressionTree);
                temp.setRight(new ExpressionTree());
                temp = temp.getRight();
            }
            LexicalAnalyzer.moveToNext();
            temp.setValue("NIL");
            temp.setAtomNode(true);
            return root;
        } else if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.LITERAL_ATOMS)) {
            root.setValue(LexicalAnalyzer.getCurrentToken().getStringTokenValue());
            root.setAtomNode(true);
            LexicalAnalyzer.moveToNext();
            return root;
        } else if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            root.setValue(LexicalAnalyzer.getCurrentToken().getIntegerTokenValue().toString());
            root.setAtomNode(true);
            LexicalAnalyzer.moveToNext();
            return root;
        } else if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.ERROR)) {
            throw new InvalidTokenException("Invalid token " + LexicalAnalyzer.getCurrentToken().getStringTokenValue());
        } else {
            throw new ParseException("Invalid Grammar");
        }
    }

    private void prettyPrint(ExpressionTree node) {
        if (node.getLeft() == null && node.getRight() == null) {
            System.out.print(node.getValue());
            return ;
        }
        System.out.print("(");
        prettyPrint(node.getLeft());
        System.out.print(" . ");
        prettyPrint(node.getRight());
        System.out.print(")");
    }


    public static void main(String[] args) {
        Parser p = new Parser();
        p.parseStart();
    }

}