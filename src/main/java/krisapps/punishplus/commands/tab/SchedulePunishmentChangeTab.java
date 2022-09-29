package krisapps.punishplus.commands.tab;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.enums.DirectAction;
import krisapps.punishplus.enums.ModifierAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchedulePunishmentChangeTab implements TabCompleter {

    PunishPlus main;
    public SchedulePunishmentChangeTab(PunishPlus main){
        this.main = main;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Syntax: /modifier <schedule/cancel> <player> <punishment>
        // schedule: <type> <modifyPeriod> <typeArgument>
        // cancel <all/type>

        List<String> completions = new ArrayList<>();

        if (args.length == 1){
            completions.add("schedule");
            completions.add("cancel");
        }
        if (args.length == 2){
            for (String playerUUID: main.data.getConfigurationSection("punishments").getKeys(false)){
                if (Bukkit.getPlayer(UUID.fromString(playerUUID)) != null){
                    completions.add(Bukkit.getPlayer(UUID.fromString(playerUUID)).getName());
                }else{
                    completions.add(Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName());
                }
            }
        }

        if (args.length == 3){
            if (main.data.getConfigurationSection("punishments." + Bukkit.getPlayer(args[1]).getUniqueId()) == null) { sender.sendMessage(ChatColor.RED + "No punishments for this player."); return null; }
                for (String punishmentUUID: main.data.getConfigurationSection("punishments." + Bukkit.getPlayer(args[1]).getUniqueId()).getKeys(false)){
                    completions.add(punishmentUUID);
                }
        }

        if (args.length == 4){
            if (args[0].equalsIgnoreCase("schedule")){
                for (ModifierAction value: ModifierAction.values()){
                    completions.add(value.name());
                }
            }else{
                completions.add("all");
                for (ModifierAction value: ModifierAction.values()){
                    completions.add(value.name());
                }
            }
        }

        if (args.length == 5){
            if (args[0].equalsIgnoreCase("schedule")){
                completions.add("<value>");
                for (DirectAction action: DirectAction.values()){
                    completions.add(action.name());
                }
            }
        }

        if (args.length == 6){
            if (args[0].equalsIgnoreCase("schedule")){
                completions.add("<interval>");
            }
        }


        return completions;
    }
}
