package com.leucine.streem.handler;

import com.leucine.streem.constant.State;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import org.springframework.stereotype.Component;

@Component
public class ParameterVerificationHandler implements IParameterVerificationHandler {


  public void canInitiateSelfVerification(User principalUserEntity, ParameterValue parameterValue) throws StreemException {
    if ((Utility.isEmpty(parameterValue)) || !parameterValue.getState().equals(State.ParameterExecution.BEING_EXECUTED)) {
      ValidationUtils.invalidate(parameterValue.getId(), ErrorCode.PARAMETER_VERIFICATION_NOT_ALLOWED);
    } else if (!principalUserEntity.getId().equals(parameterValue.getModifiedBy().getId())) {
      ValidationUtils.invalidate(principalUserEntity.getId(), ErrorCode.USER_NOT_ALLOWED_TO_SELF_VERIFIY_PARAMETER);
    }
  }

  public void canCompleteSelfVerification(User principalUserEntity, Long parameterId, ParameterVerification parameterVerification) throws StreemException {
    if (!principalUserEntity.getId().equals(parameterVerification.getUser().getId())) {
      ValidationUtils.invalidate(parameterId, ErrorCode.SELF_VERIFICATION_NOT_ALLOWED);
    }
  }

  @Override
  public void canCompletePeerVerification(User principalUserEntity, ParameterVerification lastActionPerformed) throws StreemException {
    if (!principalUserEntity.getId().equals(lastActionPerformed.getUser().getId())) {
      ValidationUtils.invalidate(lastActionPerformed.getId(), ErrorCode.PEER_VERIFICATION_NOT_ALLOWED);
    }
  }


}

