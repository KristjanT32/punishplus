package krisapps.punishplus.commands;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.events.PlayerPunishmentRepayEvent;
import krisapps.punishplus.exceptions.InvalidPunishmentItemException;
import krisapps.punishplus.exceptions.PlayerOfflineException;
import krisapps.punishplus.managers.MessageFormattingManager;
import krisapps.punishplus.managers.PunishmentInfoManager;
import krisapps.punishplus.managers.UnresolvedPunishmentNotifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public class RepayCommand implements CommandExecutor, EventExecutor {

    PunishPlus main;

    public RepayCommand(PunishPlus main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Syntax: /repay <player> <punishment> [adminArgument]

        MessageFormattingManager mfm = new MessageFormattingManager(main.config);
        if (args.length == 3) {
            if (repay(args[0], args[1], args[2].equals("/ignorestatus") && args.length == 3 && sender.hasPermission("punishplus.administrative"))) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerPunishmentRepayEvent(Bukkit.getPlayer(UUID.fromString(args[0])), UUID.fromString(args[1])));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lCongratulations! You've successfully repaid your punishment. &a&lPunishment has been revoked."));
                if (main.config.getBoolean("config.broadcastPlayerPunishmentRepaid")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', mfm.genericFormat(Bukkit.getPlayer(UUID.fromString(args[0])).getName())));
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSorry, but you do not have the required amount of items to repay this punishment."));
            }
        } else if (args.length == 2) {
            if (repay(args[0], args[1], false)) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerPunishmentRepayEvent(Bukkit.getPlayer(UUID.fromString(args[0])), UUID.fromString(args[1])));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lCongratulations! You've successfully repaid your punishment. &a&lPunishment has been revoked."));
                if (main.config.getBoolean("config.broadcastPlayerPunishmentRepaid")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', mfm.genericFormat(Bukkit.getPlayer(UUID.fromString(args[0])).getName())));
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not repay this punishment."));
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid syntax.");
            return false;
        }


        return true;
    }


    private boolean isValidItem(String itemID) {
        Material itemMaterial = Material.valueOf(itemID.toUpperCase());
        ItemStack item = new ItemStack(itemMaterial);
        main.getLogger().info("ITEM " + itemID + " > " + item.getType());
        return item != null;
    }

    private boolean hasItemsInInventory(ItemStack itemToCheck, int minimumOf, Player p) {
        Inventory inventory = p.getInventory();

        if (inventory.containsAtLeast(itemToCheck, minimumOf)) {
            main.getLogger().info("ITEMS FOUND.");
            return true;
        } else {
            return false;
        }

    }

    public void removePunishment(ItemStack itemsToTake, String player, String punishment) {

        Bukkit.getPlayer(UUID.fromString(player)).getInventory().removeItem(itemsToTake);

        main.data.set("punishments." + player + "." + punishment, null);
        UnresolvedPunishmentNotifier.untrackPlayer(UUID.fromString(player), main);

        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void silentRemove(String player, String punishmentID) {
        main.data.set("punishments." + player + "." + punishmentID, null);
        UnresolvedPunishmentNotifier.untrackPlayer(UUID.fromString(player), main);

        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean repay(String player, String punishmentReferenceID, @Nullable boolean ignoreStatus) {

        if (main.data.getConfigurationSection("punishments." + player + "." + punishmentReferenceID) == null) {
            return false;
        }

        if (!ignoreStatus) {
            if (Bukkit.getPlayer(UUID.fromString(player)) == null) {
                try {
                    throw new PlayerOfflineException("Cannot repay a punishment for an offline player.", player);
                } catch (PlayerOfflineException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }

        String specifiedItem = PunishmentInfoManager.getUnit(player, UUID.fromString(punishmentReferenceID));
        Material itemMaterial;
        ItemStack item;

        int amount = Integer.parseInt(PunishmentInfoManager.getUnitAmount(player, UUID.fromString(punishmentReferenceID)));

        if (isValidItem(specifiedItem)) {
            itemMaterial = Material.valueOf(specifiedItem.toUpperCase());
            item = new ItemStack(itemMaterial);
        } else {
            try {
                throw new InvalidPunishmentItemException("Specified punishment item is not a valid ItemStack", specifiedItem);
            } catch (InvalidPunishmentItemException e) {
                e.printStackTrace();
            }
            return false;
        }

        if (!ignoreStatus) {
            if (hasItemsInInventory(item, amount, Bukkit.getPlayer(UUID.fromString(player)))) {
                item.setAmount(amount);
                removePunishment(item, player, punishmentReferenceID);
            } else {
                return false;
            }
        } else {
            silentRemove(player, punishmentReferenceID);
        }
        return true;
    }


    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {

    }
}
