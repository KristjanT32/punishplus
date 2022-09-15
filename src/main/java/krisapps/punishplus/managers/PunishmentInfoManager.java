package krisapps.punishplus.managers;

import krisapps.punishplus.PunishPlus;

import java.util.UUID;

public class PunishmentInfoManager {

    private static final PunishPlus main = (PunishPlus) PunishPlus.getProvidingPlugin(PunishPlus.class);

    private static final String PERSISTENT_DATAFILE_PATH = "punishments.%player%.%punishment%.";

    public static String getType(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "type");
    }

    public static String getReason(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "reason");
    }

    public static String getUnit(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "unit");
    }

    public static String getUnitAmount(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "unitAmount");
    }

    public static String getPunishmentIssuer(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "issuer");
    }

    public static String getPunishedPlayer(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "player");
    }

    public static String getCreationDate(String playerUUID, UUID punishment) {
        return main.data.getString(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString()) + "createdOn");
    }

    public static boolean punishmentExists(String playerUUID, UUID punishment) {
        return main.data.getConfigurationSection(PERSISTENT_DATAFILE_PATH.replace("%player%", playerUUID).replace("%punishment%", punishment.toString())) != null;
    }


}
