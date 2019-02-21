package com.rander.lisp.interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shubham on 2/11/2019.
 */
public enum PrimitiveFunctions {

    CAR("CAR"),
    CDR("CDR"),
    CONS("CONS"),
    ATOM("ATOM"),
    INT("INT"),
    NULL("NULL"),
    EQ("EQ"),
    PLUS("PLUS"),
    LESS("LESS"),
    QUOTE("QUOTE"),
    MINUS("MINUS"),
    TIMES("TIMES"),
    GREATER("GREATER"),
    COND("COND");

    final String functionName;

    private static final Map<String, PrimitiveFunctions> FUNCTIONS_HASH_MAP = new HashMap<>();

    PrimitiveFunctions(String functionName) {
        this.functionName = functionName;
    }

    static {
        for(PrimitiveFunctions function : PrimitiveFunctions.values()) {
            FUNCTIONS_HASH_MAP.put(function.functionName, function);
        }
    }

    public static PrimitiveFunctions getPrimitiveFunction(String functionName) {
        return FUNCTIONS_HASH_MAP.get(functionName);
    }


}
