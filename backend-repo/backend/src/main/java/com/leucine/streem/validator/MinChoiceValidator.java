package com.leucine.streem.validator;

import java.util.List;

public class MinChoiceValidator implements ConstraintValidator {
    private boolean isValid;
    private final int minChoice;
    private final String errorMessage;

    public MinChoiceValidator(int minChoice, String errorMessage) {
        this.minChoice = minChoice;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value) {
        this.isValid = ((List<String>) value).size() >= this.minChoice;
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
