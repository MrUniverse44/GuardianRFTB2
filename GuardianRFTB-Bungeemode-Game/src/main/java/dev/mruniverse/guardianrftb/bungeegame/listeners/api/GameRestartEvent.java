package dev.mruniverse.guardianrftb.bungeegame.listeners.api;

import dev.mruniverse.guardianrftb.bungeegame.interfaces.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameRestartEvent extends Event {
    private final Game game;
    private static final HandlerList handlerList = new HandlerList();

    public GameRestartEvent(Game currentGame) {
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
