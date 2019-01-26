package com.rander.lisp.interpreter;

/**
 * Created by shubham on 1/25/2019.
 */
public class ExpressionTree {

    private ExpressionTree left;
    private ExpressionTree right;
    private String value;
    private Boolean isAtomNode;

    public ExpressionTree(ExpressionTree left, ExpressionTree right, String value, Boolean isAtomNode) {
        this.left = left;
        this.right = right;
        this.value = value;
        this.isAtomNode = isAtomNode;
    }

    public ExpressionTree() {
        super();
    }

    public ExpressionTree getLeft() {
        return left;
    }

    public void setLeft(ExpressionTree left) {
        this.left = left;
    }

    public ExpressionTree getRight() {
        return right;
    }

    public void setRight(ExpressionTree right) {
        this.right = right;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getAtomNode() {
        return isAtomNode;
    }

    public void setAtomNode(Boolean atomNode) {
        isAtomNode = atomNode;
    }

    @Override
    public String toString() {
        return "" + value + "";
    }
}
