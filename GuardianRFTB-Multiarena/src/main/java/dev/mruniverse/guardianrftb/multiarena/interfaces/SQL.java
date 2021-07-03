package dev.mruniverse.guardianrftb.multiarena.interfaces;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public interface SQL {

    HashMap<String, Integer> getCoins();

    HashMap<String, String> getKits();

    HashMap<String, String> getSelectedKits();

    void putData();

    void loadData();

    int getCoins(UUID uuid);

    String getKits(UUID uuid);

    String getSelectedKit(UUID uuid);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean exist(UUID uuid);

    void createPlayer(Player player);
}
