package com.leucine.streem.repository;

import com.leucine.streem.dto.projection.JobLogMigrationParameterValueMediaMapping;
import com.leucine.streem.model.ParameterValueMediaMapping;
import com.leucine.streem.model.compositekey.ParameterValueMediaCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IParameterValueMediaRepository extends JpaRepository<ParameterValueMediaMapping, ParameterValueMediaCompositeKey> {

  @Query(value = """
    select m.type as type, m.description as description, m.relative_path as relativePath, m.filename as filename, m.name as name
    from parameter_value_media_mapping
             inner join public.medias m on m.id = parameter_value_media_mapping.medias_id
    where parameter_values_id = :parameterValueId
    """, nativeQuery = true)
  List<JobLogMigrationParameterValueMediaMapping> findMediaByParameterValueId(@Param("parameterValueId") Long parameterValueId);
}
