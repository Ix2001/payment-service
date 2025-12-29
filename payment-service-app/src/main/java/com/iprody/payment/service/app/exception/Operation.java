package com.iprody.payment.service.app.exception;

public enum Operation {
    CREATE_OP("create-op"),
    UPDATE_OP("update-op"),
    UPDATE_NOTE_OP("update-note-op"),
    FIND_BY_ID_OP("find-by-id-op"),
    FIND_ALL_OP("find-all-op"),
    DELETE_OP("delete-op");

    private final String value;

    Operation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
