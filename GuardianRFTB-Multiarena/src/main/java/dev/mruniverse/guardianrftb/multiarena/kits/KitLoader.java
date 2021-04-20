package dev.mruniverse.guardianrftb.multiarena.kits;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
@SuppressWarnings("unused")
public class KitLoader {
    private final GuardianRFTB plugin;
    private HashMap<String,KitInfo> beastKits;
    private HashMap<String,KitInfo> beastKitsUsingID;
    private HashMap<String,KitInfo> runnerKits;
    private HashMap<String,KitInfo> runnerKitsUsingID;
    private HashMap<String,KitInfo> killerKits;
    private HashMap<String,KitInfo> killerKitsUsingID;
    public KitLoader(GuardianRFTB main) {
        plugin = main;
        beastKits = new HashMap<>();
        runnerKits = new HashMap<>();
        killerKits = new HashMap<>();
        killerKitsUsingID = new HashMap<>();
        beastKitsUsingID = new HashMap<>();
        runnerKitsUsingID = new HashMap<>();
        loadKits(KitType.BEAST);
        plugin.getLogs().info(beastKits.keySet().size() + " Beast(s) Kit(s) loaded!");
        loadKits(KitType.RUNNER);
        plugin.getLogs().info(runnerKits.keySet().size() + " Runner(s) Kit(s) loaded!");
        loadKits(KitType.KILLER);
        plugin.getLogs().info(killerKits.keySet().size() + " Runner(s) Kit(s) loaded!");

    }
    public void loadKits(KitType kitType) {
        switch (kitType) {
            case KILLER:
                for(String kit : plugin.getStorage().getContent(GuardianFiles.KITS,"killerKits",false)) {
                    loadKit(KitType.KILLER,kit);
                }
                return;
            case RUNNER:
                for(String kit : plugin.getStorage().getContent(GuardianFiles.KITS,"runnerKits",false)) {
                    loadKit(KitType.RUNNER,kit);
                }
                return;
            case BEAST:
                for(String kit : plugin.getStorage().getContent(GuardianFiles.KITS,"beastKits",false)) {
                    loadKit(KitType.BEAST,kit);
                }
        }
    }
    public void getToSelect(KitType kitType, Player player,String kitName) {
        PlayerManager data = plugin.getPlayerData(player.getUniqueId());
        KitInfo kitInfo = getKits(kitType).get(kitName);
        if(kitInfo == null) return;
        String kitID = kitInfo.getID();
        switch (kitType) {
            case BEAST:
                if(data.getKits().contains(kitID)) {
                    data.setSelectedKit(kitID);
                    String selected = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.select");
                    if(selected == null) selected = "&aNow you selected kit &b%kit_name%";
                    selected = selected.replace("%kit_name%",kitName)
                            .replace("%name%",kitName)
                            .replace("%price%",kitInfo.getPrice() + "")
                            .replace("%kit_price%",kitInfo.getPrice() + "");
                    plugin.getUtils().sendMessage(player,selected);
                } else {
                    if(data.getCoins() >= kitInfo.getPrice()) {
                        int coins = data.getCoins() - kitInfo.getPrice();
                        data.setCoins(coins);
                        data.addKit(kitID);
                        data.setSelectedKit(kitID);
                        String buyKit = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.purchase");
                        if(buyKit == null) buyKit = "&aNow you have the kit &b%kit_name% &a(&3-%price%&a)";
                        buyKit = buyKit.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        String selected = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.select");
                        if(selected == null) selected = "&aNow you selected kit &b%kit_name%";
                        selected = selected.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        plugin.getUtils().sendMessage(player,buyKit);
                        plugin.getUtils().sendMessage(player,selected);
                        if(player.getInventory() == plugin.getPlayerData(player.getUniqueId()).getKitMenu(kitType).getInventory()) player.closeInventory();
                    } else {
                        String cantBuy = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.enought");
                        if(cantBuy == null) cantBuy = "&eYou need &6%price% &eto buy this kit.";
                        cantBuy = cantBuy.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        plugin.getUtils().sendMessage(player,cantBuy);
                    }
                }
                return;
            case KILLER:
                if(data.getKits().contains(kitID)) {
                    data.setSelectedKit(kitID);
                    String selected = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.select");
                    if(selected == null) selected = "&aNow you selected kit &b%kit_name%";
                    selected = selected.replace("%kit_name%",kitName)
                            .replace("%name%",kitName)
                            .replace("%price%",kitInfo.getPrice() + "")
                            .replace("%kit_price%",kitInfo.getPrice() + "");
                    plugin.getUtils().sendMessage(player,selected);
                    if(player.getInventory() == plugin.getPlayerData(player.getUniqueId()).getKitMenu(kitType).getInventory()) player.closeInventory();
                } else {
                    if(data.getCoins() >= kitInfo.getPrice()) {
                        int coins = data.getCoins() - kitInfo.getPrice();
                        data.setCoins(coins);
                        data.addKit(kitID);
                        data.setSelectedKit(kitID);
                        String buyKit = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.purchase");
                        if(buyKit == null) buyKit = "&aNow you have the kit &b%kit_name% &a(&3-%price%&a)";
                        buyKit = buyKit.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        String selected = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.select");
                        if(selected == null) selected = "&aNow you selected kit &b%kit_name%";
                        selected = selected.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        plugin.getUtils().sendMessage(player,buyKit);
                        plugin.getUtils().sendMessage(player,selected);
                        if(player.getInventory() == plugin.getPlayerData(player.getUniqueId()).getKitMenu(kitType).getInventory()) player.closeInventory();
                    } else {
                        String cantBuy = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.enought");
                        if(cantBuy == null) cantBuy = "&eYou need &6%price% &eto buy this kit.";
                        cantBuy = cantBuy.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        plugin.getUtils().sendMessage(player,cantBuy);
                    }
                }
                return;
            default:
            case RUNNER:
                if(data.getKits().contains(kitID)) {
                    data.setSelectedKit(kitID);
                    String selected = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.select");
                    if(selected == null) selected = "&aNow you selected kit &b%kit_name%";
                    selected = selected.replace("%kit_name%",kitName)
                            .replace("%name%",kitName)
                            .replace("%price%",kitInfo.getPrice() + "")
                            .replace("%kit_price%",kitInfo.getPrice() + "");
                    plugin.getUtils().sendMessage(player,selected);
                    if(player.getInventory() == plugin.getPlayerData(player.getUniqueId()).getKitMenu(kitType).getInventory()) player.closeInventory();
                } else {
                    if(data.getCoins() >= kitInfo.getPrice()) {
                        int coins = data.getCoins() - kitInfo.getPrice();
                        data.setCoins(coins);
                        data.addKit(kitID);
                        data.setSelectedKit(kitID);
                        String buyKit = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.purchase");
                        if(buyKit == null) buyKit = "&aNow you have the kit &b%kit_name% &a(&3-%price%&a)";
                        buyKit = buyKit.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        String selected = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.select");
                        if(selected == null) selected = "&aNow you selected kit &b%kit_name%";
                        selected = selected.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        plugin.getUtils().sendMessage(player,buyKit);
                        plugin.getUtils().sendMessage(player,selected);
                        if(player.getInventory() == plugin.getPlayerData(player.getUniqueId()).getKitMenu(kitType).getInventory()) player.closeInventory();
                    } else {
                        String cantBuy = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.kits.enought");
                        if(cantBuy == null) cantBuy = "&eYou need &6%price% &eto buy this kit.";
                        cantBuy = cantBuy.replace("%kit_name%",kitName)
                                .replace("%name%",kitName)
                                .replace("%price%",kitInfo.getPrice() + "")
                                .replace("%kit_price%",kitInfo.getPrice() + "");
                        plugin.getUtils().sendMessage(player,cantBuy);
                    }
                }
        }
    }
    public void updateKits() {
        beastKits = new HashMap<>();
        runnerKits = new HashMap<>();
        beastKitsUsingID = new HashMap<>();
        runnerKitsUsingID = new HashMap<>();
        killerKits = new HashMap<>();
        killerKitsUsingID = new HashMap<>();
        loadKits(KitType.BEAST);
        plugin.getLogs().info(beastKits.keySet().size() + " Beast(s) Kit(s) loaded!");
        loadKits(KitType.RUNNER);
        plugin.getLogs().info(runnerKits.keySet().size() + " Runner(s) Kit(s) loaded!");
        loadKits(KitType.KILLER);
        plugin.getLogs().info(killerKits.keySet().size() + " Runner(s) Kit(s) loaded!");
    }
    public void loadKit(KitType kitType,String kitName) {
        KitInfo kitInfo = new KitInfo(plugin,kitType,kitName);
        switch (kitType) {
            case RUNNER:
                runnerKitsUsingID.put(kitInfo.getID(),kitInfo);
                runnerKits.put(kitName,kitInfo);
                return;
            case KILLER:
                killerKitsUsingID.put(kitInfo.getID(),kitInfo);
                killerKits.put(kitName,kitInfo);
                return;
            case BEAST:
            default:
                beastKitsUsingID.put(kitInfo.getID(),kitInfo);
                beastKits.put(kitName,kitInfo);
        }
    }
    public void unloadKit(KitType kitType,String kitName) {
        switch (kitType) {
            case RUNNER:
                if(runnerKits.get(kitName) != null) runnerKitsUsingID.remove(runnerKits.get(kitName).getID());
                runnerKits.remove(kitName);
                return;
            case KILLER:
                if(killerKits.get(kitName) != null) killerKitsUsingID.remove(killerKits.get(kitName).getID());
                killerKits.remove(kitName);
                return;
            case BEAST:
                if(beastKits.get(kitName) != null) beastKitsUsingID.remove(beastKits.get(kitName).getID());
                beastKits.remove(kitName);
        }
    }
    public HashMap<String,KitInfo> getKits(KitType kitType) {
        switch (kitType) {
            case BEAST:
                return beastKits;
            case RUNNER:
            default:
                return runnerKits;
        }
    }
    public HashMap<String,KitInfo> getKitsUsingID(KitType kitType) {
        switch (kitType) {
            case KILLER:
                return killerKitsUsingID;
            case BEAST:
                return beastKitsUsingID;
            case RUNNER:
            default:
                return runnerKitsUsingID;
        }
    }
}
