import java.util.HashSet;

public class LispEvaluator {

    private static final String T = "T";
    private static final String NIL = "NIL";
    private static ExpressionTree D_LIST = new ExpressionTree(null, null, "NIL", true, new Token(Tokens.NIL, "NIL", null));
    private static final String[] BUILT_IN_FUNCTIONS = new String[]{
            "NULL",
            "PLUS",
            "MINUS",
            "CAR",
            "CDR",
            "ATOM",
            "CONS",
            "INT",
            "TIMES",
            "QUOTE",
            "DEFUN",
            "QUOTIENT",
            "REMAINDER",
            "LESS",
            "GREATER",
            "COND"
    };

    private static boolean isAtom(ExpressionTree s) {
        return s.isAtomNode();
    }

    private ExpressionTree car(ExpressionTree tree) {
        ExpressionTree temp = tree;
        if (tree != null) {
            if (isAtom(tree)) {
                System.out.println("ERROR: Cannot perform CAR on Atom.");
                System.exit(1);
            } else {
                if (tree.getLeft() != null) {
                    temp = tree.getLeft();
                } else {
                    System.out.println("ERROR: Cannot perform CAR on the expression.");
                    System.exit(1);
                }
            }
        }
        return temp;
    }

    private static ExpressionTree cdr(ExpressionTree tree) {
        ExpressionTree result = tree;
        if (tree != null) {
            if (isAtom(tree)) {
                System.out.println("ERROR: Cannot perform CDR on Atom.");
                System.exit(1);
            } else {
                if (tree.getRight() != null) {
                    result = tree.getRight();
                } else {
                    System.out.println("ERROR: Cannot perform CDR on the expression.");
                    System.exit(1);
                }
            }
        }
        return result;
    }

    private ExpressionTree cons(ExpressionTree left, ExpressionTree right) {
        ExpressionTree answer = new ExpressionTree();
        answer.setLeft(left);
        answer.setRight(right);
        return answer;
    }

    private boolean isNull(ExpressionTree exp) {
        return exp.isAtomNode() && NIL.equals(exp.getValue());
    }

    private static boolean isInt(ExpressionTree exp) {
        boolean isInt = false;
        try {
            if (isAtom(exp)) {
                Integer.parseInt(exp.getValue());
                isInt = true;
            }
        } catch (java.lang.NumberFormatException e) {
            isInt = false;
        }
        return isInt;
    }

    public ExpressionTree myInt(ExpressionTree exp) {
        ExpressionTree aTree = new ExpressionTree(null, null, "NIL", true, new Token(Tokens.NIL, "NIL", null));
        return eval(exp, aTree, D_LIST);
    }

    private static boolean eq(ExpressionTree left, ExpressionTree right) {
        boolean isEq = false;
        if (isAtom(left) && isAtom(right)) {
            if (isInt(left) && isInt(right)) {
                if (Integer.parseInt(left.getValue()) == Integer.parseInt(right.getValue())) {
                    isEq = true;
                }
            } else if (left.getValue().equalsIgnoreCase(right.getValue())) {
                isEq = true;
            }
        } else {
            System.out.println("ERROR: EQ cannot be applied on lists or empty atoms.");
            System.exit(1);
        }
        return isEq;
    }

    private boolean bound(ExpressionTree tree, ExpressionTree aList) {

        boolean isBound = false;
        if (isAtom(tree)) {
            if (isNull(aList)) {
                return false;
            } else if (eq(tree, car(car(aList)))) {
                return true;
            } else {
                return bound(tree, cdr(aList));
            }
        } else {
            System.out.println("ERROR: Cannot perform BOUND for list.");
            System.exit(1);
        }
        return isBound;
    }

