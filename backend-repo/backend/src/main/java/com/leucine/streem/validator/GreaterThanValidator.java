package com.leucine.streem.validator;

public class GreaterThanValidator implements ConstraintValidator {
    private boolean isValid;
    private final double minValue;
    private final String errorMessage;

    public GreaterThanValidator(double minValue, String errorMessage) {
        this.minValue = minValue;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value) {
        this.isValid = Double.parseDouble((String) value) > this.minValue;
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
