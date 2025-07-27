package net.desolatesky.message;

public final class Messages {

    private Messages() {
        throw new UnsupportedOperationException();
    }

    public static final MessageKey ALREADY_HAS_ISLAND = new MessageKey("already-has-island");
    public static final MessageKey CREATED_ISLAND = new MessageKey("created-island");
    public static final MessageKey NOT_ISLAND_OWNER = new MessageKey("not-island-owner");
    public static final MessageKey ISLAND_NOT_EMPTY = new MessageKey("island-not-empty");
    public static final MessageKey ISLAND_CREATION_FAILED = new MessageKey("island-creation-failed");
    public static final MessageKey HAS_NO_ISLAND = new MessageKey("has-no-island");
    public static final MessageKey ISLAND_NOT_FOUND = new MessageKey("island-not-found");
    public static final MessageKey ISLAND_WORLD_NOT_FOUND = new MessageKey("island-world-not-found");
    public static final MessageKey TELEPORT_INTERVAL = new MessageKey("teleport-interval");
    public static final MessageKey TELEPORT_SUCCESS = new MessageKey("teleport-success");
    public static final MessageKey TELEPORT_CANCELLED = new MessageKey("teleport-cancelled");

    public static final MessageKey CONFIRM_DELETE_ISLAND = new MessageKey("confirm-delete-island");
    public static final MessageKey DELETING_ISLAND = new MessageKey("deleting-island");
    public static final MessageKey DELETED_ISLAND = new MessageKey("deleted-island");
    public static final MessageKey ISLAND_DELETION_FAILED = new MessageKey("island-deletion-failed");

    // invites
    public static final MessageKey INVITE_SENT = new MessageKey("invite-sent");
    public static final MessageKey INVITE_RECEIVED = new MessageKey("invite-received");
    public static final MessageKey SENT_INVITE_EXPIRED = new MessageKey("sent-invite-expired");
    public static final MessageKey RECEIVED_INVITE_EXPIRED = new MessageKey("received-invite-expired");
    public static final MessageKey SENT_INVITE_ACCEPTED = new MessageKey("sent-invite-accepted");
    public static final MessageKey RECEIVED_INVITE_ACCEPTED = new MessageKey("received-invite-accepted");
    public static final MessageKey SENT_INVITE_REJECTED = new MessageKey("sent-invite-rejected");
    public static final MessageKey RECEIVED_INVITE_REJECTED = new MessageKey("received-invite-rejected");
    public static final MessageKey SENT_INVITE_CANCELLED = new MessageKey("sent-invite-cancelled");
    public static final MessageKey RECEIVED_INVITE_CANCELLED = new MessageKey("received-invite-cancelled");
    public static final MessageKey INVITE_NOT_FOUND = new MessageKey("invite-not-found");
    public static final MessageKey INVITE_ALREADY_EXISTS = new MessageKey("invite-already-exists");
    public static final MessageKey INVITE_ALREADY_MEMBER = new MessageKey("invite-already-member");
    public static final MessageKey NOT_ISLAND_MEMBER = new MessageKey("not-island-member");
    public static final MessageKey OWNER_CANNOT_LEAVE = new MessageKey("owner-cannot-leave");

    // kicking
    public static final MessageKey RECEIVED_KICK_FROM_ISLAND = new MessageKey("received-kick-from-island");
    public static final MessageKey SENT_KICK_FROM_ISLAND = new MessageKey("sent-kick-from-island");
    public static final MessageKey LEFT_ISLAND = new MessageKey("left-island");

    // permissions
    public static final MessageKey ISLAND_PERMISSION_DENIED = new MessageKey("island-permission-denied");

    // MISC
    public static final MessageKey PLAYER_NOT_FOUND = new MessageKey("player-not-found");

}
