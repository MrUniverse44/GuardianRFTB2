package dev.mruniverse.guardianrftb.bungeegame.listeners.api;

import dev.mruniverse.guardianrftb.bungeegame.interfaces.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameSelectedBeastEvent extends Event {
    private final Game game;
    private final Player player;
    private static final HandlerList handlerList = new HandlerList();


    public GameSelectedBeastEvent(Game currentGame, Player currentSelectedBeast) {
        game = currentGame;
        player = currentSelectedBeast;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
    public Game getCurrentGame() {
        return game;
    }
    public Player getPlayer() { return player; }
}
