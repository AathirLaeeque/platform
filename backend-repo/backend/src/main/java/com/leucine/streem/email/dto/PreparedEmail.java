package com.leucine.streem.email.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class PreparedEmail {
  private String from;
  private String[] to;
  private String[] cc;
  private String[] bcc;
  private String subject;
  private String body;
  private List<EmailAttachment> attachments;
  private Map<String, String> attributes;
}
