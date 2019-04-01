/**
 * Created by shubham on 2/11/2019.
 */
public class Evaluator2 {

    private static final String T = "T";
    private static final String NIL = "NIL";
    public static ExpressionTree D_LIST = new ExpressionTree(null, null, "NIL", true, new Token(Tokens.NIL, "NIL", null));

    public ExpressionTree eval(ExpressionTree exp, ExpressionTree aList, ExpressionTree dList) throws EvaluationException {
        if (isAtomNode(exp)) {
            if ("T".equals(exp.getValue())
                    || "NIL".equals(exp.getValue())
                    || exp.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
                return exp;
            } else if (bound(exp, aList)) {
                return getVal(exp, aList);
            } else {
                throw new EvaluationException("Invalid Atom: " + exp);
            }
        } else {
            if (car(exp).getValue().equals("QUOTE")) {
                return car(cdr(exp));
            } else if (car(exp).getValue().equals("COND")) {
                return evcon(cdr(exp), aList, dList);
            } else if (car(exp).getValue().equals("DEFUN")) {
                D_LIST = addToDList(exp, dList);
                //TODO: to check side effects
                return car(cdr(exp));
            } else {
                //todo clone alist
                ExpressionTree functionName = car(exp);
                ExpressionTree cdrExpression = cdr(exp);
                ExpressionTree parameter = evList(cdrExpression, aList, dList);
                return apply(functionName,
                        parameter,
                        aList,
                        dList);
            }
        }
    }

    private ExpressionTree apply(ExpressionTree f, ExpressionTree x, ExpressionTree aList, ExpressionTree dList) throws EvaluationException {
        if (isAtomNode(f)) {
            String functionName = f.getValue();
            //TODO: validations
            if("CAR".equals(functionName)) {
                return car(car(x));
            } else if ("CDR".equals(functionName)) {
                return cdr(car(x));
            } else if ("CONS".equals(functionName)) {
                return cons(car(x), car(cdr(x)));
            } else if ("ATOM".equals(functionName)) {
                //TODO make sure x has only one element else throw error
                return atom(car(x));
            } else if ("INT".equals(functionName)) {
                //TODO: check properly
                return integer(car(x));
            } else if ("NULL".equals(functionName)) {
                //TODO check again
                return nullExpression(car(x));
            } else if ("EQ".equals(functionName)) {
                //TODO check again
                return eq(car(x), car(cdr(x)));
            } else if ("PLUS".equals(functionName)) {
                //TODO check again
                return plus(car(x), car(cdr(x)));
            } else if ("MINUS".equals(functionName)) {
                //TODO check again
                return minus(car(x), car(cdr(x)));
            } else if ("GREATER".equals(functionName)) {
                //TODO check again
                return greater(car(x), car(cdr(x)));
            } else if ("LESS".equals(functionName)) {
                //TODO check again
                return less(car(x), car(cdr(x)));
            } else if ("TIMES".equals(functionName)) {
                //TODO check again
                return times(car(x), car(cdr(x)));
            }  else {

                ExpressionTree exp = cdr(getVal(f, dList));
                ExpressionTree exp2 = getVal(f,dList);
                ExpressionTree newAList = addPairs(car(exp2), x, aList);

                return eval(
                        exp,
                        newAList,
                        dList);
            }
        } else {
            throw new EvaluationException("Received a list instead of token for using apply, the expression is " + f);
        }

    }

    private boolean isAtomNode(ExpressionTree tree) {
        return tree.isAtomNode();
    }

