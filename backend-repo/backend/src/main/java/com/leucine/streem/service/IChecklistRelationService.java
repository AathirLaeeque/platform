package com.leucine.streem.service;

import com.leucine.streem.collections.partial.PartialEntityObject;
import com.leucine.streem.dto.ParameterRelationPropertyValidationDto;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Parameter;
import com.leucine.streem.model.Checklist;
import com.leucine.streem.model.Job;
import com.leucine.streem.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IChecklistRelationService {
    void checklistRelationService(Map<Long, List<PartialEntityObject>> relationsRequestMap, Checklist checklist, Job job, User principalUserEntity) throws IOException, StreemException, ResourceNotFoundException;
    void validateParameterRelation(Long jobId, Parameter parameter, String value) throws IOException, StreemException, ResourceNotFoundException;
    void validateParameterValueChoice(String objectTypeExternalId, String objectId, List<ParameterRelationPropertyValidationDto> relationValidations) throws StreemException, ResourceNotFoundException;
}
