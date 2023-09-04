package com.leucine.streem.repository;

import com.leucine.streem.constant.Queries;
import com.leucine.streem.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IMediaRepository extends JpaRepository<Media, Long>, JpaSpecificationExecutor<Media> {
  @Query(value = Queries.GET_ALL_MEDIAS_WHERE_ID_IN)
  List<Media> findAll(@Param("mediaIds") Set<Long> mediaIds);
}
