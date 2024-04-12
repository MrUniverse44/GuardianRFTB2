package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameBossFormat;
import dev.mruniverse.guardianrftb.multiarena.scoreboard.PluginScoreboard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import dev.mruniverse.guardianrftb.multiarena.utils.FloatConverter;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
@SuppressWarnings("unused")
public class PlayerRunnable extends BukkitRunnable {
    private final GuardianRFTB plugin;

    private final FloatConverter floatConverter;
    private final Utils utils;

    private boolean bossLb,bossGm,actionLb,extraBb,extraAb;

    private String bossLobby,actionLobby,bossGameBeast,bossGameRunners,extraA,extraB;

    private GameBossFormat gameBossFormat;

    public PlayerRunnable(GuardianRFTB main) {
        plugin = main;
        floatConverter = new FloatConverter();
        utils = main.getLib().getUtils();
        FileConfiguration config = plugin.getSettings().getSettings();
        bossLb = config.getBoolean("settings.lobby.bossBar");
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        bossLobby = messages.getString("messages.lobby.bossBar");
        actionLb = config.getBoolean("settings.lobby.actionBar");
        actionLobby = messages.getString("messages.lobby.actionBar");
        extraBb = config.getBoolean("settings.game.extra-bossbar",false);
        extraAb = config.getBoolean("settings.game.extra-actionbar",true);
        extraA = messages.getString("messages.game.extra-actionbar","&eYou are playing with &fGRFTB&e.");
        extraB = messages.getString("messages.game.extra-bossbar","&6This is a &lBETA");
        bossGm = config.getBoolean("settings.game.beastDistance.toggle");
        bossGameRunners = messages.getString("messages.game.others.beastDistance.toRunners");
        bossGameBeast = messages.getString("messages.game.others.beastDistance.toBeasts");
        String format = config.getString("settings.game.beastDistance.Format");
        if(format == null) format = "BOSSBAR";
        if(format.equalsIgnoreCase("ACTIONBAR") || format.equalsIgnoreCase("ACTION BAR") || format.equalsIgnoreCase("ACTION_BAR")) {
            gameBossFormat = GameBossFormat.ACTIONBAR;
        } else {
            gameBossFormat = GameBossFormat.BOSSBAR;
        }
    }
    public void update() {
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        FileConfiguration config = plugin.getSettings().getSettings();
        bossLb = config.getBoolean("settings.lobby.bossBar");
        bossLobby = messages.getString("messages.lobby.bossBar");
        actionLb = config.getBoolean("settings.lobby.actionBar");
        actionLobby = messages.getString("messages.lobby.actionBar");
        bossGm = config.getBoolean("settings.game.beastDistance.toggle");
        extraBb = config.getBoolean("settings.game.extra-bossbar",false);
        extraAb = config.getBoolean("settings.game.extra-actionbar",true);
        extraA = messages.getString("messages.game.extra-actionbar","&eYou are playing with &fGRFTB&e.");
        extraB = messages.getString("messages.game.extra-bossbar","&6This is a &lBETA");
        bossGameRunners = messages.getString("messages.game.others.beastDistance.toRunners");
        bossGameBeast = messages.getString("messages.game.others.beastDistance.toBeasts");
        String format = config.getString("settings.game.beastDistance.Format");
        if(format == null) format = "BOSSBAR";
        if(format.equalsIgnoreCase("ACTIONBAR") || format.equalsIgnoreCase("ACTION BAR") || format.equalsIgnoreCase("ACTION_BAR")) {
            gameBossFormat = GameBossFormat.ACTIONBAR;
        } else {
            gameBossFormat = GameBossFormat.BOSSBAR;
        }
    }
    @Override
    public void run() {
        for (UUID uuid : plugin.getPlayers().keySet()) {
            GamePlayer playerManagerImpl = plugin.getGamePlayer(uuid);

            if (playerManagerImpl == null) {
                continue;
            }

            PlayerStatus playerStatus = playerManagerImpl.getStatus();
            Player player = playerManagerImpl.getPlayer();

            plugin.getScoreboards().setScoreboard(playerManagerImpl.getScoreboard(), playerManagerImpl.getPlayer());
            if (playerStatus.equals(PlayerStatus.IN_LOBBY)) {
                if(bossLb) {
                    utils.sendBossBar(player, bossLobby);
                }
                if(actionLb) {
                    utils.sendActionbar(player, actionLobby);
                }
            } else {
                if(
                        playerManagerImpl.getScoreboard().equals(PluginScoreboard.WAITING) ||
                        playerManagerImpl.getScoreboard().equals(PluginScoreboard.STARTING) ||
                        playerManagerImpl.getScoreboard().equals(PluginScoreboard.SELECTING) ||
                        playerManagerImpl.getScoreboard().equals(PluginScoreboard.BEAST_SPAWN) ||
                        playerManagerImpl.getScoreboard().equals(PluginScoreboard.WIN_RUNNERS_FOR_RUNNERS) ||
                        playerManagerImpl.getScoreboard().equals(PluginScoreboard.WIN_RUNNERS_FOR_BEAST) ||
                        playerManagerImpl.getScoreboard().equals(PluginScoreboard.WIN_BEAST_FOR_RUNNERS) ||
                        playerManagerImpl.getScoreboard().equals(PluginScoreboard.WIN_BEAST_FOR_BEAST)
                ) {
                    if(extraAb) {
                        utils.sendActionbar(player,extraA);
                    }
                    if(extraBb) {
                        utils.sendBossBar(player,extraB);
                    }
                } else {
                    if(bossGm) {
                        String message;
                        if(plugin.getUtils().isBeast(player)) {
                            message = bossGameBeast;
                        } else {
                            message = bossGameRunners;
                        }
                        if(gameBossFormat.equals(GameBossFormat.BOSSBAR)) {
                            boolean changeLife = !plugin.getUtils().isBeast(player);
                            Player beast = plugin.getUtils().getRandomBeast(player);
                            World beastWorld = beast.getLocation().getWorld();
                            World playerWorld = player.getLocation().getWorld();
                            if(beastWorld == null) return;
                            if(playerWorld == null) return;
                            if(beastWorld.equals(playerWorld)) {
                                double mainDistance = player.getLocation().distance(beast.getLocation());
                                float distance = floatConverter.converter(mainDistance);
                                message = message.replace("%runners%", playerManagerImpl.getGame().getRunners().size() + "")
                                        .replace("%beastName%", beast.getName())
                                        .replace("%beastDistance%", floatConverter.meters(mainDistance) + "m");
                                if (!changeLife) {
                                    utils.sendBossBar(player, message);
                                } else {
                                    utils.sendBossBar(player, message, distance);
                                }
                            }
                        } else {
                            Player beast = plugin.getUtils().getRandomBeast(player);
                            World beastWorld = beast.getLocation().getWorld();
                            World playerWorld = player.getLocation().getWorld();
                            if(beastWorld == null) return;
                            if(playerWorld == null) return;
                            if(beastWorld.equals(playerWorld)) {
                                double mainDistance = player.getLocation().distance(beast.getLocation());
                                float distance = floatConverter.meters(mainDistance);
                                message = message.replace("%runners%", playerManagerImpl.getGame().getRunners().size() + "")
                                        .replace("%beastName%", beast.getName())
                                        .replace("%beastDistance%", distance + "m");
                                utils.sendActionbar(player, message);
                            }
                        }
                    }
                }
            }

        }
    }
}
