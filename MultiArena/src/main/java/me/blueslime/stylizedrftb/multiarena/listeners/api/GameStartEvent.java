package me.blueslime.stylizedrftb.multiarena.listeners.api;

import me.blueslime.stylizedrftb.multiarena.interfaces.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameStartEvent extends Event {
    private final Game game;
    private static final HandlerList handlerList = new HandlerList();

    public GameStartEvent(Game currentGame) {
        game = currentGame;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
    public Game getCurrentGame() {
        return game;
    }
}
