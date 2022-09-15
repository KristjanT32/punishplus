package krisapps.punishplus.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerPunishmentRepayEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final UUID punishmentUUID;

    public PlayerPunishmentRepayEvent(Player p, UUID punishmentUUID) {
        this.player = p;
        this.punishmentUUID = punishmentUUID;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUUID() {
        return punishmentUUID;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
