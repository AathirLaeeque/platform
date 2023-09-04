package com.leucine.streem.dto.mapper.helper;

import java.util.List;
import java.util.Set;

public interface IBaseMapper<D, E> {
  D toDto(E e);

  E toEntity(D d);

  List<D> toDto(List<E> list);

  List<E> toEntity(List<D> list);

  List<D> toDto(Set<E> set);

  List<E> toEntity(Set<D> set);
}
