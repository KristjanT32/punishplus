package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.enums.PunishmentType;
import krisapps.punishplus.events.PlayerPunishEvent;
import krisapps.punishplus.managers.MessageFormattingManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PunishCommand implements CommandExecutor, EventExecutor {

    PunishPlus main;
    MessageFormattingManager formatter;

    public PunishCommand(PunishPlus main) {
        this.main = main;
        formatter = new MessageFormattingManager(main.config);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Syntax: /punish <player> <punishmentType> <punishment-sensitive-arguments> <punishment-sensitive-arguments-part-2> <reason>
        /* Cases
        1. Item punishment: /punish Player item_punishment diamond 200 Stole a big amount of emeralds from the town square.
        2. Custom punishment: /punish Player custom_punishment <unit> <amount> Custom reason for this punishment.
         */

        if (args.length >= 4) {
            final PunishmentType type = PunishmentType.valueOf(args[1].toUpperCase());
            final String argument1 = args[2];
            final String argument2 = args[3];
            String reason = "";
            for (int i = 4; i < args.length; i++) {
                reason += " " + args[i];
            }

            Player player = null;
            try {
                player = Bukkit.getPlayer(args[0]);
            } catch (NullPointerException e) {
                sender.sendMessage(ChatColor.RED + "Sorry, but this player could not be found.");
                e.printStackTrace();
            }

            punish(type, player, argument1, argument2, reason, sender);


        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid syntax."));
            return false;
        }
        return true;
    }

    private void punish(PunishmentType type, Player player, String arg1, String arg2, String reason, CommandSender sender) {

        UUID punishmentUUID = new UUID(UUID.randomUUID().getMostSignificantBits(), UUID.randomUUID().getLeastSignificantBits());

        switch (type) {

            case ITEM_PUNISHMENT:
                ItemStack item = new ItemStack(Material.valueOf(arg1.toUpperCase()));
                final int amount = Integer.parseInt(arg2);
                String itemName = "";

                if (item.getItemMeta().hasDisplayName()) {
                    itemName = item.getItemMeta().getDisplayName();
                } else {
                    itemName = item.getType().toString().toLowerCase(Locale.ROOT);
                }

                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".type", type.name());
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".reason", reason);
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".unit", itemName);
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".unitAmount", amount);
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".issuer", sender.getName());
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".player", player);
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".createdOn", new Date());

                try {
                    main.data.save(main.dataFile);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+&e] &aSuccessfully applied punishment of type " + type + " on " + player.getName()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', formatter.formatPunishmentMessage(player.getName(), reason, sender.getName(), itemName, String.valueOf(amount))));
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerPunishEvent(player, reason, type, main));
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + "Could not punish player due to: " + e.getMessage() + "\nRead console for more info on this error.");
                    e.printStackTrace();
                }

                break;
            case CUSTOM_PUNISHMENT:
                String unit = arg1;
                final String unitAmount = arg2;


                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".type", type.name());
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".reason", reason);
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".unit", unit);
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".unitAmount", unitAmount);
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".issuer", sender.getName());
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".player", player);
                main.data.set("punishments." + player.getUniqueId() + "." + punishmentUUID + ".createdOn", new Date());

                try {
                    main.data.save(main.dataFile);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+&e] &aSuccessfully applied punishment of type " + type + " on " + player.getName()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', formatter.formatPunishmentMessage(player.getName(), reason, sender.getName(), unit.replace('#', ' '), unitAmount.replace('#', ' '))));


                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + "Could not punish player due to: " + e.getMessage() + "\nRead console for more info on this error.");
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {

    }
}
