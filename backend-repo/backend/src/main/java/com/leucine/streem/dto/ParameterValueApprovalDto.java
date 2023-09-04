package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class ParameterValueApprovalDto implements Serializable {
  private static final long serialVersionUID = -4536701463163624001L;

  private String id;
  private UserAuditDto approver;
  private State.ParameterValue state;
  private Long createdAt;
}
