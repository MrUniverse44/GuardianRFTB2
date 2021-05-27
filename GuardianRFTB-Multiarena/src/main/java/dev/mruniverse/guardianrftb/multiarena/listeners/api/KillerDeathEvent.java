package dev.mruniverse.guardianrftb.multiarena.listeners.api;

import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KillerDeathEvent extends Event {
    private final Game game;
    private final Player player;
    private static final HandlerList handlerList = new HandlerList();

    public KillerDeathEvent(Game currentGame, Player currentKiller) {
        player = currentKiller;
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
