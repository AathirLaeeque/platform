package com.leucine.streem.collections;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobLogResourceChoice implements Serializable {
  @Serial
  private static final long serialVersionUID = -9019310770617620828L;

  private String objectId;
  private String collection;
  private String objectExternalId;
  private String objectDisplayName;
}
