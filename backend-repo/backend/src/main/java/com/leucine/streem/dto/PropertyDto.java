package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDto implements Serializable {
  private static final long serialVersionUID = -4450671348345525431L;

  private String id;
  private String name;
  private String label;
  private String placeHolder;
  private boolean isMandatory;


  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof final PropertyDto other)) return false;
    final Object thisId = this.getId();
    final Object otherId = other.getId();
    return Objects.equals(thisId, otherId);
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object thisId = this.getId();
    result = result * PRIME + (thisId == null ? 43 : thisId.hashCode());
    return result;
  }
}
