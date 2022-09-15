package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.managers.UnresolvedPunishmentNotifier;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ForceReloadPunishments implements CommandExecutor {

    PunishPlus main;

    public ForceReloadPunishments(PunishPlus main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Syntax: /reloadplayers

        sender.sendMessage(ChatColor.YELLOW + "Reloading UPN player list...");
        UnresolvedPunishmentNotifier.refreshTrackerList(main);
        sender.sendMessage(ChatColor.GREEN + "Player list reloaded.");
        return true;
    }
}
