package me.blueslime.stylizedrftb.multiarena.listeners.api;

import me.blueslime.stylizedrftb.multiarena.interfaces.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RunnerDeathEvent extends Event {
    private final Game game;
    private final Player player;
    private static final HandlerList handlerList = new HandlerList();

    public RunnerDeathEvent(Game currentGame, Player currentRunner) {
        player = currentRunner;
        game = currentGame;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
    public Game getCurrentGame() {
        return game;
    }
    public Player getBeast() {
        return player;
    }
}
