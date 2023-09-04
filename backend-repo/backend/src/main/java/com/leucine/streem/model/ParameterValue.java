package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@NamedEntityGraph(
  name = "readParameterValue",
  attributeNodes = {
    @NamedAttributeNode(value = "medias", subgraph = "parameter.medias"),
    @NamedAttributeNode(value = "parameter")
  },
  subgraphs = {
    @NamedSubgraph(name = "parameter.medias", attributeNodes = {
            @NamedAttributeNode("media"),
    })}
)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.PARAMETER_VALUES)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ParameterValue extends ParameterValueBase implements Serializable {
  private static final long serialVersionUID = 2939158197408374719L;
  public static final String DEFAULT_SORT = "created_at";

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parameterValue", cascade = CascadeType.ALL)
  private List<ParameterValueMediaMapping> medias = new ArrayList<>();

  @Override
  public void addMedia(Media media, User principalUserEntity) {
    ParameterValueMediaMapping parameterValueMediaMapping = new ParameterValueMediaMapping(this, media, principalUserEntity);
    medias.add(parameterValueMediaMapping);
  }

  @Override
  public void addAllMedias(List<Media> medias, User principalUserEntity) {
    for (Media media : medias) {
      ParameterValueMediaMapping parameterValueMediaMapping = new ParameterValueMediaMapping(this, media, principalUserEntity);
      this.medias.add(parameterValueMediaMapping);
    }
  }

  @Override
  public void archiveMedia(Media media, User user) {
    Iterator<ParameterValueMediaMapping> iterator = medias.iterator();
    while (iterator.hasNext()) {
      ParameterValueMediaMapping parameterValueMediaMapping = iterator.next();
      if (parameterValueMediaMapping.getParameterValue().equals(this) && parameterValueMediaMapping.getMedia().equals(media)) {
        parameterValueMediaMapping.setArchived(true);
        parameterValueMediaMapping.setModifiedBy(user);
      }
    }
  }

}
