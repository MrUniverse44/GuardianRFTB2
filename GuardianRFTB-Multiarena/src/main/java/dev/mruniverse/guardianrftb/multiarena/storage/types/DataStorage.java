package dev.mruniverse.guardianrftb.multiarena.storage.types;

import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import dev.mruniverse.guardianrftb.multiarena.storage.result.DataResult;
import org.bukkit.entity.Player;

public interface DataStorage {

    String getIdentifier();

    void initialize();

    void shutdown();

    void reload();

    void save(GamePlayer gamePlayer);

    void saveAllPlayers();

    DataResult load(Player player);
    void loadAllPlayers();
}