    private ExpressionTree getVal(ExpressionTree tree, ExpressionTree aList) {
        if (isAtom(tree)) {
            if (eq(tree, car(car(aList)))) {
                return cdr(car(aList));
            } else {
                return getVal(tree, cdr(aList));
            }
        } else {
            System.out.println("ERROR: Cannot perform GETVAL operation for list.");
            System.exit(1);
        }
        return null;
    }

    private static int getNumberOfParams(ExpressionTree tree) {
        int count = 0;
        if (isAtom(tree)) {
            return 0;
        }
        while (tree != null) {
            if (tree.getLeft() != null) {
                count++;
            }
            tree = tree.getRight();
        }
        return count;
    }

    private ExpressionTree evCon(ExpressionTree tree, ExpressionTree aList, ExpressionTree dList) {
        if (isNull(tree)) {
            System.out.println("ERROR: Expression is null.");
            System.exit(1);
        } else if (eval(car(car(tree)), aList, dList).getValue().equalsIgnoreCase(T)) {
            return eval(car(cdr(car(tree))), aList, dList);
        } else {
            return evCon(cdr(tree), aList, dList);
        }
        return null;
    }

    private boolean checkUserDefinedFunction(ExpressionTree tree) {
        boolean isOk = true;
        if (isAtom(car(tree))) {
            if (isInt(car(tree))) {
                System.out.println("ERROR: Function name cannot be a numeral isAtom.");
                System.exit(1);
            }
            String functionName = car(tree).getValue();

            for (String builtInFunction : BUILT_IN_FUNCTIONS) {
                if (functionName.equalsIgnoreCase(builtInFunction)) {
                    System.out.println("ERROR: Function name cannot be " + functionName);
                    System.exit(1);
                }
            }
            ExpressionTree params = car(cdr(tree));

            HashSet<String> paramList = new HashSet<>();
            int count = 0;
            while (!isNull(params)) {
                ExpressionTree tmp = car(params);
                if (isAtom(tmp)) {
                    if (isInt(tmp)) {
                        System.out.println("ERROR: Parameter name cannot be a numeral isAtom.");
                        System.exit(1);
                    }
                    String paramName = tmp.getValue();
                    if (paramName.equalsIgnoreCase(T) || paramName.equalsIgnoreCase(NIL)) {
                        System.out.println("ERROR: Parameter name cannot be T or NIL.");
                        System.exit(1);
                    }
                    paramList.add(paramName);
                    count++;
                } else {
                    System.out.println("ERROR: Formal parameter cannot be list.");
                    System.exit(1);
                }
                params = cdr(params);
            }
            if (count != paramList.size()) {
                System.out.println("ERROR: Parameter list has duplicate values.");
                System.exit(1);
            }

        } else {
            System.out.println("ERROR: Function name cannot be a list.");
            System.exit(1);
        }
        return isOk;
    }

