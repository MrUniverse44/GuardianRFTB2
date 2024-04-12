package dev.mruniverse.guardianrftb.multiarena.listeners;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    private final GuardianRFTB plugin;
    private boolean quitMessage;
    public QuitListener(GuardianRFTB plugin) {
        this.plugin = plugin;
        quitMessage = plugin.getSettings().getSettings().getBoolean("settings.lobby.quit.message");
    }
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        plugin.getScoreboards().removeScoreboard(event.getPlayer());
        GamePlayer manager = plugin.getGamePlayer(event.getPlayer());
        if (manager.getGame() != null) {
            manager.getGame().leave(event.getPlayer());
        }
        plugin.removePlayer(event.getPlayer());
        plugin.getLib().getNMS().deleteBossBar(event.getPlayer());
        if(quitMessage) {
            event.setQuitMessage(null);
        }
    }
    public void updateAll() {
        quitMessage = plugin.getSettings().getSettings().getBoolean("settings.lobby.quit.message");
    }
}
