package com.leucine.streem.service;

import com.leucine.streem.constant.Type;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Checklist;
import com.leucine.streem.model.User;
import com.leucine.streem.model.Version;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface IVersionService {

  Version createNewVersionFromParent(Long entityId, Type.EntityType entityType, @NotNull Version parentVersion, Long parentEntityId);

  Version publishVersion(Version existingVersion);

  void validateForChecklistRevision(Checklist parent) throws StreemException;

  Version createNewVersion(Long entityId, Type.EntityType entityType, User principalUserEntity);

  List<Version> findAllByAncestor(Long ancestor);

}
