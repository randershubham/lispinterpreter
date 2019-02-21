package com.rander.lisp.interpreter;

/**
 * Created by shubham on 1/24/2019.
 */
public class Token {
    final private Tokens tokenType;
    final private String stringTokenValue;
    final private Integer integerTokenValue;
    final private String value;

    public Token(Tokens tokenType, String stringTokenValue, Integer integerTokenValue) {
        this.tokenType = tokenType;
        this.stringTokenValue = stringTokenValue;
        this.integerTokenValue = integerTokenValue;
        if(integerTokenValue == null && !tokenType.equals(Tokens.NUMERIC_ATOMS)) {
            //TODO: To clean this function nicely, i.e. handle the if conditions properly
            this.value = stringTokenValue;
        } else {
            this.value = integerTokenValue.toString();
        }
    }

    public String getValue() {
        return value;
    }

    public Tokens getTokenType() {
        return tokenType;
    }

    public String getStringTokenValue() {
        return stringTokenValue;
    }

    public Integer getIntegerTokenValue() {
        return integerTokenValue;
    }

    @Override
    public String toString() {
        return value;
    }
}
