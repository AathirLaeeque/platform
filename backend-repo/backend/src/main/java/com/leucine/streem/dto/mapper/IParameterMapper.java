package com.leucine.streem.dto.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leucine.streem.config.MediaConfig;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.mapper.helper.IBaseMapper;
import com.leucine.streem.dto.request.ParameterCreateRequest;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.parameter.MaterialParameter;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Mapper(uses = {IParameterVerificationMapper.class, IUserMapper.class}, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class IParameterMapper implements IBaseMapper<ParameterDto, Parameter>, IAuditMapper {
  @Autowired
  protected MediaConfig mediaConfig;

  @Autowired
  IParameterVerificationMapper parameterVerificationMapper;

  MediaDto toMediaDto(Media media) {
    var mediaDto = new MediaDto();
    mediaDto.setId(media.getIdAsString());
    mediaDto.setType(media.getType());
    mediaDto.setName(media.getName());
    mediaDto.setDescription(media.getDescription());
    mediaDto.setLink(mediaConfig.getCdn() + java.io.File.separator + media.getRelativePath() + java.io.File.separator + media.getFilename());
    mediaDto.setArchived(media.isArchived());
    mediaDto.setFilename(media.getFilename());
    return mediaDto;
  }

  public abstract Parameter toEntity(ParameterCreateRequest parameterCreateRequest);

  public abstract ParameterInfoDto toBasicDto(Parameter parameter);

  public abstract List<ParameterInfoDto> toBasicDto(List<Parameter> parameters);

  MaterialParameter toMaterialParameter(ParameterMediaMapping parameterMediaMapping, MaterialMediaDto materialMediaDto) {
    Media media = parameterMediaMapping.getMedia();
    MaterialParameter materialParameter = new MaterialParameter();
    materialParameter.setName(media.getName());
    materialParameter.setQuantity(materialMediaDto.getQuantity());
    materialParameter.setOriginalFilename(media.getOriginalFilename());
    materialParameter.setId(materialMediaDto.getId());
    materialParameter.setMediaId(media.getIdAsString());
    materialParameter.setType(media.getType());
    materialParameter.setDescription(media.getDescription());
    materialParameter.setLink(mediaConfig.getCdn() + java.io.File.separator + media.getRelativePath() + java.io.File.separator + media.getFilename());
    materialParameter.setFilename(media.getFilename());
    return materialParameter;
  }

  public abstract TempParameterDto toTempParameterDto(Parameter parameter);

  @Named(value = "toParameterDto")
  public abstract ParameterDto toDto(Parameter parameter,
                                     @Context Map<Long, ParameterValue> parameterValueMap,
                                     @Context Map<Long, TaskExecution> taskExecutionMap,
                                     @Context Map<Long, TempParameterValue> tempParameterValueMap,
                                     @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
                                     @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf);

  @Named(value = "toParameterDtoList")
  @IterableMapping(qualifiedByName = "toParameterDto")
  public abstract List<ParameterDto> toDto(Set<Parameter> parameters,
                                           @Context Map<Long, ParameterValue> parameterValueMap,
                                           @Context Map<Long, TaskExecution> taskExecutionMap,
                                           @Context Map<Long, TempParameterValue> tempParameterValueMap,
                                           @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
                                           @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf);

  @AfterMapping
  public void setParameterValues(Parameter parameter, @MappingTarget ParameterDto parameterDto) {
    if(Type.Parameter.MATERIAL.equals(parameter.getType()) && !Utility.isEmpty(parameter.getData().toString())) {
      try {
        parameterDto.setData(JsonUtils.valueToNode(getMaterialParameters(parameter)));
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  @AfterMapping
  public void setParameterValues(Parameter parameter, @MappingTarget ParameterDto parameterDto,
                                 @Context Map<Long, ParameterValue> parameterValueMap,
                                 @Context Map<Long, TaskExecution> taskExecutionMap,
                                 @Context Map<Long, TempParameterValue> tempParameterValueMap,
                                 @Context Map<Long, List<TaskPauseReasonOrComment>> pauseReasonOrCommentMap,
                                 @Context Map<Long, List<ParameterVerification>> parameterVerificationMapPeerAndSelf
  ) {
    ParameterValueDto parameterValueDto = new ParameterValueDto();
    ParameterValue parameterValue = parameterValueMap.get(parameter.getId());
    //Logic for error correction

    if(Type.Parameter.MATERIAL.equals(parameterDto.getType()) && !Utility.isEmpty(parameter.getData())) {
      try {
        parameterDto.setData(JsonUtils.valueToNode(getMaterialParameters(parameter)));
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    if (null != parameterValue) {
      parameterValueDto.setId(parameterValue.getIdAsString());
      parameterValueDto.setState(parameterValue.getState());
      List<ParameterVerification> parameterVerifications = parameterVerificationMapPeerAndSelf.get(parameterValue.getId());
      List<ParameterVerificationDto> parameterVerificationDtos = new ArrayList<>();
      if (!Utility.isEmpty(parameterVerifications)) {
        for (ParameterVerification parameterVerification : parameterVerifications) {
          ParameterVerificationDto parameterVerificationDto = parameterVerificationMapper.toDto(parameterVerification);
          parameterVerificationDtos.add(parameterVerificationDto);
        }
        parameterValueDto.setParameterVerifications(parameterVerificationDtos);
      }
      if (!State.ParameterExecution.ENABLED_FOR_CORRECTION.equals(parameterValue.getState())) {
        parameterValueDto.setChoices(parameterValue.getChoices());
        parameterValueDto.setValue(parameterValue.getValue());
        parameterValueDto.setReason(parameterValue.getReason());
        updateParameterApprovalDto(parameterValueDto, parameterValue.getParameterValueApproval());
        List<MediaDto> mediaDtos = new ArrayList<>();
        List<ParameterValueMediaMapping> parameterValueMediaMappings = parameterValue.getMedias();
        if (null != parameterValueMediaMappings) {
          for (ParameterValueMediaMapping actMedia : parameterValueMediaMappings) {
            if (!actMedia.isArchived()) {
              mediaDtos.add(toMediaDto(actMedia.getMedia()));
            }
          }
          parameterValueDto.setMedias(mediaDtos);
        }
        parameterValueDto.setState(parameterValue.getState());
        parameterValueDto.setHidden(parameterValue.isHidden());
        parameterValueDto.setAudit(IAuditMapper.createAuditDto(parameterValue.getModifiedBy(), parameterValue.getModifiedAt()));
        parameterDto.setResponse(parameterValueDto);
      } else {
        TempParameterValue tempParameterValue = tempParameterValueMap.get(parameter.getId());
        parameterValueDto.setChoices(tempParameterValue.getChoices());
        parameterValueDto.setValue(tempParameterValue.getValue());
        parameterValueDto.setReason(tempParameterValue.getReason());
        List<MediaDto> mediaDtos = new ArrayList<>();
        List<TempParameterValueMediaMapping> parameterValueMedias = tempParameterValue.getMedias();
        if (null != parameterValueMedias) {
          for (TempParameterValueMediaMapping actMedia : parameterValueMedias) {
            if (!actMedia.isArchived()) {
              mediaDtos.add(toMediaDto(actMedia.getMedia()));
            }
          }
          parameterValueDto.setMedias(mediaDtos);
        }
        parameterValueDto.setState(tempParameterValue.getState());
        parameterValueDto.setHidden(parameterValue.isHidden());
        parameterValueDto.setAudit(IAuditMapper.createAuditDto(parameterValue.getModifiedBy(), parameterValue.getModifiedAt()));
        parameterDto.setResponse(parameterValueDto);
      }
    }
  }

  private void updateParameterApprovalDto(ParameterValueDto parameterValueDto, ParameterValueApproval parameterValueApproval) {
    if (parameterValueApproval != null) {
      ParameterValueApprovalDto parameterValueApprovalDto = new ParameterValueApprovalDto();
      UserAuditDto userAuditDto = new UserAuditDto();
      if (null != parameterValueApproval.getUser()) {
        userAuditDto.setId(parameterValueApproval.getUser().getIdAsString());
        userAuditDto.setEmployeeId(parameterValueApproval.getUser().getEmployeeId());
        userAuditDto.setFirstName(parameterValueApproval.getUser().getFirstName());
        userAuditDto.setLastName(parameterValueApproval.getUser().getLastName());
      }
      parameterValueApprovalDto.setApprover(userAuditDto);
      parameterValueApprovalDto.setId(parameterValueApproval.getIdAsString());
      parameterValueApprovalDto.setState(parameterValueApproval.getState());
      parameterValueApprovalDto.setCreatedAt(parameterValueApproval.getCreatedAt());
      parameterValueDto.setParameterValueApprovalDto(parameterValueApprovalDto);
    }
  }

  private List<MaterialParameter> getMaterialParameters(Parameter parameter) throws JsonProcessingException {
    List<MaterialMediaDto> materialMediaDtos = JsonUtils.readValue(parameter.getData().toString(),
            new TypeReference<List<MaterialMediaDto>>(){});
    List<ParameterMediaMapping> parameterMediaMappings = parameter.getMedias();
    Map<Long, ParameterMediaMapping> parameterMediaMappingMap = parameterMediaMappings.stream().collect(Collectors.toMap(am -> am.getMedia().getId(), Function.identity()));

    List<MaterialParameter> materialParameters = new ArrayList<>();

    for(MaterialMediaDto materialMediaDto : materialMediaDtos) {
      if(!Utility.isEmpty(materialMediaDto.getMediaId()) && !parameterMediaMappingMap.get(Long.valueOf(materialMediaDto.getMediaId())).isArchived()) {
        materialParameters.add(toMaterialParameter(parameterMediaMappingMap.get(Long.valueOf(materialMediaDto.getMediaId())), materialMediaDto));
      } else {
        MaterialParameter materialParameter = new MaterialParameter();
        materialParameter.setName(materialMediaDto.getName());
        materialParameter.setQuantity(materialMediaDto.getQuantity());
        materialParameter.setId(materialMediaDto.getId());
        materialParameters.add(materialParameter);
      }
    }
    return materialParameters;
  }

  public List<MediaDto> getMedias(List<ParameterValueMediaMapping> parameterValueMediaMappings) {
    List<MediaDto> mediaDtos = new ArrayList<>();
    if (null != parameterValueMediaMappings) {
      for (ParameterValueMediaMapping actMedia : parameterValueMediaMappings) {
        MediaDto mediaDto = toMediaDto(actMedia.getMedia());
        // TODO: this is a workaround, this method is used for audits only do not use this anywhere else without fixing this
        // parameter execution medias get archived in the mapping entity not at the media level
        // to show the right audits we are marking archived true for the medias
        if (actMedia.isArchived()) {
          mediaDto.setArchived(true);
        }
        mediaDtos.add(mediaDto);
      }
    }
    return mediaDtos;
  }
}
