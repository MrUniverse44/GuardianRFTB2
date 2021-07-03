package dev.mruniverse.guardianrftb.multiarena.listeners.game;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianSounds;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.interfaces.PlayerManager;
import dev.mruniverse.guardianrftb.multiarena.utils.SoundsInfo;
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
        if(plugin.getUser(event.getPlayer().getUniqueId()).getGame() != null) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(plugin.getUser(event.getPlayer().getUniqueId()).getGame() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void checkpointAdd(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerManager data = plugin.getUser(player.getUniqueId());
        if(data.getGame() == null) return;
        if(event.getBlockPlaced().getType() == Material.BEACON) {
            if(!data.getPointStatus()) {
                data.setLastCheckpoint(event.getBlock().getLocation());
                data.setPointStatus(true);
                SoundsInfo sounds = plugin.getSoundsInfo();
                if(sounds.getStatus(GuardianSounds.CHECKPOINT_PLACE)) player.playSound(player.getLocation(),sounds.getSound(GuardianSounds.CHECKPOINT_PLACE),sounds.getVolume(GuardianSounds.CHECKPOINT_PLACE),sounds.getPitch(GuardianSounds.CHECKPOINT_PLACE));
                plugin.getUtils().consumeItem(player,1,event.getBlockPlaced().getType());
                player.getInventory().addItem(plugin.getItemsInfo().getCheckPoint());
            } else {
                plugin.getUtils().sendMessage(player, plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.checkpoint.already"));
            }
        }
    }

    @EventHandler
    public void gameDrop(PlayerDropItemEvent event) {
        Game game = plugin.getUser(event.getPlayer().getUniqueId()).getGame();
        if(game != null) {
            event.setCancelled(true);
        }
    }

    public void updateAll() {

    }
}
