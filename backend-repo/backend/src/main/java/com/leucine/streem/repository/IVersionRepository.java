package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public interface IVersionRepository extends JpaRepository<Version, Long>, JpaSpecificationExecutor<Version> {
  List<Version> findAllByAncestorOrderByVersionDesc(Long ancestor);

  @Query(value = Queries.GET_RECENT_VERSION_BY_ANCESTOR)
  Integer findRecentVersionByAncestor(@Param("ancestor") Long ancestor);

  @Modifying(clearAutomatically = true)
  @Query(value = Queries.UPDATE_DEPRECATE_VERSION_BY_PARENT)
  void deprecateVersion(@Param("deprecatedAt") Long deprecatedAt, @Param("parent") Long parent);

  @Query(value = Queries.GET_PROTOTYPE_CHECKLIST_ID_BY_ANCESTOR)
  List<Long> findPrototypeChecklistIdsByAncestor(@Param("ancestor") Long ancestor);
}
