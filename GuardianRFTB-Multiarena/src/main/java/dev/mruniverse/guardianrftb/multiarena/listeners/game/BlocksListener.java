package dev.mruniverse.guardianrftb.multiarena.listeners.game;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class BlocksListener implements Listener {
    private final GuardianRFTB plugin;
    public BlocksListener(GuardianRFTB plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame() != null) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void checkpointAdd(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerManager data = plugin.getPlayerData(player.getUniqueId());
        if(data.getGame() == null) return;
        if(event.getBlockPlaced().getType() == Material.BEACON) {
            if(!data.getPointStatus()) {
                if(event.isCancelled()) {
                    data.setLastCheckpoint(event.getBlock().getLocation());
                    data.setPointStatus(true);
                    plugin.getUtils().consumeItem(player,1,event.getBlockPlaced().getType());
                    player.getInventory().addItem(plugin.getItemsInfo().getCheckPoint());
                }
            } else {
                if (event.isCancelled()) {
                    plugin.getUtils().sendMessage(player, plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.checkpoint.already"));
                }
            }
        }
    }

    @EventHandler
    public void gameDrop(PlayerDropItemEvent event) {
        GameInfo game = plugin.getPlayerData(event.getPlayer().getUniqueId()).getGame();
        if(game != null) {
            event.setCancelled(true);
        }
    }

    public void updateAll() {

    }
}
