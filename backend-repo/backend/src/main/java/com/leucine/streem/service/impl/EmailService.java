package com.leucine.streem.service.impl;

import com.leucine.streem.email.dto.EmailRequest;
import com.leucine.streem.email.exception.EmailException;
import com.leucine.streem.email.service.IEmailDispatcherService;
import com.leucine.streem.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
  private final IEmailDispatcherService mailDispatcherService;

  @Override
  @Async
  public void sendEmail(EmailRequest emailRequest) {
    log.info("[sendEmail] Request to send email, emailRequest: {}", emailRequest);
    try {
      mailDispatcherService.sendMail(emailRequest);
    } catch (EmailException ee) {
      log.error("[sendEmail] Error sending email", ee);
    }
  }
}
