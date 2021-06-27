package dev.mruniverse.guardianrftb.bungeegame.storage;


import dev.mruniverse.guardianrftb.bungeegame.GuardianRFTB;
import dev.mruniverse.guardianrftb.bungeegame.enums.GuardianFiles;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("unused")
public class DataInfo {
    private final GuardianRFTB plugin;
    public HashMap<String, Integer> coins = new HashMap<>();
    public HashMap<String, String> kits = new HashMap<>();
    public HashMap<String, String> selectedKits = new HashMap<>();

    public DataInfo(GuardianRFTB plugin) {
        this.plugin = plugin;
    }

    public int getCoins(UUID uuid) { return coins.get(uuid.toString().replace("-","")); }
    public String getKits(UUID uuid) { return kits.get(uuid.toString().replace("-","")); }
    public String getSelectedKit(UUID uuid) { return selectedKits.get(uuid.toString().replace("-","")); }

    public void setCoins(UUID uuid,Integer value) { coins.put(uuid.toString().replace("-",""), value); }
    public void setKits(UUID uuid,String value) { kits.put(uuid.toString().replace("-",""), value); }
    public void setSelectedKit(UUID uuid,String value) { selectedKits.put(uuid.toString().replace("-",""), value); }

    public boolean exists(UUID uuid) { return coins.containsKey(uuid.toString().replace("-","")); }

    public void savePlayer(UUID uuid) {
        String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
        String id = uuid.toString().replace("-","");
        plugin.getData().setInt(table, "Coins", "Player", id, coins.get(id));
        plugin.getData().setString(table, "SelectedKit", "Player", id, selectedKits.get(id));
        plugin.getData().setString(table, "Kits", "Player", id, kits.get(id));
        coins.remove(id);
        selectedKits.remove(id);
        kits.remove(id);
    }

    public void addPlayer(UUID uuid) {
        if(exists(uuid)) return;
        String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
        String id = uuid.toString().replace("-","");
        DataStorage storage = plugin.getData();
        coins.put(id,storage.getInt(table,"Coins","Player",id));
        selectedKits.put(id,storage.getString(table,"SelectedKit","Player",id));
        kits.put(id,storage.getString(table,"Kits","Player",id));
    }

    public void save() {
        String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
        for(String id : kits.keySet()) {
            plugin.getData().setString(table, "Kits", "Player", id, kits.get(id));
        }
        kits.clear();
        for(String id : selectedKits.keySet()) {
            plugin.getData().setString(table, "SelectedKit", "Player", id, selectedKits.get(id));
        }
        selectedKits.clear();
        for(String id : coins.keySet()) {
            plugin.getData().setInt(table, "Coins", "Player", id, coins.get(id));
        }
        coins.clear();
    }

}

