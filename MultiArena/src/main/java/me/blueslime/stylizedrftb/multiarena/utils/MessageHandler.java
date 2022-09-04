package me.blueslime.stylizedrftb.multiarena.utils;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.player.GamePlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class MessageHandler {

    public static void sendMessage(GamePlayer player, String message) {
        if (player.isPlaying()) {
            message = player.getGame().replaceVariables(message);
        }

        sendMessage(
                player.getBukkitPlayer(),
                message
        );
    }

    public static void sendMessage(Player player, String message) {
        if (StylizedRFTB.hasPAPI()) {
            message = PlaceholderAPI.setPlaceholders(
                    player,
                    message
            );
        }

        player.sendMessage(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        message
                )
        );
    }

}
