package com.leucine.streem.collections;

import com.leucine.streem.dto.ResourceParameterChoiceDto;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobLogResource implements Serializable {
  @Serial
  private static final long serialVersionUID = -9019310770617620828L;

  private String displayName;
  private List<ResourceParameterChoiceDto> choices = new ArrayList<>();
}
