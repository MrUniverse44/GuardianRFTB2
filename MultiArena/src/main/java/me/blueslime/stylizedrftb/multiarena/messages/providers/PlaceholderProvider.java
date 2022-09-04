package me.blueslime.stylizedrftb.multiarena.messages.providers;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderProvider implements MessageProvider {
    private final DefaultProvider provider;

    public PlaceholderProvider(StylizedRFTB plugin) {
        this.provider = new DefaultProvider(
                plugin
        );
    }

    @Override
    public String replace(Player player, String message) {
        return PlaceholderAPI.setPlaceholders(
                player,
                provider.replace(
                        player,
                        message
                )
        );
    }
}
