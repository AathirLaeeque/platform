package com.leucine.streem.controller.impl;

import com.leucine.streem.collections.JobLog;
import com.leucine.streem.controller.IJobLogController;
import com.leucine.streem.dto.response.Response;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import com.leucine.streem.service.IJobLogService;
import com.leucine.streem.util.DateTimeUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class JobLogController implements IJobLogController {
  private final IJobLogService jobLogService;

  public JobLogController(IJobLogService jobLogService) {
    this.jobLogService = jobLogService;
  }

  @Override
  public Response<List<JobLog>> getAllJobLogs(String filters, Pageable pageable) {
    return Response.builder().data(jobLogService.findAllJobLogs(filters, pageable)).build();
  }

  @Override
  public Response<JobLog> getJobLog(Long checklistId, Long jobId) {
    return Response.builder().data(jobLogService.findByJobId(jobId.toString())).build();
  }

  @Override
  public void downloadJobLogs(String customViewId, String filters, HttpServletResponse httpServletResponse) throws IOException, ResourceNotFoundException, StreemException {
    httpServletResponse.setContentType("application/octet-stream");
    httpServletResponse.setHeader("Content-Disposition", String.format("attachment; filename=%s.xlsx", DateTimeUtils.now()));
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    ByteArrayInputStream jobLogExcelStream = jobLogService.createJobLogExcel(customViewId, filters);
    IOUtils.copy(jobLogExcelStream, httpServletResponse.getOutputStream());
  }
}
