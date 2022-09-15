package krisapps.punishplus.managers;

import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;

public class MessageFormattingManager {

    final String PERSISTENT_PUNISMENT_MESSAGE_PATH_PREFIX = "customization.punishment_message.";
    final String PERSISTENT_REPAY_REQUEST_PATH_PREFIX = "customization.repay_request.";
    final String PERSISTENT_REPAYBROADCAST_MESSAGE_PATH_PREFIX = "customization.punishment_repaid_message.";

    FileConfiguration configurationFile;

    public MessageFormattingManager(FileConfiguration configurationFile) {
        this.configurationFile = configurationFile;
    }

    public String formatPunishmentMessage(String player, String punishmentReason, String punishmentIssuer, String punishmentUnit, String punishmentUnitAmount) {
        String msg = configurationFile.getString(PERSISTENT_PUNISMENT_MESSAGE_PATH_PREFIX + "divider")
                + "\n"
                + configurationFile.getString(PERSISTENT_PUNISMENT_MESSAGE_PATH_PREFIX + "title")
                + "\n"
                + configurationFile.getString(PERSISTENT_PUNISMENT_MESSAGE_PATH_PREFIX + "reason").replace("%reason%", punishmentReason)
                + "\n"
                + configurationFile.getString(PERSISTENT_PUNISMENT_MESSAGE_PATH_PREFIX + "punishedBy").replace("%punishedBy%", punishmentIssuer)
                + "\n"
                + configurationFile.getString(PERSISTENT_PUNISMENT_MESSAGE_PATH_PREFIX + "unit").replace("%unit%", punishmentUnit).replace("%amount%", punishmentUnitAmount)
                + "\n"
                + configurationFile.getString(PERSISTENT_PUNISMENT_MESSAGE_PATH_PREFIX + "divider");
        return msg;
    }

    public String formatInfoMessage(String player, String punishmentReason, String punishmentIssuer, String punishmentUnit, String punishmentUnitAmount, String punishmentCreationDate) {
        String msg = configurationFile.getString(PERSISTENT_PUNISMENT_MESSAGE_PATH_PREFIX + "divider")
                + "\n"
                + "&bPunishment Information"
                + "\n"
                + "&ePunishment creation reason: &b%reason%".replace("%reason%", punishmentReason)
                + "\n"
                + "&ePunishment created on: &b" + punishmentCreationDate
                + "\n"
                + "&ePunishment created by: &b%punishedBy%".replace("%punishedBy%", punishmentIssuer)
                + "\n"
                + "&ePunishment details: &bRequire payment of &dx%amount% &a%unit%".replace("%unit%", punishmentUnit).replace("%amount%", punishmentUnitAmount)
                + "\n"
                + "&eAffected player: &b&l" + player
                + "\n"
                + configurationFile.getString(PERSISTENT_PUNISMENT_MESSAGE_PATH_PREFIX + "divider");
        return msg;
    }

    public String formatRepayMessage(String unit, String amount) {
        String msg = configurationFile.getString(PERSISTENT_REPAY_REQUEST_PATH_PREFIX + "divider")
                + "\n"
                + configurationFile.getString(PERSISTENT_REPAY_REQUEST_PATH_PREFIX + "title")
                + "\n"
                + configurationFile.getString(PERSISTENT_REPAY_REQUEST_PATH_PREFIX + "message")
                + "\n"
                + configurationFile.getString(PERSISTENT_REPAY_REQUEST_PATH_PREFIX + "unit").replace("%amount%", amount).replace("%unit%", unit)
                + "\n"
                + configurationFile.getString(PERSISTENT_REPAY_REQUEST_PATH_PREFIX + "divider");
        return msg;
    }

    public String genericFormat(String player, String unit, String amount) {
        String msg = configurationFile.getString(PERSISTENT_REPAYBROADCAST_MESSAGE_PATH_PREFIX + "message")
                .replace("%player%", player)
                .replace("%unit%", unit)
                .replace("%amount%", amount);

        return msg;
    }

    public String genericFormat(String unit, String amount) {
        String msg = configurationFile.getString(PERSISTENT_REPAYBROADCAST_MESSAGE_PATH_PREFIX + "message")
                .replace("%unit%", unit)
                .replace("%amount%", amount);

        return msg;
    }

    public String genericFormat(String player) {
        String msg = configurationFile.getString(PERSISTENT_REPAYBROADCAST_MESSAGE_PATH_PREFIX + "message")
                .replace("%player%", player);

        return msg;
    }

    public String formatMessage(String input, @Nullable String player, @Nullable String unit, @Nullable String unitAmount, @Nullable String punisher, @Nullable String reason, @Nullable String creationDate) {
        String msg = input;
        if (player != null) {
            input.replace("%player%", player);
        }
        if (unit != null) {
            input.replace("%unit%", unit);
        }
        if (unitAmount != null) {
            input.replace("%amount%", unitAmount);
        }
        if (punisher != null) {
            input.replace("%punishedBy%", punisher);
        }
        if (reason != null) {
            input.replace("%reason%", reason);
        }
        if (creationDate != null) {
            input.replace("%creationDate%", creationDate);
        }
        return msg;
    }

}
