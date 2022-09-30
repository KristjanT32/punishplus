package krisapps.punishplus;

import krisapps.punishplus.commands.*;
import krisapps.punishplus.commands.tab.*;
import krisapps.punishplus.events.OnServerReloadEvent;
import krisapps.punishplus.events.PlayerPunishEvent;
import krisapps.punishplus.events.PlayerPunishmentRepayEvent;
import krisapps.punishplus.managers.scheduler.UnresolvedPunishmentNotifier;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class PunishPlus extends JavaPlugin {

    public final File configFile = new File(getDataFolder(), "config.yml");
    public final File dataFile = new File(getDataFolder(), "data.yml");
    public final File logFile = new File(getDataFolder(), "punishment-log.log");

    public FileConfiguration config;
    public FileConfiguration data;

    @Override
    public void onEnable() {
        getLogger().info("Enabling plugin...");
        registerFiles();
        registerComponents();
        getLogger().info("Plugin initialization complete!");
        getLogger().info("[Punish+] Start UnresolvedPunishmentNotifier [...]");
    }

    private void registerComponents() {

        getCommand("punish").setExecutor(new PunishCommand(this));
        getCommand("viewpunishment").setExecutor(new ViewPunishmentCommand(this));
        getCommand("forcenotify").setExecutor(new ForceNotifyCommand(this));
        getCommand("repay").setExecutor(new RepayCommand(this));
        getCommand("modifier").setExecutor(new SchedulePunishmentChangeCommand(this));
        getCommand("getactivetasks").setExecutor(new GetActiveTasksCommand(this));
        getCommand("setnotifierinterval").setExecutor(new SetNotifierIntervalCommand(this));
        getCommand("stopnotifier").setExecutor(new StopNotifierCommand(this));
        getCommand("getdebuginfo").setExecutor(new GetCoreDebugInfoCommand(this));


        getCommand("punish").setTabCompleter(new PunishCommandTab());
        getCommand("viewpunishment").setTabCompleter(new ViewPunishmentTab(this));
        getCommand("repay").setTabCompleter(new RepayCommandTab(this));
        getCommand("modifier").setTabCompleter(new SchedulePunishmentChangeTab(this));
        getCommand("getactivetasks").setTabCompleter(new GetActiveTasksTab());
        getCommand("getdebuginfo").setTabCompleter(new GetDebugInfoTab());

        // Events

        Bukkit.getServer().getPluginManager().registerEvent(PlayerPunishEvent.class, new UnresolvedPunishmentNotifier(this), EventPriority.NORMAL, new PunishCommand(this), this);
        Bukkit.getServer().getPluginManager().registerEvent(PlayerPunishmentRepayEvent.class, new UnresolvedPunishmentNotifier(this), EventPriority.NORMAL, new RepayCommand(this), this);

        Bukkit.getServer().getPluginManager().registerEvents(new UnresolvedPunishmentNotifier(this), this);
        UnresolvedPunishmentNotifier.startNotifier(this);

        // EventHandlers
        Bukkit.getServer().getPluginManager().registerEvents(new OnServerReloadEvent(this), this);

    }

    private void registerFiles() {

        data = new YamlConfiguration();
        config = new YamlConfiguration();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }


        if (!dataFile.getParentFile().exists() || !dataFile.exists()) {
            try {
                data.save(dataFile);
            } catch (IOException e) {
                getLogger().info("[File Setup] Could not create plugin data file: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            getLogger().info("[File Setup] Data file found! Skipping...");
        }

        if (!configFile.getParentFile().exists() || !configFile.exists()) {
            try {
                config.save(configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            saveResource("config.yml", true);
        } else {
            getLogger().info("[File Setup] Config found! Skipping...");
        }

        try {
            data.load(dataFile);
            config.load(configFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        getLogger().info("[Punish+] Shutting down UnresolvedPunishmentNotifier [...]");
        UnresolvedPunishmentNotifier.stopNotifier(this);
        // Plugin shutdown logic
    }

}