    private ExpressionTree plus(ExpressionTree left, ExpressionTree right) {
        ExpressionTree result = new ExpressionTree();
        if (isAtom(left) && isAtom(right)) {
            if (isInt(left) && isInt(right)) {
                int leftVal = Integer.parseInt(left.getValue());
                int rightVal = Integer.parseInt(right.getValue());

                Token token = new Token(Tokens.NUMERIC_ATOMS, null, leftVal + rightVal);
                result.setToken(token);
                result.setAtomNode(true);
            } else {
                System.out.println("ERROR: Cannot perform PLUS operation on literal atoms.");
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: Cannot perform PLUS operation on Non atoms.");
            System.exit(1);
        }
        return result;
    }

    private ExpressionTree minus(ExpressionTree left, ExpressionTree right) {
        ExpressionTree result = new ExpressionTree();
        if (isAtom(left) && isAtom(right)) {
            if (isInt(left) && isInt(right)) {
                int leftVal = Integer.parseInt(left.getValue());
                int rightVal = Integer.parseInt(right.getValue());
                Token token = new Token(Tokens.NUMERIC_ATOMS, null, leftVal - rightVal);
                result.setToken(token);
                result.setAtomNode(true);
            } else {
                System.out.println("ERROR: Cannot perform MINUS operation on literal atoms.");
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: Cannot perform MINUS operation on Non atoms.");
            System.exit(1);
        }
        return result;
    }

    private ExpressionTree times(ExpressionTree left, ExpressionTree right) {
        ExpressionTree result = new ExpressionTree();
        if (isAtom(left) && isAtom(right)) {
            if (isInt(left) && isInt(right)) {
                int leftVal = Integer.parseInt(left.getValue());
                int rightVal = Integer.parseInt(right.getValue());
                Token token = new Token(Tokens.NUMERIC_ATOMS, null, leftVal * rightVal);
                result.setToken(token);
                result.setAtomNode(true);
            } else {
                System.out.println("ERROR: Cannot perform TIMES operation on literal atoms.");
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: Cannot perform TIMES operation on Non atoms.");
            System.exit(1);
        }
        return result;
    }

    private ExpressionTree quotient(ExpressionTree left, ExpressionTree right) {
        ExpressionTree result = new ExpressionTree();
        if (isAtom(left) && isAtom(right)) {
            if (isInt(left) && isInt(right)) {
                int leftVal = Integer.parseInt(left.getValue());
                int rightVal = Integer.parseInt(right.getValue());
                int res;
                if (rightVal == 0) {
                    res = 0;
                } else {
                    res = leftVal / rightVal;
                }

                Token token = new Token(Tokens.NUMERIC_ATOMS, null, res);
                result.setToken(token);
                result.setAtomNode(true);
            } else {
                System.out.println("ERROR: Cannot perform QUOTIENT operation on literal atoms.");
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: Cannot perform QUOTIENT operation on Non atoms.");
            System.exit(1);
        }
        return result;
    }

    private ExpressionTree remainder(ExpressionTree left, ExpressionTree right) {
        ExpressionTree result = new ExpressionTree();
        if (isAtom(left) && isAtom(right)) {
            if (isInt(left) && isInt(right)) {
                int leftVal = Integer.parseInt(left.getValue());
                int rightVal = Integer.parseInt(right.getValue());
                Token token = new Token(Tokens.NUMERIC_ATOMS, null, leftVal % rightVal);
                result.setToken(token);
                result.setAtomNode(true);

            } else {
                System.out.println("ERROR: Cannot perform REMAINDER operation on literal atoms.");
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: Cannot perform REMAINDER operation on Non atoms.");
            System.exit(1);
        }
        return result;
    }

    private ExpressionTree less(ExpressionTree left, ExpressionTree right) {
        ExpressionTree result = new ExpressionTree();
        if (isAtom(left) && isAtom(right)) {
            if (isInt(left) && isInt(right)) {
                int leftVal = Integer.parseInt(left.getValue());
                int rightVal = Integer.parseInt(right.getValue());

                Token token = new Token(Tokens.LITERAL_ATOMS, leftVal < rightVal ? T : NIL, null);
                result.setToken(token);
                result.setAtomNode(true);
            } else {
                System.out.println("ERROR: Cannot perform TIMES operation on literal atoms.");
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: Cannot perform TIMES operation on Non atoms.");
            System.exit(1);
        }
        return result;
    }

    private ExpressionTree greater(ExpressionTree left, ExpressionTree right) {
        ExpressionTree result = new ExpressionTree();
        if (isAtom(left) && isAtom(right)) {
            if (isInt(left) && isInt(right)) {
                int leftVal = Integer.parseInt(left.getValue());
                int rightVal = Integer.parseInt(right.getValue());
                Token token = new Token(Tokens.LITERAL_ATOMS, leftVal > rightVal ? T : NIL, null);
                result.setToken(token);
                result.setAtomNode(true);
            } else {
                System.out.println("ERROR: Cannot perform TIMES operation on literal atoms.");
                System.exit(1);
            }
        } else {
            System.out.println("ERROR: Cannot perform TIMES operation on Non atoms.");
            System.exit(1);
        }
        return result;
    }

    private ExpressionTree addPairs(ExpressionTree varList, ExpressionTree valueList, ExpressionTree oldList) {
        if (isNull(varList)) {
            return oldList;
        } else {
            return (cons(cons(car(varList), car(valueList)), addPairs(cdr(varList), cdr(valueList), oldList)));
        }
    }

    private ExpressionTree eval(ExpressionTree s, ExpressionTree aList, ExpressionTree dList) {
        ExpressionTree resultantTree = null;
        if (isAtom(s)) {
            if (T.equals(s.getValue()) || isNull(s) || isInt(s)) {
                resultantTree = s;
            } else if (bound(s, aList)) {
                resultantTree = getVal(s, aList);
            } else {
                System.out.println("ERROR: Unbound variable.");
                System.exit(1);
            }
        } else {
            int numberOfParams = getNumberOfParams(cdr(s));
            if ("QUOTE".equals(car(s).getValue())) {
                if (numberOfParams != 1) {
                    System.out.println("ERROR: Only one parameter required for QUOTE operation.");
                    System.exit(1);
                }
                resultantTree = car(cdr(s));
            } else if ("COND".equals(car(s).getValue())) {
                resultantTree = evCon(cdr(s), aList, dList);
            } else if ("DEFUN".equals(car(s).getValue())) {
                if (numberOfParams != 3) {
                    System.out.println("ERROR: Three parameters required for DEFUN operation.");
                    System.exit(1);
                }
                if (checkUserDefinedFunction(cdr(s))) {
                    ExpressionTree functionName = car(cdr(s));
                    ExpressionTree formalParameter = car(cdr(cdr(s)));
                    ExpressionTree body = car(cdr(cdr(cdr(s))));
                    ExpressionTree functionDefinition = cons(functionName, cons(formalParameter, body));
                    D_LIST = cons(functionDefinition, D_LIST);
                    resultantTree = car(cdr(s));
                } else {
                    System.out.println("ERROR: Cannot define function with function name " + car(cdr(s)));
                    System.exit(1);
                }
            } else {
                resultantTree = apply(car(s), evList(cdr(s), aList, dList), aList, dList);
            }
        }
        return resultantTree;
    }

    private ExpressionTree apply(ExpressionTree tree, ExpressionTree params, ExpressionTree aList, ExpressionTree dList) {

        if (isAtom(tree)) {
            int numberOfParams = getNumberOfParams(params);
            //System.out.println("Number of params: " + numberOfParams);
            if ("CAR".equals(tree.getValue())) {
                if (numberOfParams != 1) {
                    System.out.println("ERROR: Only one parameter is expected for CAR operation.");
                    System.exit(1);
                }
                return car(car(params));
            } else if ("CDR".equals(tree.getValue())) {
                if (numberOfParams != 1) {
                    System.out.println("ERROR: Only one parameter is expected for CDR operation.");
                    System.exit(1);
                }
                return cdr(car(params));
            } else if ("CONS".equals(tree.getValue())) {
                if (numberOfParams != 2) {
                    System.out.println("ERROR: Two parameters required for CONS operation.");
                    System.exit(1);
                }
                return cons(car(params), car(cdr(params)));
            } else if ("ATOM".equals(tree.getValue())) {
                if (numberOfParams != 1) {
                    System.out.println("ERROR: Only one parameter is expected for ATOM operation.");
                    System.exit(1);
                }
                ExpressionTree temp = new ExpressionTree();
                Token token = new Token(Tokens.LITERAL_ATOMS, isAtom(car(params)) ? T : NIL, null);
                temp.setAtomNode(true);
                temp.setToken(token);
                return temp;
            } else if ("EQ".equals(tree.getValue())) {

                if (numberOfParams != 2) {
                    System.out.println("ERROR: Two parameters required for EQ operation.");
                    System.exit(1);
                }

                ExpressionTree temp = new ExpressionTree();
                Token token = new Token(Tokens.LITERAL_ATOMS, eq(car(params), car(cdr(params))) ? T : NIL, null);
                temp.setAtomNode(true);
                temp.setToken(token);
                return temp;
            } else if ("PLUS".equals(tree.getValue())) {
                if (numberOfParams != 2) {
                    System.out.println("ERROR: Two parameters required for PLUS operation.");
                    System.exit(1);
                }
                return plus(car(params), car(cdr(params)));
            } else if ("MINUS".equals(tree.getValue())) {
                if (numberOfParams != 2) {
                    System.out.println("ERROR: Two parameters required for MINUS operation.");
                    System.exit(1);
                }
                return minus(car(params), car(cdr(params)));
            } else if ("NULL".equals(tree.getValue())) {
                if (numberOfParams != 1) {
                    System.out.println("ERROR: Only one parameter is required for NULL operation.");
                    System.exit(1);
                }
                ExpressionTree temp = new ExpressionTree();
                Token token = new Token(Tokens.LITERAL_ATOMS, isNull(car(params)) ? T : NIL, null);
                temp.setAtomNode(true);
                temp.setToken(token);
                return temp;
            } else if ("INT".equals(tree.getValue())) {

                if (numberOfParams != 1) {
                    System.out.println("ERROR: Only one parameter is required for INT operation.");
                    System.exit(1);
                }

                ExpressionTree temp = new ExpressionTree();
                Token token = new Token(Tokens.LITERAL_ATOMS, isInt(car(params)) ? T : NIL, null);
                temp.setAtomNode(true);
                temp.setToken(token);
                return temp;

            } else if ("TIMES".equals(tree.getValue())) {
                if (numberOfParams != 2) {
                    System.out.println("ERROR: Two parameters are required for TIMES operation.");
                    System.exit(1);
                }
                return times(car(params), car(cdr(params)));
            } else if ("QUOTIENT".equals(tree.getValue())) {
                if (numberOfParams != 2) {
                    System.out.println("ERROR: Two parameters are required for QUOTIENT operation.");
                    System.exit(1);
                }
                return quotient(car(params), car(cdr(params)));
            } else if ("REMAINDER".equals(tree.getValue())) {
                if (numberOfParams != 2) {
                    System.out.println("ERROR: Two parameters are required for REMAINDER operation.");
                    System.exit(1);
                }
                return remainder(car(params), car(cdr(params)));
            } else if ("LESS".equals(tree.getValue())) {
                if (numberOfParams != 2) {
                    System.out.println("ERROR: Two parameters are required for LESS operation.");
                    System.exit(1);
                }
                return less(car(params), car(cdr(params)));
            } else if ("GREATER".equals(tree.getValue())) {
                if (numberOfParams != 2) {
                    System.out.println("ERROR: Two parameters are required for GREATER operation.");
                    System.exit(1);
                }
                return greater(car(params), car(cdr(params)));
            } else {
                int numParamsUserDef = getNumberOfParams(car(getVal(tree, dList)));
                if (numberOfParams != numParamsUserDef) {
                    System.out.println("ERROR: Number of parameters do no match.");
                    System.exit(1);
                }

                return eval(cdr(getVal(tree, dList)), addPairs(car(getVal(tree, dList)), params, aList), dList);
            }
        } else {
            System.out.println("ERROR: Error in apply function.");
            System.exit(1);
        }
        return null;
    }

    private ExpressionTree evList(ExpressionTree tree, ExpressionTree aList, ExpressionTree dList) {
        if (isNull(tree)) {
            return new ExpressionTree(null, null, "NIL", true, new Token(Tokens.NIL, "NIL", null));
        } else {
            return cons(eval(car(tree), aList, dList), evList(cdr(tree), aList, dList));
        }
    }
}