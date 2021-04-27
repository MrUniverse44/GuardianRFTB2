package dev.mruniverse.guardianrftb.multiarena.storage;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.SaveMode;
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
        coins.put(player.getUniqueId().toString().replace("-",""), 0);
        kits.put(player.getUniqueId().toString().replace("-",""),"K" + plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.defaultKitID"));
        selectedKits.put(player.getUniqueId().toString().replace("-",""),"NONE");
    }
}
