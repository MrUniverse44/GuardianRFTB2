package dev.mruniverse.guardianrftb.multiarena.listeners.lobby;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("deprecation")
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
                if (plugin.getUser(player.getUniqueId()).getGame() != null) {
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

    @EventHandler
    public void onJoinTab(PlayerJoinEvent event) {
        if(plugin.getSettings().getSettings().getBoolean("settings.perWorldTab")) {
            checkPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        if(plugin.getSettings().getSettings().getBoolean("settings.perWorldTab")) {
            checkPlayer(e.getPlayer());
        }
    }
    private void checkPlayer(Player p) {
        if(plugin.getSettings().getLocation() == null) {
            showInWorld(p);
            for (Player all : Bukkit.getOnlinePlayers()){
                if (all == p) continue;
                if (!shouldSee(p, all) && p.canSee(all)) p.hidePlayer(all);
                if (shouldSee(p, all) && !p.canSee(all)) p.showPlayer(all);
                if (!shouldSee(all, p) && all.canSee(p)) all.hidePlayer(p);
                if (shouldSee(all, p) && !all.canSee(p)) all.showPlayer(p);
            }
            return;
        }
        World world = plugin.getSettings().getLocation().getWorld();
        if(world != null) {
            if (world.equals(p.getWorld()) && plugin.getSettings().getSettings().getBoolean("settings.lobby.show-all-players")) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    p.showPlayer(all);
                }
                return;
            }
        }
        showInWorld(p);
    }

    private void showInWorld(Player p) {
        for (Player all : Bukkit.getOnlinePlayers()){
            if (all == p) continue;
            if (!shouldSee(p, all) && p.canSee(all)) p.hidePlayer(all);
            if (shouldSee(p, all) && !p.canSee(all)) p.showPlayer(all);
            if (!shouldSee(all, p) && all.canSee(p)) all.hidePlayer(p);
            if (shouldSee(all, p) && !all.canSee(p)) all.showPlayer(p);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void command(PlayerCommandPreprocessEvent e) {
        if(e.isCancelled()) return;
        if(plugin.getSettings().getSettings().getBoolean("settings.game.commands.toggle")) {
            Player p = e.getPlayer();
            if (plugin.getUser(p.getUniqueId()).getGame() != null) {
                String[] args = e.getMessage().split(" ");
                String type = plugin.getSettings().getSettings().getString("settings.game.commands.type");
                if (type == null) type = "WHITELIST";
                List<String> commands = plugin.getSettings().getSettings().getStringList("settings.game.commands.list");
                if (type.equalsIgnoreCase("WHITELIST") || type.equalsIgnoreCase("WHITE_LIST")) {
                    for (String list : commands) {
                        if (args[0].equalsIgnoreCase("/" + list)) {
                            return;
                        }
                        e.setCancelled(true);
                    }
                    return;
                }
                for (String list : commands) {
                    if (args[0].equalsIgnoreCase("/" + list)) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void leaveCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (plugin.getUser(p.getUniqueId()).getGame() != null) {
            String[] args = e.getMessage().split(" ");
            List<String> commands = plugin.getSettings().getSettings().getStringList("settings.leaveCMDs");
            for (String list : commands) {
                if (args[0].equalsIgnoreCase(list)) {
                    e.setCancelled(true);
                    plugin.getUser(p.getUniqueId()).getGame().leave(p);
                }
            }
        }
    }

    private boolean shouldSee(Player viewer, Player target) {
        if (target == viewer) return true;
        String viewerWorld = viewer.getWorld().getName();
        String targetWorld = target.getWorld().getName();
        return viewerWorld.equals(targetWorld);
    }


}
