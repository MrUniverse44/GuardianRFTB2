package dev.mruniverse.guardianrftb.multiarena.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

@SuppressWarnings("unused")
public class GuardianText {
    private final BaseComponent baseComponent;

    public GuardianText(String text) {
        baseComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&',text));
    }

    public GuardianText(String text,String replace,String replaceValue) {
        baseComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&',text.replace(replace,replaceValue)));
    }

    public void setColor(ChatColor color) {
        baseComponent.setColor(color);
    }

    public void setClickEvent(ClickEvent.Action clickAction,String clickValue) {
        baseComponent.setClickEvent(new ClickEvent(clickAction,clickValue));
    }

    public void setClickEvent(String action, String clickValue) {
        ClickEvent.Action clickAction = getClickAction(action);
        baseComponent.setClickEvent(new ClickEvent(clickAction,clickValue));
    }

    public ClickEvent.Action getClickAction(String actionFromString) {
        if(actionFromString.equalsIgnoreCase("URL") || actionFromString.equalsIgnoreCase("OPEN_URL")) return ClickEvent.Action.OPEN_URL;
        if(actionFromString.equalsIgnoreCase("SUGGEST") || actionFromString.equalsIgnoreCase("SUGGEST_COMMAND")) return ClickEvent.Action.SUGGEST_COMMAND;
        return ClickEvent.Action.RUN_COMMAND;
    }

    public void setBold(boolean bold) {
        baseComponent.setBold(bold);
    }

    @SuppressWarnings("deprecation")
    public void setHoverEvent(HoverEvent.Action hoverAction, String hoverMessage) {
        baseComponent.setHoverEvent(new HoverEvent(hoverAction,new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',hoverMessage)).create()));
    }

    @SuppressWarnings("deprecation")
    public void setHoverEvent(String action, String hoverMessage) {
        HoverEvent.Action hoverAction = getHoverAction(action);
        baseComponent.setHoverEvent(new HoverEvent(hoverAction,new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',hoverMessage)).create()));
    }

    public HoverEvent.Action getHoverAction(String actionFromString) {
        if(actionFromString.equalsIgnoreCase("SHOW_ITEM") || actionFromString.equalsIgnoreCase("ITEM")) return HoverEvent.Action.SHOW_ITEM;
        return HoverEvent.Action.SHOW_TEXT;
    }

    public void setItalic(boolean toggle) {
        baseComponent.setItalic(toggle);
    }

    public BaseComponent getTextConverted() {
        return baseComponent;
    }

    public void add(GuardianText text){
        baseComponent.addExtra(" " + text.getTextConverted());
    }
}

