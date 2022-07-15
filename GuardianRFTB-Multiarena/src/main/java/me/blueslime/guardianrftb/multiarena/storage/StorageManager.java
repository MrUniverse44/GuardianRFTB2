package me.blueslime.guardianrftb.multiarena.storage;

import me.blueslime.guardianrftb.multiarena.enums.GameType;
import me.blueslime.guardianrftb.multiarena.game.GameChests;
import me.blueslime.guardianrftb.multiarena.game.GameMenu;
import me.blueslime.guardianrftb.multiarena.interfaces.Game;
import me.blueslime.guardianrftb.multiarena.player.GamePlayer;
import me.blueslime.guardianrftb.multiarena.storage.players.PluginStorage;
import org.bukkit.World;

import java.util.HashMap;
import java.util.UUID;

public class StorageManager {

    private final PluginStorage<String, GameChests> chestStorage = new PluginStorage<>(new HashMap<>());

    private final PluginStorage<GameType, GameMenu> menuStorage = new PluginStorage<>(new HashMap<>());

    private final PluginStorage<UUID, GamePlayer> playerStorage = new PluginStorage<>(new HashMap<>());

    private final PluginStorage<World, Game> gameWorldStorage = new PluginStorage<>(new HashMap<>());


    public PluginStorage<UUID, GamePlayer> getPlayerStorage() {
        return playerStorage;
    }

    public PluginStorage<GameType, GameMenu> getMenuStorage() {
        return menuStorage;
    }

    public PluginStorage<String, GameChests> getChestStorage() {
        return chestStorage;
    }

    public PluginStorage<World, Game> getGameWorldStorage() {
        return gameWorldStorage;
    }
}
