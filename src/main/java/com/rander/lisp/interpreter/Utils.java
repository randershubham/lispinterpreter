package com.rander.lisp.interpreter;

/**
 * Created by shubham on 2/19/2019.
 */
public abstract class Utils {
    public static StringBuilder prettyPrintExpressionTree(ExpressionTree node, StringBuilder stringBuilder) {
        if (node.getLeft() == null && node.getRight() == null) {
            stringBuilder.append(node.getValue());
            return stringBuilder;
        }
        stringBuilder.append("(");
        prettyPrintExpressionTree(node.getLeft(), stringBuilder);
        stringBuilder.append(" . ");
        prettyPrintExpressionTree(node.getRight(), stringBuilder);
        stringBuilder.append(")");
        return stringBuilder;
    }
}
