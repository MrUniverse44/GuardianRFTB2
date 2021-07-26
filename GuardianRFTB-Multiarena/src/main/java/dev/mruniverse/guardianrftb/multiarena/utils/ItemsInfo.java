package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianrftb.multiarena.enums.ItemFunction;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
@SuppressWarnings("unused")
public class ItemsInfo {
    private ItemStack exit;
    private ItemStack checkPoint;
    private ItemStack kitRunner;
    private ItemStack kitKiller;
    private ItemStack kitBeast;
    private ItemStack beastHelmet;
    private ItemStack beastChestplate;
    private ItemStack beastLeggings;
    private ItemStack beastBoots;

    private Integer exitSlot;
    private Integer runnerSlot;
    private Integer beastSlot;
    private Integer killerSlot;

    private ItemStack spectator;
    private ItemStack settings;
    private ItemStack playAgain;
    private ItemStack leave;

    private Integer spectatorSlot;
    private Integer settingsSlot;
    private Integer playAgainSlot;
    private Integer leaveSlot;

    private boolean spectatorItem;
    private boolean settingsItem;
    private boolean playAgainItem;
    private boolean leaveItem;

    private final HashMap<ItemStack, Integer> lobbyItems = new HashMap<>();
    private final HashMap<ItemStack, Integer> beastInventory = new HashMap<>();
    private final HashMap<ItemStack, ItemFunction> currentItem = new HashMap<>();

    public void setSpectator(ItemStack itemStack) { spectator = itemStack; }
    public void setSettings(ItemStack itemStack) { settings = itemStack; }
    public void setPlayAgain(ItemStack itemStack) { playAgain = itemStack; }
    public void setLeave(ItemStack itemStack) { leave = itemStack; }

    public void setExit(ItemStack itemStack) { exit = itemStack; }
    public void setCheckPoint(ItemStack itemStack) { checkPoint = itemStack; }
    public void setKitRunner(ItemStack itemStack) { kitRunner = itemStack; }
    public void setKitKiller(ItemStack itemStack) { kitKiller = itemStack; }
    public void setKitBeast(ItemStack itemStack) { kitBeast = itemStack; }
    public void setBeastHelmet(ItemStack itemStack) { beastHelmet = itemStack; }
    public void setBeastChestplate(ItemStack itemStack) { beastChestplate = itemStack; }
    public void setBeastLeggings(ItemStack itemStack) { beastLeggings = itemStack; }
    public void setBeastBoots(ItemStack itemStack) { beastBoots = itemStack; }


    public void setSpectatorSlot(int slot) { spectatorSlot = slot; }
    public void setSettingsSlot(int slot) { settingsSlot = slot; }
    public void setPlayAgainSlot(int slot) { playAgainSlot = slot; }
    public void setLeaveSlot(int slot) { leaveSlot = slot; }

    public void setExitSlot(int slot) { exitSlot = slot; }
    public void setRunnerSlot(int slot) { runnerSlot = slot; }
    public void setKillerSlot(int slot) { killerSlot = slot; }
    public void setBeastSlot(int slot) { beastSlot = slot; }

    public ItemStack getSpectator() { return spectator; }
    public ItemStack getSettings() { return settings; }
    public ItemStack getPlayAgain() { return playAgain; }
    public ItemStack getLeave() { return leave; }

    public ItemStack getExit() { return exit; }
    public ItemStack getCheckPoint() { return checkPoint; }
    public ItemStack getKitRunner() { return kitRunner; }
    public ItemStack getKitBeast() { return kitBeast; }
    public ItemStack getKitKiller() { return kitKiller; }
    public ItemStack getBeastHelmet() { return beastHelmet; }
    public ItemStack getBeastChestplate() { return beastChestplate; }
    public ItemStack getBeastLeggings() { return beastLeggings; }
    public ItemStack getBeastBoots() { return beastBoots; }

    public void setSpectatorItem(boolean toggle) { spectatorItem = toggle; }
    public void setSettingsItem(boolean toggle) { settingsItem = toggle; }
    public void setPlayAgainItem(boolean toggle) { playAgainItem = toggle; }
    public void setLeaveItem(boolean toggle) { leaveItem = toggle; }

    public int getExitSlot() { return exitSlot; }
    public int getKillerSlot() { return killerSlot; }
    public int getRunnerSlot() { return runnerSlot; }
    public int getBeastSlot() { return beastSlot; }

    public int getSpectatorSlot() { return spectatorSlot; }
    public int getSettingsSlot() { return settingsSlot; }
    public int getPlayAgainSlot() { return playAgainSlot; }
    public int getLeaveSlot() { return leaveSlot; }

    public boolean isSpectatorItem() { return spectatorItem; }
    public boolean isSettingsItem() { return settingsItem; }
    public boolean isPlayAgainItem() { return playAgainItem; }
    public boolean isLeaveItem() { return leaveItem; }

    public int getSlot(ItemStack itemStack) { return lobbyItems.get(itemStack); }

    public HashMap<ItemStack,Integer> getLobbyItems() { return lobbyItems; }
    public HashMap<ItemStack,Integer> getBeastInventory() { return beastInventory; }
    public HashMap<ItemStack,ItemFunction> getCurrentItem() { return currentItem; }

    public void unload() {
        lobbyItems.clear();
        beastInventory.clear();
        currentItem.clear();
    }

}
