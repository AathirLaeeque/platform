package com.leucine.streem.service;

import com.leucine.streem.dto.UseCaseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUseCaseService {
  List<UseCaseDto> getUseCases(String filters, Pageable pageable);
}
