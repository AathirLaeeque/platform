package com.leucine.streem.validator;

import java.util.List;

// TODO Group number validations, string validations, date validations
public class MaxChoiceValidator implements ConstraintValidator {
    private boolean isValid;
    private final int maxChoice;
    private final String errorMessage;

    public MaxChoiceValidator(int maxChoice, String errorMessage) {
        this.maxChoice = maxChoice;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value) {
        this.isValid = ((List<String>) value).size() <= this.maxChoice;
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
