package me.blueslime.guardianrftb.multiarena.scoreboard;

import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.SlimeFile;
import me.blueslime.guardianrftb.multiarena.scoreboard.netherboard.BPlayerBoard;
import me.blueslime.guardianrftb.multiarena.scoreboard.netherboard.Netherboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class ScoreboardHandler {

    private final HashMap<UUID, BPlayerBoard> players = new HashMap<>();

    private final Netherboard netherboard = new Netherboard();

    private final ScoreboardInformation scoreboardInformation;

    private final GuardianRFTB plugin;

    public ScoreboardHandler(GuardianRFTB main) {
        scoreboardInformation = new ScoreboardInformation(main);
        plugin = main;
    }

    public BPlayerBoard getPlayerBoard(Player player) {
        return players.get(player.getUniqueId());
    }

    public void removeScore(Player player) {
        players.remove(player.getUniqueId());
    }

    public void setScoreboard(PluginScoreboard board, Player player) {
        if (!existPlayer(player)) {
            players.put(player.getUniqueId(), netherboard.createBoard(player,"guardianBoard"));
        }
        updateScoreboard(board,player);
    }
    public void setTitle(Player player,String title){
        if (!existPlayer(player)) {
            players.put(player.getUniqueId(), netherboard.createBoard(player,"guardianBoard"));
        }
        BPlayerBoard scoreboard = getPlayerBoard(player);
        scoreboard.setName(ChatColor.translateAlternateColorCodes('&',title));
    }
    public void updateScoreboard(PluginScoreboard board, Player player) {
        
        BPlayerBoard scoreboard = getPlayerBoard(player);
        
        if (!plugin.getConfigurationHandler(SlimeFile.SCOREBOARDS).getStatus("scoreboards.animatedTitle.toggle")) {

            scoreboard.setName(ChatColor.translateAlternateColorCodes('&', scoreboardInformation.getTitle(board)));

        }

        List<String> line = scoreboardInformation.getLines(board,player);
        String[] lines = new String[line.size()];
        line.toArray(lines);
        scoreboard.setAll(lines);
    }
    @SuppressWarnings("unused")
    public void deletePlayer(Player player) {
        if(existPlayer(player)) {
            players.remove(player.getUniqueId());
        }
    }
    private boolean existPlayer(Player player) {
        if(player == null) {
            return false;
        }
        return players.containsKey(player.getUniqueId());
    }

}

