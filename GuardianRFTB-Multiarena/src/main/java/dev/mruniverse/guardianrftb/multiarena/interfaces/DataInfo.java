package dev.mruniverse.guardianrftb.multiarena.interfaces;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;

import java.util.HashMap;
import java.util.UUID;

public interface DataInfo {
    GuardianRFTB getPlugin();

    HashMap<String, Integer> getCoins();

    HashMap<String, String> getKits();

    HashMap<String, String> getSelectedKits();

    void setPlugin(GuardianRFTB plugin);

    int getCoins(UUID uuid);

    String getKits(UUID uuid);

    String getSelectedKit(UUID uuid);

    void setCoins(UUID uuid,Integer value);

    void setKits(UUID uuid,String value);

    void setSelectedKit(UUID uuid,String value);

    boolean exists(UUID uuid);

    void savePlayer(UUID uuid);

    void addPlayer(UUID uuid);

    void save();

}
