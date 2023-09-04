package com.leucine.streem.dto.mapper;

import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.model.*;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper(uses = {IChecklistPropertyValuesMapper.class, IStageMapper.class, IAuditMapper.class,
  IChecklistCollaboratorMapper.class, IChecklistCollaboratorCommentMapper.class})
public interface IChecklistMapper extends IBaseMapper<ChecklistDto, Checklist> {

  @Named("toCommentsDto")
  static List<CollaboratorCommentDto> toCommentsDto(Set<ChecklistCollaboratorMapping> collaborators) {
    List<CollaboratorCommentDto> comments = new ArrayList<>();
    collaborators.forEach(crm -> {
      User user = crm.getUser();
      UserAuditDto createdBy = UserAuditDto.builder().id(user.getIdAsString()).employeeId(user.getEmployeeId()).firstName(user.getFirstName()).lastName(user.getLastName()).build();
      crm.getComments().forEach(c -> {
        CollaboratorCommentDto collaboratorCommentDto = new CollaboratorCommentDto();
        collaboratorCommentDto.setId(c.getIdAsString());
        collaboratorCommentDto.setComments(c.getComments());
        collaboratorCommentDto.setState(c.getChecklistCollaboratorMapping().getState());
        collaboratorCommentDto.setCommentedAt(c.getCreatedAt());
        collaboratorCommentDto.setModifiedAt(c.getModifiedAt());
        collaboratorCommentDto.setPhase(c.getChecklistCollaboratorMapping().getPhase());

        collaboratorCommentDto.setCommentedBy(createdBy);
        comments.add(collaboratorCommentDto);
      });
    });
    return comments;
  }

  @Override
  @Mapping(source = "checklistPropertyValues", target = "properties")
  @Mapping(source = "modifiedAt", target = "audit.modifiedAt")
  @Mapping(source = "modifiedBy", target = "audit.modifiedBy")
  @Mapping(source = "createdAt", target = "audit.createdAt")
  @Mapping(source = "createdBy", target = "audit.createdBy")
  @Mapping(source = "version.version", target = "versionNumber")
  @Mapping(source = "collaborators", target = "collaborators")
  @Mapping(source = "collaborators", target = "comments", qualifiedByName = "toCommentsDto")
  @Mapping(source = "reviewCycle", target = "phase")
  @Mapping(source = "relations", target = "relations", qualifiedByName = "toRelationsDto")
  ChecklistDto toDto(Checklist checklist);

  @Named("toRelationsDto")
  static List<RelationDto> toRelationsDto(Set<Relation> relations) {
//    TODO: use relation mapper
    List<RelationDto> relationsDto = new ArrayList<>();
    relations.forEach(r -> {
      RelationDto relationDto = new RelationDto();
      relationDto.setId(r.getIdAsString());
      relationDto.setExternalId(r.getExternalId());
      relationDto.setVariables(r.getVariables());
      relationDto.setObjectTypeId(r.getObjectTypeId());
      relationDto.setOrderTree(r.getOrderTree());
      relationDto.setDisplayName(r.getDisplayName());
      relationDto.setIsMandatory(r.isMandatory());
      RelationTargetDto relationTargetDto = new RelationTargetDto();
      relationTargetDto.setCollection(r.getCollection());
      relationTargetDto.setUrlPath(r.getUrlPath());
      relationTargetDto.setCardinality(r.getCardinality().name());
      relationDto.setTarget(relationTargetDto);

      relationsDto.add(relationDto);
    });
    return relationsDto;
  }

  List<ChecklistPartialDto> toPartialDto(List<Checklist> checklists);

  @Mapping(source = "checklistPropertyValues", target = "properties", qualifiedByName = "toPropertyValueList")
  @Mapping(target = "stages", qualifiedByName = "toStageDtoList")
  ChecklistJobDto toChecklistJobDto(Checklist checklist, @Context Map<Long, ParameterValue> parameterValueMap,
                                    @Context Map<Long, TaskExecution> taskExecutionMap,
                                    @Context Map<Long, TempParameterValue> tempParameterValueMap,
                                    @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
                                    @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf
  );

  @Mapping(source = "checklistPropertyValues", target = "properties", qualifiedByName = "toPropertyValueList")
  ChecklistInfoJobReportDto toChecklistInfoJobReportDto(Checklist checklist);

  @Mapping(source = "checklistPropertyValues", target = "properties", qualifiedByName = "toPropertyValueList")
  @Mapping(source = "version.version", target = "version")
  @Mapping(source = "createdBy", target = "audit.createdBy")
  @Mapping(source = "relations", target = "relations", qualifiedByName = "toRelationsDto")
  ChecklistPartialDto toPartialDto(Checklist checklist);

  @Mapping(source = "reviewCycle", target = "phase")
  ChecklistBasicDto toChecklistBasicDto(Checklist checklist);

  @Mapping(source = "checklist.id", target = "checklist.id")
  @Mapping(source = "checklist.name", target = "checklist.name")
  @Mapping(source = "checklist.state", target = "checklist.state")
  @Mapping(source = "checklist.reviewCycle", target = "checklist.phase")
  @Mapping(source = "checklistCollaboratorMappings", target = "collaborators")
  ChecklistReviewDto toChecklistReviewDto(Checklist checklist, List<ChecklistCollaboratorMapping> checklistCollaboratorMappings);

  @Mapping(source = "checklist.id", target = "checklist.id")
  @Mapping(source = "checklist.state", target = "checklist.state")
  @Mapping(source = "checklist.reviewCycle", target = "checklist.phase")
  @Mapping(source = "checklistCollaboratorMappings", target = "collaborators")
  @Mapping(source = "checklistCollaboratorComments", target = "comment")
  ChecklistCommentDto toChecklistCommentDto(Checklist checklist, ChecklistCollaboratorComments checklistCollaboratorComments, List<ChecklistCollaboratorMapping> checklistCollaboratorMappings);

}
