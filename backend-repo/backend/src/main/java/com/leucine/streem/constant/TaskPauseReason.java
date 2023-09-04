package com.leucine.streem.constant;

public enum TaskPauseReason {
  BIO_BREAK("Bio break"),
  SHIFT_BREAK("Shift break"),
  EQUIPMENT_BREAKDOWN("Equipment Breakdown"),
  LUNCH_BREAK("Lunch break"),
  AREA_BREAKDOWN("Area Breakdown"),
  EMERGENCY_DRILL("Emergency Drill"),
  FIRE_ALARM("Fire alarm"),
  OTHER("Other");


  private final String text;


  TaskPauseReason(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

}
