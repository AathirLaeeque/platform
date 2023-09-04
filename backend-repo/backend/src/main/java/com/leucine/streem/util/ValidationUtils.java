package com.leucine.streem.util;

import com.leucine.streem.dto.response.Error;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.StreemException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ValidationUtils {
  private ValidationUtils() {
  }

  public static void validateNotEmpty(Object object, String error) throws StreemException {
    if (ObjectUtils.isEmpty(object)) {
      throw new StreemException(error);
    }
  }

  public static void validateNotEmpty(Collection<?> object, String error) throws StreemException {
    if (CollectionUtils.isEmpty(object)) {
      throw new StreemException(error);
    }
  }

  public static void validateNotEmpty(String field, Long id, ErrorCode errorCode) throws StreemException {
    if (Utility.isEmpty(field)) {
      List<Error> errorList = new ArrayList<>();
      errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
          .id(String.valueOf(id))
          .code(errorCode.getCode())
          .message(errorCode.getDescription())
          .build());
      throw new StreemException(errorCode.getDescription(), errorList);
    }
  }

  public static void invalidate(Long id, ErrorCode errorCode) throws StreemException {
    List<Error> errorList = new ArrayList<>();
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
        .id(String.valueOf(id))
        .code(errorCode.getCode())
        .message(errorCode.getDescription())
        .build());
    throw new StreemException(errorCode.getDescription(), errorList);
  }

  public static void invalidate(String id, ErrorCode errorCode) throws StreemException {
    List<Error> errorList = new ArrayList<>();
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
            .id(id)
            .code(errorCode.getCode())
            .message(errorCode.getDescription())
            .build());
    throw new StreemException(errorCode.getDescription(), errorList);
  }

  public static void invalidate(String message, List<Error> errorList) throws StreemException {
    throw new StreemException(message, errorList);
  }

  public static void addError(Long id, List<Error> errorList, ErrorCode errorCode) {
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
        .id(String.valueOf(id))
        .code(errorCode.getCode())
        .message(errorCode.getDescription())
        .build());
  }

  public static void addError(List<Error> errorList, ErrorCode errorCode) {
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
      .code(errorCode.getCode())
      .message(errorCode.getDescription())
      .build());
  }

  public static void addError(Long id, String userId, List<Error> errorList, ErrorCode errorCode) {
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
            .id(String.valueOf(id))
            .userId(userId)
            .code(errorCode.getCode())
            .message(errorCode.getDescription())
            .build());
  }

  public static void addError(String id, List<Error> errorList, ErrorCode errorCode, String errorMessage) {
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
            .id(id)
            .code(errorCode.getCode())
            .message(errorMessage)
            .build());
  }

  public static void invalidate(String id, ErrorCode errorCode, String errorMessage) throws StreemException {
    List<Error> errorList = new ArrayList<>();
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
      .id(String.valueOf(id))
      .code(errorCode.getCode())
      .message(errorMessage)
      .build());
    throw new StreemException(errorCode.getDescription(), errorList);
  }
}
