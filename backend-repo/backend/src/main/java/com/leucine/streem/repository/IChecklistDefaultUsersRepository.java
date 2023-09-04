package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.ChecklistDefaultUsers;
import com.leucine.streem.model.Task;
import com.leucine.streem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface IChecklistDefaultUsersRepository extends JpaRepository<ChecklistDefaultUsers, Long> {
  @Query(Queries.GET_CHECKLIST_DEFAULT_USER_IDS_BY_CHECKLIST_ID_TASK_ID)
  Set<Long> findUserIdsByChecklistIdAndTaskId(@Param("checklistId") Long checklistId, @Param("taskId") Long taskId);

  void deleteByTaskAndUser(Task task, User user);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UNASSIGN_DEFAULT_USERS_BY_CHECKLISTID_AND_TASKID, nativeQuery = true)
  void unassignUsersByChecklistIdAndTaskIds(@Param("userIds") Set<Long> userIds, @Param("checklistId") Long checklistId, @Param("taskIds") Set<Long> taskIds);

  @Query(Queries.GET_DEFAULT_USERS_TASK_BY_CHECKLIST_ID)
  Set<Long> findTaskIdsByChecklistId(@Param("checklistId") Long checklistId);

  @Query(Queries.GET_CHECKLIST_DEFAULT_USER_IDS_BY_CHECKLIST_ID)
  Set<Long> findUserIdsByChecklistIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("facilityId") Long facilityId);

  @Query(Queries.GET_TASK_IDS_BY_CHECKLIST_ID_AND_USER_ID)
  Set<String> findTaskIdsByChecklistIdAndUserIdAndFacilityId(@Param("checklistId") Long checklistId, @Param("userId") Long userId, @Param("facilityId") Long facilityId);

  List<ChecklistDefaultUsers> findByChecklistId(Long checklistId);
}
