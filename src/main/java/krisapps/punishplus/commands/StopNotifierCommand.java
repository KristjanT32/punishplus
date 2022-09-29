package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.managers.scheduler.UnresolvedPunishmentNotifier;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StopNotifierCommand implements CommandExecutor {

    PunishPlus main;
    public StopNotifierCommand(PunishPlus main){
        this.main = main;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Syntax: /stopnotifier

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &b&lSchedulers&e]: &cYou have now manually disabled the notifier task. It will automatically be re-enabled once a player is punished."));
        UnresolvedPunishmentNotifier.stopNotifier(main);

        return true;
    }
}
