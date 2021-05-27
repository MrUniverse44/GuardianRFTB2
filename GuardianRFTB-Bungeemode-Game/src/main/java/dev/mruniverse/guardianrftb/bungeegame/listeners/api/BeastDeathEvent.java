package dev.mruniverse.guardianrftb.bungeegame.listeners.api;

import dev.mruniverse.guardianrftb.bungeegame.interfaces.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BeastDeathEvent extends Event {
    private final Game game;
    private final Player beast;
    private static final HandlerList handlerList = new HandlerList();

    public BeastDeathEvent(Game currentGame, Player currentBeast) {
        beast = currentBeast;
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
        return beast;
    }
}