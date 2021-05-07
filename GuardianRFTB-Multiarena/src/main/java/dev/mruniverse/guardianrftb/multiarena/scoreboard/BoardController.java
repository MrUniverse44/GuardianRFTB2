package dev.mruniverse.guardianrftb.multiarena.scoreboard;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.netherboard.BPlayerBoard;
import dev.mruniverse.guardianrftb.multiarena.netherboard.Netherboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class BoardController {
    private final GuardianRFTB plugin;
    private final Netherboard netherboard = new Netherboard();
    private final ScoreInfo scoreInfo;
    public BoardController(GuardianRFTB main) {
        scoreInfo = new ScoreInfo(main);
        plugin = main;
    }
    private final HashMap<UUID, BPlayerBoard> players = new HashMap<>();

    public BPlayerBoard getBoardOfPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public void removeScore(Player player) {
        players.remove(player.getUniqueId());
    }

    public void setScoreboard(GuardianBoard board, Player player) {
        if(!existPlayer(player)) {
            players.put(player.getUniqueId(), netherboard.createBoard(player,"guardianBoard"));
        }
        updateScoreboard(board,player);
    }
    public void setTitle(Player player,String title){
        if(!existPlayer(player)) {
            players.put(player.getUniqueId(), netherboard.createBoard(player,"guardianBoard"));
        }
        BPlayerBoard scoreboard = getBoardOfPlayer(player);
        scoreboard.setName(ChatColor.translateAlternateColorCodes('&',title));
    }
    public void updateScoreboard(GuardianBoard board, Player player) {
        BPlayerBoard scoreboard = getBoardOfPlayer(player);
        if(!plugin.getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.animatedTitle.toggle")) {
            scoreboard.setName(ChatColor.translateAlternateColorCodes('&',scoreInfo.getTitle(board)));
        }
        List<String> line = scoreInfo.getLines(board,player);
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
        return players.containsKey(player.getUniqueId());
    }

}

