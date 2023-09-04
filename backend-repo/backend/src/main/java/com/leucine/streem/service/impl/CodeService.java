package com.leucine.streem.service.impl;

import com.leucine.streem.constant.Type;
import com.leucine.streem.repository.ICodeRepository;
import com.leucine.streem.service.ICodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CodeService implements ICodeService {
  private static final String CODE_DATE_PATTERN = "MMMyy";
  private static final String CODE_YEAR_PATTERN = "yy";
  private static final String CODE_MONTH_PATTERN = "MM";
  private static final String HYPHEN = "-";

  private final ICodeRepository codeRepository;

  @Override
  @Transactional
  public String getCode(Type.EntityType entityType, Long organisationsId) {
    LocalDate now = LocalDate.now();
    String formattedDate = now.format(DateTimeFormatter.ofPattern(CODE_DATE_PATTERN)).toUpperCase();
    String formattedYear = now.format(DateTimeFormatter.ofPattern(CODE_YEAR_PATTERN)).toUpperCase();
    String formattedMonth = now.format(DateTimeFormatter.ofPattern(CODE_MONTH_PATTERN)).toUpperCase();

    Integer integer = Integer.parseInt(formattedYear + formattedMonth);

    return String.join(HYPHEN, Arrays.asList(entityType.getCode(), formattedDate, codeRepository.getCode(organisationsId, entityType, integer).getCounter().toString()));
  }
}
