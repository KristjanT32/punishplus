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

public class RepayCommandTab implements TabCompleter {

    PunishPlus main;

    public RepayCommandTab(PunishPlus main) {
        this.main = main;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getUniqueId().toString());
            }
        }

        if (args.length == 2) {
            for (String record : main.data.getConfigurationSection("punishments." + args[0]).getKeys(false)) {
                completions.add(record);
            }
        }

        if (args.length == 3) {
            if (sender.hasPermission("punishplus.administrative")) {
                completions.add("/ignorestatus");
            } else {
                completions.clear();
            }
        }

        return completions;
    }
}
