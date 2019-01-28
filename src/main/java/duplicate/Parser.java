package duplicate;

import com.rander.lisp.interpreter.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

/**
 * Created by shubham on 1/23/2019.
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
            Deque<String> tokenStrings = new ArrayDeque<>();
            try {
                parseExpression(tokenStrings);
            } catch (ParseException | InvalidTokenException e) {
                System.out.println("ERROR: " + e.getLocalizedMessage());
                // TODO: FIX ME
                System.exit(0);
            }

            ExpressionTree root = new ExpressionTree();
            getExpressionTree(tokenStrings, root);
            System.out.println(prettyPrint(root, new StringBuilder()).toString());
            LexicalAnalyzer.moveToNext();
        }
    }

    private ExpressionTree getExpressionTree(Deque<String> tokenStrings, ExpressionTree root) {
        ExpressionTree temp = root;
        if (tokenStrings.peek().equals("(")) {
            tokenStrings.pop();
            while (!tokenStrings.peek().equals(")")) {
                ExpressionTree expressionTree = getExpressionTree(tokenStrings, new ExpressionTree());
                temp.setLeft(expressionTree);
                temp.setRight(new ExpressionTree());
                temp = temp.getRight();
            }
            tokenStrings.pop();
            temp.setValue("NIL");
            temp.setAtomNode(true);
            return root;
        } else if (!tokenStrings.peek().equals(")")) {
            root.setValue(tokenStrings.pop());
            root.setAtomNode(true);
            return root;
        }
        return null;
    }

    private StringBuilder prettyPrint(ExpressionTree node, StringBuilder builder) {
        if (node.getLeft() == null && node.getRight() == null) {
            builder.append(node.getValue());
            return builder;
        }
        builder.append("(");
        prettyPrint(node.getLeft(), builder);
        builder.append(" . ");
        prettyPrint(node.getRight(), builder);
        builder.append(")");
        return builder;
    }


    private void parseExpression(Deque<String> stringDeque) throws ParseException, InvalidTokenException {
        Stack<String> evaluationTreeStack = new Stack<>();
        Token currentToken = LexicalAnalyzer.getCurrentToken();
        Tokens currentTokenType = currentToken.getTokenType();
        while (true) {
            if (currentTokenType.equals(Tokens.NUMERIC_ATOMS)) {
                stringDeque.addLast(currentToken.getIntegerTokenValue().toString());
                if (evaluationTreeStack.isEmpty()) {
                    break;
                }
            } else if (currentTokenType.equals(Tokens.LITERAL_ATOMS)) {
                stringDeque.addLast(currentToken.getStringTokenValue());
                if (evaluationTreeStack.isEmpty()) {
                    break;
                }
            } else if (currentTokenType.equals(Tokens.OPEN_PARENTHESES)) {
                stringDeque.addLast(currentToken.getStringTokenValue());
                evaluationTreeStack.push("(");
            } else if (currentTokenType.equals(Tokens.CLOSING_PARENTHESES)) {
                if (evaluationTreeStack.isEmpty()) {
                    throw new ParseException("Not following the right Grammar");
                }
                stringDeque.addLast(currentToken.getStringTokenValue());
                evaluationTreeStack.pop();
                if (evaluationTreeStack.isEmpty()) {
                    break;
                }
            } else if (currentTokenType.equals(Tokens.ERROR)) {
                throw new InvalidTokenException("Invalid token " + currentToken.getStringTokenValue());
            } else if (currentTokenType.equals(Tokens.EOF)) {
                if (!evaluationTreeStack.isEmpty()) {
                    throw new ParseException("Not following the right Grammar");
                }
            }
            LexicalAnalyzer.moveToNext();
            currentToken = LexicalAnalyzer.getCurrentToken();
            currentTokenType = currentToken.getTokenType();
        }

        if (!evaluationTreeStack.isEmpty()) {
            throw new ParseException("Not following the right Grammar");
        }
    }

    public static void main(String[] args) {
        Parser p = new Parser();
        p.parseStart();
    }

}