package dev.mruniverse.guardianrftb.multiarena.listeners.lobby;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ExtrasListener implements Listener {
    private final GuardianRFTB plugin;
    public ExtrasListener(GuardianRFTB plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void lobbyDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(plugin.getSettings().getLocation() == null) {
            plugin.getLogs().error("The lobby location is not set!");
            return;
        }
        if (player.getWorld().equals(plugin.getSettings().getLocation().getWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void lobbyHunger(FoodLevelChangeEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(plugin.getSettings().getSettings().getBoolean("settings.lobby.noHunger")) {
                if(plugin.getSettings().getLocation() == null) {
                    plugin.getLogs().error("The lobby location is not set!");
                    return;
                }
                if (event.getEntity().getWorld().equals(plugin.getSettings().getLocation().getWorld())) {
                    event.setFoodLevel(20);
                }
                Player player = (Player) event.getEntity();
                if (plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                    event.setFoodLevel(20);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void perWorldTab(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        boolean lobbyWorld = false;
        boolean showInGamePlayers = plugin.getSettings().getSettings().getBoolean("settings.lobby.show-all-players");
        if(plugin.getSettings().getLocation() == null) {
            plugin.getLogs().error("The lobby location is not set!");
            return;
        }
        if(world == plugin.getSettings().getLocation().getWorld()) {
            lobbyWorld = true;
        }
        if(plugin.getSettings().getSettings().getBoolean("settings.perWorldTab")) {
            for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                if (players.getWorld() == world) {
                    if(player.getGameMode() != GameMode.SPECTATOR) {
                        players.showPlayer(player);
                    }
                    if(players.getGameMode() != GameMode.SPECTATOR) {
                        player.showPlayer(players);
                    }
                } else {
                    players.hidePlayer(player);
                    if(!lobbyWorld && !showInGamePlayers) {
                        player.hidePlayer(players);
                    } else {
                        player.showPlayer(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void lobbyWeather(WeatherChangeEvent event) {
        if(plugin.getSettings().getLocation() != null) {
            if (plugin.getSettings().getLocation().getWorld() == event.getWorld()) {
                if (plugin.getSettings().getSettings().getBoolean("settings.lobby.disableWeather")) {
                    event.setCancelled(event.toWeatherState());
                }
            }
            return;
        }
        plugin.getLogs().error("The lobby is not set!");
    }

    @EventHandler
    public void lobbyClickInventory(InventoryClickEvent event) {
        if(plugin.getSettings().getSettings().getBoolean("settings.lobby.blockInventoryClick")) {
            if(plugin.getSettings().getLocation() != null) {
                if (event.getWhoClicked().getWorld() == plugin.getSettings().getLocation().getWorld()) {
                    event.setCancelled(true);
                }
                return;
            }
            plugin.getLogs().error("The lobby is not set!");
        }
    }
    @EventHandler
    public void lobbyPlaceEvent(BlockPlaceEvent event) {
        if(plugin.getSettings().getSettings().getBoolean("settings.lobby.cancelBlockPlace")) {
            if(plugin.getSettings().getLocation() != null) {
                if (event.getPlayer().getWorld() == plugin.getSettings().getLocation().getWorld()) {
                    event.setCancelled(true);
                }
                return;
            }
            plugin.getLogs().error("The lobby is not set!");
        }
    }
    @EventHandler
    public void lobbyBreakEvent(BlockBreakEvent event) {
        if(plugin.getSettings().getSettings().getBoolean("settings.lobby.cancelBlockBreak")) {
            if(plugin.getSettings().getLocation() != null) {
                if (event.getPlayer().getWorld() == plugin.getSettings().getLocation().getWorld()) {
                    event.setCancelled(true);
                }
                return;
            }
            plugin.getLogs().error("The lobby is not set!");
        }
    }


}
