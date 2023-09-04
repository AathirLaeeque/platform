package com.leucine.streem.service.impl;

import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.PropertyOption;
import com.leucine.streem.collections.PropertyValue;
import com.leucine.streem.collections.partial.PartialEntityObject;
import com.leucine.streem.dto.*;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.ExceptionType;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.*;
import com.leucine.streem.model.helper.parameter.ResourceParameter;
import com.leucine.streem.repository.IEntityObjectRepository;
import com.leucine.streem.repository.IParameterRepository;
import com.leucine.streem.repository.IParameterValueRepository;
import com.leucine.streem.repository.IRelationValueRepository;
import com.leucine.streem.service.IChecklistRelationService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.JsonUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import com.leucine.streem.validator.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistRelationService implements IChecklistRelationService {
  private final IRelationValueRepository relationValueRepository;
  private final IEntityObjectRepository entityObjectRepository;
  private final IParameterRepository parameterRepository;
  private final IParameterValueRepository parameterValueRepository;

  // TODO Make this method return the formed relation value list let job service set it
  @Override
  @Transactional
  public void checklistRelationService(Map<Long, List<PartialEntityObject>> relationsRequestMap, Checklist checklist, Job job, User principalUserEntity) throws IOException, StreemException, ResourceNotFoundException {
    // TODO Handle cases like relation value not present, object property value not present
    Set<Relation> relations = checklist.getRelations();
    List<RelationValue> relationValuesList = new ArrayList<>();

    for (Relation requiredRelation : relations) {
      if (!Utility.isEmpty(requiredRelation.getValidations())) {
        ChecklistRelationValidationDto relationValidation = JsonUtils.readValue(requiredRelation.getValidations().toString(), ChecklistRelationValidationDto.class);
        List<PartialEntityObject> relationList = relationsRequestMap.get(requiredRelation.getId());

        if (requiredRelation.isMandatory() && Utility.isEmpty(relationList)) {
            ValidationUtils.invalidate(requiredRelation.getId(), ErrorCode.JOB_RELATION_MANDATORY);
        }

        // TODO current scenario if relations not mapped, just ignore everything relations, validations etc
        if (!Utility.isEmpty(relationList)) {
          for (PartialEntityObject partialEntityObject : relationList) {
            EntityObject entityObject = entityObjectRepository.findById(partialEntityObject.getCollection(), partialEntityObject.getId().toString())
              .orElseThrow(() -> new ResourceNotFoundException(partialEntityObject.getId().toString(), ErrorCode.ENTITY_OBJECT_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
            Map<String, PropertyValue> propertyValueMap = entityObject.getProperties().stream().collect(Collectors.toMap(pv -> pv.getId().toString()
              , Function.identity()));

            if (!Utility.isEmpty(relationValidation.getRelationPropertyValidations())) {
              for (ChecklistRelationPropertyValidationDto validation : relationValidation.getRelationPropertyValidations()) {
                PropertyValue propertyValue = propertyValueMap.get(validation.getPropertyId());

                ConstraintValidator validator = null;
                if (Utility.isEmpty(propertyValue)) {
                  ValidationUtils.invalidate(validation.getId(), ErrorCode.PROCESS_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
                }
                String propertyInput = propertyValue.getValue();
                String value = validation.getValue();
                switch (validation.getPropertyInputType()) {
                  case DATE, DATE_TIME ->  {
                    if (Utility.isEmpty(propertyInput)) {
                      ValidationUtils.invalidate(validation.getId(), ErrorCode.PROCESS_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
                    }
                    validator = new DateValidator(DateTimeUtils.now(), Long.valueOf(value), validation.getErrorMessage(), validation.getDateUnit(), validation.getConstraint());
                    validator.validate(propertyInput);
                  }
                  case NUMBER -> {
                    if (Utility.isEmpty(propertyInput)) {
                      ValidationUtils.invalidate(validation.getId(), ErrorCode.PROCESS_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA);
                    }
                    switch (validation.getConstraint()) {
                      case EQ -> {
                        validator = new EqualValueValidator(Double.parseDouble(value), validation.getErrorMessage());
                        validator.validate(propertyInput);
                      }
                      case LT -> {
                        validator = new LessThanValidator(Double.parseDouble(value), validation.getErrorMessage());
                        validator.validate(propertyInput);
                      }
                      case GT -> {
                        validator = new GreaterThanValidator(Double.parseDouble(value), validation.getErrorMessage());
                        validator.validate(propertyInput);
                      }
                      case LTE -> {
                        validator = new LessThanOrEqualValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                        validator.validate(value);
                      }
                      case GTE -> {
                        validator = new GreaterThanOrEqualValidator(Double.parseDouble(value), validation.getErrorMessage());
                        validator.validate(propertyInput);
                      }
                      case NE -> {
                        validator = new NotEqualValueValidator(Double.parseDouble(value), validation.getErrorMessage());
                        validator.validate(propertyInput);
                      }
                    }
                  }
                  case SINGLE_LINE, MULTI_LINE ->  {
                    if (Utility.isEmpty(propertyInput)) {
                      ValidationUtils.invalidate(validation.getId(), ErrorCode.PROCESS_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
                    }
                    switch (validation.getConstraint()) {
                      case EQ -> {
                        validator = new StringEqualValidator(value, validation.getErrorMessage());
                        validator.validate(propertyInput);
                      }
                      case NE -> {
                        validator = new StringNotEqualValidator(value, validation.getErrorMessage());
                        validator.validate(propertyInput);
                      }
                    }
                  }
                  case SINGLE_SELECT -> {
                    List<PropertyOption> choices = propertyValue.getChoices();
                    if (!Utility.isEmpty(choices) && !Utility.isEmpty(validation.getOptions())) {
                      String propertyChoice = choices.get(0).getId().toString();
                      String choice = validation.getOptions().get(0).getId().toString();
                      switch (validation.getConstraint()) {
                        case EQ -> {
                          validator = new StringEqualValidator(choice, validation.getErrorMessage());
                          validator.validate(propertyChoice);
                        }
                        case NE -> {
                          validator = new StringNotEqualValidator(choice, validation.getErrorMessage());
                          validator.validate(propertyChoice);
                        }
                      }
                    } else {
                      ValidationUtils.invalidate(validation.getId(), ErrorCode.PROCESS_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
                    }
                  }
                }
                if (null != validator && !validator.isValid()) {
                  ValidationUtils.invalidate(validation.getId(), ErrorCode.PROCESS_RELATION_PROPERTY_VALIDATION_ERROR, validator.getErrorMessage());
                }
              }
            }

            RelationValue relationValue = new RelationValue();
            relationValue.setRelationId(requiredRelation.getId());
            relationValue.setJobId(job.getId());
            relationValue.setObjectTypeDisplayName(requiredRelation.getDisplayName());
            relationValue.setObjectTypeExternalId(requiredRelation.getExternalId());
            relationValue.setCreatedBy(principalUserEntity);
            relationValue.setModifiedBy(principalUserEntity);
            relationValue.setDisplayName(partialEntityObject.getDisplayName());
            relationValue.setExternalId(partialEntityObject.getExternalId());
            relationValue.setRelationId(requiredRelation.getId());
            relationValue.setCollection(partialEntityObject.getCollection());
            relationValue.setObjectId(partialEntityObject.getId().toString());
            relationValuesList.add(relationValue);
          }
        }
      }
    }

    if (!Utility.isEmpty(relationValuesList)) {
      relationValuesList = relationValueRepository.saveAll(relationValuesList);
    }

    job.setRelationValues(new HashSet<>(relationValuesList));

  }

  @Override
  public void validateParameterRelation(Long jobId, Parameter parameter, String value) throws StreemException, ResourceNotFoundException, IOException {

    if (!Utility.isEmpty(parameter.getValidations())) {
      ParameterRelationValidationDto parameterRelationValidationDto = JsonUtils.readValue(parameter.getValidations().toString(), ParameterRelationValidationDto.class);
      // TODO Refactor
      if (!Utility.isEmpty(parameterRelationValidationDto.getRelationPropertyValidations())) {
        for (ParameterRelationPropertyValidationDto validation : parameterRelationValidationDto.getRelationPropertyValidations()) {
          RelationValue relationValue = relationValueRepository.findByRelationIdAndJobId(Long.parseLong(validation.getRelationId()), jobId);
          // TODO relation is optional how will you tackle ?
          if (!Utility.isNull(relationValue)) {
            EntityObject entityObject = entityObjectRepository.findById(relationValue.getCollection(), relationValue.getObjectId())
              .orElseThrow(() -> new ResourceNotFoundException(relationValue.getObjectId(), ErrorCode.ENTITY_OBJECT_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
            Map<String, PropertyValue> propertyValueMap = entityObject.getProperties().stream().collect(Collectors.toMap(pv -> pv.getId().toString(), Function.identity()));

            PropertyValue propertyValue = propertyValueMap.get(validation.getPropertyId());

            ConstraintValidator validator = null;
            if (Utility.isEmpty(propertyValue)) {
              ValidationUtils.invalidate(validation.getId(), ErrorCode.NUMBER_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
            }
            String propertyInput = propertyValue.getValue();
            // TODO For now this is always number hence this is written like this
            if (Utility.isEmpty(propertyInput)) {
              ValidationUtils.invalidate(validation.getId(), ErrorCode.NUMBER_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
            }
            switch (validation.getConstraint()) {
              case EQ -> {
                validator = new EqualValueValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                validator.validate(value);
              }
              case LT -> {
                validator = new LessThanValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                validator.validate(value);
              }
              case GT -> {
                validator = new GreaterThanValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                validator.validate(value);
              }
              case LTE -> {
                validator = new LessThanOrEqualValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                validator.validate(value);
              }
              case GTE -> {
                validator = new GreaterThanOrEqualValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                validator.validate(value);
              }
              case NE -> {
                validator = new NotEqualValueValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                validator.validate(value);
              }
            }
            if (null != validator && !validator.isValid()) {
              ValidationUtils.invalidate(validation.getId(), ErrorCode.NUMBER_PARAMETER_RELATION_PROPERTY_VALIDATION_ERROR, validator.getErrorMessage());
            }
          }
        }
      }

      if (!Utility.isEmpty(parameterRelationValidationDto.getResourceParameterValidations())) {
        for (ResourceParameterPropertyValidationDto validation : parameterRelationValidationDto.getResourceParameterValidations()) {
          Long parameterId = Long.valueOf(validation.getParameterId());
          ParameterValue parameterValue = parameterValueRepository.findByJobIdAndParameterId(jobId, parameterId);
          Parameter parameterForValidation = parameterRepository.findById(parameterId).get();
          if (null != parameterValue) {
            ResourceParameter resourceParameter = JsonUtils.readValue(parameterForValidation.getData().toString(), ResourceParameter.class);
            List<ResourceParameterChoiceDto> parameterChoices = JsonUtils.jsonToCollectionType(parameterValue.getChoices(), List.class, ResourceParameterChoiceDto.class);
            if (!Utility.isEmpty(parameterChoices)) {
              for (ResourceParameterChoiceDto resourceParameterChoice : parameterChoices) {
                EntityObject entityObject = entityObjectRepository.findById(resourceParameter.getObjectTypeExternalId(), resourceParameterChoice.getObjectId())
                  .orElseThrow(() -> new ResourceNotFoundException(resourceParameterChoice.getObjectId(), ErrorCode.ENTITY_OBJECT_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
                Map<String, PropertyValue> propertyValueMap = entityObject.getProperties().stream().collect(Collectors.toMap(pv -> pv.getId().toString()
                  , Function.identity()));
                PropertyValue propertyValue = propertyValueMap.get(validation.getPropertyId());

                ConstraintValidator validator = null;
                if (Utility.isEmpty(propertyValue)) {
                  ValidationUtils.invalidate(validation.getId(), ErrorCode.NUMBER_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
                }

                String propertyInput = propertyValue.getValue();
                if (Utility.isEmpty(propertyInput)) {
                  ValidationUtils.invalidate(validation.getId(), ErrorCode.NUMBER_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
                }
                switch (validation.getConstraint()) {
                  case EQ -> {
                    validator = new EqualValueValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                    validator.validate(value);
                  }
                  case LT -> {
                    validator = new LessThanValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                    validator.validate(value);
                  }
                  case GT -> {
                    validator = new GreaterThanValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                    validator.validate(value);
                  }
                  case LTE -> {
                    validator = new LessThanOrEqualValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                    validator.validate(value);
                  }
                  case GTE -> {
                    validator = new GreaterThanOrEqualValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                    validator.validate(value);
                  }
                  case NE -> {
                    validator = new NotEqualValueValidator(Double.parseDouble(propertyInput), validation.getErrorMessage());
                    validator.validate(value);
                  }
                }
                if (null != validator && !validator.isValid()) {
                  ValidationUtils.invalidate(validation.getId(), ErrorCode.NUMBER_PARAMETER_RELATION_PROPERTY_VALIDATION_ERROR, validator.getErrorMessage());
                }
              }
            }
          }
        }
      }

      if (!Utility.isEmpty(parameterRelationValidationDto.getCustomValidations())) {
        for (CustomRelationPropertyValidationDto validation : parameterRelationValidationDto.getCustomValidations()) {
          ConstraintValidator validator = null;
          double propertyInput = Double.parseDouble(validation.getValue());
          switch (validation.getConstraint()) {
            case EQ -> {
              validator = new EqualValueValidator(propertyInput, validation.getErrorMessage());
              validator.validate(value);
            }
            case LT -> {
              validator = new LessThanValidator(propertyInput, validation.getErrorMessage());
              validator.validate(value);
            }
            case GT -> {
              validator = new GreaterThanValidator(propertyInput, validation.getErrorMessage());
              validator.validate(value);
            }
            case LTE -> {
              validator = new LessThanOrEqualValidator(propertyInput, validation.getErrorMessage());
              validator.validate(value);
            }
            case GTE -> {
              validator = new GreaterThanOrEqualValidator(propertyInput, validation.getErrorMessage());
              validator.validate(value);
            }
            case NE -> {
              validator = new NotEqualValueValidator(propertyInput, validation.getErrorMessage());
              validator.validate(value);
            }
          }
          if (null != validator && !validator.isValid()) {
            ValidationUtils.invalidate(validation.getId(), ErrorCode.NUMBER_PARAMETER_CUSTOM_VALIDATION_ERROR, validator.getErrorMessage());
          }
        }
      }
    }

  }

  @Override
  public void validateParameterValueChoice(String objectTypeExternalId, String objectId, List<ParameterRelationPropertyValidationDto> relationValidations) throws StreemException, ResourceNotFoundException {
    if (!Utility.isEmpty(relationValidations)) {
      for (ParameterRelationPropertyValidationDto validation : relationValidations) {
        // TODO relation is optional how will you tackle ?
        EntityObject entityObject = entityObjectRepository.findById(objectTypeExternalId, objectId)
          .orElseThrow(() -> new ResourceNotFoundException(objectId, ErrorCode.ENTITY_OBJECT_NOT_FOUND, ExceptionType.ENTITY_NOT_FOUND));
        Map<String, PropertyValue> propertyValueMap = entityObject.getProperties().stream().collect(Collectors.toMap(pv -> pv.getId().toString(), Function.identity()));

        PropertyValue propertyValue = propertyValueMap.get(validation.getPropertyId());

        ConstraintValidator validator = null;
        if (Utility.isEmpty(propertyValue)) {
          ValidationUtils.invalidate(validation.getId(), ErrorCode.RESOURCE_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
        }
        String propertyInput = propertyValue.getValue();
        String value = validation.getValue();
        switch (validation.getPropertyInputType()) {
          case DATE, DATE_TIME ->  {
            if (Utility.isEmpty(propertyInput)) {
              ValidationUtils.invalidate(validation.getId(), ErrorCode.RESOURCE_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
            }
            validator = new DateValidator(Long.valueOf(propertyInput), Long.valueOf(value), validation.getErrorMessage(), validation.getDateUnit(), validation.getConstraint());
            validator.validate(String.valueOf(DateTimeUtils.now()));
          }
          case NUMBER -> {
            if (Utility.isEmpty(propertyInput)) {
              ValidationUtils.invalidate(validation.getId(), ErrorCode.RESOURCE_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
            }
            var validationValue = Double.parseDouble(value);
            switch (validation.getConstraint()) {
              case EQ -> {
                validator = new EqualValueValidator(validationValue, validation.getErrorMessage());
                validator.validate(propertyInput);
              }
              case LT -> {
                validator = new LessThanValidator(validationValue, validation.getErrorMessage());
                validator.validate(propertyInput);
              }
              case GT -> {
                validator = new GreaterThanValidator(validationValue, validation.getErrorMessage());
                validator.validate(propertyInput);
              }
              case LTE -> {
                validator = new LessThanOrEqualValidator(validationValue, validation.getErrorMessage());
                validator.validate(propertyInput);
              }
              case GTE -> {
                validator = new GreaterThanOrEqualValidator(validationValue, validation.getErrorMessage());
                validator.validate(propertyInput);
              }
              case NE -> {
                validator = new NotEqualValueValidator(validationValue, validation.getErrorMessage());
                validator.validate(propertyInput);
              }
            }
          }
          case SINGLE_LINE, MULTI_LINE ->  {
            switch (validation.getConstraint()) {
              case EQ -> {
                validator = new StringEqualValidator(value, validation.getErrorMessage());
                validator.validate(propertyInput);
              }
              case NE -> {
                validator = new StringNotEqualValidator(value, validation.getErrorMessage());
                validator.validate(propertyInput);
              }
            }
          }
          case SINGLE_SELECT -> {
            List<PropertyOption> choices = propertyValue.getChoices();
            if (!Utility.isEmpty(choices) && !Utility.isEmpty(validation.getOptions())) {
              String propertyChoice = choices.get(0).getId().toString();
              String choice = validation.getOptions().get(0).getId().toString();
              switch (validation.getConstraint()) {
                case EQ -> {
                  validator = new StringEqualValidator(propertyChoice, validation.getErrorMessage());
                  validator.validate(choice);
                }
                case NE -> {
                  validator = new StringNotEqualValidator(value, validation.getErrorMessage());
                  validator.validate(choice);
                }
              }
            } else {
              ValidationUtils.invalidate(validation.getId(), ErrorCode.RESOURCE_PARAMETER_RELATION_PROPERTY_VALIDATION_COULD_NOT_RUN_MISSING_DATA, validation.getErrorMessage());
            }
          }
        }
        if (null != validator && !validator.isValid()) {
          ValidationUtils.invalidate(validation.getId(), ErrorCode.RESOURCE_PARAMETER_RELATION_PROPERTY_VALIDATION_ERROR, validator.getErrorMessage());
        }
      }
    }
  }
}
