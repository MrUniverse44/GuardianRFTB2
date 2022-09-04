package me.blueslime.stylizedrftb.multiarena.storage;

import me.blueslime.stylizedrftb.multiarena.enums.GameType;
import me.blueslime.guardianrftb.multiarena.game.GameChests;
import me.blueslime.guardianrftb.multiarena.game.GameMenu;
import me.blueslime.stylizedrftb.multiarena.interfaces.Game;
import me.blueslime.stylizedrftb.multiarena.player.GamePlayer;
import me.blueslime.stylizedrftb.multiarena.player.PluginStorage;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StorageManager {
    private final PluginStorage<UUID, GamePlayer> playerStorage = PluginStorage.initAsHash();

    public PluginStorage<UUID, GamePlayer> getPlayerStorage() {
        return playerStorage;
    }

    public GamePlayer fetchGamePlayer(Player player) {
        return new GamePlayer(player);
    }

}
