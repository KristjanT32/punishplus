package krisapps.punishplus.enums;

public enum DirectAction {

    BAN_PLAYER("You've been banned due to not managing to pay off your punishment in time."),
    KICK_PLAYER("You've been kicked due to not managing to pay off your punishment in time."),
    KILL_PLAYER,
    NONE

    ;

    private String reason;

    DirectAction(String reason) {

    }

    DirectAction(){

    }

    public String getReason() {
        return reason;
    }

    public DirectAction setReason(String reason) {
        this.reason = reason;
        return this;
    }
}
