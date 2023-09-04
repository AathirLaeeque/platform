package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class ParameterValueDto extends BaseParameterValueDto implements Serializable {
  private static final long serialVersionUID = -3890284164639179924L;
  private String id;
  private State.ParameterExecution state;
  private ParameterValueApprovalDto parameterValueApprovalDto;
  private List<ParameterVerificationDto> parameterVerifications;
}
