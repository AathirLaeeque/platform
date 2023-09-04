package com.leucine.streem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leucine.streem.collections.EntityObject;
import com.leucine.streem.collections.changelogs.EntityObjectChangeLog;
import com.leucine.streem.collections.partial.PartialEntityObject;
import com.leucine.streem.constant.CollectionKey;
import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.request.ArchiveObjectRequest;
import com.leucine.streem.dto.request.EntityObjectValueRequest;
import com.leucine.streem.dto.request.UnarchiveObjectRequest;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.helper.BaseEntity;
import io.undertow.util.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/objects")
public interface IEntityObjectController {
  @GetMapping()
  @ResponseBody
  Response<Page<EntityObject>> findAll(@RequestParam(name = CollectionKey.COLLECTION) String collection, @RequestParam(name = CollectionKey.USAGE_STATUS, defaultValue = "1") int usageStatus,
                                       @RequestParam(name = CollectionKey.EXTERNAL_ID, defaultValue = "") String propertyExternalId, @RequestParam(name = CollectionKey.VALUE, defaultValue = "") String value,
                                       @SortDefault(sort = BaseEntity.ID, direction = Sort.Direction.DESC) Pageable pageable);

  @GetMapping("/partial")
  @ResponseBody
  Response<Page<PartialEntityObject>> findAllPartial(@RequestParam(name = CollectionKey.COLLECTION) String collection, @RequestParam(name = CollectionKey.USAGE_STATUS, defaultValue = "1") int usageStatus,
                                                     @RequestParam(name = CollectionKey.EXTERNAL_ID, defaultValue = "") String propertyExternalId, @RequestParam(name = CollectionKey.VALUE, defaultValue = "") String value, @RequestParam(value = "filters", required = false) String filters,
                                                     @SortDefault(sort = BaseEntity.ID, direction = Sort.Direction.DESC) Pageable pageable);

  @PostMapping()
  @ResponseBody
  Response<List<EntityObject>> saveObject(@RequestBody EntityObjectValueRequest entityObjectValueRequest) throws StreemException, ResourceNotFoundException, JsonProcessingException;

  @PatchMapping("/{objectId}")
  @ResponseBody
  Response<List<EntityObject>> updateEntityObject(@PathVariable String objectId, @RequestBody EntityObjectValueRequest entityObjectValueRequest) throws StreemException, ResourceNotFoundException, JsonProcessingException;

  @GetMapping("/{objectId}")
  @ResponseBody
  Response<EntityObject> findObjectById(@PathVariable String objectId, @RequestParam(name = CollectionKey.COLLECTION) String collection) throws ResourceNotFoundException;

  @PatchMapping("/{objectId}/archive")
  @ResponseBody
  Response<BasicDto> archiveObject(@RequestBody ArchiveObjectRequest archiveObjectRequest, @PathVariable String objectId) throws StreemException, BadRequestException, ResourceNotFoundException, JsonProcessingException;

  @PatchMapping("/{objectId}/unarchive")
  Response<BasicDto> unarchiveObject(@RequestBody UnarchiveObjectRequest unarchiveObjectRequest, @PathVariable String objectId) throws ResourceNotFoundException, StreemException, JsonProcessingException;


  @GetMapping("/change-logs")
  Response<Page<EntityObjectChangeLog>> findAllChangeLogs(@RequestParam(name = "filters", required = false) String filters, @SortDefault(sort = BaseEntity.ID, direction = Sort.Direction.DESC) Pageable pageable);

  @PatchMapping("/searchable")
  Response<BasicDto> updateSearchable();
}
