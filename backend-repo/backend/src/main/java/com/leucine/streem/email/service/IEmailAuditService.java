package com.leucine.streem.email.service;

import com.leucine.streem.email.dto.PreparedEmail;

public interface IEmailAuditService {
  void writeToAudit(PreparedEmail preparedEmail);
}
