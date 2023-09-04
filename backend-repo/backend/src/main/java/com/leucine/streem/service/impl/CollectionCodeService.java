package com.leucine.streem.service.impl;

import com.leucine.streem.repository.ICollectionCodeRepository;
import com.leucine.streem.service.ICollectionCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CollectionCodeService implements ICollectionCodeService {
  private static final String CODE_DATE_PATTERN = "MMMyy";
  private static final String CODE_YEAR_PATTERN = "yy";
  private static final String CODE_MONTH_PATTERN = "MM";
  private static final String HYPHEN = "-";
  private final ICollectionCodeRepository objectTypeCodeRepository;

  @Override
  public String getCode(String prefix) {
    final LocalDate now = LocalDate.now();
    String formattedDate = now.format(DateTimeFormatter.ofPattern(CODE_DATE_PATTERN)).toUpperCase();
    String formattedYear = now.format(DateTimeFormatter.ofPattern(CODE_YEAR_PATTERN)).toUpperCase();
    String formattedMonth = now.format(DateTimeFormatter.ofPattern(CODE_MONTH_PATTERN)).toUpperCase();

    Integer integer = Integer.parseInt(formattedYear + formattedMonth);
    return String.join(HYPHEN, Arrays.asList(prefix, formattedDate, objectTypeCodeRepository.getCode(prefix, integer).getCounter().toString()));
  }
}
