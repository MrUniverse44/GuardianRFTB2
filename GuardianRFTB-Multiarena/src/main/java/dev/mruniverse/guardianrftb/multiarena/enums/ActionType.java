package dev.mruniverse.guardianrftb.multiarena.enums;

import java.util.Locale;

public enum ActionType {
    USER_COMMAND,
    CONSOLE_COMMAND,
    BOSS_BAR,
    SOUND,
    ACTION_BAR,
    MESSAGE;

    public String msg = "";
    public String replacedMsg = "";

    public void setMessage(String msg) {
        this.msg = msg;
        this.replacedMsg = msg.replace("<sound>","")
                .replace("<action>","")
                .replace("<action_bar>","")
                .replace("<a_bar>","")
                .replace("<actionbar>","")
                .replace("<boss>","")
                .replace("<boss_bar>","")
                .replace("<b_bar>","")
                .replace("<bossbar>","")
                .replace("<cmd_user>","")
                .replace("<user>","")
                .replace("<u>","")
                .replace("<cmd_console>","")
                .replace("<console>","")
                .replace("<c>","")
                .replace("<message>","")
                .replace("<msg>","");
    }

    public String getMsg(boolean replaced) {
        if(replaced) return replacedMsg;
        return msg;
    }

    public boolean isForConsole() {
        if(msg.contains("<sound>")) return false;
        if(msg.contains("<action>") || msg.contains("<action_bar>") || msg.contains("<a_bar>") || msg.contains("<actionbar>")) return false;
        if(msg.contains("<boss>") || msg.contains("<boss_bar>") || msg.contains("<b_bar>") || msg.contains("<bossbar>")) return false;
        if(msg.contains("<cmd_user>") || msg.contains("<user>") || msg.contains("<u>")) return false;
        return msg.contains("<cmd_console>") && !msg.contains("<player_name>") || msg.contains("<console>")  && !msg.contains("<player_name>") || msg.contains("<c>")  && !msg.contains("<player_name>");
    }

    public String getMsg(String nick,boolean replaced) {
        if(replaced) return replacedMsg.replace("<player_name>",nick);
        return msg.replace("<player_name>",nick);
    }

    public static ActionType getAction(String text) {
        text = text.toLowerCase();
        ActionType type;
        type = ActionType.MESSAGE;
        if(text.contains("<sound>")) type = ActionType.SOUND;
        if(text.contains("<action>") || text.contains("<action_bar>") || text.contains("<a_bar>") || text.contains("<actionbar>")) type = ActionType.ACTION_BAR;
        if(text.contains("<boss>") || text.contains("<boss_bar>") || text.contains("<b_bar>") || text.contains("<bossbar>")) type = ActionType.BOSS_BAR;
        if(text.contains("<cmd_user>") || text.contains("<user>") || text.contains("<u>")) type = ActionType.USER_COMMAND;
        if(text.contains("<cmd_console>") || text.contains("<console>") || text.contains("<c>")) type = ActionType.CONSOLE_COMMAND;
        type.setMessage(text);
        return type;
    }
}
