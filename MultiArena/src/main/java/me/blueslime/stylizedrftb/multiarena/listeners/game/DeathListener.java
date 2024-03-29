package me.blueslime.stylizedrftb.multiarena.listeners.game;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.enums.GameStatus;
import me.blueslime.stylizedrftb.multiarena.enums.GameType;
import me.blueslime.stylizedrftb.multiarena.enums.SpectatorItems;
import me.blueslime.stylizedrftb.multiarena.interfaces.Game;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathListener implements Listener {
    private final StylizedRFTB plugin;

    public DeathListener(StylizedRFTB plugin) {
        this.plugin = plugin;
    }
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void inGameDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if(plugin.getUser(player.getUniqueId()).getGame() != null) {
            Game game = plugin.getUser(player.getUniqueId()).getGame();
            event.getDrops().clear();
            event.setDeathMessage(null);
            event.setDroppedExp(0);
            if(game.getStatus().equals(GameStatus.WAITING) || game.getStatus().equals(GameStatus.STARTING)) {
                player.spigot().respawn();
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(game.getWaiting());
                player.setHealth(20);
                player.setFoodLevel(20);
                return;
            }
            if(game.getBeasts().contains(player)) {
                player.spigot().respawn();
                game.deathBeast(player);
                player.teleport(game.getBeastSpawn());
                if(!plugin.getSettings().isSecondSpectator()) {
                    player.setGameMode(GameMode.SPECTATOR);
                } else {
                    for(Player player1 : game.getPlayers()) {
                        if(player != player1) player1.hidePlayer(player);
                    }
                    for(SpectatorItems items : SpectatorItems.values()) {
                        items.giveItem(player,plugin);
                    }
                    player.setGameMode(GameMode.ADVENTURE);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                }
            } else {
                player.spigot().respawn();
                game.deathRunner(player);
                if(!game.getType().equals(GameType.INFECTED)) {
                    player.teleport(game.getRunnerSpawn());
                    if(!plugin.getSettings().isSecondSpectator()) {
                        player.setGameMode(GameMode.SPECTATOR);
                    } else {
                        for(Player player1 : game.getPlayers()) {
                            if(player != player1) player1.hidePlayer(player);
                        }
                        for(SpectatorItems items : SpectatorItems.values()) {
                            items.giveItem(player,plugin);
                        }
                        player.setGameMode(GameMode.ADVENTURE);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Game game = plugin.getUser(player.getUniqueId()).getGame();
        if(game != null) {
            if(game.getBeasts().contains(player)) {
                player.teleport(game.getBeastSpawn());
            } else {
                player.teleport(game.getRunnerSpawn());
            }
            if(!game.getType().equals(GameType.INFECTED)) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    public void updateAll() { }
}
