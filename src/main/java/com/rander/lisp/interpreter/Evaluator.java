package com.rander.lisp.interpreter;

/**
 * Created by shubham on 2/11/2019.
 */
public class Evaluator {

    private static final String T = "T";
    private static final String NIL = "NIL";


    public ExpressionTree eval(ExpressionTree expressionTree) throws EvaluationException {
        if(expressionTree.isAtomNode()) {
            return evalAtom(expressionTree);
        } else {
            return evalList(expressionTree);
        }
    }

    private ExpressionTree evalList(ExpressionTree tree) throws EvaluationException {

        if(!isList(tree)) {
            throw new EvaluationException("The given expression tree is not a list :" + tree);
        }

        if(lengthOfExpressionTree(tree) < 2) {
            throw new EvaluationException("Length of the expression tree is not a greater than or equal to 2: " + tree);
        }

        if (!tree.getLeft().isAtomNode()) {
            throw new EvaluationException("Did not found a function " + tree.getLeft());
        }

        PrimitiveFunctions function = getFirstFunction(tree);

        switch (function) {
            case CAR:
            case CDR:
                return evalCarCdr(tree);
            case QUOTE:
                return tree.getRight().getLeft();
            case CONS:
                return evalCons(tree);
            case PLUS:
            case MINUS:
            case TIMES:
            case LESS:
            case GREATER:
            case EQ:
                return evalArithmetic(tree);
            case COND:
                return evalCond(tree);
            case ATOM:
            case INT:
            case NULL:
                return evalToken(tree);
            default:
                throw new EvaluationException("Invalid function defined: " + function.getFunctionName());
        }

    }

    private ExpressionTree evalCond(ExpressionTree tree) throws EvaluationException {

        for (ExpressionTree t = tree.getRight(); !t.isAtomNode(); t = t.getRight()) {

            if (!isList(t.getLeft())) {
                throw new EvaluationException(t.getLeft() + " is not a list");
            }

            validateLength(tree, 2);
        }
        
        for (ExpressionTree t = tree.getRight(); !t.isAtomNode(); t = t.getRight()) {
            ExpressionTree tree1 = eval(t.getLeft().getLeft());
            ExpressionTree tree2 = eval(t.getLeft().getRight().getLeft());

            if (!isNilNode(tree1)) {
                return tree2;
            }
        }

        throw new EvaluationException("Condition did not match");
    }

