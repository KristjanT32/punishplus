package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.managers.scheduler.UnresolvedPunishmentNotifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GetActiveTasksCommand implements CommandExecutor {

    PunishPlus main;
    public GetActiveTasksCommand(PunishPlus main){
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Syntax: /getactivetasks [type]

        sender.sendMessage(ChatColor.YELLOW + "===========================================");

        if (args.length > 0){
            ScheduledTaskType taskType = ScheduledTaskType.valueOf(args[0].toUpperCase());

            switch (taskType){
                case MODIFIER:
                    sendModifierTasks(main, sender);
                    sender.sendMessage(ChatColor.YELLOW + "===========================================");
                    break;

                case NOTIFIER:
                    sendNotifierTask(main, sender);
                    sender.sendMessage(ChatColor.YELLOW + "===========================================");
                    break;
            }

        }else{
            sendNotifierTask(main, sender);
            sendModifierTasks(main, sender);
            sender.sendMessage(ChatColor.YELLOW + "===========================================");
        }
        return true;
    }

    void sendNotifierTask(PunishPlus main, CommandSender sender){
        if (UnresolvedPunishmentNotifier.getNotifierTask() != 0){

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&e[ &a&lNotifier &r&bTask &e] &b#TASKID &d"
                            + UnresolvedPunishmentNotifier.getNotifierTask()
                            + "&e is running with interval of &d"
                            + UnresolvedPunishmentNotifier.getInterval()
                            + "s"
            ));

        }else{
            sender.sendMessage(ChatColor.RED + "The notifier task is not active at the moment.");
        }
    }

    void sendModifierTasks(PunishPlus main, CommandSender sender){
        if (main.data.getList("coredata.modifier.taskList") != null) {

            for (String player : main.data.getConfigurationSection("modifier").getKeys(false)) {
                for (String punishment : main.data.getConfigurationSection("modifier." + player).getKeys(false)) {
                    for (String modifierTask : main.data.getConfigurationSection("modifier." + player + "." + punishment).getKeys(false)) {
                        Player p;
                        if (Bukkit.getPlayer(UUID.fromString(player)) == null){
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&e[ &b&lModifier &r&bTask &e]: &aFOR &d#"
                                            + player
                                            + "'s &aPUNISHMENT &d#"
                                            + punishment
                                            + " &e(Period: &b"
                                            + main.data.getInt("modifier." + player + "." + punishment + "." + modifierTask + ".period")
                                            + "&e, Type: &b"
                                            + main.data.getString("modifier." + player + "." + punishment + "." + modifierTask + ".modifierType")
                                            + "&e)"));
                        }else{
                            p = Bukkit.getPlayer(UUID.fromString(player));
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&e[ &b&lModifier &r&bTask &e]: &aFOR &d#"
                                            + p.getName()
                                            + "'s &aPUNISHMENT &d#"
                                            + punishment
                                            + " &e(Period: &b"
                                            + main.data.getInt("modifier." + player + "." + punishment + "." + modifierTask + ".period")
                                            + "&e, Type: &b"
                                            + main.data.getString("modifier." + player + "." + punishment + "." + modifierTask + ".modifierType")
                                            + "&e)"));
                        }
                    }
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED + "No modifier tasks are active at the moment.");
        }
    }
}
