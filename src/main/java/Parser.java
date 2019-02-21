/**
 * Created by Shubham on 1/23/2019.
 */
public class Parser {

    private final Evaluator evaluator;

    public Parser(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    private void parseStart() {
        while (true) {
            Token currentToken = LexicalAnalyzer.getCurrentToken();
            if (currentToken.getTokenType() == Tokens.EOF) {
                break;
            } else if (currentToken.getTokenType() == Tokens.ERROR) {
                System.out.println(Tokens.ERROR.getTokenName() + ":  Invalid token " + currentToken.getStringTokenValue());
                System.exit(1);
            }

            try {
                ExpressionTree tree = parse();
                //System.out.println(Utils.prettyPrintExpressionTree(tree, new StringBuilder()).toString());
                ExpressionTree answer = evaluator.eval(tree);
                System.out.println(answer.toString());
            } catch (ParseException | InvalidTokenException | EvaluationException e) {
                System.out.println("ERROR: " + e.getLocalizedMessage());
                System.exit(1);
            }
        }
    }

    private ExpressionTree parse() throws ParseException, InvalidTokenException {

        if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.OPEN_PARENTHESES)) {
            ExpressionTree root = new ExpressionTree();
            ExpressionTree temp = root;
            LexicalAnalyzer.moveToNext();
            while (!LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.CLOSING_PARENTHESES)) {
                ExpressionTree expressionTree = parse();
                temp.setLeft(expressionTree);
                temp.setRight(new ExpressionTree());
                temp = temp.getRight();
            }
            LexicalAnalyzer.moveToNext();
            temp.setValue("NIL");
            temp.setAtomNode(true);
            temp.setToken(new Token(Tokens.NIL, "NIL", null));
            return root;
        } else if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.LITERAL_ATOMS)) {
            ExpressionTree root = new ExpressionTree();
            root.setValue(LexicalAnalyzer.getCurrentToken().getStringTokenValue());
            root.setAtomNode(true);
            root.setToken(LexicalAnalyzer.getCurrentToken());
            LexicalAnalyzer.moveToNext();
            return root;
        } else if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            ExpressionTree root = new ExpressionTree();
            root.setValue(LexicalAnalyzer.getCurrentToken().getIntegerTokenValue().toString());
            root.setAtomNode(true);
            root.setToken(LexicalAnalyzer.getCurrentToken());
            LexicalAnalyzer.moveToNext();
            return root;
        } else if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.ERROR)) {
            throw new InvalidTokenException("Invalid token " + LexicalAnalyzer.getCurrentToken().getStringTokenValue());
        } else {
            if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.OPEN_PARENTHESES)) {
                throw new InvalidTokenException("Invalid Grammar: extra open Parenthesis");
            }

            if (LexicalAnalyzer.getCurrentToken().getTokenType().equals(Tokens.CLOSING_PARENTHESES)) {
                throw new InvalidTokenException("Invalid Grammar: extra closed Parenthesis");
            }

            throw new ParseException("Invalid Grammar: extra open Parenthesis");
        }
    }


    public static void main(String[] args) {
        Parser p = new Parser(new Evaluator());
        p.parseStart();
    }

}