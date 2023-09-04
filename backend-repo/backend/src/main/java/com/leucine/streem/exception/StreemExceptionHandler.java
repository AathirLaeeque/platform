package com.leucine.streem.exception;

import com.leucine.streem.constant.ErrorMessage;
import com.leucine.streem.constant.ObjectType;
import com.leucine.streem.constant.ResponseStatus;
import com.leucine.streem.dto.response.Error;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class StreemExceptionHandler extends ResponseEntityExceptionHandler {

  @Value("${spring.servlet.multipart.max-file-size}")
  private DataSize maxFileSize;

  @Value("${spring.servlet.multipart.max-request-size}")
  private DataSize maxRequestSize;


  //Default handler
  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleException(Exception ex) {
    log.error("[handleException] error", ex);
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {StreemException.class})
  protected ResponseEntity<Object> handleStreemException(StreemException ex) {
    log.error("[handleStreemException] error", ex);
    return buildResponseEntity(ex.getErrorList(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {ResourceNotFoundException.class, ConnectException.class})
  protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
    log.error("[handleResourceNotFoundException] error", ex);
    List<Error> errorList = new ArrayList<>();
    ErrorCode errorCode = ex.getErrorCode();
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
                    .id(String.valueOf(ex.getId()))
                    .code(errorCode.getCode())
                    .message(errorCode.getDescription())
                    .build());
    return buildResponseEntity(errorList, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {IllegalArgumentException.class})
  protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error("[handleIllegalArgumentException] error", ex);
    List<Error> errorList = new ArrayList<>();
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
                    .code("bad arguments provided").message("bad arguments provided").build());
    return buildResponseEntity(errorList, HttpStatus.BAD_REQUEST);
  }

  //TODO handle bean validation errors
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request) {
    log.error("[handleMethodArgumentNotValid] error", ex);
    List<Error> errorList = new ArrayList<>();
    // Get the error messages for invalid fields
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
                      .message(fieldError.getDefaultMessage()).build());
    }
    return buildResponseEntity(errorList, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
  protected ResponseEntity<Object> handleIllegalFileSizeLimitExceededException(MaxUploadSizeExceededException ex) {
    log.error("[handleIllegalFileSizeLimitExceededException] error", ex);
    List<Error> errorList = new ArrayList<>();
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
      .code(ErrorCode.FILE_UPLOAD_LIMIT_EXCEEDED.getCode())
      .message(ErrorCode.FILE_UPLOAD_LIMIT_EXCEEDED.getDescription()).build());
    return buildResponseEntity(errorList, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {MultipartException.class})
  protected ResponseEntity<Object> handleMultipartException(MultipartException ex, HttpServletRequest request) throws ServletException, IOException {
    log.error("[handleIllegalFileSizeLimitExceededException] error", ex);
    long fileSize = 0;
    long requestSize = request.getContentLength();
    Part part = request.getPart("file");
    if (part != null) {
      fileSize = part.getSize();
    }

    List<Error> errorList = new ArrayList<>();
    if (fileSize > maxFileSize.toBytes()) {
      errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
        .code(ErrorCode.FILE_UPLOAD_LIMIT_EXCEEDED.getCode())
        .message(ErrorMessage.FILE_SIZE_EXCEEDED.formatted(fileSize, maxFileSize.toBytes())).build());
    }
    if (requestSize > maxRequestSize.toBytes()) {
      errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
        .code(ErrorCode.FILE_UPLOAD_LIMIT_EXCEEDED.getCode())
        .message(ErrorMessage.REQUEST_SIZE_EXCEEDED.formatted(requestSize, maxRequestSize.toBytes())).build());
    }

    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
      .code(ErrorCode.FILE_UPLOAD_LIMIT_EXCEEDED.getCode())
      .message(ErrorMessage.REQUEST_SIZE_EXCEEDED.formatted(request.getContentLength(), maxFileSize.toBytes())).build());
    return buildResponseEntity(errorList, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {FileStorageException.class})
  protected ResponseEntity<Object> handleFileStorageException(FileStorageException ex) {
    log.error("[handleIllegalFileSizeLimitExceededException] error", ex);
    List<Error> errorList = new ArrayList<>();
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
      .code(ErrorCode.FILE_EXTENSION_INVALID.getCode())
      .message(ex.getMessage()).build());
    return buildResponseEntity(errorList, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {AccessDeniedException.class})
  protected ResponseEntity<Object> handleAuhtorizationException(AccessDeniedException ex) {
    log.error("[handleAuhtorizationException] error", ex);
    List<Error> errorList = new ArrayList<>();
    errorList.add(Error.builder().type(ExceptionType.UNAUTHORIZED.get())
      .code(ErrorCode.NOT_AUTHORIZED.getCode())
      .message(ErrorCode.NOT_AUTHORIZED.getDescription()).build());
    return buildResponseEntity(errorList, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(value = {AuthenticationException.class})
  protected ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
    log.error("[handleAuthenticationException] error", ex);
    List<Error> errorList = new ArrayList<>();
    errorList.add(Error.builder().type(ExceptionType.BAD_CREDENTIALS.get())
                    .code(ErrorCode.ACCESS_DENIED.getCode())
                    .message(ErrorCode.ACCESS_DENIED.getDescription()).build());
    return buildResponseEntity(errorList, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = {JaasServiceException.class})
  protected ResponseEntity<Object> handleJaasServiceException(JaasServiceException ex) {
    log.error("[handleJaasServiceException] error", ex);
    return buildResponseEntity(ex.getPayload(), ex.getStatus());
  }

  @ExceptionHandler(value = {AlreadyExistsException.class})
  protected ResponseEntity<Object> handleAlreadyExistsException(AlreadyExistsException ex) {
    log.error("[handleAlreadyExistsException] error", ex);
    List<Error> errorList = new ArrayList<>();
    return buildResponseEntity(errorList, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(value = {DuplicateKeyException.class})
  protected ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException ex) {
    log.error("[handleDuplicateKeyException] error", ex);
    List<Error> errorList = new ArrayList<>();
    errorList.add(Error.builder().type(ExceptionType.BAD_REQUEST.get())
      .code(ErrorCode.DUPLICATE_RECORD_ERROR.getCode())
      .message(ErrorCode.DUPLICATE_RECORD_ERROR.getDescription()).build());
    return buildResponseEntity(errorList, HttpStatus.CONFLICT);
  }

  private ResponseEntity<Object> buildResponseEntity(List<Error> errors, HttpStatus status) {
    return new ResponseEntity<>(Response.builder().errors(errors).object(ObjectType.LIST)
                                  .status(ResponseStatus.ERROR).message("error").build(), status);
  }

  private ResponseEntity<Object> buildResponseEntity(String payload, HttpStatus status) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>(payload, headers, status);
  }

  @ExceptionHandler(value = {MultiStatusException.class})
  protected ResponseEntity<Object> handleMultiStatusException(MultiStatusException ex) {
    log.error("[handleMultiStatusException] error", ex);
    return buildResponseEntity(ex.getErrorList(), HttpStatus.MULTI_STATUS);
  }
}
