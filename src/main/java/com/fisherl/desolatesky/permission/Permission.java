package com.fisherl.desolatesky.permission;

public enum Permission {

    CMD_STOP("desolatesky.command.stop");

    private final String path;

    Permission(String path) {
        this.path = path;
    }

    public String path() {
        return this.path;
    }
}
