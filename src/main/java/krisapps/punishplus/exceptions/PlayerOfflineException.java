package krisapps.punishplus.exceptions;

import java.util.UUID;

public class PlayerOfflineException extends Exception {

    private final UUID playerUUID;

    public PlayerOfflineException(String message, String playerUUID) {
        super(message);
        this.playerUUID = UUID.fromString(playerUUID);
    }


    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
