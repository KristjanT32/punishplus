package krisapps.punishplus.events;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.enums.PunishmentType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerPunishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private String reason;
    private PunishmentType type;

    private PunishPlus main;

    public PlayerPunishEvent(Player player, String reason, PunishmentType type, PunishPlus main) {
        this.player = player;
        this.reason = reason;
        this.type = type;
        this.main = main;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public PunishmentType getType() {
        return type;
    }

    public void setType(PunishmentType type) {
        this.type = type;
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
