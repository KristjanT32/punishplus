package krisapps.punishplus.events;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.managers.scheduler.PunishmentModifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.*;

import java.io.IOException;

public class OnServerReloadEvent implements Listener {

    PunishPlus main;
    public OnServerReloadEvent(PunishPlus main){
        this.main = main;
    }

    @EventHandler
    void onServerReload(ServerLoadEvent e){
        if (e.getType() == ServerLoadEvent.LoadType.RELOAD){
            for (Player p: Bukkit.getOnlinePlayers()){
                if (p.isOp()){
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[&aPunish&b+ &b&lSchedulers&e]: &ePlease wait while the scheduler reapplies all of the punishment tasks..."));
                }
            }
            main.data.set("coredata.modifier.taskList", null);
            try {
                main.data.save(main.dataFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            PunishmentModifier.scheduleExistingTasks(main);
        }
    }
}
