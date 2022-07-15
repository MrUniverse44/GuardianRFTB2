package dev.mruniverse.guardianrftb.multiarena.storage;

import dev.mruniverse.guardianrftb.multiarena.player.GamePlayer;
import dev.mruniverse.guardianrftb.multiarena.storage.players.PluginStorage;

import java.util.HashMap;
import java.util.UUID;

public class StorageManager {

    private final PluginStorage<UUID, GamePlayer> playerMap = new PluginStorage<>(new HashMap<>());

    public PluginStorage<UUID, GamePlayer> getPlayerMap() {
        return playerMap;
    }
}
