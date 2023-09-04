package com.leucine.streem.handler;

import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.ParameterValue;
import com.leucine.streem.model.ParameterVerification;
import com.leucine.streem.model.User;

public interface IParameterVerificationHandler {
  void canInitiateSelfVerification(User principalUserEntity, ParameterValue parameterValue) throws StreemException;
  void canCompleteSelfVerification(User principalUserEntity, Long parameterId, ParameterVerification parameterVerification) throws StreemException;
  void canCompletePeerVerification(User principalUserEntity, ParameterVerification lastActionPerformed) throws StreemException;
}
