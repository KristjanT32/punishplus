package krisapps.punishplus.events;

import krisapps.punishplus.PunishPlus;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerPunishmentRepayEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final UUID punishmentUUID;
    private PunishPlus main;

    public PlayerPunishmentRepayEvent(Player p, UUID punishmentUUID, PunishPlus main) {
        this.player = p;
        this.punishmentUUID = punishmentUUID;
        this.main = main;
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

    public PunishPlus getMain() {
        return main;
    }
}
