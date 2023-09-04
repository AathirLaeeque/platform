package com.leucine.streem.repository;

import com.leucine.streem.collections.JobLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface IJobLogRepository extends MongoRepository<JobLog, String> {

    List<JobLog> findByChecklistIdAndFacilityId(String checklistId, String facilityId);
}
