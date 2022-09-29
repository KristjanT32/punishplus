package krisapps.punishplus.managers.scheduler;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.enums.PunishmentType;
import krisapps.punishplus.events.PlayerPunishEvent;
import krisapps.punishplus.events.PlayerPunishmentRepayEvent;
import krisapps.punishplus.managers.MessageFormattingManager;
import krisapps.punishplus.managers.PunishmentInfoManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UnresolvedPunishmentNotifier implements Listener {

    static MessageFormattingManager mfm;
    private static ArrayList<UUID> trackedPlayersList = new ArrayList<>();
    private static int notifierTask = 0;
    private static int interval;
    private static boolean isRunning = false;


    public UnresolvedPunishmentNotifier(PunishPlus main) {
        mfm = new MessageFormattingManager(main.config);
        interval = main.config.getInt("config.punishmentReminderInterval");
    }

    public static void trackPlayer(UUID playerUUID, PunishPlus main) {
        trackedPlayersList.add(playerUUID);
        main.getLogger().info("[Punish+] New player added to track: " + playerUUID);
        refreshTrackerList(main);
    }

    public static void untrackPlayer(UUID playerUUID, PunishPlus main) {
        main.getLogger().info("[Punish+] Removing player [" + playerUUID + "] ...");
        trackedPlayersList.remove(playerUUID);
        main.data.set("coredata.tracker.trackerList", main.data.getList("coredata.tracker.trackerList").remove(playerUUID));
        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            main.getLogger().info("[Punish+] Failed to remove player: " + playerUUID);
            throw new RuntimeException(e);
        }
        main.getLogger().info("[Punish+] Player removed, restarting notifier...");
        restartNotifier(main);
    }

    // Used for the /forcenotify command.
    public static void notifyTrackedPlayers(PunishPlus main) {
        main.getLogger().info("[Punish+] Received a force notify request.");
        Bukkit.getServer().getScheduler().runTask(main, new Notifier(UnresolvedPunishmentNotifier.trackedPlayersList, main));
    }

    public static void refreshTrackerList(PunishPlus main) {
        main.getLogger().info("Refreshing tracker list. Original: " + Arrays.toString(trackedPlayersList.toArray()));
        if (trackedPlayersList.size() > 0) {
            if ((ArrayList<String>) main.data.getList("coredata.tracker.trackerList") == null) { return; }
            if (!trackedPlayersList.equals(main.data.getList("coredata.tracker.trackerList"))){
                for (UUID trackedPlayerUUID: trackedPlayersList){
                    ArrayList<String> list = (ArrayList<String>) main.data.getList("coredata.tracker.trackerList");
                    list.add(trackedPlayerUUID.toString());
                }
                try {
                    main.data.save(main.dataFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Notifier.setTrackedPlayerList(trackedPlayersList);

        }else{
            if ((ArrayList<String>) main.data.getList("coredata.tracker.trackerList") == null) { return; }
            for (String trackedPlayerUUID: (ArrayList<String>) main.data.getList("coredata.tracker.trackerList")){
                trackedPlayersList.add(UUID.fromString(trackedPlayerUUID));
            }
        }
        main.getLogger().info("Tracker list refreshed>. Now: " + Arrays.toString(Notifier.getTrackedPlayers().toArray()));

    }

    public static void startNotifier(PunishPlus main) {
        int interval = main.config.getInt("config.punishmentReminderInterval") | 300;

        Bukkit.getServer().getScheduler().cancelTask(notifierTask);
        if (trackedPlayersList.size() == 0) {
            main.getLogger().info("[Punish+] Tracker list not found. Refreshing...");
            refreshTrackerList(main);
        }

        notifierTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, new Notifier(trackedPlayersList, main), 40, 20L * interval);
        main.getLogger().info("[Punish+] Started new punishment notifier with TaskID " + notifierTask + " and delay of " + interval + " seconds.");
        isRunning = true;
    }

    public static void startNotifierWithTrackerList(PunishPlus main, ArrayList<UUID> trackedPlayersList) {
        Bukkit.getServer().getScheduler().cancelTask(notifierTask);
        UnresolvedPunishmentNotifier.trackedPlayersList = trackedPlayersList;
        notifierTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, new Notifier(trackedPlayersList, main), 20 * 10L, 20L * interval);
        main.getLogger().info("[Punish+] Started new punishment notifier with TaskID " + notifierTask + " and delay of " + interval * 20 + " seconds.");
        isRunning = true;
    }

    public static void stopNotifier(PunishPlus main) {
        Bukkit.getScheduler().cancelTask(notifierTask);
        main.getLogger().info("[PunishmentWatcher]: Stopped punishment notifier with TaskID " + notifierTask);
        isRunning = false;
    }

    public static void restartNotifier(PunishPlus main){
        stopNotifier(main);
        refreshTrackerList(main);
        startNotifier(main);
    }

    @EventHandler
    public void OnPlayerPunished(PlayerPunishEvent e) {
        if (!isRunning){
            startNotifier(e.getMain());
            for (Player p: Bukkit.getServer().getOnlinePlayers()){
                if (p.isOp()){
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &b&lSchedulers&e]: &aNotifier has been (re-)enabled."));
                }
            }
        }
        trackPlayer(e.getPlayer().getUniqueId(), e.getMain());
        refreshTrackerList(e.getMain());
    }

    @EventHandler
    public void OnPlayerPunishmentRepaid(PlayerPunishmentRepayEvent e) {
        untrackPlayer(e.getPlayer().getUniqueId(), e.getMain());
        refreshTrackerList(e.getMain());
    }

    static class Notifier implements Runnable {

        private static ArrayList<UUID> trackedPlayersList = new ArrayList<>();
        private static PunishPlus main;
        MessageFormattingManager mfm;
        PunishmentInfoManager pim;

        public Notifier(ArrayList<UUID> trackedPlayersList, PunishPlus main) {
            Notifier.trackedPlayersList = trackedPlayersList;
            Notifier.main = main;
            mfm = new MessageFormattingManager(main.config);
            pim = new PunishmentInfoManager(main);
        }

        public static void setTrackedPlayerList(ArrayList<UUID> trackedPlayersList) {
            Notifier.trackedPlayersList = trackedPlayersList;
        }

        public static ArrayList<UUID> getTrackedPlayers(){
            return Notifier.trackedPlayersList;
        }

        @Override
        public void run() {
            if (trackedPlayersList.size() > 0) {
                for (UUID player: trackedPlayersList) {
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

                        String unit = pim.getUnit(player.toString(), UUID.fromString(field));
                        String amount = pim.getUnitAmount(player.toString(), UUID.fromString(field));
                        String type = pim.getType(player.toString(), UUID.fromString(field));

                        if (PunishmentType.valueOf(type).equals(PunishmentType.ITEM_PUNISHMENT)) {
                            Bukkit.getPlayer(player).sendMessage(ChatColor.translateAlternateColorCodes('&', mfm.formatRepayMessage(unit, amount)));
                            Bukkit.getPlayer(player).spigot().sendMessage(constructClickableText("[ CLICK TO REPAY ]", field, Bukkit.getPlayer(player).getName(), "x" + amount + " of " + unit));
                        }
                    }
                }

            } else {
                main.getLogger().info("[PunishmentWatcher]: No punishment records have been found (this usually means there are no punished players yet).");
                if (main.config.getBoolean("config.disableNotifierIfNoPlayersPunished")) {
                    main.getLogger().info("[PunishmentWatcher]: Notifier will be disabled until a player is punished.");
                    stopNotifier(main);
                }else{
                    refreshTrackerList(main);
                }
            }
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

    public static int getNotifierTask() {
        return notifierTask;
    }

    public static int getInterval() {
        return interval;
    }
}