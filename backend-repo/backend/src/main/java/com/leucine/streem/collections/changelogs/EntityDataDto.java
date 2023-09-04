package com.leucine.streem.collections.changelogs;

import com.leucine.streem.constant.CollectionMisc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EntityDataDto implements Serializable {
  @Serial
  private static final long serialVersionUID = -2725327909733519849L;
  private String entityId;
  private String displayName;
  private String collection;
  private String externalId;
  private String input;
  private CollectionMisc.ChangeLogInputType inputType;
  private String entityObjectTypeId;
}