    private ExpressionTree plus(ExpressionTree left, ExpressionTree right) throws EvaluationException {
        if(isAtomNode(left) && isAtomNode(right)
                && left.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)
                && right.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            ExpressionTree answer = new ExpressionTree();
            Integer sum = left.getToken().getIntegerTokenValue() + right.getToken().getIntegerTokenValue();
            answer.setToken(new Token(Tokens.NUMERIC_ATOMS, null, sum));
            answer.setAtomNode(true);
            return answer;
        } else {
            throw new EvaluationException("The atoms are not numeric or Found list instead of atom while evaluating PLUS, the expressions are: " + left + " " + right);
        }
    }

    private ExpressionTree minus(ExpressionTree left, ExpressionTree right) throws EvaluationException {
        if(isAtomNode(left) && isAtomNode(right)
                && left.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)
                && right.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            ExpressionTree answer = new ExpressionTree();
            Integer difference = left.getToken().getIntegerTokenValue()  - right.getToken().getIntegerTokenValue();
            answer.setToken(new Token(Tokens.NUMERIC_ATOMS, null, difference));
            answer.setAtomNode(true);
            return answer;
        } else {
            throw new EvaluationException("The atoms are not numeric or Found list instead of atom while evaluating MINUS, the expressions are: " + left + " " + right);
        }
    }

    private ExpressionTree times(ExpressionTree left, ExpressionTree right) throws EvaluationException {
        if(isAtomNode(left) && isAtomNode(right)
                && left.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)
                && right.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            ExpressionTree answer = new ExpressionTree();
            Integer product = left.getToken().getIntegerTokenValue() * right.getToken().getIntegerTokenValue();
            answer.setToken(new Token(Tokens.NUMERIC_ATOMS, null, product));
            answer.setAtomNode(true);
            return answer;
        } else {
            throw new EvaluationException("The atoms are not numeric or Found list instead of atom while evaluating LESS, the expressions are: " + left + " " + right);
        }
    }

    private ExpressionTree greater(ExpressionTree left, ExpressionTree right) throws EvaluationException {
        if(isAtomNode(left) && isAtomNode(right)
                && left.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)
                && right.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            ExpressionTree answer = new ExpressionTree();
            String isGreater = left.getToken().getIntegerTokenValue() > right.getToken().getIntegerTokenValue() ? T : NIL;
            answer.setToken(new Token(Tokens.LITERAL_ATOMS, isGreater, null));
            answer.setAtomNode(true);
            return answer;
        } else {
            throw new EvaluationException("The atoms are not numeric or Found list instead of atom while evaluating GREATER, the expressions are: " + left + " " + right);
        }
    }

    private ExpressionTree less(ExpressionTree left, ExpressionTree right) throws EvaluationException {
        if(isAtomNode(left) && isAtomNode(right)
                && left.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)
                && right.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            ExpressionTree answer = new ExpressionTree();
            String isGreater = left.getToken().getIntegerTokenValue() < right.getToken().getIntegerTokenValue() ? T : NIL;
            answer.setToken(new Token(Tokens.LITERAL_ATOMS, isGreater, null));
            answer.setAtomNode(true);
            return answer;
        } else {
            throw new EvaluationException("The atoms are not numeric or Found list instead of atom while evaluating LESS, the expressions are: " + left + " " + right);
        }
    }

    private ExpressionTree addPairs(ExpressionTree xList, ExpressionTree yList, ExpressionTree originalList) throws EvaluationException {
        if(isNilNode(xList)) {
            return originalList;
        } else {
            ExpressionTree left = cons(car(xList), car(yList));
            ExpressionTree right = addPairs(cdr(xList), cdr(yList), originalList);
            return cons(left, right);
        }
    }

    private ExpressionTree atom(ExpressionTree tree) throws EvaluationException {
        if(!isAtomNode(tree)) {
            throw new EvaluationException("Requires an atom but found something else: " + tree);
        }
        ExpressionTree answer = new ExpressionTree();
        answer.setToken(new Token(Tokens.LITERAL_ATOMS, tree.isAtomNode() ? T : NIL, null));
        answer.setAtomNode(true);
        return answer;
    }

    private ExpressionTree integer(ExpressionTree tree) throws EvaluationException {
        if(!isAtomNode(tree)) {
            throw new EvaluationException("Requires an atom but found something else: " + tree);
        }
        ExpressionTree answer = new ExpressionTree();
        answer.setToken(new Token(Tokens.LITERAL_ATOMS, tree.isAtomNode() ? (tree.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS) ? T : NIL) : NIL, null));
        answer.setAtomNode(true);
        return answer;
    }

    private ExpressionTree eq(ExpressionTree left, ExpressionTree right) throws EvaluationException {

        if(isAtomNode(left) && isAtomNode(right)) {
            ExpressionTree answer = new ExpressionTree();
            answer.setToken(new Token(Tokens.LITERAL_ATOMS, left.getValue().equals(right.getValue()) ? T : NIL, null));
            answer.setAtomNode(true);
            return answer;
        } else {
            throw new EvaluationException("Found list instead of atom while evaluating EQ, the expressions are: " + left + " " + right);
        }
    }

    private ExpressionTree nullExpression(ExpressionTree tree) throws EvaluationException {
        ExpressionTree answer = new ExpressionTree();
        Token token = new Token(Tokens.LITERAL_ATOMS, isNilNode(tree) ? T : NIL, null);
        answer.setToken(token);
        answer.setAtomNode(true);
        return answer;
    }

    private ExpressionTree evList(ExpressionTree exp, ExpressionTree aList, ExpressionTree dList) throws EvaluationException {
        if(isNilNode(exp)) {
            return new ExpressionTree(null, null, "NIL", true, new Token(Tokens.NIL, "NIL", null));
            // return null;
        } else {
            ExpressionTree carExpression = car(exp);
            ExpressionTree cdrExpression = cdr(exp);
            ExpressionTree left = eval(carExpression,aList,dList);
            ExpressionTree right = evList(cdrExpression,aList,dList);
            return cons(left, right);
        }
    }


    private ExpressionTree cons(ExpressionTree eval, ExpressionTree expressionTree) throws EvaluationException {
        ExpressionTree answer = new ExpressionTree();
        answer.setLeft(eval);
        answer.setRight(expressionTree);
        return answer;
    }

    private ExpressionTree addToDList(ExpressionTree exp, ExpressionTree dList) throws EvaluationException {
        ExpressionTree functionName = car(cdr(exp));
        ExpressionTree left = car(cdr(cdr(exp)));
        ExpressionTree right = car(cdr(cdr(cdr(exp))));
        ExpressionTree fullExpression = cons(functionName, cons(left, right));
        return cons(fullExpression, dList);
    }

    private ExpressionTree evcon(ExpressionTree exp, ExpressionTree aList, ExpressionTree dList) throws EvaluationException {
        if (isNilNode(exp)) {
            throw new EvaluationException("Received literal atom during evaluating evcon, the expression is : " + exp);
        } else if (eval(car(car(exp)), aList, dList).getValue().equals(T)) {
            return eval(car(cdr(car(exp))), aList, dList);
        } else {
            return evcon(cdr(exp), aList, dList);
        }
    }

    public boolean bound(ExpressionTree exp, ExpressionTree aList) throws EvaluationException {

        if(isAtomNode(exp)) {
            if(isNilNode(aList)) {
                return false;
            } else if(exp.getValue().equals(car(car(aList)).getValue())) {
                return true;
            } else {
                return bound(exp, cdr(aList));
            }
        } else {
            throw new EvaluationException("ERROR: Cannot perform BOUND for list.");
        }
    }

    public ExpressionTree getVal(ExpressionTree exp, ExpressionTree aList) throws EvaluationException {
        if(isAtomNode(exp)) {
            ExpressionTree expected = car(car(aList));
            if(expected.getValue().equals(exp.getValue())) {
                return cdr(car(aList));
            } else {
                return getVal(exp, cdr(aList));
            }

        } else {
            throw new EvaluationException("ERROR: Cannot perform GETVAL operation for list, found exp: " + exp);
        }
    }

    public ExpressionTree car(ExpressionTree exp) throws EvaluationException {
        if(isAtomNode(exp)) {
            throw new EvaluationException("The expression should be a sExpression for car, found atom: " + exp);
        }
        validateLengthGreaterThanEquals(exp, 1);
        return exp.getLeft();
    }

    public ExpressionTree cdr(ExpressionTree exp) throws EvaluationException {
        if(isAtomNode(exp)) {
            throw new EvaluationException("Requires a list for cdr, found atom");
        }
        validateLengthGreaterThanEquals(exp, 1);
        return exp.getRight();
    }


    public ExpressionTree eval(ExpressionTree expressionTree) throws EvaluationException {
        if (expressionTree.isAtomNode()) {
            return evalAtom(expressionTree);
        } else {
            return evalList(expressionTree);
        }
    }

    private ExpressionTree evalList(ExpressionTree tree) throws EvaluationException {

        if (!isList(tree)) {
            throw new EvaluationException("The given expression tree is not a list :" + tree);
        }

        if (lengthOfExpressionTree(tree) < 2) {
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

            validateLength(t.getLeft(), 2);
        }

        for (ExpressionTree t = tree.getRight(); !t.isAtomNode(); t = t.getRight()) {
            ExpressionTree tree1 = eval(t.getLeft().getLeft());
            ExpressionTree tree2 = eval(t.getLeft().getRight().getLeft());

            if (!isNilNode(tree1)) {
                return tree2;
            }
        }

        throw new EvaluationException("All conditions failed, verify the input");
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
                Token token = new Token(Tokens.LITERAL_ATOMS, isNilNode(tree1) ? T : NIL, null);
                answer.setToken(token);
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

        if (tree1.getToken() == null || !tree1.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            throw new EvaluationException(function.getFunctionName() + ": " + tree1 + " requires numbers, but got literal atom");
        }
        if (tree2.getToken() == null || !tree2.getToken().getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
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
            case MINUS: {
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.NUMERIC_ATOMS, null, i1 - i2));
                answer.setAtomNode(true);
                return answer;
            }
            case TIMES: {
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.NUMERIC_ATOMS, null, i1 * i2));
                answer.setAtomNode(true);
                return answer;
            }
            case LESS: {
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.LITERAL_ATOMS, i1 < i2 ? T : NIL, null));
                answer.setAtomNode(true);
                return answer;
            }
            case GREATER: {
                ExpressionTree answer = new ExpressionTree();
                answer.setToken(new Token(Tokens.LITERAL_ATOMS, i1 > i2 ? T : NIL, null));
                answer.setAtomNode(true);
                return answer;
            }
            case EQ: {
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

        if (tree1.isAtomNode()) {
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
        if (token.getTokenType().equals(Tokens.NIL)
                || token.getTokenType().equals(Tokens.NUMERIC_ATOMS)) {
            return expressionTree;
        }

        if (token.getTokenType().equals(Tokens.LITERAL_ATOMS)
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
        return expressionTree.isAtomNode() ? isNilNode(expressionTree) : isNilNode(expressionTree) || isList(expressionTree.getRight());
    }

    private boolean isNilNode(ExpressionTree s) {
        return s.isAtomNode()
                && (s.getToken() != null &&
                (s.getToken().getTokenType().equals(Tokens.LITERAL_ATOMS) || s.getToken().getTokenType().equals(Tokens.NIL))
        )
                && (s.getToken() != null && s.getToken().getStringTokenValue().equals("NIL"));
    }

    private void validateLength(ExpressionTree tree, int requireLength) throws EvaluationException {
        int length = lengthOfExpressionTree(tree);

        if (length != requireLength) {
            throw new EvaluationException(tree.getLeft() + ": requires " + requireLength + " elements, found " + length + " in " + tree);
        }
    }

    private void validateLengthGreaterThanEquals(ExpressionTree tree, int requireLength) throws EvaluationException {
        int length = lengthOfExpressionTree(tree);

        if (length < requireLength) {
            throw new EvaluationException(tree.getLeft() + ": requires " + requireLength + " elements, found " + length + " in " + tree);
        }
    }

    private PrimitiveFunctions getFirstFunction(ExpressionTree tree) throws EvaluationException {
        String functionName = tree.getLeft().getToken().getValue();
        PrimitiveFunctions function = PrimitiveFunctions.getPrimitiveFunction(functionName);

        if (function == null) {
            throw new EvaluationException("Invalid function defined: " + functionName);
        }

        return function;
    }

}
