package com.leucine.streem.validator;

public class GreaterThanOrEqualValidator implements ConstraintValidator {
    private boolean isValid;
    private final double value;
    private final String errorMessage;

    public GreaterThanOrEqualValidator(double value, String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value) {
        this.isValid = Double.parseDouble((String) value) >= this.value;
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
