package krisapps.punishplus.enums;

import org.jetbrains.annotations.Nullable;

public enum ModifierAction {

    INCREASE_BY,
    DECREASE_BY,
    SET_TO,
    MULTIPLY_BY,
    VOID,

    PERFORM_ACTION
    ;

    private int newValue;
    private DirectAction action;

    ModifierAction(int newValue){
        setNewValue(newValue);
    }

    ModifierAction(DirectAction action, @Nullable String reason){
        setAction(action.setReason(reason));
    }

    ModifierAction() {
        newValue = 0;
        action = DirectAction.NONE;
    }

    public int getNewValue() {
        return newValue;
    }

    public DirectAction getAction() {
        return action;
    }

    public void setNewValue(int newValue) {
        this.newValue = newValue;
    }

    public void setAction(@Nullable DirectAction action) {
        if ( action == null ) { return; }
        this.action = action;
    }
}
