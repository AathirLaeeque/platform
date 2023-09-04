package com.leucine.streem.collections;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class JobLogMediaData implements Serializable {
    private static final long serialVersionUID = 7446535905970311983L;
    private String link;
    private String type;
    private String name;
    private String description;
}
