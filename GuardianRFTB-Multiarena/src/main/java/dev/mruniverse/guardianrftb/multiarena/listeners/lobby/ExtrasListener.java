package dev.mruniverse.guardianrftb.multiarena.listeners.lobby;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
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
import org.bukkit.inventory.ItemStack;

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
    public void giveLobbyItems(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if(player.getWorld() == plugin.getSettings().getLocation().getWorld()) {
            if (plugin.getSettings().getSettings().getBoolean("settings.lobby.join.clearInventory")) {
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
