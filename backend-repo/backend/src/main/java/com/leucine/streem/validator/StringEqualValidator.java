package com.leucine.streem.validator;

public class StringEqualValidator implements ConstraintValidator {
    private boolean isValid;
    private final String value;
    private final String errorMessage;

    public StringEqualValidator(String value, String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value) {
        this.isValid = value.equals(this.value);
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
