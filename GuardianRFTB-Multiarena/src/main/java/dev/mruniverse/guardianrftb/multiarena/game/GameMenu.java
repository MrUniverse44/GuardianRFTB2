package dev.mruniverse.guardianrftb.multiarena.game;

import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianlib.core.utils.xseries.XMaterial;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class GameMenu {
    private final GuardianRFTB plugin;
    private final GameType gameType;
    private String name;
    private Inventory chestInventory;
    private String waiting;
    private String starting;
    private String playing;
    private String ending;
    private List<String> lore;
    private String iName;
    public GameMenu(GuardianRFTB plugin,GameType gameType) {
        this.plugin = plugin;
        this.gameType = gameType;
        createInv();
        loadItems();
    }
    private void createInv() {
        String invName = plugin.getStorage().getControl(GuardianFiles.MENUS).getString("menus.game.inventoryName");

        if(invName == null) invName = "&8Games";

        invName = ChatColor.translateAlternateColorCodes('&',invName);

        int rows = getRows(plugin.getStorage().getControl(GuardianFiles.MENUS).getInt("menus.game.inventoryRows"));

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
        int slot = 0;
        int maxSlot = chestInventory.getSize();
        for(Game game : plugin.getGameManager().getGames()) {
            if(game.getGameType() == gameType) {
                if (slot != maxSlot) {
                    ItemStack gameItem = getGameItem(game);
                    chestInventory.setItem(slot, gameItem);
                }
                slot++;
            }
        }
    }
    public void setSlots() {
        int slot = 0;
        for(Game game : plugin.getGameManager().getGames()) {
            if(game.getGameType() == gameType) {
                game.menuSlot = slot;
                slot++;
            }
        }
    }
    public void updateSlot(int slot,Game game) {
        if(slot != -1) {
            ItemStack gameItem = getGameItem(game);
            chestInventory.setItem(slot, gameItem);
            return;
        }
        setSlots();
    }
    public HashMap<ItemStack,String> getGameItems() {
        HashMap<ItemStack,String> hash = new HashMap<>();
        for(Game game : plugin.getGameManager().getGames()) {
            if(game.getGameType() == gameType) {
                ItemStack gameItem = getGameItem(game);
                hash.put(gameItem, game.getConfigName());
            }
        }
        return hash;
    }
    private List<String> getLore(Game game) {
        List<String> newLore = new ArrayList<>();
        for(String line : lore) {
            String newLine = "&7" + line.replace("%map_name%", game.getName()).replace("%map_status%", game.gameStatus.getStatus()).replace("%map_mode%", game.getGameType().getType()).replace("%map_on%", game.getPlayers().size() + "").replace("%map_max%", game.max + "");
            newLore.add(newLine);
        }
        return Utils.recolorLore(newLore);
    }
    private ItemStack getGameItem(Game game) {
        String name = iName.replace("%map_name%", game.getName()
                .replace("%map_status%", game.gameStatus.getStatus()
                        .replace("%map_mode%", game.getGameType().getType())
                        .replace("%map_on%", game.getPlayers().size() + "")
                        .replace("%map_max%", game.max + "")));
        Utils utils = plugin.getLib().getUtils();
        Optional<XMaterial> optionalXMaterial;
        switch (game.gameStatus) {
            case IN_GAME:
            case PLAYING:
                optionalXMaterial = XMaterial.matchXMaterial(playing);
                if(optionalXMaterial.isPresent()) return utils.getItem(optionalXMaterial.get(),name,getLore(game));
            case STARTING:
                optionalXMaterial = XMaterial.matchXMaterial(starting);
                if(optionalXMaterial.isPresent()) return utils.getItem(optionalXMaterial.get(),name,getLore(game));
            case PREPARING:
            case RESTARTING:
                optionalXMaterial = XMaterial.matchXMaterial(ending);
                if(optionalXMaterial.isPresent()) return utils.getItem(optionalXMaterial.get(),name,getLore(game));
            case WAITING:
            default:
                optionalXMaterial = XMaterial.matchXMaterial(waiting);
                return optionalXMaterial.map(xMaterial -> utils.getItem(xMaterial, name, getLore(game))).orElse(null);
        }
    }

    public void reloadMenu() {
        loadItems();
    }

    private void loadItems() {
        FileConfiguration loadConfig = plugin.getStorage().getControl(GuardianFiles.MENUS);
        try {
            String WaitingMaterial = loadConfig.getString("menus.game.item-status.waiting");
            String StartingMaterial = loadConfig.getString("menus.game.item-status.starting");
            String PlayingMaterial = loadConfig.getString("menus.game.item-status.playing");
            String EndingMaterial = loadConfig.getString("menus.game.item-status.ending");
            String itemName = loadConfig.getString("menus.game.item.name");

            List<String> itemLore = loadConfig.getStringList("menus.game.item.lore");

            if(WaitingMaterial == null) WaitingMaterial = "STAINED_CLAY:5";
            if(StartingMaterial == null) StartingMaterial = "STAINED_CLAY:4";
            if(PlayingMaterial == null) PlayingMaterial = "STAINED_CLAY:14";
            if(EndingMaterial == null) EndingMaterial = "STAINED_CLAY:3";
            if(itemName == null) itemName = "&e&nMap: %map_name%";

            lore = itemLore;
            iName = itemName;

            waiting = WaitingMaterial;
            starting = StartingMaterial;
            playing = PlayingMaterial;
            ending = EndingMaterial;

            pasteItems();
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't get chests items on startup");
            plugin.getLogs().error(throwable);
        }
    }
    public Inventory getInventory() {
        pasteItems();
        return chestInventory;
    }
    public void setName(String newName) { name = newName; }
    public String getName() { return name; }


}
