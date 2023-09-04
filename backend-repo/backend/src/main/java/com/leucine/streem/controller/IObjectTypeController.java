package com.leucine.streem.controller;

import com.leucine.streem.collections.ObjectType;
import com.leucine.streem.collections.Property;
import com.leucine.streem.collections.Relation;
import com.leucine.streem.constant.CollectionKey;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.request.ObjectTypePropertyCreateRequest;
import com.leucine.streem.dto.request.ObjectTypeRelationCreateRequest;
import com.leucine.streem.dto.request.ObjectTypeCreateRequest;
import com.leucine.streem.dto.request.ObjectTypeUpdateRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.helper.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/object-types")
public interface IObjectTypeController {
  @GetMapping
  @ResponseBody
  Response<Page<ObjectType>> findAll(@RequestParam(name = CollectionKey.USAGE_STATUS, defaultValue = "1") int usageStatus, @RequestParam(name = "displayName", required = false) String displayName, @SortDefault(sort = BaseEntity.ID, direction = Sort.Direction.DESC) Pageable pageable) throws StreemException;

  @GetMapping("/{objectTypeId}")
  @ResponseBody
  Response<ObjectType> findById(@PathVariable String objectTypeId) throws ResourceNotFoundException;

  @PostMapping
  @ResponseBody
  Response<BasicDto> createObjectType(@RequestBody ObjectTypeCreateRequest objectTypeCreateRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/{objectTypeId}/properties")
  @ResponseBody
  Response<BasicDto> addObjectTypeProperty(@PathVariable String objectTypeId, @RequestBody ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/{objectTypeId}/relations")
  @ResponseBody
  Response<BasicDto> addObjectTypeRelation(@PathVariable String objectTypeId, @RequestBody ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/{objectTypeId}")
  @ResponseBody
  Response<BasicDto> editObjectType(@PathVariable String objectTypeId, @RequestBody ObjectTypeUpdateRequest objectTypeUpdateRequest) throws StreemException, ResourceNotFoundException;

  @PatchMapping("/{objectTypeId}/properties/{propertyId}")
  @ResponseBody
  Response<BasicDto> editObjectTypeProperty(@PathVariable String objectTypeId, @PathVariable String propertyId, @RequestBody ObjectTypePropertyCreateRequest objectTypePropertyCreateRequest) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/{objectTypeId}/relations/{relationId}")
  @ResponseBody
  Response<BasicDto> editObjectTypeRelation(@PathVariable String objectTypeId, @PathVariable String relationId, @RequestBody ObjectTypeRelationCreateRequest objectTypeRelationCreateRequest) throws StreemException, ResourceNotFoundException;

  @PatchMapping("/{objectTypeId}/properties/{propertyId}/archive")
  @ResponseBody
  Response<BasicDto> archiveObjectTypeProperty(@PathVariable String objectTypeId, @PathVariable String propertyId) throws ResourceNotFoundException, StreemException;

  @PatchMapping("/{objectTypeId}/relations/{relationId}/archive")
  @ResponseBody
  Response<BasicDto> archiveObjectTypeRelation(@PathVariable String objectTypeId, @PathVariable String relationId) throws ResourceNotFoundException;

  @GetMapping("/{objectTypeId}/properties")
  @ResponseBody
  Response<Page<Property>> getAllObjectTypeProperties(@PathVariable String objectTypeId, @RequestParam(name = CollectionKey.USAGE_STATUS, defaultValue = "1") int usageStatus, @RequestParam(name = "displayName", required = false) String displayName, Pageable pageable) throws ResourceNotFoundException, StreemException;
  @GetMapping("/{objectTypeId}/relations")
  @ResponseBody
  Response<Page<Relation>> getAllObjectTypeRelations(@PathVariable String objectTypeId, @RequestParam(name = CollectionKey.USAGE_STATUS, defaultValue = "1") int usageStatus, @RequestParam(name = "displayName", required = false) String displayName, Pageable pageable) throws ResourceNotFoundException, StreemException;

}
