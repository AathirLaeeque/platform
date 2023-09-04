package com.leucine.streem.collections;

import com.leucine.streem.constant.CollectionMisc;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PropertyValidation implements Serializable {
    private static final long serialVersionUID = -7190420921948136848L;
    private String value;
    private CollectionMisc.PropertyValidationConstraint constraint;
    private String errorMessage;
}
