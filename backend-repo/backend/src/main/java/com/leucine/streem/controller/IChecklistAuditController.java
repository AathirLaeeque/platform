package com.leucine.streem.controller;

import com.leucine.streem.dto.ChecklistAuditDto;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.model.ChecklistAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/audits/checklists")
public interface IChecklistAuditController {

  @GetMapping("/{checklistId}")
  @ResponseBody
  Response<Page<ChecklistAuditDto>> getAuditsByChecklistId(@PathVariable Long checklistId, @RequestParam(name = "filters", defaultValue = "") String filters,
                                                           @SortDefault(sort = ChecklistAudit.DEFAULT_SORT, direction = Sort.Direction.DESC) Pageable pageable) throws StreemException;

}
