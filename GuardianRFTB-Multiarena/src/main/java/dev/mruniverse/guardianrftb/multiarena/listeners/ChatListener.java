package dev.mruniverse.guardianrftb.multiarena.listeners;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.interfaces.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class ChatListener implements Listener {
    private final GuardianRFTB plugin;

    private String lobbyChat;
    private String gameChat;
    private String spectatorChat;

    public ChatListener(GuardianRFTB plugin) {
        this.plugin = plugin;

        lobbyChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.lobby");
        if(lobbyChat == null) lobbyChat = "&7<player_name>&8: &f%message%";

        spectatorChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.spectator");
        if(spectatorChat == null) spectatorChat = "&8[SPECTATOR] &7<player_name>&8: &f%message%";

        gameChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.inGame");
        if(gameChat == null) gameChat = "&a[%player_role%&a] &7<player_name>&8: &f%message%";
    }

    public void updateAll() {
        lobbyChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.lobby");
        if(lobbyChat == null) lobbyChat = "&7<player_name>&8: &f%message%";

        spectatorChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.spectator");
        if(spectatorChat == null) spectatorChat = "&8[SPECTATOR] &7<player_name>&8: &f%message%";

        gameChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.inGame");
        if(gameChat == null) gameChat = "&a[%player_role%&a] &7<player_name>&8: &f%message%";

    }

    @EventHandler
    public void pluginChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;
        if(!plugin.getSettings().getSettings().getBoolean("settings.lobby.chat")) return;
        Player player = event.getPlayer();
        plugin.getLogs().debug("&3CHAT | &f" + player.getName() + ": " + event.getMessage());
        String message = plugin.getSettings().getSettings().getString("settings.chat-log-format","&f[&9DEBUG &f| GuardianRFTB] &bCHAT | &f%player%: %message%");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',message.replace("%player%",player.getName()).replace("%message%",event.getMessage())));
        PlayerManager playerManagerImpl = plugin.getUser(player.getUniqueId());
        if(playerManagerImpl == null || playerManagerImpl.getGame() == null) {
            if(player.getWorld() == plugin.getSettings().getLocation().getWorld()) {
                event.setCancelled(true);
                for (Player lobby : plugin.getSettings().getLocation().getWorld().getPlayers()) {
                    plugin.getUtils().sendMessage(lobby, lobbyChat.replace("<player_name>", player.getName()),event.getPlayer(),event.getMessage());
                }
            }
            return;
        }
        event.setCancelled(true);
        Game game = playerManagerImpl.getGame();
        if(game.getSpectators().contains(player)) {
            for(Player spectator : game.getSpectators()) {
                plugin.getUtils().sendMessage(spectator, spectatorChat.replace("<player_name>", player.getName()).replace("%player_role%", playerManagerImpl.getCurrentRole()),event.getPlayer(),event.getMessage());
            }
            return;
        }
        ArrayList<Player> players = new ArrayList<>(game.getPlayers());
        for(Player spectator : players) {
            plugin.getUtils().sendMessage(spectator, gameChat.replace("<player_name>", player.getName()).replace("%player_role%", playerManagerImpl.getCurrentRole()),event.getPlayer(),event.getMessage());
        }
    }

}
