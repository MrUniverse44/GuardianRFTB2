package me.blueslime.guardianrftb.multiarena.scoreboard;

import dev.mruniverse.slimelib.control.Control;
import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.SlimeFile;
import me.blueslime.guardianrftb.multiarena.game.Game;
import me.blueslime.guardianrftb.multiarena.player.GamePlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ScoreboardInformation {
    private final GuardianRFTB plugin;

    public ScoreboardInformation(GuardianRFTB plugin) { this.plugin = plugin; }

    public List<String> getLines(PluginScoreboard board, Player player) {
        List<String> lines = new ArrayList<>();

        Control scoreboard = plugin.getConfigurationHandler(SlimeFile.SCOREBOARDS);
        StringBuilder white = new StringBuilder("&f");

        switch(board) {
            case LOBBY:
            default:
                for (String line : scoreboard.getStringList("scoreboards.lobby.lines")) {
                    String replacedLine = replaceVariables(line,player);
                    if(!size(replacedLine)) {
                        if (!lines.contains(replacedLine)) {
                            line = replacedLine;
                        } else {
                            if(!size(white + replacedLine)) {
                                line = white + replacedLine;
                                white.append("&r");
                            }
                        }
                        lines.add(line);
                    }
                }
                return lines;
            case WAITING:
                for (String line : scoreboard.getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isStarting>") && !line.contains("<isSelecting>") && !line.contains("<BeastAppear>")) {
                        if (line.contains("<isWaiting>")) line = line.replace("<isWaiting>", "");
                        String replacedLine = replaceVariables(line,player);
                        if(!size(replacedLine)) {
                            if (!lines.contains(replacedLine)) {
                                line = replacedLine;
                            } else {
                                if(!size(white + replacedLine)) {
                                    line = white + replacedLine;
                                    white.append("&r");
                                }
                            }
                            lines.add(line);
                        }
                    }
                }
                return lines;
            case SELECTING:
                for (String line : scoreboard.getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isWaiting>") && !line.contains("<isStarting>") && !line.contains("<BeastAppear>")) {
                        if (line.contains("<isSelecting>")) line = line.replace("<isSelecting>", "");
                        if(!size(line)) {
                            String replacedLine = replaceVariables(line,player);
                            if (!lines.contains(replacedLine)) {
                                line = replacedLine;
                            } else {
                                if(!size(white + replacedLine)) {
                                    line = white + replacedLine;
                                    white.append("&r");
                                }
                            }
                            lines.add(line);
                        }
                    }
                }
                return lines;
            case STARTING:
                for (String line : scoreboard.getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isWaiting>") && !line.contains("<isSelecting>") && !line.contains("<BeastAppear>")) {
                        if (line.contains("<isStarting>")) line = line.replace("<isStarting>", "");
                        String replacedLine = replaceVariables(line,player);
                        if(!size(replacedLine)) {
                            if (!lines.contains(replacedLine)) {
                                line = replacedLine;
                            } else {
                                if(!size(white + replacedLine)) {
                                    line = white + replacedLine;
                                    white.append("&r");
                                }
                            }
                            lines.add(line);
                        }
                    }
                }
                return lines;
            case BEAST_SPAWN:
                for (String line : scoreboard.getStringList("scoreboards.waiting.lines")) {
                    if (!line.contains("<isWaiting>") && !line.contains("<isSelecting>") && !line.contains("<isStarting>")) {
                        if (line.contains("<BeastAppear>")) line = line.replace("<BeastAppear>", "");
                        String replacedLine = replaceVariables(line,player);
                        if(!size(replacedLine)) {
                            if (!lines.contains(replacedLine)) {
                                line = replacedLine;
                            } else {
                                if(!size(white + replacedLine)) {
                                    line = white + replacedLine;
                                    white.append("&r");
                                }
                            }
                            lines.add(line);
                        }
                    }
                }
                return lines;
            case PLAYING:
                for (String line : scoreboard.getStringList("scoreboards.playing.lines")) {
                    String replacedLine = replaceVariables(line,player);
                    if(!size(replacedLine)) {
                        if (!lines.contains(replacedLine)) {
                            line = replacedLine;
                        } else {
                            if(!size(white + replacedLine)) {
                                line = white + replacedLine;
                                white.append("&r");
                            }
                        }
                        lines.add(line);
                    }
                }
                return lines;
            case WIN_BEAST_FOR_BEAST:
                for (String line : scoreboard.getStringList("scoreboards.beastWin.forBeast.lines")) {
                    String replacedLine = replaceVariables(line,player);
                    if(!size(replacedLine)) {
                        if (!lines.contains(replacedLine)) {
                            line = replacedLine;
                        } else {
                            if(!size(white + replacedLine)) {
                                line = white + replacedLine;
                                white.append("&r");
                            }
                        }
                        lines.add(line);
                    }
                }
                return lines;
            case WIN_BEAST_FOR_RUNNERS:
                for (String line : scoreboard.getStringList("scoreboards.beastWin.forRunners.lines")) {
                    String replacedLine = replaceVariables(line,player);
                    if(!size(replacedLine)) {
                        if (!lines.contains(replacedLine)) {
                            line = replacedLine;
                        } else {
                            if(!size(white + replacedLine)) {
                                line = white + replacedLine;
                                white.append("&r");
                            }
                        }
                        lines.add(line);
                    }
                }
                return lines;
            case WIN_RUNNERS_FOR_BEAST:
                for (String line : scoreboard.getStringList("scoreboards.runnersWin.forBeast.lines")) {
                    String replacedLine = replaceVariables(line,player);
                    if(!size(replacedLine)) {
                        if (!lines.contains(replacedLine)) {
                            line = replacedLine;
                        } else {
                            if(!size(white + replacedLine)) {
                                line = white + replacedLine;
                                white.append("&r");
                            }
                        }
                        lines.add(line);
                    }
                }
                return lines;
            case WIN_RUNNERS_FOR_RUNNERS:
                for (String line : scoreboard.getStringList("scoreboards.runnersWin.forRunners.lines")) {
                    String replacedLine = replaceVariables(line,player);
                    if(!size(replacedLine)) {
                        if (!lines.contains(replacedLine)) {
                            line = replacedLine;
                        } else {
                            if(!size(white + replacedLine)) {
                                line = white + replacedLine;
                                white.append("&r");
                            }
                        }
                        lines.add(line);
                    }
                }
                return lines;
        }
    }

    public boolean size(String line) {

        line = ChatColor.translateAlternateColorCodes('&',line);

        if(39 <= line.length()) {
            plugin.getLogs().error("&fLine: '" + line + "&f' has more than 39 characters, String length is longer than maximum allowed (" + line.length() + " > 39)");
            plugin.getLogs().error("This issue can kick users showing an error.");
            return true;
        }

        return false;
    }

    public String getTitle(PluginScoreboard board) {

        Control scoreboard = plugin.getConfigurationHandler(SlimeFile.SCOREBOARDS);

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

        GamePlayer gamePlayer = plugin.getStorageManager().getPlayerStorage().get(
                player.getUniqueId(),
                new GamePlayer(player)
        );

        text = text.replace("<player_coins>", "" + gamePlayer.getData().getCoins())
                .replace("<player_kits>","" + gamePlayer.getData().getKits().size())
                .replace("<player_wins>","" + gamePlayer.getData().getWins())
                .replace("<player_beast_kit>","Not selected")
                .replace("<player_runner_kit>","Not selected")
                .replace("<server_online>",plugin.getServer().getOnlinePlayers().size() + "")
                .replace("<timeFormat>",getDateFormat());


        if (gamePlayer.getGame() != null) {
            String timer;

            Game game = gamePlayer.getGame();

            if(game.getRunnable().getTimer() >= 2) {
                timer = plugin.getConfigurationHandler(SlimeFile.SETTINGS).getString("settings.timer.seconds", "seconds");
            } else {
                timer = plugin.getConfigurationHandler(SlimeFile.SETTINGS).getString("settings.timer.second", "second");
            }
            text = text.replace("<arena_name>", game.getName())
                    .replace("<arena_online>","" +  game.getPlayerList().size())
                    .replace("<arena_max>", "" + game.getMax())
                    .replace("<arena_need>", "" + game.getNeedPlayers())
                    .replace("<arena_time_text>", timer)
                    .replace("<arena_beast>", plugin.getGameUtils().getRandomBeast(player).getDisplayName())
                    .replace("<arena_runners>","" + game.getRunnerList().size())
                    .replace("<arena_mode>", game.getGameType().getType())
                    .replace("<arena_timeLeft>", "" + game.getRunnable().getTimer())
                    .replace("<arena_time_number>", "" + game.getRunnable().getTimer())
                    .replace("<arena_time_left>", game.getRunnable().getTimer() + " " + timer)
                    .replace("<arena_status>", game.getStatus().getStatus())
                    .replace("<player_role>", gamePlayer.getTeam().getRole());
        }
        if (GuardianRFTB.hasPAPI()) {
            text = PlaceholderAPI.setPlaceholders(player,text);
        }
        return ChatColor.translateAlternateColorCodes('&',text);
    }

    public String getDateFormat() {

        String dateFormat = plugin.getConfigurationHandler(SlimeFile.SETTINGS).getString("settings.dateFormat", "dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance(
                TimeZone.getTimeZone(
                        plugin.getConfigurationHandler(SlimeFile.SETTINGS).getString("settings.timeZone", "GMT-5")
                )
        );

        return "" + (
                new SimpleDateFormat(dateFormat).format(
                        calendar.getTime()
                )
        );

    }

}
