package com.leucine.streem.email.service.impl;

import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.email.dto.PreparedEmail;
import com.leucine.streem.email.model.EmailAudit;
import com.leucine.streem.email.repository.IEmailAuditRepository;
import com.leucine.streem.email.service.IEmailAuditService;
import com.leucine.streem.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailAuditService implements IEmailAuditService {

  private final IEmailAuditRepository emailAuditRepository;

  @Autowired
  public EmailAuditService(IEmailAuditRepository emailAuditRepository) {
    this.emailAuditRepository = emailAuditRepository;
  }

  @Override
  public void writeToAudit(PreparedEmail preparedEmail) {
    EmailAudit emailAudit = new EmailAudit();
    emailAudit.setId(IdGenerator.getInstance().nextId());
    emailAudit.setFromAddress(preparedEmail.getFrom());
    if(!Utility.isEmpty(preparedEmail.getBcc())) {
      emailAudit.setBcc(preparedEmail.getBcc());
    }
    if(!Utility.isEmpty(preparedEmail.getCc())) {
      emailAudit.setCc(preparedEmail.getCc());
    }
    if(!Utility.isEmpty(preparedEmail.getTo())) {
      emailAudit.setToAddresses(preparedEmail.getTo());
    }
    emailAudit.setBody(preparedEmail.getBody());
    emailAudit.setSubject(preparedEmail.getSubject());
    emailAuditRepository.save(emailAudit);
  }
}
