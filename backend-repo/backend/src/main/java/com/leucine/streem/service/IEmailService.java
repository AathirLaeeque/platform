package com.leucine.streem.service;

import com.leucine.streem.email.dto.EmailRequest;

public interface IEmailService {
  void sendEmail(EmailRequest emailRequest);
}
