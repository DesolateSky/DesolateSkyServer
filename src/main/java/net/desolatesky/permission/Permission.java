package net.desolatesky.permission;

public enum Permission {

    CMD_STOP("desolatesky.command.stop"),
    CMD_SPARK("desolatesky.command.spark"),
    CMD_GIVE("desolatesky.command.give"),
    ADMIN("desolatesky.admin"),
    ;

    private final String path;

    Permission(String path) {
        this.path = path;
    }

    public String path() {
        return this.path;
    }
}
