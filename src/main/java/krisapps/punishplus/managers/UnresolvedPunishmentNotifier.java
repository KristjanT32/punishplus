package krisapps.punishplus.managers;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.enums.PunishmentType;
import krisapps.punishplus.events.PlayerPunishEvent;
import krisapps.punishplus.events.PlayerPunishmentRepayEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnresolvedPunishmentNotifier implements Listener {

    static MessageFormattingManager mfm;
    private static PunishPlus main;
    private static ArrayList<UUID> trackedPlayersList = new ArrayList<>();
    private static int notifierTask = 0;
    private static int interval;


    public UnresolvedPunishmentNotifier(PunishPlus main) {
        UnresolvedPunishmentNotifier.main = main;
        mfm = new MessageFormattingManager(main.config);
        interval = main.config.getInt("config.punishmentReminderInterval");
    }

    public static void trackPlayer(UUID playerUUID, PunishPlus main) {
        trackedPlayersList.add(playerUUID);
        main.getLogger().info("[Punish+] New player added to track: " + playerUUID);
        refreshTrackerList(main);
    }

    public static void untrackPlayer(UUID playerUUID, PunishPlus main) {
        trackedPlayersList.remove(playerUUID);
        main.getLogger().info("[Punish+] Stopped tracking a player: " + playerUUID);
        refreshTrackerList(main);
    }

    // Used for the /forcenotify command.
    public static void notifyTrackedPlayers(PunishPlus main) {
        main.getLogger().info("[Punish+] Received a force notify request.");
        if (trackedPlayersList.size() > 0) {
            for (UUID player : trackedPlayersList) {
                if (Bukkit.getPlayer(player) == null) {
                    main.getLogger().info("[PunishmentWatcher]: Player offline, skipping: " + player);
                    continue;
                }

                // If there are no punishments left for this player, untrack them and skip.
                if (main.data.getConfigurationSection("punishments." + player).getKeys(false).size() == 0) {
                    main.getLogger().info("[PunishmentWatcher]: No active punishments are left for: " + Bukkit.getPlayer(player).getName());
                    untrackPlayer(player, main);
                    continue;
                }

                main.getLogger().info("[PunishmentWatcher]: Found " + main.data.getConfigurationSection("punishments." + player).getKeys(false).size() + " active punishments to report for " + Bukkit.getPlayer(player).getName());

                // Loop through player's punishment records, notify.
                for (String field : main.data.getConfigurationSection("punishments." + player).getKeys(false)) {

                    String unit = PunishmentInfoManager.getUnit(player.toString(), UUID.fromString(field));
                    String amount = PunishmentInfoManager.getUnitAmount(player.toString(), UUID.fromString(field));
                    String type = PunishmentInfoManager.getType(player.toString(), UUID.fromString(field));

                    if (PunishmentType.valueOf(type).equals(PunishmentType.ITEM_PUNISHMENT)) {
                        Bukkit.getPlayer(player).sendMessage(ChatColor.translateAlternateColorCodes('&', mfm.formatRepayMessage(unit, amount)));
                        Bukkit.getPlayer(player).spigot().sendMessage(constructClickableText("[ CLICK TO REPAY ]", field, Bukkit.getPlayer(player).getName(), "x" + amount + " of " + unit));
                    }
                }
            }

        } else {
            main.getLogger().info("[PunishmentWatcher]: No punishment records have been found (this usually means there are no punished players yet)");
        }
        main.getLogger().info("[PunishmentWatcher]: Routine complete [!]");
    }

    public static void refreshTrackerList(PunishPlus main) {

        ArrayList<String> savedTrackerList = (ArrayList<String>) main.data.getList("coredata.tracker.trackerList");

        try {

            ArrayList<String> stringUUIDList = new ArrayList<>();

            if (main.data.getList("coredata.tracker.trackerList") != null) {
                for (String fakeUUID : (ArrayList<String>) main.data.getList("coredata.tracker.trackerList")) {
                    trackedPlayersList.add(UUID.fromString(fakeUUID));
                }
            }

            if (trackedPlayersList.size() == 0) {
                for (String player : main.data.getConfigurationSection("punishments").getKeys(false)) {
                    trackedPlayersList.add(UUID.fromString(player));
                    stringUUIDList.add(player);
                }
                main.data.set("coredata.tracker.trackerList", stringUUIDList);

                try {
                    main.data.save(main.dataFile);
                } catch (IOException e) {
                    main.getLogger().info("Failed to save core data. Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException ignored) {

        }

        if (Notifier.trackedPlayersList == null && savedTrackerList.size() > 0) {
            stopNotifier(main);
            startNotifierWithTrackerList(main, trackedPlayersList);
        }
    }

    public static void startNotifier(PunishPlus main) {
        Bukkit.getServer().getScheduler().cancelTasks(main);
        if (trackedPlayersList.size() == 0) {
            main.getLogger().info("[Punish+] Tracker list not found. Refreshing...");
            refreshTrackerList(main);
        }

        notifierTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, new Notifier(trackedPlayersList), 40, 20L * interval);
        main.getLogger().info("[Punish+] Started new punishment notifier with TaskID " + notifierTask + " and delay of " + interval * 20 + " seconds.");
    }

    public static void startNotifierWithTrackerList(PunishPlus main, ArrayList<UUID> trackedPlayersList) {
        Bukkit.getServer().getScheduler().cancelTasks(main);
        UnresolvedPunishmentNotifier.trackedPlayersList = trackedPlayersList;
        notifierTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, new Notifier(trackedPlayersList), 20 * 10L, 20L * interval);
        main.getLogger().info("[Punish+] Started new punishment notifier with TaskID " + notifierTask + " and delay of " + interval * 20 + " seconds.");
    }

    private static TextComponent constructClickableText(String text, String punishment, String player, String payment) {

        List<Content> content = new ArrayList<>();
        content.add(new Text(ChatColor.YELLOW + "Punishment Information\n"));
        content.add(new Text(ChatColor.AQUA + "Reference ID: " + ChatColor.YELLOW + punishment + "\n"));
        content.add(new Text(ChatColor.GREEN + "Payment: " + ChatColor.DARK_GREEN + payment));
        TextComponent textComponent = new TextComponent();
        textComponent.setText(text);
        textComponent.setColor(ChatColor.GREEN.asBungee());
        textComponent.setBold(true);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, content));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/repay " + Bukkit.getPlayer(player).getUniqueId() + " " + punishment));

        return textComponent;
    }

    public static void stopNotifier(PunishPlus main) {
        Bukkit.getScheduler().cancelTask(notifierTask);
        main.getLogger().info("[PunishmentWatcher]: Stopped punishment notifier with TaskID " + notifierTask);
    }

    @EventHandler
    public void OnPlayerPunished(PlayerPunishEvent e) {
        trackPlayer(e.getPlayer().getUniqueId(), main);
        refreshTrackerList(main);
    }

    @EventHandler
    public void OnPlayerPunishmentRepaid(PlayerPunishmentRepayEvent e) {
        untrackPlayer(e.getPlayer().getUniqueId(), main);
        refreshTrackerList(main);
    }

    static class Notifier implements Runnable {

        private static ArrayList<UUID> trackedPlayersList = new ArrayList<>();
        MessageFormattingManager mfm = new MessageFormattingManager(main.config);

        public Notifier(ArrayList<UUID> trackedPlayersList) {
            Notifier.trackedPlayersList = trackedPlayersList;
        }

        @Override
        public void run() {
            if (trackedPlayersList.size() > 0) {
                for (UUID player : trackedPlayersList) {
                    if (Bukkit.getPlayer(player) == null) {
                        main.getLogger().info("[PunishmentWatcher]: Player offline, skipping: " + player);
                        continue;
                    }

                    // If there are no punishments left for this player, untrack them and skip.
                    if (main.data.getConfigurationSection("punishments." + player).getKeys(false).size() == 0) {
                        main.getLogger().info("[PunishmentWatcher]: No active punishments are left for: " + Bukkit.getPlayer(player).getName());
                        untrackPlayer(player, main);
                        continue;
                    }

                    main.getLogger().info("[PunishmentWatcher]: Found " + main.data.getConfigurationSection("punishments." + player).getKeys(false).size() + " active punishments to report for " + Bukkit.getPlayer(player).getName());

                    // Loop through player's punishment records, notify.
                    for (String field : main.data.getConfigurationSection("punishments." + player).getKeys(false)) {

                        String unit = PunishmentInfoManager.getUnit(player.toString(), UUID.fromString(field));
                        String amount = PunishmentInfoManager.getUnitAmount(player.toString(), UUID.fromString(field));
                        String type = PunishmentInfoManager.getType(player.toString(), UUID.fromString(field));

                        if (PunishmentType.valueOf(type).equals(PunishmentType.ITEM_PUNISHMENT)) {
                            Bukkit.getPlayer(player).sendMessage(ChatColor.translateAlternateColorCodes('&', mfm.formatRepayMessage(unit, amount)));
                            Bukkit.getPlayer(player).spigot().sendMessage(constructClickableText("[ CLICK TO REPAY ]", field, Bukkit.getPlayer(player).getName(), "x" + amount + " of " + unit));
                        }
                    }
                }

            } else {
                main.getLogger().info("[PunishmentWatcher]: No punishment records have been found (this usually means there are no punished players yet)");
                refreshTrackerList(main);
            }
            main.getLogger().info("[PunishmentWatcher]: Routine complete [!]");
        }

        private TextComponent constructClickableText(String text, String punishment, String player, String payment) {

            List<Content> content = new ArrayList<>();
            content.add(new Text(ChatColor.YELLOW + "Punishment Information\n"));
            content.add(new Text(ChatColor.AQUA + "Reference ID: " + ChatColor.YELLOW + punishment + "\n"));
            content.add(new Text(ChatColor.GREEN + "Payment: " + ChatColor.DARK_GREEN + payment));
            TextComponent textComponent = new TextComponent();
            textComponent.setText(text);
            textComponent.setColor(ChatColor.GREEN.asBungee());
            textComponent.setBold(true);
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, content));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/repay " + Bukkit.getPlayer(player).getUniqueId() + " " + punishment));

            return textComponent;
        }
    }
}