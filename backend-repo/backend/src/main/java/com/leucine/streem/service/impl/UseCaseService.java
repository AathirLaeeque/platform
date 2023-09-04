package com.leucine.streem.service.impl;

import com.leucine.streem.constant.Misc;
import com.leucine.streem.constant.Operator;
import com.leucine.streem.dto.UseCaseDto;
import com.leucine.streem.dto.mapper.IUseCaseMapper;
import com.leucine.streem.model.FacilityUseCaseMapping;
import com.leucine.streem.model.UseCase;
import com.leucine.streem.model.helper.PrincipalUser;
import com.leucine.streem.model.helper.SpecificationBuilder;
import com.leucine.streem.model.helper.search.SearchCriteria;
import com.leucine.streem.repository.IFacilityUseCaseMappingRepository;
import com.leucine.streem.repository.IUseCaseRepository;
import com.leucine.streem.service.IUseCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UseCaseService implements IUseCaseService {
  private final IUseCaseRepository useCaseRepository;
  private final IFacilityUseCaseMappingRepository facilityUseCaseMappingRepository;
  private final IUseCaseMapper mapper;

  @Override
  public List<UseCaseDto> getUseCases(String filters, Pageable pageable) {
    log.info("[getUseCases] Request to get use cases, filters: {}, pageable: {}", filters, pageable);
    PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Long currentFacilityId = principalUser.getCurrentFacilityId();
    List<Object> facilityIds = null;
    if (currentFacilityId != null && !currentFacilityId.equals(Misc.ALL_FACILITY_ID)) {
      facilityIds = Collections.singletonList(currentFacilityId);
    } else {
      facilityIds = Collections.unmodifiableList(principalUser.getFacilities().stream().map(r -> (Object) Long.valueOf(r.getId())).toList());
    }
    SearchCriteria facilitySearchCriteria = (new SearchCriteria()).setField(FacilityUseCaseMapping.FACILITY_ID).setOp(Operator.Search.ANY.toString()).setValues(facilityIds);
    Specification<FacilityUseCaseMapping> specification = SpecificationBuilder.createSpecification(filters, List.of(facilitySearchCriteria));
    List<UseCase> allUseCases = useCaseRepository.findAllByArchivedOrderByOrderTree(false);
    List<FacilityUseCaseMapping> result = facilityUseCaseMappingRepository.findAll(specification);
    List<UseCase> mappedUseCases = result.stream().map(FacilityUseCaseMapping::getUseCase).distinct().sorted(Comparator.comparing(UseCase::getOrderTree)).toList();
    Set<Long> mappedUseCaseIds = new HashSet<>();
    List<UseCaseDto> finalUseCases = new ArrayList<>(allUseCases.size());
    for (UseCase uc : mappedUseCases) {
      UseCaseDto useCaseDto = mapper.toDto(uc);
      useCaseDto.setEnabled(true);
      finalUseCases.add(useCaseDto);
      mappedUseCaseIds.add(uc.getId());
    }
    for (UseCase uc : allUseCases) {
      if (!mappedUseCaseIds.contains(uc.getId())) {
        UseCaseDto useCaseDto = mapper.toDto(uc);
        useCaseDto.setEnabled(false);
        finalUseCases.add(useCaseDto);
      }
    }
    return finalUseCases;
  }
}
