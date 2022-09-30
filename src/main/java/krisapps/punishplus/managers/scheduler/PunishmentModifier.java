package krisapps.punishplus.managers.scheduler;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.enums.DirectAction;
import krisapps.punishplus.enums.ModifierAction;
import krisapps.punishplus.managers.PunishmentInfoManager;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishmentModifier {
    private static int modifierTask;
    private static PunishPlus main;


    //TODO: Add proper modifier cancellation logic.

    public static void startModifierTask(PunishPlus main, long delayInTicks, ModifierAction action, int value, @Nullable DirectAction directAction, UUID player, UUID punishment){
        BukkitScheduler scheduler = Bukkit.getScheduler();
        PunishmentModifier.main = main;

        action.setNewValue(value);
        action.setAction(directAction);

        modifierTask = scheduler.scheduleAsyncRepeatingTask(main, () -> {

            PunishmentInfoManager pim = new PunishmentInfoManager(main);

            long startTime = System.currentTimeMillis();
            long endTime;

            log(main, "[SPMC/#" + punishment + "]: Start procedure");

            switch (action){

                case INCREASE_BY:
                    pim.setPunishment(player, punishment, Integer.parseInt(pim.getUnitAmount(String.valueOf(player), punishment)) + action.getNewValue());
                    log(main, "[SPMC/#" + punishment + "]: Perform: " + action + " => " + value);
                    break;

                case DECREASE_BY:
                    if (Integer.parseInt(pim.getUnitAmount(player.toString(), punishment)) - value > 0) {
                        pim.setPunishment(player, punishment, Integer.parseInt(pim.getUnitAmount(String.valueOf(player), punishment)) - action.getNewValue());
                        log(main, "[SPMC/#" + punishment + "]: Perform: " + action + " => " + value);
                    }else{
                        log(main, "[SPMC/#" + punishment + "]: Minimum value reached, ignoring active task.");
                    }
                    break;

                case SET_TO:
                    pim.setPunishment(player, punishment, action.getNewValue());
                    log(main, "[SPMC/#" + punishment + "]: Perform: " + action + " => " + value);
                    break;

                case MULTIPLY_BY:
                    pim.setPunishment(player, punishment, Integer.parseInt(pim.getUnitAmount(String.valueOf(player), punishment)) * action.getNewValue());
                    log(main, "[SPMC/#" + punishment + "]: Perform: " + action + " => " + value);
                    break;

                case VOID:
                    pim.voidPunishment(player, punishment);
                    log(main, "[SPMC/#" + punishment + "]: Punishment record voided.");
                    break;

                case PERFORM_ACTION:

                    log(main, "[SPMC/#" + punishment + "]: Begin perform direct action: " + directAction);


                    switch (directAction){

                        case BAN_PLAYER:
                            log(main, "[SPMC/#" + punishment + "]: Perform: " + directAction);
                            Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(player.toString(), directAction.getReason(), null, null);
                            Bukkit.getPlayer(player).kickPlayer(directAction.getReason());
                            break;
                        case KICK_PLAYER:
                            log(main, "[SPMC/#" + punishment + "]: Perform: " + directAction);
                            Bukkit.getPlayer(player).kickPlayer(directAction.getReason());
                            break;
                        case KILL_PLAYER:
                            log(main, "[SPMC/#" + punishment + "]: Perform: " + directAction);
                            Bukkit.getPlayer(player).setHealth(0.0d);
                            break;
                        case NONE:
                            break;
                    }

                    break;
            }

            endTime = System.currentTimeMillis();

            log(main, "[SPMC/#" + punishment + "]: Procedure completed in " + (endTime - startTime) + "ms." );

        }, delayInTicks, delayInTicks);
        main.data.set("modifier." + player + "." + punishment + "." + action + ".taskIdentifier", modifierTask);

        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addModifierTask(modifierTask, player, action, punishment, main);
    }

    private static void addModifierTask(int taskID, UUID player, ModifierAction action, UUID punishment, PunishPlus main) {

        if ( (ArrayList<String>) main.data.getList("coredata.modifier.taskList") == null){
            List<String> taskList = new ArrayList<>();
            taskList.add(String.valueOf(taskID));
            main.data.set("coredata.modifier.taskList", taskList);
            if (main.data.getInt("modifier." + player.toString() + "." + punishment.toString() + "." + action.name() + ".taskIdentifier") == 0){
                main.data.set("modifier." + player.toString() + "." + punishment.toString() + "." + action.name() + ".taskIdentifier", taskID);
            }
        }else{
            ArrayList<String> taskList = (ArrayList<String>) main.data.getList("coredata.modifier.taskList");
            taskList.add(String.valueOf(taskID));
            main.data.set("coredata.modifier.taskList", taskList);
            if (main.data.getInt("modifier." + player.toString() + "." + punishment.toString() + "." + action.name() + ".taskIdentifier") == 0){
                main.data.set("modifier." + player.toString() + "." + punishment.toString() + "." + action.name() + ".taskIdentifier", taskID);
            }
        }

        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add modifier entry.

        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void cancelActiveModifierTask(int modifierTask){
        ArrayList<String> taskList = (ArrayList<String>) main.data.getList("coredata.modifier.taskList");
        taskList.remove(String.valueOf(modifierTask));
        Bukkit.getServer().getScheduler().cancelTask(modifierTask);

    }

    public static void cancelAllModifierTasks(PunishPlus main){

        if ((List<String>) main.data.getList("coredata.modifier.taskList") == null) { return; }

        for (String task: (List<String>) main.data.getList("coredata.modifier.taskList")){
            Bukkit.getServer().getScheduler().cancelTask(Integer.parseInt(task));
            main.data.set("coredata.modifier." + task, null);
            try {
                main.data.save(main.dataFile);
            } catch (IOException e) {
                log(main, "Failed to cancel task: TASKID #" + task);
                throw new RuntimeException(e);
            }
        }
    }

    public static void removeModifier(UUID player, UUID punishment, ModifierAction modifierType){

        if (main.data.getString("modifier." + player) != null){
            cancelActiveModifierTask(main.data.getInt("modifier." + player + "." + punishment + "." + modifierType.name() + ".taskIdentifier"));
            main.data.set("modifier." + player + "." + punishment + "." + modifierType.name(), null);
        }

        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void removeAllModifiers(UUID player, UUID punishment){
        for (String entry: main.data.getConfigurationSection("modifier." + player + "." + punishment).getKeys(false)){
            cancelActiveModifierTask(main.data.getInt("modifier." + player + "." + punishment + "." + entry + ".taskIdentifier"));
            main.data.set("modifier." + player + "." + punishment, null);
        }

        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void scheduleExistingTasks(PunishPlus main){
        log(main, "Performing post-reload rescheduling, please wait");
        cancelAllModifierTasks(main);
        log(main, "Cancelled active modifier tasks");
        if (main.data.getConfigurationSection("modifier") == null) { main.getLogger().warning(" [ Rescheduler/WARN ]: No modifier entries were found."); return; }
        for (String player: main.data.getConfigurationSection("modifier").getKeys(false)){
            log(main, "Reschedule for: " + player);
            for (String punishment: main.data.getConfigurationSection("modifier." + player).getKeys(false)){
                log(main, "Reschedule #: " + punishment);
                for (String modifierTask: main.data.getConfigurationSection("modifier." + player + "." + punishment).getKeys(false)) {
                    int delay = main.data.getInt("modifier." + player + "." + punishment + "." + modifierTask  + ".period");
                    int modifierValue = main.data.getInt("modifier." + player + "." + punishment + "." + modifierTask  + ".modifierValue");
                    ModifierAction action = ModifierAction.valueOf(main.data.getString("modifier." + player + "." + punishment + "." + modifierTask + ".modifierType"));
                    DirectAction directAction = DirectAction.NONE;
                    if (action == ModifierAction.PERFORM_ACTION) {
                        directAction = DirectAction.valueOf(main.data.getString("modifier." + player + "." + punishment + "." + modifierTask  + ".directAction"));
                    }
                    log(main, "Setting new task: " + player + "." + punishment + " ( " + action + " )");
                    startModifierTask(main, delay, action, modifierValue, directAction, UUID.fromString(player), UUID.fromString(punishment));
                }
            }
        }
        for (Player p: Bukkit.getOnlinePlayers()){
            if (p.isOp()){
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &b&lSchedulers&e]: &aRescheduling completed."));
            }
        }
    }

    public static int getModifierTask() {
        return modifierTask;
    }

    public static boolean hasModifiersRunning(PunishPlus main){
        return main.data.getConfigurationSection("coredata.modifier").getKeys(false).size() > 0;
    }

    static void log(PunishPlus main, String msg){
        String prefix = "[ Rescheduler/INFO ]: ";
        main.getLogger().info(prefix + msg);

    }

}
