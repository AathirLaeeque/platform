package com.leucine.streem.service.impl;

import com.leucine.commons.id.IdGenerator;
import com.leucine.streem.constant.State;
import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.response.ErrorCode;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.Checklist;
import com.leucine.streem.model.User;
import com.leucine.streem.model.Version;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.repository.IChecklistRepository;
import com.leucine.streem.repository.IUserRepository;
import com.leucine.streem.repository.IVersionRepository;
import com.leucine.streem.service.IVersionService;
import com.leucine.streem.util.DateTimeUtils;
import com.leucine.streem.util.Utility;
import com.leucine.streem.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VersionService implements IVersionService {
  private final IChecklistRepository checklistRepository;
  private final IUserRepository userRepository;
  private final IVersionRepository versionRepository;

  /**
   * use method to publish the created version
   * this method will set the version number, if inherited from a parent the version count will increase by 1
   * @param existingVersion version that needs to be published
   * @return the created version
   */
  @Transactional
  @Override
  public Version publishVersion(Version existingVersion) {
    try {
      int versionNumber = 0;
      Long versionedAt = null;
      // Newly created entity
      // update the version number of the entity
      Long ancestor = existingVersion.getAncestor();
      Long parent = existingVersion.getParent();
      if (Utility.isNull(parent)) {
        versionNumber = 1;
        versionedAt = DateTimeUtils.now();
        existingVersion.setVersion(versionNumber);
        existingVersion.setVersionedAt(versionedAt);
        versionRepository.save(existingVersion);

        return existingVersion;
      } else {
        /*
          parent is present. i.e. This is a revised entity.
          - fetch the MAX version so far using its the ancestor.
          - increment the version.
          - deprecate the previous version i.e. Parent
          - mark the parent's state as STALE.
         */
        Integer recentVersion = versionRepository.findRecentVersionByAncestor(ancestor);
        versionNumber = recentVersion + 1;
        versionedAt = DateTimeUtils.now();
        existingVersion.setVersion(versionNumber);
        existingVersion.setVersionedAt(versionedAt);
        versionRepository.save(existingVersion);

        versionRepository.deprecateVersion(DateTimeUtils.now(), parent);

        return existingVersion;
      }
    } catch (Exception e) {
      log.error("[generateVersion] Error generating version", e);
      throw e;
    }
  }

  @Override
  @Transactional
  public Version createNewVersionFromParent(Long entityId, Type.EntityType entityType, Version parentVersion, Long parentEntityId) {
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.getReferenceById(principalUser.getId());
    Version version = new Version();
    version.setType(entityType)
      .setAncestor(entityId)
      .setSelf(entityId)
      .setCreatedBy(user)
      .setModifiedBy(user)
      .setId(IdGenerator.getInstance().nextId());
    // maintain the ancestor and write the parent for this checklist
    version.setAncestor(parentVersion.getAncestor());
    version.setParent(parentEntityId);

    return versionRepository.save(version);
  }

  @Override
  public void validateForChecklistRevision(Checklist parentChecklist) throws StreemException {
    if (!parentChecklist.getState().equals(State.Checklist.PUBLISHED)) {
      ValidationUtils.invalidate(parentChecklist.getId(), ErrorCode.CANNOT_START_REVISION_FROM_NON_PUBLISHED_PROCESS);
    }
    // at any given point only a single instance of non-published prototype can exist (in the entire ancestor tree)
    List<Long> prototypeChecklistIds = versionRepository.findPrototypeChecklistIdsByAncestor(parentChecklist.getVersion().getAncestor());

    List<Checklist> checklists = checklistRepository.findAllById(prototypeChecklistIds);
    List<Checklist> unarchivedChecklists = checklists.stream().filter(c -> !c.isArchived()).collect(Collectors.toList());
    if (!Utility.isEmpty(unarchivedChecklists)) {
      ValidationUtils.invalidate(unarchivedChecklists.get(0).getId(), ErrorCode.REVISION_ALREADY_BEING_BUILT);
    }

  }

  /**
   * Use method to initialize a new version
   * @param entityId entity id for which the version needs to be created
   * @param entityType entity type of the versioned entity
   * @param principalUserEntity user who generates the version
   * @return created version
   */
  @Override
  public Version createNewVersion(Long entityId, Type.EntityType entityType, User principalUserEntity) {
    var version = new Version();
    version.setType(entityType)
      .setAncestor(entityId)
      .setParent(null)
      .setSelf(entityId)
      .setCreatedBy(principalUserEntity)
      .setModifiedBy(principalUserEntity)
      .setId(IdGenerator.getInstance().nextId());
    version = versionRepository.save(version);

    return version;
  }

  /**
   * method to find all ancestor versions
   * @param ancestor ancestor id
   * @return list of all the versions
   */
  @Override
  public List<Version> findAllByAncestor(Long ancestor) {
    log.info("[findAllByAncestor] request to find all versions by ancestor: {}", ancestor);
    return versionRepository.findAllByAncestorOrderByVersionDesc(ancestor);
  }
}

