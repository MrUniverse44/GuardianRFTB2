package me.blueslime.stylizedrftb.multiarena.messages.providers;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.player.GamePlayer;
import org.bukkit.entity.Player;

public class DefaultProvider implements MessageProvider {
    private final StylizedRFTB plugin;

    public DefaultProvider(StylizedRFTB plugin) {
        this.plugin = plugin;
    }

    @Override
    public String replace(GamePlayer player, String message) {

        GamePlayer.Data data = player.getData();

        return message.replace(
                "%wins%",
                data.getWins() + ""
        ).replace(
                "%kit-list%",
                removeBorders(
                        data.getKits().toString()
                )
        ).replace(
                "%games_played%",
                data.getGamesPlayed() + ""
        ).replace(
                "%deaths%",
                data.getDeaths() + ""
        ).replace(
                "%selected_kit%",
                data.getSelectedKit()
        ).replace(
                "%coins%",
                data.getCoins() + ""

        );
    }

    @Override
    public String replace(Player player, String message) {
        return replace(
                plugin.getStorageManager().fetchGamePlayer(player),
                message
        );
    }
}
