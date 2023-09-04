package com.leucine.streem.dto;

import com.leucine.streem.constant.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ChecklistBasicDto implements Serializable {
  private static final long serialVersionUID = 7378771848941731114L;
  private String id;
  private String name;
  private State.Checklist state;
  private int phase;
  private boolean isGlobal;
}
