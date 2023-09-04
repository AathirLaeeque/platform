package com.leucine.streem.migration.properties.dto;

import lombok.Data;

@Data
public class FacilityUseCasePropertyMappingDto {
  private Long id;
  private Long facilityId;
  private Long useCaseId;
  private Long propertiesId;
  private String labelAlias;
  private String placeHolderAlias;
  private Integer orderTree;
  private boolean isMandatory;
  private Long createdBy;
  private Long modifiedBy;
  private Long createdAt;
  private Long modifiedAt;
}
