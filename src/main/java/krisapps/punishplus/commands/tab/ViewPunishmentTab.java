package krisapps.punishplus.commands.tab;

import krisapps.punishplus.PunishPlus;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ViewPunishmentTab implements TabCompleter {

    PunishPlus main;

    public ViewPunishmentTab(PunishPlus main) {
        this.main = main;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        }

        if (args.length == 2) {
            for (String record : main.data.getConfigurationSection("punishments." + Bukkit.getPlayer(args[0]).getUniqueId()).getKeys(false)) {
                completions.add(record);
            }
        }

        return completions;
    }
}