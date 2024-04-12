package dev.mruniverse.guardianrftb.multiarena.scoreboard;

import com.xism4.sternalboard.SternalBoard;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class Scoreboards {
    private final Map<UUID, SternalBoard> boardMap = new ConcurrentHashMap<>();

    private final ScoreboardData data;
    private final GuardianRFTB plugin;


    public Scoreboards(GuardianRFTB main) {
        this.data   = new ScoreboardData(main);
        this.plugin = main;
    }

    public boolean isEnabled() {
        return getSettings().getBoolean("enabled", true);
    }

    public FileConfiguration getSettings() {
        return plugin.getStorage().getControl(GuardianFiles.SCOREBOARD);
    }

    public SternalBoard getPlayerScoreboard(Player player) {
        if (!isEnabled()) {
            return null;
        }

        SternalBoard scoreboard = boardMap.get(
            player.getUniqueId()
        );

        if (scoreboard == null) {
            scoreboard = new SternalBoard(player);

            boardMap.put(
                player.getUniqueId(),
                scoreboard
            );
        }
        return scoreboard;
    }

    public void removeScoreboard(Player player) {
        SternalBoard scoreboard = getPlayerScoreboard(player);

        if (scoreboard == null) {
            return;
        }

        scoreboard.delete();

        boardMap.remove(player.getUniqueId());
    }

    public void removeScoreboard(GamePlayer player) {
        removeScoreboard(player.getPlayer());
    }

    public void setScoreboard(PluginScoreboard scoreboard, GamePlayer gamePlayer, Player player) {
        updateScoreboard(scoreboard, gamePlayer, player);
    }

    public void setScoreboard(PluginScoreboard scoreboard, Player player) {
        updateScoreboard(scoreboard, plugin.getGamePlayer(player), player);
    }

    public void setTitle(Player player, String title) {
        SternalBoard scoreboard = getPlayerScoreboard(player);

        if (scoreboard == null) {
            return;
        }

        scoreboard.updateTitle(
                color(title)
        );
    }

    public void setTitle(GamePlayer player, String title) {
        setTitle(player.getPlayer(), title);
    }

    public void updateScoreboard(PluginScoreboard pluginScoreboard, GamePlayer gamePlayer, Player player) {
        SternalBoard scoreboard = getPlayerScoreboard(player);

        if (scoreboard == null) {
            return;
        }

        scoreboard.updateTitle(
            color(data.getTitle(pluginScoreboard))
        );

        List<String> line = data.getLines(pluginScoreboard, gamePlayer, player);

        String[] lines = new String[line.size()];

        line.toArray(lines);

        scoreboard.updateLines(lines);
    }

    public void updateScoreboard(PluginScoreboard pluginScoreboard, GamePlayer gamePlayer, Player player, String title) {
        SternalBoard scoreboard = getPlayerScoreboard(player);

        scoreboard.updateTitle(color(title));

        List<String> line = data.getLines(pluginScoreboard, gamePlayer, player);

        String[] lines = new String[line.size()];

        line.toArray(lines);

        scoreboard.updateLines(lines);
    }

    public void updateScoreboard(PluginScoreboard pluginScoreboard, SternalBoard scoreboard, GamePlayer gamePlayer, Player player) {
        scoreboard.updateTitle(
            color(data.getTitle(pluginScoreboard))
        );

        List<String> line = data.getLines(pluginScoreboard, gamePlayer, player);

        String[] lines = new String[line.size()];

        line.toArray(lines);

        scoreboard.updateLines(lines);
    }

    public void updateScoreboard(PluginScoreboard pluginScoreboard, SternalBoard scoreboard, GamePlayer gamePlayer, Player player, String title) {
        scoreboard.updateTitle(color(title));

        List<String> line = data.getLines(pluginScoreboard, gamePlayer, player);

        String[] lines = new String[line.size()];

        line.toArray(lines);

        scoreboard.updateLines(lines);
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

