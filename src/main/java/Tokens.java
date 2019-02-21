/**
 * Created by shubham on 1/24/2019.
 */
public enum Tokens {

    LITERAL_ATOMS("LITERAL ATOMS"),
    NUMERIC_ATOMS("NUMERIC ATOMS"),
    OPEN_PARENTHESES("OPEN PARENTHESES"),
    CLOSING_PARENTHESES("CLOSING PARENTHESES"),
    ERROR("ERROR"),
    NIL("NIL"),
    EOF("EOF");

    String tokenName;

    Tokens(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenName() {
        return tokenName;
    }
}
