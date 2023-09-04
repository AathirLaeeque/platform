package com.leucine.streem.validator;

import java.util.regex.Pattern;

public class RegexValidator implements ConstraintValidator {
  private boolean isValid;
  private final Pattern pattern;
  private final String errorMessage;

  public RegexValidator(String regex, String errorMessage) {
    this.pattern = Pattern.compile(regex);
    this.errorMessage = errorMessage;
  }

  public RegexValidator(String regex) {
    this.pattern = Pattern.compile(regex);
    this.errorMessage = "Input does not match the given pattern";
  }

  @Override
  public void validate(Object value) {
    this.isValid = pattern.matcher((String) value).matches();
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
