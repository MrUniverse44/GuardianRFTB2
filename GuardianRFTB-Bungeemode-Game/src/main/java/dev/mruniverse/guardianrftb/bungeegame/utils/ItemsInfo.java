package dev.mruniverse.guardianrftb.bungeegame.utils;

import dev.mruniverse.guardianrftb.bungeegame.enums.ItemFunction;
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

    private final HashMap<ItemStack, Integer> beastInventory = new HashMap<>();
    private final HashMap<ItemStack, ItemFunction> currentItem = new HashMap<>();

    public void setExit(ItemStack itemStack) { exit = itemStack; }
    public void setCheckPoint(ItemStack itemStack) { checkPoint = itemStack; }
    public void setKitRunner(ItemStack itemStack) { kitRunner = itemStack; }
    public void setKitKiller(ItemStack itemStack) { kitKiller = itemStack; }
    public void setKitBeast(ItemStack itemStack) { kitBeast = itemStack; }
    public void setBeastHelmet(ItemStack itemStack) { beastHelmet = itemStack; }
    public void setBeastChestplate(ItemStack itemStack) { beastChestplate = itemStack; }
    public void setBeastLeggings(ItemStack itemStack) { beastLeggings = itemStack; }
    public void setBeastBoots(ItemStack itemStack) { beastBoots = itemStack; }


    public void setExitSlot(int slot) { exitSlot = slot; }
    public void setRunnerSlot(int slot) { runnerSlot = slot; }
    public void setKillerSlot(int slot) { killerSlot = slot; }
    public void setBeastSlot(int slot) { beastSlot = slot; }

    public ItemStack getExit() { return exit; }
    public ItemStack getCheckPoint() { return checkPoint; }
    public ItemStack getKitRunner() { return kitRunner; }
    public ItemStack getKitBeast() { return kitBeast; }
    public ItemStack getKitKiller() { return kitKiller; }
    public ItemStack getBeastHelmet() { return beastHelmet; }
    public ItemStack getBeastChestplate() { return beastChestplate; }
    public ItemStack getBeastLeggings() { return beastLeggings; }
    public ItemStack getBeastBoots() { return beastBoots; }

    public int getExitSlot() { return exitSlot; }
    public int getKillerSlot() { return killerSlot; }
    public int getRunnerSlot() { return runnerSlot; }
    public int getBeastSlot() { return beastSlot; }
    public HashMap<ItemStack,Integer> getBeastInventory() { return beastInventory; }
    public HashMap<ItemStack,ItemFunction> getCurrentItem() { return currentItem; }

}
