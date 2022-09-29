package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.enums.DirectAction;
import krisapps.punishplus.enums.ModifierAction;
import krisapps.punishplus.managers.PunishmentInfoManager;
import krisapps.punishplus.managers.scheduler.PunishmentModifier;
import krisapps.punishplus.managers.scheduler.UnresolvedPunishmentNotifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public class SchedulePunishmentChangeCommand implements CommandExecutor {

    PunishPlus main;
    public SchedulePunishmentChangeCommand(PunishPlus main){
        this.main = main;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Syntax: /modifier <schedule/cancel> <player> <punishment>
        // schedule: <type> <modifyPeriod> <typeArgument>
        // cancel <all/type>

        PunishmentInfoManager pim = new PunishmentInfoManager(main);

            String operation = args[0];
            String punishment = args[2];
            String player = "";
            try {
                player = String.valueOf(Bukkit.getPlayer(args[1]).getUniqueId());
            }catch (NullPointerException e){
                sender.sendMessage(ChatColor.RED + "Could not set/reset the modifier: " + ChatColor.YELLOW + "Unknown player '" + args[1] + "'");
            }
            ModifierAction action = ModifierAction.valueOf(args[3].toUpperCase());
            long modifyPeriod  = 0;
            try {
                 modifyPeriod = Integer.parseInt(args[5]) * 20L;
            }catch (IndexOutOfBoundsException ignored){

            }
            int typeArgument = 1;
            DirectAction modifierDirectAction = DirectAction.NONE;

            if (args.length >= 6) {
                try {
                    modifierDirectAction = DirectAction.valueOf(args[4].toUpperCase());
                }catch (EnumConstantNotPresentException | IllegalArgumentException e){
                    typeArgument = Integer.parseInt(args[4]);
                }
            }


                switch (operation){

                    case "schedule":

                        // Add a scheduled modifier entry to modifier.%punishment%
                        // Reference by TYPE, containing: typeArgument or directAction and period.
                        if (pim.punishmentExists(player, UUID.fromString(punishment))){

                            if (action != ModifierAction.PERFORM_ACTION) {
                                addModifierEntry(player, punishment, action, modifyPeriod, typeArgument, null);
                            }else{
                                addModifierEntry(player, punishment, action, modifyPeriod, null, modifierDirectAction);
                            }

                        }else{
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNo punishment with this reference ID exists."));
                        }

                        break;

                    case "cancel":

                        if (pim.punishmentExists(player, UUID.fromString(punishment))){
                            PunishmentModifier.cancelActiveModifierTask(pim.getPunishmentModifierTask(player, UUID.fromString(punishment), action));
                            sender.sendMessage(ChatColor.GREEN + "Successfully cancelled the modifier.");
                        }

                        break;
                }


            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully set the following task: &b" + action + " &eby/to/of &d" + typeArgument + " &a// &b" + modifierDirectAction + "\n&aTo be performed every &b" + modifyPeriod / 20 + " seconds"));
            PunishmentModifier.startModifierTask(main, modifyPeriod, action, typeArgument, modifierDirectAction, UUID.fromString(player), UUID.fromString(punishment));


        return true;
    }

    private void addModifierEntry(String p, String punishment, ModifierAction action, long period, @Nullable Integer value, @Nullable DirectAction directAction){

        if (directAction == null){
            main.data.set("modifier." + p + "." + punishment + "." + action + ".period", period);
            main.data.set("modifier." + p + "." + punishment + "." + action + ".modifierType", action.name());
            main.data.set("modifier." + p + "." + punishment + "." + action + ".modifierValue", value);

        }else{
            main.data.set("modifier." + p + "." + punishment + "." + action + ".period", period);
            main.data.set("modifier." + p + "." + punishment + "." + action + ".modifierType", action.name());
            main.data.set("modifier." + p + "." + punishment + "." + action + ".modifierValue", value);
            main.data.set("modifier." + p + "." + punishment + "." + action + ".directAction", directAction);
        }

        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
