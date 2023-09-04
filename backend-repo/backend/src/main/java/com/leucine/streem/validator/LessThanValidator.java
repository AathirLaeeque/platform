package com.leucine.streem.validator;

public class LessThanValidator implements ConstraintValidator {
    private boolean isValid;
    private final double maxValue;
    private final String errorMessage;

    public LessThanValidator(double maxValue, String errorMessage) {
        this.maxValue = maxValue;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value) {
        this.isValid = Double.parseDouble((String) value) < this.maxValue;
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
