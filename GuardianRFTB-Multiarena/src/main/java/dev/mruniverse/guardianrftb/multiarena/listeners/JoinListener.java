package dev.mruniverse.guardianrftb.multiarena.listeners;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class JoinListener implements Listener {
    private final GuardianRFTB plugin;
    private boolean onlyLobbyScore;
    private boolean onlyItemsLobby;
    private boolean joinTeleport;
    private boolean joinAutoHeal;
    private boolean joinInventory;
    private boolean joinMessage;
    private GameMode joinGamemode;
    private Location lobbyBoard;
    public JoinListener(GuardianRFTB plugin) {
        this.plugin = plugin;
        onlyLobbyScore = plugin.getSettings().getSettings().getBoolean("settings.lobby.scoreboard-only-in-lobby-world");
        onlyItemsLobby = plugin.getSettings().getSettings().getBoolean("settings.lobby.items-only-in-lobby-world");
        joinAutoHeal = plugin.getSettings().getSettings().getBoolean("settings.lobby.join.autoHeal");
        joinInventory = plugin.getSettings().getSettings().getBoolean("settings.lobby.join.clearInventory");
        joinTeleport = plugin.getSettings().getSettings().getBoolean("settings.lobby.join.teleport");
        joinMessage = plugin.getSettings().getSettings().getBoolean("settings.lobby.join.message");
        joinGamemode = GameMode.valueOf(plugin.getSettings().getSettings().getString("settings.lobby.join.gamemode"));
        lobbyBoard = plugin.getSettings().getLocation();
    }
    @EventHandler
    public void teleport(PlayerJoinEvent event) {
        if(joinTeleport) {
            if(plugin.getSettings().getLocation() != null) {
                try {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> event.getPlayer().teleport(plugin.getSettings().getLocation()), 4L);
                } catch (Throwable throwable) {
                    plugin.getLogs().error("Can't teleport " + event.getPlayer().getName() + " to the lobby!");
                    plugin.getLogs().error(throwable);
                }
                return;
            }
            plugin.getLogs().error("The lobby is not set!");
        }
    }
    @EventHandler
    public void scoreboard(PlayerJoinEvent event) {
        try {
            if(onlyLobbyScore) {
                if (event.getPlayer().getWorld().equals(lobbyBoard.getWorld())) {
                    if(plugin.getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                        plugin.getScoreboards().setScoreboard(GuardianBoard.LOBBY,event.getPlayer());
                    }
                }
            } else {
                if(plugin.getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.lobby.toggle")) {
                    plugin.getScoreboards().setScoreboard(GuardianBoard.LOBBY,event.getPlayer());
                }
            }
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't generate lobby scoreboard for " + event.getPlayer().getName() +"!");
            plugin.getLogs().error(throwable);
        }
    }

    @EventHandler
    public void extras(PlayerJoinEvent event) {
        plugin.addPlayer(event.getPlayer());
        Player player = event.getPlayer();
        if(joinMessage) event.setJoinMessage(null);
        if(joinAutoHeal) {
            player.setHealth(20.0D);
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setExp(0.0F);
        }
        player.setGameMode(joinGamemode);
        if(!onlyItemsLobby) {
            player.setFlying(false);
            player.setAllowFlight(false);
            if (joinInventory) {
                player.getInventory().clear();
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);
            }
            for (ItemStack item : plugin.getItemsInfo().getLobbyItems().keySet()) {
                player.getInventory().setItem(plugin.getItemsInfo().getSlot(item), item);
            }
            return;
        }
        try {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                if(player.getWorld() == plugin.getSettings().getLocation().getWorld()) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    if (joinInventory) {
                        player.getInventory().clear();
                        player.getInventory().setHelmet(null);
                        player.getInventory().setChestplate(null);
                        player.getInventory().setLeggings(null);
                        player.getInventory().setBoots(null);
                    }
                    for (ItemStack item : plugin.getItemsInfo().getLobbyItems().keySet()) {
                        player.getInventory().setItem(plugin.getItemsInfo().getSlot(item), item);
                    }
                }
            }, 8L);
        } catch (Throwable ignored) { }
    }

    public void updateAll() {
        onlyLobbyScore = plugin.getSettings().getSettings().getBoolean("settings.lobby.scoreboard-only-in-lobby-world");
        lobbyBoard = plugin.getSettings().getLocation();
        onlyItemsLobby = plugin.getSettings().getSettings().getBoolean("settings.lobby.items-only-in-lobby-world");
        joinAutoHeal = plugin.getSettings().getSettings().getBoolean("settings.lobby.join.autoHeal");
        joinInventory = plugin.getSettings().getSettings().getBoolean("settings.lobby.join.clearInventory");
        joinTeleport = plugin.getSettings().getSettings().getBoolean("settings.lobby.join.teleport");
        joinGamemode = GameMode.valueOf(plugin.getSettings().getSettings().getString("settings.lobby.join.gamemode"));
        joinMessage = plugin.getSettings().getSettings().getBoolean("settings.lobby.join.message");
    }
}
