package com.leucine.streem.collections;

import com.leucine.streem.constant.CollectionName;
import com.leucine.streem.constant.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Document(CollectionName.CUSTOM_VIEWS)
public class CustomView implements Serializable {
  private static final long serialVersionUID = -5613455151318182443L;
  @Id
  private String id;
  private String facilityId;
  private String useCaseId;
  private Type.ConfigurableViewTargetType targetType;
  private String processId;
  private String label;
  private List<CustomViewColumn> columns;
  private List<CustomViewFilter> filters;
  private boolean archived = false;
  private Long createdAt;
  private Long modifiedAt;
  private String createdBy;
  private String modifiedBy;
}
