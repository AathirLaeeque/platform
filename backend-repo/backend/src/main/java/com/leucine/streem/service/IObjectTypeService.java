package com.leucine.streem.service;

import com.leucine.streem.collections.ObjectType;
import com.leucine.streem.collections.Property;
import com.leucine.streem.collections.Relation;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.request.ObjectTypeCreateRequest;
import com.leucine.streem.dto.request.ObjectTypePropertyCreateRequest;
import com.leucine.streem.dto.request.ObjectTypeRelationCreateRequest;
import com.leucine.streem.dto.request.ObjectTypeUpdateRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IObjectTypeService {
    ObjectType findById(String id) throws ResourceNotFoundException;
    Page<ObjectType> findAll(int usageStatus, String name, Pageable pageable) throws StreemException;
    BasicDto createObjectType(ObjectTypeCreateRequest objectTypeCreateRequests) throws StreemException;
    BasicDto addObjectTypeProperty(String objectTypeId, ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest) throws ResourceNotFoundException, StreemException;
    BasicDto addObjectTypeRelation(String objectTypeId, ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest) throws ResourceNotFoundException, StreemException;
    BasicDto editObjectType(String objectTypeId, ObjectTypeUpdateRequest objectTypeUpdateRequest) throws ResourceNotFoundException, StreemException;
    BasicDto editObjectTypeProperty(String objectTypeId, String propertyId, ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest) throws ResourceNotFoundException, StreemException;
    BasicDto editObjectTypeRelation(String objectTypeId, String relationId, ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest) throws ResourceNotFoundException, StreemException;
    BasicDto archiveObjectTypeProperty(String objectTypeId, String propertyId) throws ResourceNotFoundException, StreemException;
    BasicDto archiveObjectTypeRelation(String objectTypeId, String relationId) throws ResourceNotFoundException;
    Page<Property> getAllObjectTypeProperties(String objectTypeId, int usageStatus, String displayName, Pageable pageable) throws ResourceNotFoundException, StreemException;
    Page<Relation> getAllObjectTypeRelations(String objectTypeId, int usageStatus, String displayName, Pageable pageable) throws ResourceNotFoundException, StreemException;

}
