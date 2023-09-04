package com.leucine.streem.repository;

import com.leucine.streem.collections.Code;
import org.springframework.stereotype.Repository;

@Repository
public interface ICollectionCodeRepository {
  Code getCode(String prefix, Integer clause);
}
