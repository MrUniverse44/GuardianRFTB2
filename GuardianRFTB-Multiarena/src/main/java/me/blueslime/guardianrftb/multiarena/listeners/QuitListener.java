package me.blueslime.guardianrftb.multiarena.listeners;

import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.player.GamePlayer;
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
        plugin.getScoreboards().removeScore(event.getPlayer());
        GamePlayer manager = plugin.getUser(event.getPlayer().getUniqueId());
        if(manager.getGame() != null) {
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
