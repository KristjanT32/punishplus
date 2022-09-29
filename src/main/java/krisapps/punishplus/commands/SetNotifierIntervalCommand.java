package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.managers.scheduler.UnresolvedPunishmentNotifier;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class SetNotifierIntervalCommand implements CommandExecutor {

    PunishPlus main;
    public SetNotifierIntervalCommand(PunishPlus main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Syntax: /setnotifierinterval <interval>

        int interval;

        if (args.length == 1){
            try {
                interval = Integer.parseInt(args[0]);
                main.config.set("config.punishmentReminderInterval", interval);
                sender.sendMessage(ChatColor.GREEN + "Successfully updated the Notifier Interval.");
            }catch (NumberFormatException e){
                sender.sendMessage(ChatColor.RED + "Could not set the Notifier Interval - the entered parameter is not a number.");
            }

            UnresolvedPunishmentNotifier.restartNotifier(main);

            try {
                main.config.save(main.configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }else{
            return false;
        }

        return true;
    }
}
