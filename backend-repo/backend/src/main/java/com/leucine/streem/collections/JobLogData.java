package com.leucine.streem.collections;

import com.leucine.streem.constant.Type;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobLogData implements Serializable {
    @Serial
    private static final long serialVersionUID = -9019310770617620828L;
    private String entityId;
    private Type.JobLogTriggerType triggerType;
    private String displayName;
    private String value;
    private String identifierValue;
    private List<JobLogMediaData> medias;
    private Map<String, JobLogResource> resourceParameters = new HashMap<>();
}
