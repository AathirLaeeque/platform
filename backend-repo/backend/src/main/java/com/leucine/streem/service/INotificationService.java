package com.leucine.streem.service;

import java.io.IOException;
import java.util.Set;

public interface INotificationService {

  void notifyAssignedUsers(Set<Long> assignIds, Long jobId, Long organisationId);

  void notifyUnassignedUsers(Set<Long> unassignIds, Long organisationId);

  void notifyAllShouldBeParameterReviewersForApproval(Long jobId, Long organisationId) throws IOException;

  void notifyChecklistCollaborators(Set<Long> userIds, String template, String subject, Long checklistId, Long organisationId);

  void notifyAuthors(Set<Long> ids, Long checklistId, Long organisationId);
}
