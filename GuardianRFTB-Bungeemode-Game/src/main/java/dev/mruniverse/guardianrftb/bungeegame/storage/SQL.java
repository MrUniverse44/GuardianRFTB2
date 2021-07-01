package dev.mruniverse.guardianrftb.bungeegame.storage;

import dev.mruniverse.guardianrftb.bungeegame.GuardianRFTB;
import dev.mruniverse.guardianrftb.bungeegame.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.bungeegame.enums.SaveMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQL {
    public HashMap<String, Integer> coins = new HashMap<>();
    public HashMap<String, String> kits = new HashMap<>();
    public HashMap<String, String> selectedKits = new HashMap<>();
    private final GuardianRFTB plugin;
    public SQL(GuardianRFTB main) {
        plugin = main;
    }

    public void putData() {
        if (coins.size() != 0)
            for (Map.Entry<String, Integer> k : coins.entrySet())
                plugin.getStorage().getControl(GuardianFiles.DATA).set("coins." + k.getKey(), k.getValue());
        if (kits.size() != 0)
            for (Map.Entry<String, String> k : kits.entrySet())
                plugin.getStorage().getControl(GuardianFiles.DATA).set("kits." + k.getKey(), k.getValue());
        if (selectedKits.size() != 0)
            for (Map.Entry<String, String> k : selectedKits.entrySet())
                plugin.getStorage().getControl(GuardianFiles.DATA).set("selected-kit." + k.getKey(), k.getValue());
        plugin.getStorage().save(SaveMode.DATA);
    }

    public void loadData() {
        FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.DATA);
        if(plugin.getStorage().getControl(GuardianFiles.DATA).contains("selected-kit")) {
            for (String player : plugin.getStorage().getContent(GuardianFiles.DATA,"selected-kit",false)) {
                selectedKits.put(player,file.getString("selected-kit." + player));
            }
        }
        if(plugin.getStorage().getControl(GuardianFiles.DATA).contains("kits")) {
            for (String player : plugin.getStorage().getContent(GuardianFiles.DATA,"kits",false)) {
                selectedKits.put(player,file.getString("kits." + player));
            }
        }
        if(plugin.getStorage().getControl(GuardianFiles.DATA).contains("coins")) {
            for (String player : plugin.getStorage().getContent(GuardianFiles.DATA,"coins",false)) {
                selectedKits.put(player,file.getString("coins." + player));
            }
        }
    }

    public int getCoins(UUID uuid) { return coins.get(uuid.toString().replace("-","")); }
    public String getKits(UUID uuid) { return kits.get(uuid.toString().replace("-","")); }
    public String getSelectedKit(UUID uuid) { return selectedKits.get(uuid.toString().replace("-","")); }
    public boolean exist(UUID uuid) { return kits.containsKey(uuid.toString().replace("-","")); }

    public void createPlayer(Player player) {
        if(!exist(player.getUniqueId())) {
            coins.put(player.getUniqueId().toString().replace("-", ""), 0);
            String defaultRunner = plugin.getSettings().getSettings().getString("settings.default-kits.runner");
            String defaultBeast = plugin.getSettings().getSettings().getString("settings.default-kits.beast");
            String defaultKiller = plugin.getSettings().getSettings().getString("settings.default-kits.killer");
            if (plugin.getSettings().getSettings().getBoolean("settings.default-kits.toggle")) {

                kits.put(player.getUniqueId().toString().replace("-", ""), "K" + defaultRunner + ",K" + defaultBeast + ",K" + defaultKiller);
                selectedKits.put(player.getUniqueId().toString().replace("-", ""), defaultRunner);
            } else {
                kits.put(player.getUniqueId().toString().replace("-", ""), "NONE");
                selectedKits.put(player.getUniqueId().toString().replace("-", ""), "NONE");
            }
        } else {
            String id = player.getUniqueId().toString().replace("-","");
            PlayerManager playerManager = plugin.getUser(player.getUniqueId());
            playerManager.setCoins(coins.get(id));
            playerManager.setSelectedKit(selectedKits.get(id));
            playerManager.setKits(kits.get(id));
        }
    }
}
