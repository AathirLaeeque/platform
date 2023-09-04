package com.leucine.streem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssigneeSignOffDto implements Serializable {
  @Serial
  private static final long serialVersionUID = 1321174023482610809L;

  private String id;
  private String employeeId;
  private String firstName;
  private String lastName;
  private String email;
  private Long recentSignOffAt;

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof final AssigneeSignOffDto other)) return false;
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
