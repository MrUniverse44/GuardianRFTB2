package dev.mruniverse.guardianrftb.multiarena.storage;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.interfaces.DataInfo;
import dev.mruniverse.guardianrftb.multiarena.interfaces.DataStorage;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("unused")
public class DataInfoImpl implements DataInfo {
    private GuardianRFTB plugin;
    public HashMap<String, Integer> coins = new HashMap<>();
    public HashMap<String, String> kits = new HashMap<>();
    public HashMap<String, String> selectedKits = new HashMap<>();

    public DataInfoImpl(GuardianRFTB plugin) {
        this.plugin = plugin;
    }

    @Override
    public GuardianRFTB getPlugin() { return plugin; }

    @Override
    public HashMap<String, Integer> getCoins() { return coins; }

    @Override
    public HashMap<String, String> getKits() { return kits; }

    @Override
    public HashMap<String, String> getSelectedKits() { return selectedKits; }

    @Override
    public void setPlugin(GuardianRFTB plugin) { this.plugin = plugin; }

    @Override
    public int getCoins(UUID uuid) { return coins.get(uuid.toString().replace("-","")); }

    @Override
    public String getKits(UUID uuid) { return kits.get(uuid.toString().replace("-","")); }

    @Override
    public String getSelectedKit(UUID uuid) { return selectedKits.get(uuid.toString().replace("-","")); }

    @Override
    public void setCoins(UUID uuid,Integer value) { coins.put(uuid.toString().replace("-",""), value); }

    @Override
    public void setKits(UUID uuid,String value) { kits.put(uuid.toString().replace("-",""), value); }

    @Override
    public void setSelectedKit(UUID uuid,String value) { selectedKits.put(uuid.toString().replace("-",""), value); }

    @Override
    public boolean exists(UUID uuid) { return coins.containsKey(uuid.toString().replace("-","")); }

    @Override
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

    @Override
    public void addPlayer(UUID uuid) {
        if(exists(uuid)) return;
        String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
        String id = uuid.toString().replace("-","");
        DataStorage storage = plugin.getData();
        coins.put(id,storage.getInt(table,"Coins","Player",id));
        selectedKits.put(id,storage.getString(table,"SelectedKit","Player",id));
        kits.put(id,storage.getString(table,"Kits","Player",id));
    }

    @Override
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
