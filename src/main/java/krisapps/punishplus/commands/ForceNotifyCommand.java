package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.managers.UnresolvedPunishmentNotifier;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ForceNotifyCommand implements CommandExecutor {

    PunishPlus main;

    public ForceNotifyCommand(PunishPlus main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Syntax: /forcenotify
        sender.sendMessage(ChatColor.YELLOW + "Forcing UPN routine...");
        UnresolvedPunishmentNotifier.notifyTrackedPlayers(main);
        sender.sendMessage(ChatColor.GREEN + "Routine force request sent.");

        return true;
    }
}
