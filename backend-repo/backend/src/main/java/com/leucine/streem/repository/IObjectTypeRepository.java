package com.leucine.streem.repository;

import com.leucine.streem.collections.ObjectType;
import com.leucine.streem.collections.Property;
import com.leucine.streem.collections.Relation;
import com.leucine.streem.exception.StreemException;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface IObjectTypeRepository {
  List<ObjectType> findAll();

  Page<ObjectType> findAll(int usageStatus, String name, Pageable pageable) throws StreemException;

  Page<Property> getAllObjectTypeProperties(String objectTypeId, int usageStatus, String name, Pageable pageable) throws StreemException;

  Page<Relation> getAllObjectTypeRelations(String objectTypeId, int usageStatus, String name, Pageable pageable) throws StreemException;

  Optional<Property> findPropertyByIdAndObjectTypeExternalId(String objectTypeExternalId, ObjectId propertyId) throws StreemException;

  void save (ObjectType objectType);

  Optional<ObjectType> findById(String id);
}
