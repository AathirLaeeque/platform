package com.leucine.streem.dto.request;

import com.leucine.streem.constant.TaskPauseReason;

public record TaskPauseOrResumeRequest(Long jobId, TaskPauseReason reason, String comment) {
}
