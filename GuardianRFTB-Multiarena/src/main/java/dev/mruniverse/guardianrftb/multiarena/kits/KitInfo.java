package dev.mruniverse.guardianrftb.multiarena.kits;

import dev.mruniverse.guardianlib.core.utils.xseries.XMaterial;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianArmor;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class KitInfo {
    private final GuardianRFTB plugin;
    private final KitType type;
    private final String name;
    private HashMap<ItemStack, Integer> inventoryItems;
    private ItemStack kitItem = null;
    private ItemStack helmet = null;
    private ItemStack chestplate = null;
    private ItemStack leggings = null;
    private ItemStack boots = null;
    private final int price;
    private final int kitSlot;
    private final String id;
    public KitInfo(GuardianRFTB main, KitType kitType, String name) {
        this.name = name;
        inventoryItems = new HashMap<>();
        plugin = main;
        type = kitType;
        loadArmor();
        loadKitItem();
        loadInventory();
        price = plugin.getStorage().getControl(GuardianFiles.KITS).getInt(getPath() + ".KitInfo.price");
        kitSlot = plugin.getStorage().getControl(GuardianFiles.KITS).getInt(getPath() + ".KitInfo.menuSlot");
        id = plugin.getStorage().getControl(GuardianFiles.KITS).getString(getPath() + ".KitInfo.kitID");
    }
    public void loadKitItem() {
        FileConfiguration items = plugin.getStorage().getControl(GuardianFiles.KITS);
        String material = items.getString(getPath() + ".KitInfo.item");
        if(material == null) return;
        if(material.equalsIgnoreCase("DISABLE")) return;
        Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
        if(!optionalXMaterial.isPresent()) return;
        String itemName = items.getString(getPath() + ".KitInfo.name");
        List<String> lore = items.getStringList(getPath() + ".KitInfo.lore");
        ItemStack returnItem = plugin.getLib().getUtils().getItem(optionalXMaterial.get(),itemName,lore);
        if(items.get(getPath() + ".KitInfo.enchantments") == null) kitItem = returnItem;
        int amount = getAmount(items,getPath() + ".KitInfo.amount");
        returnItem.setAmount(amount);
        List<String> enchantments = plugin.getStorage().getControl(GuardianFiles.KITS).getStringList(getPath() + ".KitInfo.enchantments");
        kitItem = plugin.getLib().getUtils().getEnchantmentList(returnItem, enchantments,"none");
    }
    public void loadInventory() {
        try {
            inventoryItems = new HashMap<>();
            FileConfiguration items = plugin.getStorage().getControl(GuardianFiles.KITS);
            ConfigurationSection section = plugin.getStorage().getControl(GuardianFiles.KITS).getConfigurationSection(getPath() + ".Inventory");
            if (section == null) return;
            for (String itemInfo : section.getKeys(false)) {
                String material = items.getString(getPath() + ".Inventory." + itemInfo + ".item");
                int amount = getAmount(items,getPath() + ".Inventory." + itemInfo + ".amount");
                if (material == null) material = "BEDROCK";
                Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
                if (optionalXMaterial.isPresent()) {
                    XMaterial m = optionalXMaterial.get();
                    if (m.parseMaterial() != null) {
                        String itemName = items.getString(getPath() + ".Inventory." + itemInfo + ".name");
                        Integer slot = items.getInt(getPath() + ".Inventory." + itemInfo + ".slot");
                        List<String> lore = items.getStringList(getPath() + ".Inventory." + itemInfo + ".lore");
                        ItemStack item = plugin.getLib().getUtils().getItem(m, itemName, lore);
                        item.setAmount(amount);

                        if (items.get(getPath() + ".Inventory." + itemInfo + ".enchantments") != null) {
                            List<String> enchantments = items.getStringList(getPath() + ".Inventory." + itemInfo + ".enchantments");
                            item = plugin.getLib().getUtils().getEnchantmentList(item, enchantments,"none");
                        }
                        inventoryItems.put(item, slot);
                    }
                } else {
                    plugin.getLogs().error("Item: " + material + " doesn't exists.");
                }
            }
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't load inventory items of kit: " + name);
        }
    }

    public void loadArmor() {
        helmet = loadPart(GuardianArmor.HELMET);
        chestplate = loadPart(GuardianArmor.CHESTPLATE);
        leggings = loadPart(GuardianArmor.LEGGINGS);
        boots = loadPart(GuardianArmor.BOOTS);
    }

    private ItemStack loadPart(GuardianArmor armorPart) {
        FileConfiguration items = plugin.getStorage().getControl(GuardianFiles.KITS);
        String material = items.getString(getPath() + ".Armor." + armorPart.getPartName() + ".item");
        if(material == null) return null;
        if(material.equalsIgnoreCase("DISABLE")) return null;
        Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
        if(!optionalXMaterial.isPresent()) return null;
        XMaterial m = optionalXMaterial.get();
        if (m.parseMaterial() == null) return null;
        String itemName = items.getString(getPath() + ".Armor." + armorPart.getPartName() + ".name");
        List<String> lore = items.getStringList(getPath() + ".Armor." + armorPart.getPartName() + ".lore");
        ItemStack returnItem = plugin.getLib().getUtils().getItem(m,itemName,lore);
        if(items.get(getPath() + ".Armor." + armorPart.getPartName() + ".enchantments") == null) return returnItem;
        List<String> enchantments = items.getStringList(getPath() + ".Armor." + armorPart.getPartName() + ".enchantments");
        return plugin.getLib().getUtils().getEnchantmentList(returnItem, enchantments,"none");
    }

    private String getPath() {
        switch (type) {
            case BEAST:
                return "beastKits." + name;
            case RUNNER:
            default:
                return "runnerKits." + name;
        }
    }

    public int getKitSlot() {
        return kitSlot;
    }

    public HashMap<ItemStack, Integer> getInventoryItems() {
        return inventoryItems;
    }

    public ItemStack getArmor(GuardianArmor armorPart) {
        switch (armorPart) {
            case HELMET:
                return helmet;
            case LEGGINGS:
                return leggings;
            case BOOTS:
                return boots;
            default:
            case CHESTPLATE:
                return chestplate;
        }
    }

    public int getAmount(FileConfiguration fileConfiguration,String path) {
        if(fileConfiguration.contains(path)) {
            return fileConfiguration.getInt(path);
        }
        return 1;
    }

    public ItemStack getKitItem() {
        return kitItem;
    }

    public String getName() { return name; }
    public String getID() { return id; }
    public int getPrice() {
        return price;
    }

    public KitType getType() {
        return type;
    }
}

