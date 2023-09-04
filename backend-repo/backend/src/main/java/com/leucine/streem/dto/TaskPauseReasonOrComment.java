package com.leucine.streem.dto;

import com.leucine.streem.constant.TaskPauseReason;

public record TaskPauseReasonOrComment(TaskPauseReason taskPauseReason, String comment) {
}
