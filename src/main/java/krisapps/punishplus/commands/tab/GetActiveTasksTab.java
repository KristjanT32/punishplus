package krisapps.punishplus.commands.tab;

import krisapps.punishplus.commands.ScheduledTaskType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetActiveTasksTab implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1){
            for (ScheduledTaskType taskType: ScheduledTaskType.values()){
                completions.add(taskType.name().toLowerCase(Locale.ROOT));
            }
        }

        return completions;
    }
}
