package me.blueslime.stylizedrftb.multiarena.listeners;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianlib.core.events.GuardianChunkUnloadEvent;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class ChunkListener implements Listener {
    private final StylizedRFTB plugin;
    private boolean cancelUnload;

    public ChunkListener(StylizedRFTB plugin) {
        this.plugin = plugin;
        cancelUnload = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.cancel-chunk-unload",true);
    }

    public void updateAll() {
        cancelUnload = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.cancel-chunk-unload",true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(GuardianChunkUnloadEvent event) {
        if(cancelUnload) {
            Set<World> worlds = new HashSet<>(plugin.getGameManager().getGameWorlds().keySet());
            if (plugin.getSettings().getLocation() != null) worlds.add(plugin.getSettings().getLocation().getWorld());
            if (worlds.contains(event.getWorld())) {
                event.setCancelled(true);
            }
        }
    }



    //settings.cancel-chunk-unload
}
