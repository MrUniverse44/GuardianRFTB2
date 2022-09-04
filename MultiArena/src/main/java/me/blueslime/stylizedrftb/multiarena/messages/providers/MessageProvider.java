package me.blueslime.stylizedrftb.multiarena.messages.providers;

import dev.mruniverse.slimelib.source.SlimeSource;
import dev.mruniverse.slimelib.source.player.SlimePlayer;
import me.blueslime.stylizedrftb.multiarena.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public interface MessageProvider {

    @SuppressWarnings("rawtypes")
    default String replace(SlimeSource source, String message) {
        if (source.isPlayer()) {
            return replace(
                    ((SlimePlayer)source).get(),
                    message
            );
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    default String replace(GamePlayer player, String message) {
        return replace(
                player.getBukkitPlayer(),
                message
        );
    }

    default String removeBorders(String text) {
        return text.replace(
                "[",
                ""
        ).replace(
                "}",
                ""
        ).replace(
                "{",
                ""
        ).replace(
                "]",
                ""
        );
    }

    String replace(Player player, String message);
}
