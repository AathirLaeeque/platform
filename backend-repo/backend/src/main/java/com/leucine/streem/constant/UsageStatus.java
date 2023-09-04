package com.leucine.streem.constant;

public enum UsageStatus {
    ACTIVE(1),
    DRAFT(2),
    BEING_REVIEWED(3),
    READY_FOR_APPROVAL(4),
    REQUESTED_CHANGES(5),
    APPROVED(6),
    DEPRECATED(7),
    DELETED(8);

    private final int code;

    UsageStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
