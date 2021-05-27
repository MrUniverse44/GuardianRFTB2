package dev.mruniverse.guardianrftb.bungeegame.api;

import dev.mruniverse.guardianrftb.bungeegame.interfaces.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameQuitEvent extends Event {
    private final Game game;
    private final Player player;
    private static final HandlerList handlerList = new HandlerList();

    public GameQuitEvent(Game currentGame, Player currentPlayer) {
        player = currentPlayer;
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
