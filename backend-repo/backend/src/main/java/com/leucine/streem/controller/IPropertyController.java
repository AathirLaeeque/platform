package com.leucine.streem.controller;

import com.leucine.streem.constant.Type;
import com.leucine.streem.dto.PropertyDto;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.model.FacilityUseCasePropertyMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/properties")
public interface IPropertyController {
  @GetMapping
  @ResponseBody
  Response<List<PropertyDto>> getAll(@RequestParam Type.PropertyType type,
                                     @RequestParam(name = "useCaseId", required = false) Long useCaseId,
                                     @RequestParam(name = "archived", defaultValue = "false", required = false) boolean archived,
                                     @RequestParam(name = "filters", defaultValue = "") String filters,
                                     @SortDefault.SortDefaults({@SortDefault(sort = FacilityUseCasePropertyMapping.ORDER_TREE, direction = Sort.Direction.ASC)}) Pageable pageable);
}