package krisapps.punishplus.managers;

import krisapps.punishplus.PunishPlus;
import krisapps.punishplus.enums.ModifierAction;

import java.io.IOException;
import java.util.UUID;

public class PunishmentInfoManager {

    PunishPlus main;
    public PunishmentInfoManager(PunishPlus main){
        this.main = main;
    }

    private final String PERSISTENT_DATAFILE_PATH = "punishments.%player%.%punishment%.";
    private final String PERSISTENT_MODIFIER_PATH = "modifier.%player%.%punishment%.%action%.";

    public String getType(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "type");
    }

    public String getReason(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "reason");
    }

    public String getUnit(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "unit");
    }

    public String getUnitAmount(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "unitAmount");
    }

    public String getPunishmentIssuer(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "issuer");
    }

    public String getPunishedPlayer(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "player");
    }

    public String getCreationDate(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "createdOn");
    }

    public boolean punishmentExists(String playerUUID, UUID punishment) {
        return main.data.getConfigurationSection(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString())) != null;
    }

    public void setPunishment(UUID playerUUID, UUID punishment, int value){
        main.data.set(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID.toString()).replace("%punishment%", punishment.toString()) + "unitAmount", value);
        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void voidPunishment(UUID playerUUID, UUID punishment) {
        main.data.set(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID.toString()).replace("%punishment%", punishment.toString()), null);
        try {
            main.data.save(main.dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPunishmentModifierTask(String player, UUID punishment, ModifierAction action) {
        return main.data.getInt(PERSISTENT_MODIFIER_PATH.replace("%player%", player.toString()).replace("%punishment%", punishment.toString()).replace("%action%", action.name()) + "taskIdentifier");
    }
}
