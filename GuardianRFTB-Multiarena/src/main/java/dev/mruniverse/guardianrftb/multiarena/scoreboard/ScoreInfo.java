package dev.mruniverse.guardianrftb.multiarena.scoreboard;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScoreInfo {
    private final GuardianRFTB plugin;
    public ScoreInfo(GuardianRFTB plugin) {
        this.plugin = plugin;
    }

    public List<String> getLines(GuardianBoard board, Player player) {
        List<String> lines = new ArrayList<>();
        FileConfiguration scoreboard = plugin.getStorage().getControl(GuardianFiles.SCOREBOARD);
        switch(board) {
            case LOBBY:
            default:
                for (String line : scoreboard.getStringList("scoreboards.lobby.lines")) {
                    if(!lines.contains(replaceVariables(line,player))) {
                        line = replaceVariables(line, player);
                    } else {
                        line = replaceVariables("&f" + line, player);
                    }
                    lines.add(line);
                }
                return lines;
            case WAITING:
                for (String line : scoreboard.getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isStarting>") && !line.contains("<isSelecting>") && !line.contains("<BeastAppear>")) {
                        if (line.contains("<isWaiting>")) line = line.replace("<isWaiting>", "");
                        if(!lines.contains(replaceVariables(line,player))) {
                            line = replaceVariables(line, player);
                        } else {
                            line = replaceVariables("&f" + line, player);
                        }
                        lines.add(line);
                    }
                }
                return lines;
            case SELECTING:
                for (String line : scoreboard.getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isWaiting>") && !line.contains("<isStarting>") && !line.contains("<BeastAppear>")) {
                        if (line.contains("<isSelecting>")) line = line.replace("<isSelecting>", "");
                        if(!lines.contains(replaceVariables(line,player))) {
                            line = replaceVariables(line, player);
                        } else {
                            line = replaceVariables("&f" + line, player);
                        }
                        lines.add(line);
                    }
                }
                return lines;
            case STARTING:
                for (String line : scoreboard.getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isWaiting>") && !line.contains("<isSelecting>") && !line.contains("<BeastAppear>")) {
                        if (line.contains("<isStarting>")) line = line.replace("<isStarting>", "");
                        if(!lines.contains(replaceVariables(line,player))) {
                            line = replaceVariables(line, player);
                        } else {
                            line = replaceVariables("&f" + line, player);
                        }
                        lines.add(line);
                    }
                }
                return lines;
            case BEAST_SPAWN:
                for (String line : scoreboard.getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isWaiting>") && !line.contains("<isSelecting>") && !line.contains("<isStarting>")) {
                        if (line.contains("<BeastAppear>")) line = line.replace("<BeastAppear>", "");
                        if(!lines.contains(replaceVariables(line,player))) {
                            line = replaceVariables(line, player);
                        } else {
                            line = replaceVariables("&f" + line, player);
                        }
                        lines.add(line);
                    }
                }
                return lines;
            case PLAYING:
                for (String line : scoreboard.getStringList("scoreboards.playing.lines")) {
                    if(!lines.contains(replaceVariables(line,player))) {
                        line = replaceVariables(line, player);
                    } else {
                        line = replaceVariables("&f" + line, player);
                    }
                    lines.add(line);
                }
                return lines;
            case WIN_BEAST_FOR_BEAST:
                for (String line : scoreboard.getStringList("scoreboards.beastWin.forBeast.lines")) {
                    if(!lines.contains(replaceVariables(line,player))) {
                        line = replaceVariables(line, player);
                    } else {
                        line = replaceVariables("&f" + line, player);
                    }
                    lines.add(line);
                }
                return lines;
            case WIN_BEAST_FOR_RUNNERS:
                for (String line : scoreboard.getStringList("scoreboards.beastWin.forRunners.lines")) {
                    if(!lines.contains(replaceVariables(line,player))) {
                        line = replaceVariables(line, player);
                    } else {
                        line = replaceVariables("&f" + line, player);
                    }
                    lines.add(line);
                }
                return lines;
            case WIN_RUNNERS_FOR_BEAST:
                for (String line : scoreboard.getStringList("scoreboards.runnersWin.forBeast.lines")) {
                    if(!lines.contains(replaceVariables(line,player))) {
                        line = replaceVariables(line, player);
                    } else {
                        line = replaceVariables("&f" + line, player);
                    }
                    lines.add(line);
                }
                return lines;
            case WIN_RUNNERS_FOR_RUNNERS:
                for (String line : scoreboard.getStringList("scoreboards.runnersWin.forRunners.lines")) {
                    if(!lines.contains(replaceVariables(line,player))) {
                        line = replaceVariables(line, player);
                    } else {
                        line = replaceVariables("&f" + line, player);
                    }
                    lines.add(line);
                }
                return lines;
        }
    }

    public String getTitle(GuardianBoard board) {
        FileConfiguration scoreboard = plugin.getStorage().getControl(GuardianFiles.SCOREBOARD);
        switch (board) {
            case LOBBY:
                if (scoreboard.getString("scoreboards.lobby.title") != null) {
                    return scoreboard.getString("scoreboards.lobby.title");
                }
                return "";
            case WAITING:
            case STARTING:
            case SELECTING:
            case BEAST_SPAWN:
                if (scoreboard.getString("scoreboards.waiting.title") != null) {
                    return scoreboard.getString("scoreboards.waiting.title");
                }
                return "";
            case PLAYING:
                if (scoreboard.getString("scoreboards.playing.title") != null) {
                    return scoreboard.getString("scoreboards.playing.title");
                }
                return "";
            case WIN_BEAST_FOR_BEAST:
                if (scoreboard.getString("scoreboards.beastWin.forBeast.title") != null) {
                    return scoreboard.getString("scoreboards.beastWin.forBeast.title");
                }
                return "";
            case WIN_BEAST_FOR_RUNNERS:
                if (scoreboard.getString("scoreboards.beastWin.forRunners.title") != null) {
                    return scoreboard.getString("scoreboards.beastWin.forRunners.title");
                }
                return "";
            case WIN_RUNNERS_FOR_BEAST:
                if (scoreboard.getString("scoreboards.runnersWin.forBeast.title") != null) {
                    return scoreboard.getString("scoreboards.runnersWin.forBeast.title");
                }
                return "";
            case WIN_RUNNERS_FOR_RUNNERS:
                if (scoreboard.getString("scoreboards.runnersWin.forRunners.title") != null) {
                    return scoreboard.getString("scoreboards.runnersWin.forRunners.title");
                }
                return "";
        }
        return "";
    }

    public String replaceVariables(String text,Player player) {
        text = text.replace("<player_name>",player.getName());
        if(plugin.getPlayerData(player.getUniqueId()) != null) {
            text = text.replace("<player_coins>", "" + plugin.getPlayerData(player.getUniqueId()).getCoins())
                    .replace("<player_kits>","" + plugin.getPlayerData(player.getUniqueId()).getKits().size())
                    .replace("<player_wins>","" + plugin.getPlayerData(player.getUniqueId()).getWins());
        } else {
            text = text.replace("<player_coins>", "0")
                    .replace("<player_kits>", "1")
                    .replace("<player_wins>", "0");
        }
        text = text.replace("<player_beast_kit>","Not selected")
                .replace("<player_runner_kit>","Not selected")
                .replace("<server_online>",plugin.getServer().getOnlinePlayers().size() + "")
                .replace("<timeFormat>",getDateFormat());

        if(plugin.getPlayerData(player.getUniqueId()) != null) {
            if (plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
                GameInfo playerGame = plugin.getPlayerData(player.getUniqueId()).getGame();
                text = text.replace("<arena_name>",playerGame.getName())
                        .replace("<arena_online>","" + playerGame.getPlayers().size())
                        .replace("<arena_max>","" + playerGame.getMax())
                        .replace("<arena_need>","" + playerGame.getNeedPlayers())
                        .replace("<arena_time_text>","...")
                        .replace("<arena_beast>","...")
                        .replace("<arena_runners>","" + playerGame.getRunners().size())
                        .replace("<arena_mode>",playerGame.getType().getType())
                        .replace("<arena_timeLeft>",playerGame.getLastTimer() + "")
                        .replace("<arena_status>",playerGame.getStatus().getStatus())
                        .replace("<player_role>","...")
                        .replace("<arena_time_number>", playerGame.getLastTimer() + "");

            }
        }
        if(plugin.hasPAPI()) { text = PlaceholderAPI.setPlaceholders(player,text); }
        return ChatColor.translateAlternateColorCodes('&',text);
    }

    public String getDateFormat() {
        String dateFormat = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.dateFormat");
        if(dateFormat == null) dateFormat = "dd/MM/yyyy";
        return "" + (new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime()));

    }

}
