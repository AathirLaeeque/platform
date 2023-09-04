package com.leucine.streem.email.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Getter
public class EmailRequest {
  private final String templateName;
  private final String from;
  private final Set<String> to;
  private final Set<String> cc;
  private final Set<String> bcc;
  private final String subject;
  private final List<EmailAttachment> attachments;
  private final Map<String, String> attributes;
}

