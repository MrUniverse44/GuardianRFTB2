package me.blueslime.guardianrftb.multiarena.listeners.lobby;

import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {
    private final GuardianRFTB plugin;
    public DamageListener(GuardianRFTB plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void damage(EntityDamageEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player player = (Player)event.getEntity();
            GamePlayer data = plugin.getUser(player.getUniqueId());
            if(data == null) return;
            if(data.getGame() != null) return;
            Location lobby = plugin.getSettings().getLocation();
            if (event.getEntity().getWorld().equals(lobby.getWorld())) {
                event.setCancelled(true);
                if(plugin.getSettings().getSettings().getBoolean("settings.lobby.voidSpawnTP")) {
                    if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                        event.getEntity().teleport(lobby);
                    }
                }
            }
        }
    }

    public void updateAll() {

    }
}
