package dev.mruniverse.guardianrftb.multiarena.listeners.game;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameStatus;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathListener implements Listener {
    private final GuardianRFTB plugin;

    private String byLava;
    private String byVoid;
    private String byDefault;

    public DeathListener(GuardianRFTB plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void inGameDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            GameInfo game = plugin.getPlayerData(player.getUniqueId()).getGame();
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
                player.setGameMode(GameMode.SPECTATOR);
                game.deathBeast(player);
                player.teleport(game.getBeastSpawn());
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.spigot().respawn();
                game.deathRunner(player);
                if(!game.getType().equals(GameType.INFECTED)) {
                    player.teleport(game.getRunnerSpawn());
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            player.setGameMode(GameMode.SPECTATOR);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        GameInfo game = plugin.getPlayerData(player.getUniqueId()).getGame();
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
