package com.leucine.streem.validator;

// TODO see if facade pattern be applied
public  interface ConstraintValidator {
    void validate(Object value);

    boolean isValid();
    String getErrorMessage();
}
