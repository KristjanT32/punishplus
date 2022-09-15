package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.managers.MessageFormattingManager;
import krisapps.punishplus.managers.PunishmentInfoManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ViewPunishmentCommand implements CommandExecutor {

    PunishPlus main;

    public ViewPunishmentCommand(PunishPlus main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Syntax: /viewpunishment <player> <uuid>
        MessageFormattingManager mfm = new MessageFormattingManager(main.config);

        if (args.length == 2) {
            if (main.data.get("punishments." + Bukkit.getPlayer(args[0]).getUniqueId()) != null) {
                if (main.data.getConfigurationSection("punishments." + Bukkit.getPlayer(args[0]).getUniqueId()).getKeys(false).contains(args[1])) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mfm.formatInfoMessage(
                                    Bukkit.getPlayer(args[0]).getName(),
                                    PunishmentInfoManager.getReason(Bukkit.getPlayer(args[0]).getUniqueId().toString(), UUID.fromString(args[1])),
                                    PunishmentInfoManager.getPunishmentIssuer(Bukkit.getPlayer(args[0]).getUniqueId().toString(), UUID.fromString(args[1])),
                                    PunishmentInfoManager.getUnit(Bukkit.getPlayer(args[0]).getUniqueId().toString(), UUID.fromString(args[1])),
                                    PunishmentInfoManager.getUnitAmount(Bukkit.getPlayer(args[0]).getUniqueId().toString(), UUID.fromString(args[1])),
                            PunishmentInfoManager.getCreationDate(Bukkit.getPlayer(args[0]).getUniqueId().toString(), UUID.fromString(args[1]))
                            )
                    ));
                } else {
                    sender.sendMessage(ChatColor.RED + "Sorry, this punishment cannot be found.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Sorry, no punishments are present for this player.");
            }
        }

        return true;
    }
}
