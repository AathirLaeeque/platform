package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@NamedEntityGraph(
    name = "readTempParameterValue",
    attributeNodes = {
        @NamedAttributeNode(value = "medias"),
        @NamedAttributeNode(value = "parameter")
    }
)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.TEMP_PARAMETER_VALUES)
public class TempParameterValue extends ParameterValueBase implements Serializable {
  private static final long serialVersionUID = -2600881470234101686L;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "tempParameterValue", cascade = CascadeType.ALL)
  private List<TempParameterValueMediaMapping> medias = new ArrayList<>();

  @Override
  public void addMedia(Media media, User principalUserEntity) {
    TempParameterValueMediaMapping parameterValueMedia = new TempParameterValueMediaMapping(this, media, principalUserEntity);
    medias.add(parameterValueMedia);
  }

  @Override
  public void addAllMedias(List<Media> medias, User principalUserEntity) {
    for (Media media : medias) {
      TempParameterValueMediaMapping parameterValueMediaMapping = new TempParameterValueMediaMapping(this, media, principalUserEntity);
      this.medias.add(parameterValueMediaMapping);
    }
  }

  @Override
  public void archiveMedia(Media media, User principalUserEntity) {
    Iterator<TempParameterValueMediaMapping> iterator = medias.iterator();
    while (iterator.hasNext()) {
      TempParameterValueMediaMapping parameterValueMediaMapping = iterator.next();
      if (parameterValueMediaMapping.getTempParameterValue().equals(this) && parameterValueMediaMapping.getMedia().equals(media)) {
        parameterValueMediaMapping.setArchived(true);
        parameterValueMediaMapping.setModifiedBy(principalUserEntity);
      }
    }
  }

}
