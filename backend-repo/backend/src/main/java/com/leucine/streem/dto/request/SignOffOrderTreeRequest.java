package com.leucine.streem.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class SignOffOrderTreeRequest {
  private Set<ChecklistSignOffUser> users;
}
