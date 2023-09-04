package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.projection.ParameterView;
import com.leucine.streem.model.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface IParameterRepository extends JpaRepository<Parameter, Long>, JpaSpecificationExecutor<Parameter> {
  @Query(value = Queries.GET_ALL_INCOMPLETE_PARAMETERS_BY_JOB_ID, nativeQuery = true)
  List<ParameterView> findIncompleteParametersByJobId(@Param("jobId") Long jobId);

  @Query(value = Queries.GET_PARAMETERS_BY_TASK_ID_IN_AND_ORDER_BY_ORDER_TREE)
  List<Parameter> findByTaskIdInOrderByOrderTree(@Param("taskIds") Set<Long> taskIds);

  @Query(value = Queries.GET_ENABLED_PARAMETERS_COUNT_BY_PARAMETER_TYPE_IN_AND_ID_IN)
  Integer getEnabledParametersCountByTypeAndIdIn(@Param("parameterIds") Set<Long> parameterIds, @Param("types") Set<Type.Parameter> types);

  @Query(value = Queries.GET_PARAMETERS_BY_CHECKLIST_ID_AND_TARGET_ENTITY_TYPE)
  List<Parameter> getParametersByChecklistIdAndTargetEntityType(@Param("checklistId") Long checklistId, @Param("targetEntityType") Type.ParameterTargetEntityType targetEntityType);

  @Query(value = Queries.GET_ARCHIVED_PARAMETERS_BY_REFERENCED_PARAMETER_ID, nativeQuery = true)
  List<Parameter> getArchivedParametersByReferencedParameterIds(@Param("referencedParameterIds") List<Long> referencedParameterIds);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = Queries.UPDATE_PARAMETER_TARGET_ENTITY_TYPE_BY_CHECKLIST_ID_AND_TARGET_ENTITY_TYPE)
  void updateParametersTargetEntityType(@Param("checklistId") Long checklistId, @Param("targetEntityType") Type.ParameterTargetEntityType targetEntityType, @Param("updatedTargetEntityType") Type.ParameterTargetEntityType updatedTargetEntityType);

  @Query(value = Queries.GET_PARAMETERS_COUNT_BY_CHECKLIST_ID_AND_PARAMETER_ID_IN_AND_TARGET_ENTITY_TYPE)
  Integer getParametersCountByChecklistIdAndParameterIdInAndTargetEntityType(@Param("checklistId") Long checklistId, @Param("parameterIds") Set<Long> parameterIds, @Param("targetEntityType") Type.ParameterTargetEntityType targetEntityType);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = Queries.UPDATE_PARAMETERS_TARGET_ENTITY_TYPE)
  Integer updateParametersTargetEntityType(@Param("parameterIds") Set<Long> parameterIds, @Param("targetEntityType") Type.ParameterTargetEntityType targetEntityType);

  @Transactional
  @Modifying
  @Query(value = Queries.UPDATE_PARAMETER_ORDER, nativeQuery = true)
  void reorderParameter(@Param("parameterId") Long parameterId, @Param("order") Integer order, @Param("userId") Long userId, @Param("modifiedAt") Long modifiedAt);

  @Transactional
  @Modifying
  @Query(value = Queries.UPDATE_PARAMETER_AUTO_INITIALIZE_BY_PARAMETER_ID, nativeQuery = true)
  void updateParameterAutoInitializeById(@Param("parameterId") Long parameterId, @Param("autoInitialize") String autoInitialize);

  @Transactional
  @Modifying
  @Query(value = Queries.UPDATE_PARAMETER_VALIDATION_BY_PARAMETER_ID, nativeQuery = true)
  void updateParameterValidationByParameterId(@Param("parameterId") Long parameterId, @Param("validations") String validations);

  @Transactional
  @Modifying
  @Query(value = Queries.UPDATE_PARAMETER_RULES_BY_PARAMETER_ID, nativeQuery = true)
  void updateParameterRulesById(@Param("parameterId") Long parameterId, @Param("rules") String rules);

  @Transactional
  @Modifying
  @Query(value = Queries.UPDATE_PARAMETER_DATA_BY_PARAMETER_ID, nativeQuery = true)
  void updateParameterDataById(@Param("parameterId") Long parameterId, @Param("data") String data);

  @Modifying
  @Transactional
  @Query(value = Queries.UPDATE_VISIBILITY_OF_PARAMETERS)
  void updateParameterVisibility(@Param("hiddenParameterIds") Set<Long> hiddenParameterIds, @Param("visibleParameterIds") Set<Long> visibleParameterIds);

  @Query(value = Queries.IS_LINKED_PARAMETER_EXISTS_BY_PARAMETER_ID, nativeQuery = true)
  boolean isLinkedParameterExistsByParameterId(@Param("checklistId") Long checklistId, @Param("parameterId") String parameterId);

  @Query(value = Queries.GET_ALL_CHECKLIST_IDS_BY_TARGET_ENTITY_TYPE_AND_OBJECT_TYPE_IN_DATA, nativeQuery = true)
  Set<Long> getChecklistIdsByTargetEntityTypeAndObjectTypeInData(@Param("targetEntityType") String targetEntityType, @Param("objectTypeId") String objectTypeId);

  List<Parameter> findByChecklistIdAndArchived(Long checklistId, boolean isArchived);

}
