package me.blueslime.stylizedrftb.multiarena.listeners;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.enums.GuardianFiles;
import me.blueslime.stylizedrftb.multiarena.interfaces.Game;
import me.blueslime.stylizedrftb.multiarena.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class ChatListener implements Listener {
    private final StylizedRFTB plugin;

    private String lobbyChat;
    private String gameChat;
    private String console;
    private String spectatorChat;

    public ChatListener(StylizedRFTB plugin) {
        this.plugin = plugin;

        lobbyChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.lobby","&7<player_name>&8: &f%message%");

        spectatorChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.spectator","&8[SPECTATOR] &7<player_name>&8: &f%message%");

        gameChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.inGame","&a[%player_role%&a] &7<player_name>&8: &f%message%");

        console = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.chat-log-format","&f[&9DEBUG &f| GuardianRFTB] &bCHAT | &f%player%: %message%");
    }

    public void updateAll() {
        lobbyChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.lobby","&7<player_name>&8: &f%message%");

        spectatorChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.spectator","&8[SPECTATOR] &7<player_name>&8: &f%message%");

        gameChat = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.customChat.inGame","&a[%player_role%&a] &7<player_name>&8: &f%message%");

        console = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.chat-log-format","&f[&9DEBUG &f| GuardianRFTB] &bCHAT | &f%player%: %message%");

    }

    @EventHandler
    public void pluginChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return;
        if(!plugin.getSettings().getSettings().getBoolean("settings.lobby.chat")) return;
        Player player = event.getPlayer();
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',console.replace("%player%",player.getName()).replace("%message%",event.getMessage())));
        GamePlayer gamePlayerImpl = plugin.getUser(player.getUniqueId());
        if(gamePlayerImpl == null || gamePlayerImpl.getGame() == null) {
            if(player.getWorld() == plugin.getSettings().getLocation().getWorld()) {
                event.setCancelled(true);
                for (Player lobby : plugin.getSettings().getLocation().getWorld().getPlayers()) {
                    plugin.getUtils().sendMessage(lobby, lobbyChat.replace("<player_name>", player.getName()),event.getPlayer(),event.getMessage());
                }
            }
            return;
        }
        event.setCancelled(true);
        Game game = gamePlayerImpl.getGame();
        if(game.getSpectators().contains(player)) {
            for(Player spectator : game.getSpectators()) {
                plugin.getUtils().sendMessage(spectator, spectatorChat.replace("<player_name>", player.getName()).replace("%player_role%", gamePlayerImpl.getCurrentRole()),event.getPlayer(),event.getMessage());
            }
            return;
        }
        ArrayList<Player> players = new ArrayList<>(game.getPlayers());
        for(Player spectator : players) {
            plugin.getUtils().sendMessage(spectator, gameChat.replace("<player_name>", player.getName()).replace("%player_role%", gamePlayerImpl.getCurrentRole()),event.getPlayer(),event.getMessage());
        }
    }

}
