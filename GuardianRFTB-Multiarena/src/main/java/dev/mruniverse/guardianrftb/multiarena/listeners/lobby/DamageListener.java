package dev.mruniverse.guardianrftb.multiarena.listeners.lobby;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
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
            Location lobby = plugin.getSettings().getLocation();
            if (event.getEntity().getWorld().equals(lobby.getWorld())) {
                event.setCancelled(true);
                if(plugin.getSettings().getSettings().getBoolean("settings.lobby.voidSpawnTP")) {
                    if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                        event.getEntity().teleport(lobby);
                    }
                }
            }
            Player player = (Player)event.getEntity();
            if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                GuardianBoard board = plugin.getPlayerData(player.getUniqueId()).getBoard();
                if(board.equals(GuardianBoard.WAITING) || board.equals(GuardianBoard.BEAST_SPAWN) || board.equals(GuardianBoard.SELECTING) || board.equals(GuardianBoard.STARTING) || board.equals(GuardianBoard.WIN_BEAST_FOR_BEAST) || board.equals(GuardianBoard.WIN_BEAST_FOR_RUNNERS) || board.equals(GuardianBoard.WIN_RUNNERS_FOR_BEAST) || board.equals(GuardianBoard.WIN_RUNNERS_FOR_RUNNERS)) {
                    if(event.getCause() == EntityDamageEvent.DamageCause.VOID) player.teleport(lobby);
                    event.setCancelled(true);
                } else {
                    if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL) || plugin.getPlayerData(player.getUniqueId()).getGame().getSpectators().contains(player)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public void updateAll() {

    }
}
