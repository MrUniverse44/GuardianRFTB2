package dev.mruniverse.guardianrftb.multiarena.listeners;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashSet;
import java.util.Set;

public class ChunkListener implements Listener {
    private final GuardianRFTB plugin;
    private boolean cancelUnload;

    public ChunkListener(GuardianRFTB plugin) {
        this.plugin = plugin;
        cancelUnload = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.cancel-chunk-unload");
    }

    public void updateAll() {
        cancelUnload = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.cancel-chunk-unload");
    }
    /*
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Set<World> worlds = new HashSet<>(plugin.getGameManager().getGameWorlds().keySet());
        if(plugin.getSettings().getLocation() != null) worlds.add(plugin.getSettings().getLocation().getWorld());
        if(worlds.contains(event.getWorld())) {

             GUARDIAN LIB
             plugin.getLib().getNMS().cancelUnloadChunk();
        }
    }
    */


    //settings.cancel-chunk-unload
}
