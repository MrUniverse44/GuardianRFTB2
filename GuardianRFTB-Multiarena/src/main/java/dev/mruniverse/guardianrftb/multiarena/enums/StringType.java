package dev.mruniverse.guardianrftb.multiarena.enums;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public enum StringType {
    UPPER_CASE,
    LOWER_CASE,
    DEFAULT,
    UNCOLORED,
    COLORED;

    private String currentText = "";

    public StringType setText(String text) {
        this.currentText = text;
        return this;
    }

    public StringType setType(StringType newType) {
        return newType.setText(currentText);
    }

    public String toString() {
        return "{stringType:" + currentText + ":" + this + "}";
    }

    @SuppressWarnings("unused")
    public static StringType fromString(String string) {
        List<String> list;
        string = string.replace("{stringType:","").replace("}","");
        list = Arrays.asList(string.split(":"));
        if(list.size() == 2) {
            try {
                return StringType.fromText(list.get(0)).setType(StringType.valueOf(list.get(1).toUpperCase()));
            }catch (Throwable ignored) {}
        }
        return StringType.fromText(string.replace(":" + list.get(1),""));
    }

    public String getText() {
        switch (this) {
            case UNCOLORED:
                return ChatColor.stripColor(currentText);
            case COLORED:
                return ChatColor.translateAlternateColorCodes('&', currentText);
            case LOWER_CASE:
                return currentText.toLowerCase();
            case UPPER_CASE:
                return currentText.toUpperCase();
            case DEFAULT:
                return currentText;
        }
        return currentText;
    }


    public static StringType fromText(String text) {
        return StringType.DEFAULT.setText(text);
    }
}
