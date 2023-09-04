package com.leucine.streem.collections;

import com.leucine.streem.constant.CollectionName;
import com.leucine.streem.constant.State;
import com.leucine.streem.dto.UserAuditDto;
import com.leucine.streem.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(CollectionName.JOB_LOGS)
public class JobLog implements Serializable {
  private static final long serialVersionUID = 2716952842611164720L;
  public static final String COMMON_COLUMN_ID = "-1";
  @Id
  private String id;
  private String facilityId;
  private String checklistId;
  private String checklistName;
  private String checklistCode;
  private String code;
  private State.Job state;
  private Long startedAt;
  private Long endedAt;
  private UserAuditDto createdBy;
  private UserAuditDto startedBy;
  private UserAuditDto modifiedBy;
  private UserAuditDto endedBy;
  private Long createdAt;
  private Long modifiedAt;
  private List<JobLogData> logs;
  private Map<String, Object> parameterValues;
}
