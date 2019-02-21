/**
 * Created by shubham on 1/25/2019.
 */
public class ExpressionTree {

    private ExpressionTree left;
    private ExpressionTree right;
    private Token token;
    private String value;
    private boolean isAtomNode;

    public ExpressionTree(ExpressionTree left, ExpressionTree right, String value, Boolean isAtomNode, Token token) {
        this.left = left;
        this.right = right;
        this.value = value;
        this.isAtomNode = isAtomNode;
        this.token = token;
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

    public Boolean isAtomNode() {
        return isAtomNode;
    }

    public void setAtomNode(Boolean atomNode) {
        isAtomNode = atomNode;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        if (isAtomNode()) {
            return token.getValue();
        }
        StringBuilder stringBuilder = new StringBuilder("(");
        ExpressionTree t;

        for (t = this; !t.isAtomNode(); t = t.getRight()) {
            stringBuilder.append(t.getLeft());
            if (!t.getRight().isAtomNode()) {
                stringBuilder.append(" ");
            }
        }

        if (t.getToken().getValue().equals("NIL")) {
            stringBuilder.append(")");
        } else {
            stringBuilder.append(" . ");
            stringBuilder.append(t.getToken().toString());
            stringBuilder.append(")");
        }
        return stringBuilder.toString();
    }
}
