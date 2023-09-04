package com.leucine.streem.validator;

public class MinLengthValidator implements ConstraintValidator {
    private boolean isValid;
    private final int minLength;
    private final String errorMessage;

    public MinLengthValidator(int minLength, String errorMessage) {
        this.minLength = minLength;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value) {
        this.isValid = ((String) value).length() >= this.minLength;
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
