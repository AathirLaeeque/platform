package com.leucine.streem.collections.partial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PartialEntityObject implements Serializable {
  private static final long serialVersionUID = 463349450701597343L;
  @Id
  @JsonSerialize(using = ToStringSerializer.class)
  private ObjectId id;
  private String collection;
  private String externalId;
  private String displayName;
}
