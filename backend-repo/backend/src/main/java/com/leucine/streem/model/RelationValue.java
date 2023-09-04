package com.leucine.streem.model;

import com.leucine.streem.constant.TableName;
import com.leucine.streem.model.helper.UserAuditIdentifiableBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.RELATION_VALUES)
public class RelationValue extends UserAuditIdentifiableBase implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relations_id", nullable = false, insertable = false, updatable = false)
    private Relation relation;

    @Column(name = "relations_id", columnDefinition = "bigint")
    private Long relationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jobs_id", nullable = false, insertable = false, updatable = false)
    private Job job;

    @Column(name = "jobs_id")
    private Long jobId;

    @Column(name = "object_id")
    private String objectId;

    @Column(name = "collection")
    private String collection;

    @Column(name = "object_external_id")
    private String externalId;

    @Column(name = "object_display_name")
    private String displayName;

    @Column(name = "object_type_external_id")
    private String objectTypeExternalId;

    @Column(name = "object_type_display_name")
    private String objectTypeDisplayName;
}
