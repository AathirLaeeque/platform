package com.leucine.streem.validator;

import java.util.Objects;

public class StringNotEqualValidator implements ConstraintValidator {
    private boolean isValid;
    private final String value;
    private final String errorMessage;

    public StringNotEqualValidator(String value, String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(Object value) {
        this.isValid = !Objects.equals(value, this.value);
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
