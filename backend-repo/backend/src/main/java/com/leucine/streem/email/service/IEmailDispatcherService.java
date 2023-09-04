package com.leucine.streem.email.service;

import com.leucine.streem.email.dto.EmailRequest;
import com.leucine.streem.email.exception.EmailException;

public interface IEmailDispatcherService {
    String SUCCESS_MESSAGE = "SUCCESS";

    String sendMail(EmailRequest emailRequest) throws EmailException;
}
