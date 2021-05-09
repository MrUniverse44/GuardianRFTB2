package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianlib.core.utils.xseries.XMaterial;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.KitType;
import dev.mruniverse.guardianrftb.multiarena.enums.ShopAction;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopMenu {
    private final GuardianRFTB plugin;
    private String name;
    private HashMap<ItemStack,Integer> shopItems;
    private HashMap<ItemStack, ShopAction> shopAction;
    private HashMap<Integer,ItemStack> fills;
    private Inventory chestInventory;
    public ShopMenu(GuardianRFTB main) {
        plugin = main;
        shopItems = new HashMap<>();
        shopAction = new HashMap<>();
        fills = new HashMap<>();
        createInv();
        loadItems();
    }
    public void updateInv(){
        shopItems = new HashMap<>();
        shopAction = new HashMap<>();
        fills = new HashMap<>();
        createInv();
        loadItems();
    }
    private void createInv() {
        String invName = plugin.getStorage().getControl(GuardianFiles.MENUS).getString("menus.shop.inventoryName");

        if(invName == null) invName = "&8Shop Menu";

        invName = ChatColor.translateAlternateColorCodes('&',invName);

        int rows = getRows(plugin.getStorage().getControl(GuardianFiles.MENUS).getInt("menus.shop.inventoryRows"));

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
    private void addOnlyFill(ItemStack itemFill,String fillPath) {
        for(int slot : plugin.getStorage().getControl(GuardianFiles.MENUS).getIntegerList(ShopAction.FILL.getPath() + "." + fillPath + ".list.values")) {
            fills.put(slot,itemFill);
        }
    }
    private void addIgnoreFill(ItemStack itemFill,String fillPath) {
        List<Integer> ignoreSlots = plugin.getStorage().getControl(GuardianFiles.MENUS).getIntegerList(ShopAction.FILL.getPath() + "." + fillPath + ".list.values");
        for(int i = 0; i < chestInventory.getSize(); i++) {
            if(!ignoreSlots.contains(i)) fills.put(i,itemFill);
        }
    }
    public void execute(Player player,ShopAction action) {
        switch (action) {
            case KIT_BEASTS:
                player.openInventory(plugin.getPlayerData(player.getUniqueId()).getKitMenu(KitType.BEAST).getInventory());
                return;
            case KIT_RUNNERS:
                player.openInventory(plugin.getPlayerData(player.getUniqueId()).getKitMenu(KitType.RUNNER).getInventory());
                return;
            case KIT_KILLERS:
                player.openInventory(plugin.getPlayerData(player.getUniqueId()).getKitMenu(KitType.KILLER).getInventory());
                return;
            default:
                plugin.getUtils().sendMessage(player,"&cSoon..");
        }
    }
    private void loadFills() {
        FileConfiguration menu = plugin.getStorage().getControl(GuardianFiles.MENUS);
        Utils utils = plugin.getLib().getUtils();
        for(String fills : plugin.getStorage().getContent(GuardianFiles.MENUS,ShopAction.FILL.getPath(),false)) {
            String name = menu.getString(ShopAction.FILL.getPath() + "." + fills + ".name");
            String material = menu.getString(ShopAction.FILL.getPath() + "." + fills + ".item");
            List<String> lore = menu.getStringList(ShopAction.FILL.getPath() + "." + fills + ".lore");
            if(material == null) material = "BEDROCK";
            Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
            if(optionalXMaterial.isPresent()) {
                ItemStack item = utils.getItem(optionalXMaterial.get(), name, lore);
                if (menu.get(ShopAction.FILL.getPath() + "." + fills + ".list.type") == null) return;
                String check = menu.getString(ShopAction.FILL.getPath() + "." + fills + ".list.type");
                shopAction.put(item, ShopAction.FILL);
                if (check == null) check = "ONLY";
                if (check.equalsIgnoreCase("ONLY")) {
                    addOnlyFill(item, fills);
                } else {
                    addIgnoreFill(item, fills);
                }
            }
        }
    }
    private void loadItems() {
        FileConfiguration menu = plugin.getStorage().getControl(GuardianFiles.MENUS);
        Utils utils = plugin.getLib().getUtils();
        for(ShopAction shopAction : ShopAction.values()) {
            if(shopAction != ShopAction.FILL && shopAction != ShopAction.CUSTOM) {
                String name = menu.getString(shopAction.getPath() + ".name");
                String material = menu.getString(shopAction.getPath() + ".item");
                List<String> lore = menu.getStringList(shopAction.getPath() + ".lore");
                int slot = menu.getInt(shopAction.getPath() + ".slot");
                if(material == null) material = "BEDROCK";
                Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);;
                if(optionalXMaterial.isPresent()) {
                    ItemStack item = utils.getItem(optionalXMaterial.get(), name, lore);
                    shopItems.put(item, slot);
                    this.shopAction.put(item, shopAction);
                }
            }
        }

        loadFills();
    }
    private void pasteItems() {
        chestInventory.clear();
        for(Map.Entry<ItemStack,Integer> data : shopItems.entrySet()) {
            chestInventory.setItem(data.getValue(),data.getKey());
        }
        for(Map.Entry<Integer,ItemStack> fills : fills.entrySet()) {
            chestInventory.setItem(fills.getKey(),fills.getValue());
        }
    }
    public HashMap<ItemStack,ShopAction> getItems() {
        return shopAction;
    }
    public Inventory getInventory() {
        pasteItems();
        return chestInventory;
    }
    public void setName(String newName) { name = newName; }
    public String getName() { return name; }
}
