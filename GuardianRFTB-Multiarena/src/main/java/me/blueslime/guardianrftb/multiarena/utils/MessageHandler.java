package me.blueslime.guardianrftb.multiarena.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class MessageHandler {

    public static void sendMessage(Player player, String message) {
        player.sendMessage(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        message
                )
        );
    }

}
