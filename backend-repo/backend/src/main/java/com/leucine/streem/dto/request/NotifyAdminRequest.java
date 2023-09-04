package com.leucine.streem.dto.request;

import lombok.Data;

@Data
public class NotifyAdminRequest {
  String token;
  String purpose;
}
