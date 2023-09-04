package com.leucine.streem.controller;

import com.leucine.streem.dto.JobAuditDto;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.StreemException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/audits/jobs")
public interface IJobAuditController {

  @GetMapping("/{jobId}")
  @ResponseBody
  Response<Page<JobAuditDto>> getAuditsByJobId(@PathVariable Long jobId, @RequestParam(name = "filters", defaultValue = "") String filters, Pageable pageable) throws StreemException;

}
