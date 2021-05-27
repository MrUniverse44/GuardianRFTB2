package dev.mruniverse.guardianrftb.bungeegame.api;

import dev.mruniverse.guardianrftb.bungeegame.interfaces.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameEndEvent extends Event {
    private final Game game;
    private static final HandlerList handlerList = new HandlerList();

    public GameEndEvent(Game currentGame) {
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
