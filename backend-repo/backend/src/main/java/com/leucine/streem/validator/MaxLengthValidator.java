package com.leucine.streem.validator;

public class MaxLengthValidator implements ConstraintValidator {
    private boolean isValid;
    private final int maxLength;
    private final String errorMessage;

    public MaxLengthValidator(int maxLength, String errorMessage) {
        this.maxLength = maxLength;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value) {
        this.isValid = ((String) value).length() <= this.maxLength;
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
