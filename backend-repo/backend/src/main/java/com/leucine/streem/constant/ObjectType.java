package com.leucine.streem.constant;

public enum ObjectType {
  OBJECT("object"),
  AUDIT("audit"),
  LIST("list");

  private final String type;

  ObjectType(String type) {
    this.type = type;
  }

  public String get() {
    return type;
  }
}
