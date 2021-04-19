package dev.mruniverse.guardianrftb.multiarena.game;

import dev.mruniverse.guardianlib.core.utils.xseries.XMaterial;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class GameChests {
    private final GuardianRFTB plugin;
    private String name;
    private final String chestID;
    private final HashMap<ItemStack, Integer> chestItems = new HashMap<>();
    private Inventory chestInventory;
    public GameChests(GuardianRFTB main, String chestName) {
        plugin = main;
        chestID = chestName;
        createInv();
        loadItems();
    }
    private void createInv() {
        String invName = plugin.getStorage().getControl(GuardianFiles.CHESTS).getString("chests." + chestID + ".inventoryName");

        if(invName == null) invName = "&8Chest";

        invName = ChatColor.translateAlternateColorCodes('&',invName);

        int rows = getRows(plugin.getStorage().getControl(GuardianFiles.CHESTS).getInt("chests." + chestID + ".inventoryRows"));

        chestInventory = plugin.getServer().createInventory(null,rows,invName);
    }
    private int getRows(int small) {
        if(small == 1) return 9;
        if(small == 2) return 18;
        if(small == 3) return 27;
        if(small == 4) return 36;
        if(small == 5) return 46;
        return 54;
    }
    private void pasteItems() {
        chestInventory.clear();
        for(ItemStack item : chestItems.keySet()) {
            chestInventory.setItem(getSlot(item),item);
        }
    }
    private void loadItems() {
        FileConfiguration loadConfig = plugin.getStorage().getControl(GuardianFiles.CHESTS);
        String path = "chests." + chestID + ".items.";
        try {
            ConfigurationSection section = loadConfig.getConfigurationSection("chests." + chestID + ".items");
            if(section == null) throw new Throwable("Can't found beast inventory section in chests.yml");
            for (String item : section.getKeys(false)) {
                String material = loadConfig.getString(path + item + ".item");
                int amount = getAmount(loadConfig,path + item + ".amount");
                if(material == null) material = "BEDROCK";
                Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
                if(optionalXMaterial.isPresent()) {
                    String itemName = loadConfig.getString(path + item + ".name");
                    Integer slot = loadConfig.getInt(path + item + ".slot");
                    List<String> lore = loadConfig.getStringList(path + item + ".lore");
                    ItemStack currentItem = plugin.getLib().getUtils().getItem(optionalXMaterial.get(),itemName,lore);
                    currentItem.setAmount(amount);
                    if(loadConfig.get(path + item + ".enchantments") != null) {
                        currentItem = plugin.getLib().getUtils().getEnchantmentList(currentItem, loadConfig.getStringList(path + item + ".enchantments"),"none");
                    }
                    chestItems.put(currentItem,slot);
                } else {
                    plugin.getLogs().error("Item: " + material + " doesn't exists.");
                }
            }
            pasteItems();
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't get chests items on startup");
            plugin.getLogs().error(throwable);
        }
    }
    public int getSlot(ItemStack item) { return chestItems.get(item); }
    public Inventory getInventory() {
        pasteItems();
        return chestInventory;
    }
    public int getAmount(FileConfiguration fileConfiguration,String path) {
        if(fileConfiguration.contains(path)) {
            return fileConfiguration.getInt(path);
        }
        return 1;
    }
    public void setName(String newName) { name = newName; }
    public String getName() { return name; }


}
