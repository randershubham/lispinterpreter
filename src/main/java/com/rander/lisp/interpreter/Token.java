package com.rander.lisp.interpreter;

/**
 * Created by shubham on 1/24/2019.
 */
class Token {
    final private Tokens tokenType;
    final private String stringTokenValue;
    final private Integer integerTokenValue;

    public Token(Tokens tokenType, String stringTokenValue, Integer integerTokenValue) {
        this.tokenType = tokenType;
        this.stringTokenValue = stringTokenValue;
        this.integerTokenValue = integerTokenValue;
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
        return "Token{" +
                "tokenType=" + tokenType +
                ", stringTokenValue='" + stringTokenValue + '\'' +
                ", integerTokenValue=" + integerTokenValue +
                '}';
    }
}
