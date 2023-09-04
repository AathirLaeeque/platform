package com.leucine.streem.email.service.impl;

import com.leucine.streem.email.config.EmailProperties;
import com.leucine.streem.email.dto.EmailRequest;
import com.leucine.streem.email.dto.PreparedEmail;
import com.leucine.streem.email.exception.EmailException;
import com.leucine.streem.email.exception.FreeMarkerException;
import com.leucine.streem.email.service.IEmailAuditService;
import com.leucine.streem.email.service.IEmailDispatcherService;
import com.leucine.streem.email.util.FreeMarkerUtil;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Service
public class EmailDispatcherService implements IEmailDispatcherService {
  private static final String FAILED_TO_SEND_EMAIL = "Failed to send email";
  private final JavaMailSender mailSender;
  private final Configuration freemarkerConfig;
  private final EmailProperties emailProperties;
  private final IEmailAuditService emailAuditService;

  @Autowired
  public EmailDispatcherService(final JavaMailSender javaMailSender, final Configuration configuration,
                                final EmailProperties emailProperties, final IEmailAuditService emailAuditService) {
    this.mailSender = javaMailSender;
    this.freemarkerConfig = configuration;
    this.emailProperties = emailProperties;
    this.emailAuditService = emailAuditService;
  }

  @Override
  public String sendMail(EmailRequest emailRequest) throws EmailException {
    try {
      var preparedEmail = prepareMail(emailRequest);
      mailSender.send(prepareMimeMessage(preparedEmail));
      writeToAudit(preparedEmail);
    } catch (Exception ex) {
      log.error("[sendMail] Failed to send email", ex);
      throw new EmailException(FAILED_TO_SEND_EMAIL);
    }
    return SUCCESS_MESSAGE;
  }

  private PreparedEmail prepareMail(EmailRequest emailRequest) throws IOException, FreeMarkerException {
    var preparedEmail = new PreparedEmail();
    String fromAddress = preparedEmail.getFrom() == null ? emailProperties.getFromAddress() : preparedEmail.getFrom();
    preparedEmail.setFrom(fromAddress);
    if (null != emailRequest.getTo()) {
      preparedEmail.setTo(emailRequest.getTo().stream().filter(Objects::nonNull).toArray(String[]::new));
    }
    if (null != emailRequest.getCc()) {
      preparedEmail.setCc(emailRequest.getCc().stream().filter(Objects::nonNull).toArray(String[]::new));
    }
    if (null != emailRequest.getBcc()) {
      preparedEmail.setBcc(emailRequest.getBcc().stream().filter(Objects::nonNull).toArray(String[]::new));
    }
    preparedEmail.setSubject(emailRequest.getSubject());
    var template = freemarkerConfig.getTemplate(emailRequest.getTemplateName());
    String html = FreeMarkerUtil.processTemplate(template, emailRequest.getAttributes());
    preparedEmail.setBody(html);
    return preparedEmail;
  }

  private MimeMessage prepareMimeMessage(PreparedEmail preparedEmail) throws MessagingException {
    var message = mailSender.createMimeMessage();
    var helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
    helper.setFrom(preparedEmail.getFrom());
    if (null != preparedEmail.getTo()) {
      helper.setTo(preparedEmail.getTo());
    }
    if (null != preparedEmail.getCc()) {
      helper.setCc(preparedEmail.getCc());
    }
    if (null != preparedEmail.getBcc()) {
      helper.setBcc(preparedEmail.getBcc());
    }
    helper.setText(preparedEmail.getBody(), true);
    helper.setSubject(preparedEmail.getSubject());
    //TODO host image to avoid no image show in emails ?
    helper.addInline("leucine-blue-logo", new ClassPathResource("leucine-blue-logo.png"));
    return message;
  }

  private void writeToAudit(PreparedEmail preparedEmail) {
    emailAuditService.writeToAudit(preparedEmail);
  }
}
