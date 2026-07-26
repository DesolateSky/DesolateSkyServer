package net.desolatesky.permission;

public enum Permission {

    CMD_STOP("desolatesky.command.stop"),
    CMD_SPARK("desolatesky.command.spark"),
    CMD_GIVE("desolatesky.command.give"),
    CMD_WHITELIST("desolatesky.command.whitelist"),
    CMD_GAMEMODE("desolatesky.command.gamemode"),
    CMD_FLY("desolatesky.command.fly"),
    CMD_TELEPORT("desolatesky.command.teleport"),
    CMD_BAN("desolatesky.command.ban"),
    CMD_LOG("desolatesky.command.log"),
    CMD_CLEAR_CHAT("desolatesky.command.clearchat"),
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
