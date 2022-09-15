package krisapps.punishplus.commands.tab;

import krisapps.punishplus.enums.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PunishCommandTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getDisplayName());
            }
        }
        if (args.length == 2) {
            completions.add(PunishmentType.ITEM_PUNISHMENT.name().toLowerCase());
            completions.add(PunishmentType.CUSTOM_PUNISHMENT.name().toLowerCase());
        }
        if (args.length == 3) {
            if (args[1].equals(PunishmentType.ITEM_PUNISHMENT.name().toLowerCase(Locale.ROOT))) {
                for (Material item : Material.values()) {
                    if (!args[2].equals("")) {
                        if (item.name().toLowerCase().startsWith(args[2].toLowerCase()) || item.name().toLowerCase().equalsIgnoreCase(args[2])) {
                            completions.add(item.name().toLowerCase());
                        }
                    } else {
                        for (Material _item : Material.values()) {
                            completions.add(_item.name().toLowerCase());
                        }
                    }
                }
            } else {
                completions.add("<unit>");
            }
        }
        if (args.length == 5) {
            completions.add("<reason>");
        }

        return completions;
    }
}
