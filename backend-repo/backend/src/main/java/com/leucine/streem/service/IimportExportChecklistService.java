package com.leucine.streem.service;

import com.leucine.streem.dto.BasicDto;
import com.leucine.streem.dto.request.ImportChecklistRequest;
import com.leucine.streem.exception.ResourceNotFoundException;
import com.leucine.streem.exception.StreemException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IimportExportChecklistService {
  BasicDto importChecklists(MultipartFile file) throws StreemException, ResourceNotFoundException, IOException;
  List<ImportChecklistRequest> exportChecklists(List<Long> checklistIds) throws ResourceNotFoundException;
}

