package dev.mruniverse.guardianrftb.multiarena.enums;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.utils.ItemsInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum SpectatorItems {
    SPECTATE_MENU,
    SETTINGS_MENU,
    PLAY_AGAIN_MENU,
    EXIT_ITEM;

    public String getPath() {
        String path = "playing.spectator-inventory.";
        switch (this) {
            case EXIT_ITEM:
                return path + "leave.";
            case SETTINGS_MENU:
                return path + "settings.";
            case PLAY_AGAIN_MENU:
                return path + "play-again.";
            default:
            case SPECTATE_MENU:
                return path + "spect.";
        }
    }

    public void giveItem(Player player, GuardianRFTB plugin) {
        ItemsInfo items = plugin.getItemsInfo();
        switch (this) {
            case EXIT_ITEM:
                if(items.isLeaveItem()) player.getInventory().setItem(plugin.getItemsInfo().getLeaveSlot(),plugin.getItemsInfo().getLeave());
                break;
            case SETTINGS_MENU:
                if(items.isSettingsItem()) player.getInventory().setItem(plugin.getItemsInfo().getSettingsSlot(),plugin.getItemsInfo().getSettings());
                break;
            case PLAY_AGAIN_MENU:
                if(items.isPlayAgainItem()) player.getInventory().setItem(plugin.getItemsInfo().getPlayAgainSlot(),plugin.getItemsInfo().getPlayAgain());
                break;
            default:
            case SPECTATE_MENU:
                if(items.isSpectatorItem()) player.getInventory().setItem(plugin.getItemsInfo().getSpectatorSlot(),plugin.getItemsInfo().getSpectator());
        }
    }

    public void setItem(GuardianRFTB plugin, ItemStack item,int slot, boolean toggle) {
        switch (this) {
            case EXIT_ITEM:
                plugin.getItemsInfo().setLeave(item);
                plugin.getItemsInfo().setLeaveSlot(slot);
                plugin.getItemsInfo().setLeaveItem(toggle);
                break;
            case SETTINGS_MENU:
                plugin.getItemsInfo().setSettings(item);
                plugin.getItemsInfo().setSettingsSlot(slot);
                plugin.getItemsInfo().setSettingsItem(toggle);
                break;
            case PLAY_AGAIN_MENU:
                plugin.getItemsInfo().setPlayAgain(item);
                plugin.getItemsInfo().setPlayAgainSlot(slot);
                plugin.getItemsInfo().setPlayAgainItem(toggle);
                break;
            default:
            case SPECTATE_MENU:
                plugin.getItemsInfo().setSpectator(item);
                plugin.getItemsInfo().setSpectatorSlot(slot);
                plugin.getItemsInfo().setSpectatorItem(toggle);
        }
    }

    public ItemFunction getFunction() {
        switch (this) {
            case EXIT_ITEM:
                return ItemFunction.EXIT_SPECTATE_GAME;
            case SETTINGS_MENU:
                return ItemFunction.SETTINGS_MENU;
            case PLAY_AGAIN_MENU:
                return ItemFunction.PLAY_AGAIN_MENU;
            default:
            case SPECTATE_MENU:
                return ItemFunction.SPECTATE_MENU;
        }
    }
}
