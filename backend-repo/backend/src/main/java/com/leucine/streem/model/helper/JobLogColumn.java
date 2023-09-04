package com.leucine.streem.model.helper;

import com.leucine.streem.constant.Type;
import lombok.Data;

@Data
public class JobLogColumn {
    private String id;
    private String displayName;
    private Type.JobLogColumnType type;
    private Type.JobLogTriggerType triggerType;
}