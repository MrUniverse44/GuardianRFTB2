package dev.mruniverse.guardianrftb.multiarena.scoreboard;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.KitType;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ScoreboardData {
    private final GuardianRFTB plugin;

    public ScoreboardData(GuardianRFTB plugin) { this.plugin = plugin; }

    public List<String> getLines(PluginScoreboard board, GamePlayer gamePlayer, Player player) {
        List<String> lines = new ArrayList<>();
        FileConfiguration scoreboard = plugin.getStorage().getControl(GuardianFiles.SCOREBOARD);
        StringBuilder white = new StringBuilder("&f");
        switch(board) {
            case LOBBY:
            default:
                for (String line : scoreboard.getStringList("scoreboards.lobby.lines")) {
                    String replacedLine = replaceVariables(line, gamePlayer, player);
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
                        String replacedLine = replaceVariables(line, gamePlayer, player);
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
                            String replacedLine = replaceVariables(line, gamePlayer, player);
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
                        String replacedLine = replaceVariables(line, gamePlayer, player);
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
                        String replacedLine = replaceVariables(line, gamePlayer, player);
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
                    String replacedLine = replaceVariables(line, gamePlayer, player);
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
                    String replacedLine = replaceVariables(line, gamePlayer, player);
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
                    String replacedLine = replaceVariables(line, gamePlayer, player);
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
                    String replacedLine = replaceVariables(line, gamePlayer, player);
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
                    String replacedLine = replaceVariables(line, gamePlayer, player);
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

    public String replaceVariables(String text, GamePlayer gamePlayer, Player player) {
        text = text.replace("<player_name>",player.getName());

        FileConfiguration settings = plugin.getSettings().getSettings();

        text = text.replace("<player_coins>", String.valueOf(gamePlayer.getStatistics().getCoins()))
            .replace("<player_kits>", String.valueOf(gamePlayer.getStatistics().getKits().size()))
            .replace("<player_wins>", String.valueOf(gamePlayer.getStatistics().getWins())
            .replace("<player_beast_kit>", gamePlayer.getSelectedKit(KitType.BEAST))
            .replace("<player_runner_kit>", gamePlayer.getSelectedKit(KitType.RUNNER))
            .replace("<player_killer_kit", gamePlayer.getSelectedKit(KitType.KILLER))
            .replace("<server_online>", String.valueOf(plugin.getServer().getOnlinePlayers().size()))
            .replace("<timeFormat>", getDateFormat());

        if (gamePlayer.isPlaying()) {
            Game game = gamePlayer.getGame();

            String path = game.getLastTimer() >= 2 ? "seconds" : "second";

            String time = settings.getString("settings.timer." + path, path);

            UUID beast = game.getBeasts().isEmpty() ? null : game.getBeasts().get(0);

            Player beastPlayer = beast != null ? plugin.getServer().getPlayer(beast) : null;

            text = text.replace("<arena_name>", game.getName())
                .replace("<arena_online>", String.valueOf(game.getPlayers().size()))
                .replace("<arena_max>", String.valueOf(game.getMax()))
                .replace("<arena_need>", String.valueOf(game.getNeedPlayers()))
                .replace("<arena_time_text>", time)
                .replace("<arena_beast>", beastPlayer != null ? beastPlayer.getName() : "")
                .replace("<arena_runners>", String.valueOf(game.getRunners().size()))
                .replace("<arena_mode>", settings.getString(game.getType().getPath(), ""))
                .replace("<arena_timeLeft>", String.valueOf(game.getLastTimer()))
                .replace("<arena_status>", game.getStatus().getStatus())
                .replace("<player_role>", gamePlayer.getCurrentRole())
                .replace("<arena_timeLeft>",  String.valueOf(game.getLastTimer()))
                .replace("<arena_time_number>", String.valueOf(game.getLastTimer()));
        }

        if (plugin.hasPAPI()) {
            text = PlaceholderAPI.setPlaceholders(player,text);
        }

        return ChatColor.translateAlternateColorCodes('&',text);
    }

    public String getDateFormat() {
        String dateFormat = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.dateFormat");
        if(dateFormat == null) dateFormat = "dd/MM/yyyy";
        return new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());

    }

}
