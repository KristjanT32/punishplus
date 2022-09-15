package krisapps.punishplus.exceptions;

public class InvalidPunishmentItemException extends Exception {

    private final String item;

    public InvalidPunishmentItemException(String message, String item) {
        super(message);
        this.item = item;
    }

    public String getItem() {
        return item;
    }

}