    private ExpressionTree evalToken(ExpressionTree tree) throws EvaluationException {

        validateLength(tree, 2);
        PrimitiveFunctions function = getFirstFunction(tree);

        ExpressionTree tree1 = eval(tree.getRight().getLeft());

        switch (function) {
            case ATOM: {
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.LITERAL_ATOMS, tree1.isAtomNode() ? T : NIL, null));
                answer.setAtomNode(true);
                return answer;
            }
            case INT: {
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.LITERAL_ATOMS, tree1.isAtomNode() ? (tree1.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS) ? T : NIL) : NIL, null));
                answer.setAtomNode(true);
                return answer;
            }
            case NULL: {
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.LITERAL_ATOMS, isNilNode(tree1) ? T : NIL, null));
                answer.setAtomNode(true);
                return answer;
            }
            default:
                throw new EvaluationException("Invalid function: " + function.getFunctionName());
        }

    }

    private ExpressionTree evalArithmetic(ExpressionTree tree) throws EvaluationException {

        validateLength(tree, 3);
        PrimitiveFunctions function = getFirstFunction(tree);

        ExpressionTree tree1 = eval(tree.getRight().getLeft());
        ExpressionTree tree2 = eval(tree.getRight().getRight().getLeft());

        if (function.equals(PrimitiveFunctions.EQ)
                && tree1.isAtomNode() && tree1.getToken().getTokenType().equals(Tokens.LITERAL_ATOMS)
                && tree2.isAtomNode() && tree2.getToken().getTokenType().equals(Tokens.LITERAL_ATOMS)) {

            ExpressionTree answer = new ExpressionTree();
            Token token = new Token(Tokens.LITERAL_ATOMS,
                    tree1.getToken().getValue().equals(tree2.getToken().getValue()) ? T : NIL, null);
            answer.setToken(token);
            answer.setAtomNode(true);
            return answer;
        }

        if (!tree1.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            throw new EvaluationException(function.getFunctionName() + ": " + tree1 + " requires numbers, but got literal atom");
        }
        if (!tree2.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            throw new EvaluationException(function.getFunctionName() + ": " + tree2 + " requires numbers, but got literal atom");
        }

        int i1 = tree1.getToken().getIntegerTokenValue();
        int i2 = tree2.getToken().getIntegerTokenValue();
        switch (function) {
            case PLUS: {
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.NUMERIC_ATOMS, null, i1 + i2));
                answer.setAtomNode(true);
                return answer;
            }
            case MINUS:{
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.NUMERIC_ATOMS, null, i1 - i2));
                answer.setAtomNode(true);
                return answer;
            } case TIMES:{
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.NUMERIC_ATOMS, null, i1 * i2));
                answer.setAtomNode(true);
                return answer;
            }
            case LESS:{
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.LITERAL_ATOMS, i1 < i2 ? T : NIL, null));
                answer.setAtomNode(true);
                return answer;
            }
            case GREATER:{
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.LITERAL_ATOMS, i1 > i2 ? T : NIL, null));
                answer.setAtomNode(true);
                return answer;
            }
            case EQ:{
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.LITERAL_ATOMS, i1 == i2 ? T : NIL, null));
                answer.setAtomNode(true);
                return answer;
            }
            default:
                throw new EvaluationException("Invalid function: " + function.getFunctionName());
        }
    }

    private ExpressionTree evalCons(ExpressionTree tree) throws EvaluationException {

        validateLength(tree, 3);

        ExpressionTree tree1 = eval(tree.getRight().getLeft());
        ExpressionTree tree2 = eval(tree.getRight().getRight().getLeft());

        ExpressionTree answer = new ExpressionTree();
        answer.setRight(tree2);
        answer.setLeft(tree1);

        return answer;
    }

    private ExpressionTree evalCarCdr(ExpressionTree tree) throws EvaluationException {

        validateLength(tree, 2);
        PrimitiveFunctions function = getFirstFunction(tree);

        ExpressionTree tree1 = eval(tree.getRight().getLeft());

        if(tree1.isAtomNode()) {
            throw new EvaluationException("Found a leaf node instead of list for CDR/CAR function in the list: " + tree1);
        }

        switch (function) {
            case CAR:
                return tree1.getLeft();
            case CDR:
                return tree1.getRight();
            default:
                throw new EvaluationException("Invalid function defined: " + function.getFunctionName());
        }

    }

    private ExpressionTree evalAtom(ExpressionTree expressionTree) throws EvaluationException {
        Token token = expressionTree.getToken();
        if(token.getTokenType().equals(Tokens.NIL)
                || token.getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            return expressionTree;
        }

        if(token.getTokenType().equals(Tokens.LITERAL_ATOMS)
                && (token.getStringTokenValue().equals(T)
                || token.getStringTokenValue().equals(NIL))) {
            return expressionTree;
        }

        throw new EvaluationException("Found an invalid atom: " + token.getStringTokenValue());
    }

    private int lengthOfExpressionTree(ExpressionTree expressionTree) {
        return expressionTree.isAtomNode() ? 0 : 1 + lengthOfExpressionTree(expressionTree.getRight());
    }

    private boolean isList(ExpressionTree expressionTree) {
        return (isNilNode(expressionTree) || isList(expressionTree.getRight()));
    }

    private boolean isNilNode(ExpressionTree s) {
        return s.isAtomNode()
                && (s.getToken().getTokenType().equals(Tokens.LITERAL_ATOMS)
                        || s.getToken().getTokenType().equals(Tokens.NIL))
                && s.getToken().getStringTokenValue().equals("NIL");
    }

    private void validateLength(ExpressionTree tree, int requireLength) throws EvaluationException {
        int length = lengthOfExpressionTree(tree);

        if(length != requireLength) {
            throw new EvaluationException(tree.getLeft() + ": requires " + requireLength + " elements, found " + length + " in " + tree);
        }
    }

    private PrimitiveFunctions getFirstFunction(ExpressionTree tree) throws EvaluationException {
        String functionName = tree.getLeft().getToken().getValue();
        PrimitiveFunctions function = PrimitiveFunctions.getPrimitiveFunction(functionName);

        if(function == null) {
            throw new EvaluationException("Invalid function defined: " + functionName);
        }

        return function;
    }

}
