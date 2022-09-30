package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.enums.DebugInfoType;
import krisapps.punishplus.managers.scheduler.PunishmentModifier;
import krisapps.punishplus.managers.scheduler.UnresolvedPunishmentNotifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GetCoreDebugInfoCommand implements CommandExecutor {

    PunishPlus main;
    public GetCoreDebugInfoCommand(PunishPlus main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Syntax: /debuginfo <type>

        DebugInfoType infoType;

        try {
            infoType = DebugInfoType.valueOf(args[0].toUpperCase());
        }catch (IllegalArgumentException | EnumConstantNotPresentException e){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &cUnrecognized information type: " + args[0]));
            return false;
        }

        switch (infoType){
            case RUNNING_TASKS:
                getModifierTasks(sender);
                getNotifierTaskInfo(sender);
                break;
            case PUNISHMENT_INFORMATION:
                showPunishmentInfo(sender);
                break;
            case TRACKED_PLAYERS:
                getTrackedPlayerList(sender);
                break;
            case TRACKER_STATUS:
                getTrackerStatus(sender);
                break;
            case MODIFIER_STATUS:
                getModifierStatus(sender);
                break;
            case PLUGIN_CONFIGURATION:
                getConfigInfo(sender);
                break;
            case ALL:
                getModifierTasks(sender);
                getNotifierTaskInfo(sender);
                showPunishmentInfo(sender);
                getTrackerStatus(sender);
                getTrackedPlayerList(sender);
                getModifierStatus(sender);
                getConfigInfo(sender);
                break;
        }




        return true;
    }

    private void getConfigInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&bShowing plugin's configured values\n&e===========================================\n"
                + "&b&lConfig &e| &ePunishment Reminder Interval: &d" + main.config.getInt("config.punishmentReminderInterval")
                + "\n&b&lConfig &e| &eBroadcast Player Punishment Repaid: &d" + main.config.getBoolean("config.broadcastPlayerPunishmentRepaid")
                + "\n&b&lConfig &e| &eDisable Notifier If No Players Punished: &d" + main.config.getBoolean("config.disableNotifierIfNoPlayersPunished")
                + "\n&a&lLogging &e| &eLog Notifier: &d" + main.config.getBoolean("logging.logNotifier")
                + "\n&e==========================================="
        ));

    }

    private void getModifierStatus(CommandSender sender) {
        if (PunishmentModifier.hasModifiersRunning(main)){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &eModifier is currently &a&lactive."));
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &eModifier is currently &c&linactive."));
        }

    }

    private void getTrackerStatus(CommandSender sender) {

        if (UnresolvedPunishmentNotifier.isRunning()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &bNotifier is currently &aRUNNING"));
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &bNotifier is currently &cSTOPPED"));
        }

    }

    private void getTrackedPlayerList(CommandSender sender) {
        if (UnresolvedPunishmentNotifier.isRunning()){

            if (!UnresolvedPunishmentNotifier.getTrackedPlayersList().isEmpty()){

                String playersString = "";

                for (UUID trackedPlayer: UnresolvedPunishmentNotifier.getTrackedPlayersList()){
                    playersString += "&d" + trackedPlayer + "&e, " ;
                }

                if (playersString.substring(playersString.length() - 1, playersString.length()).equalsIgnoreCase(", ")) {
                    playersString = playersString.substring(0, playersString.length() - 2);
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &bCurrently tracked players are: " +
                        playersString
                ));
            }else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &cThere are currently no tracked players."));
            }

        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &cThe notifier is not running at the moment."));
        }
    }

    private void showPunishmentInfo(CommandSender sender) {
        if (main.data.getConfigurationSection("punishments") != null){

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPunishments &b(&d"
                    + main.data.getConfigurationSection("punishments").getKeys(false).size()
                    + " &brecorded player)"
            ));

            // For each punished player...
            for (String player: main.data.getConfigurationSection("punishments").getKeys(false)){
                // ... and for each punishment of that player
                if (main.data.getConfigurationSection("punishments." + player) == null) { sender.sendMessage(ChatColor.RED + "No punishment records found for existing player."); continue; }
                for (String punishment: main.data.getConfigurationSection("punishments." + player).getKeys(false)){
                    String name = "";
                    String path = "punishments." + player + "." + punishment + ".";

                    // Get player name
                    if (Bukkit.getServer().getPlayer(UUID.fromString(player)) == null) {
                        name = Bukkit.getOfflinePlayer(UUID.fromString(player)).getName();
                    }else{
                        name = Bukkit.getServer().getPlayer(UUID.fromString(player)).getName();
                    }

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eShowing information for &b"
                            + name
                            + " &d#"
                            + punishment
                            + "\n&e===================================================\n"
                            + "&ePunishment Type: &b" + main.data.getString(path + "type") + "\n"
                            + "&ePunishment Reason: &b" + main.data.getString(path + "reason") + "\n"
                            + "&eRepayment requirements: &b" + main.data.getString(path + "unit") + " &b(&ex" + main.data.getString(path + "unitAmount") + "&b)" + "\n"
                            + "&ePunishment Issuer: &b" + main.data.getString(path + "issuer") + "\n"
                            + "&eEffective as of &b" + main.data.getString(path + "createdOn") + "\n"
                            + "&e====================================================="
                            ));
                }


            }


        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &cNo punishments have been registered."));
        }
    }

    private void getModifierTasks(CommandSender sender) {
        if (main.data.getList("coredata.modifier.taskList") != null) {

            for (String player : main.data.getConfigurationSection("modifier").getKeys(false)) {
                for (String punishment : main.data.getConfigurationSection("modifier." + player).getKeys(false)) {
                    for (String modifierTask : main.data.getConfigurationSection("modifier." + player + "." + punishment).getKeys(false)) {
                        Player p;
                        if (Bukkit.getPlayer(UUID.fromString(player)) == null){
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&e[ &b&lModifier &r&bTask &e]: "
                                            + "&b#TASKID &d"
                                            + main.data.getString("modifier." + player + "." + punishment + "." + modifierTask + ".taskIdentifier")
                                            + "| &aFOR &d#"
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
                                    "&e[ &b&lModifier &r&bTask &e]: "
                                            + "&b#TASKID &d"
                                            + main.data.getString("modifier." + player + "." + punishment + "." + modifierTask + ".taskIdentifier")
                                            + " | &aFOR &d#"
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
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &cNo active modifier tasks have been found."));
        }
    }

    private void getNotifierTaskInfo(CommandSender sender){
        if (UnresolvedPunishmentNotifier.getNotifierTask() != 0){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&e[ &a&lNotifier &r&bTask &e] &b#TASKID &d"
                            + UnresolvedPunishmentNotifier.getNotifierTask()
                            + "&e is running with interval of &d"
                            + UnresolvedPunishmentNotifier.getInterval()
                            + "s"
            ));

        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &e&lDebug&r&e]: &cThe Notifier Task is not active at the moment."));
        }
    }
}
