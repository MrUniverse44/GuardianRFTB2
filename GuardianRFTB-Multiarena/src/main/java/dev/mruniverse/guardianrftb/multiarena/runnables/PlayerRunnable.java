package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameBossFormat;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import dev.mruniverse.guardianrftb.multiarena.utils.FloatConverter;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;
@SuppressWarnings("unused")
public class PlayerRunnable extends BukkitRunnable {
    private final GuardianRFTB plugin;
    private boolean bossLb,bossGm,actionLb;
    private String bossLobby,actionLobby,bossGameBeast,bossGameRunners;
    private GameBossFormat gameBossFormat;
    private final FloatConverter floatConverter;
    private final Utils utils;
    public PlayerRunnable(GuardianRFTB main) {
        plugin = main;
        floatConverter = new FloatConverter();
        utils = main.getLib().getUtils();
        bossLb = plugin.getSettings().getSettings().getBoolean("settings.lobby.bossBar");
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        bossLobby = messages.getString("messages.lobby.bossBar");
        actionLb = plugin.getSettings().getSettings().getBoolean("settings.lobby.actionBar");
        actionLobby = messages.getString("messages.lobby.actionBar");
        bossGm = plugin.getSettings().getSettings().getBoolean("settings.game.beastDistance.toggle");
        bossGameRunners = messages.getString("messages.game.others.beastDistance.toRunners");
        bossGameBeast = messages.getString("messages.game.others.beastDistance.toBeasts");
        String format = plugin.getSettings().getSettings().getString("settings.game.beastDistance.Format");
        if(format == null) format = "BOSSBAR";
        if(format.equalsIgnoreCase("ACTIONBAR") || format.equalsIgnoreCase("ACTION BAR") || format.equalsIgnoreCase("ACTION_BAR")) {
            gameBossFormat = GameBossFormat.ACTIONBAR;
        } else {
            gameBossFormat = GameBossFormat.BOSSBAR;
        }
    }
    public void update() {
        bossLb = plugin.getSettings().getSettings().getBoolean("settings.lobby.bossBar");
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        bossLobby = messages.getString("messages.lobby.bossBar");
        actionLb = plugin.getSettings().getSettings().getBoolean("settings.lobby.actionBar");
        actionLobby = messages.getString("messages.lobby.actionBar");
        bossGm = plugin.getSettings().getSettings().getBoolean("settings.game.beastDistance.toggle");
        bossGameRunners = messages.getString("messages.game.others.beastDistance.toRunners");
        bossGameBeast = messages.getString("messages.game.others.beastDistance.toBeasts");
        String format = plugin.getSettings().getSettings().getString("settings.game.beastDistance.Format");
        if(format == null) format = "BOSSBAR";
        if(format.equalsIgnoreCase("ACTIONBAR") || format.equalsIgnoreCase("ACTION BAR") || format.equalsIgnoreCase("ACTION_BAR")) {
            gameBossFormat = GameBossFormat.ACTIONBAR;
        } else {
            gameBossFormat = GameBossFormat.BOSSBAR;
        }
    }
    @Override
    public void run() {
        for (UUID uuid : plugin.getRigoxPlayers().keySet()) {
            PlayerManager playerManager = plugin.getPlayerData(uuid);
            PlayerStatus playerStatus = playerManager.getStatus();
            Player player = playerManager.getPlayer();
            plugin.getScoreboards().setScoreboard(playerManager.getBoard(),playerManager.getPlayer());
            if (playerStatus.equals(PlayerStatus.IN_LOBBY)) {
                if(bossLb) {
                    utils.sendBossBar(player, bossLobby);
                }
                if(actionLb) {
                    utils.sendActionbar(player, actionLobby);
                }
            } else {
                if(playerManager.getBoard().equals(GuardianBoard.WAITING) || playerManager.getBoard().equals(GuardianBoard.STARTING) || playerManager.getBoard().equals(GuardianBoard.SELECTING) || playerManager.getBoard().equals(GuardianBoard.BEAST_SPAWN) || playerManager.getBoard().equals(GuardianBoard.WIN_RUNNERS_FOR_RUNNERS) || playerManager.getBoard().equals(GuardianBoard.WIN_RUNNERS_FOR_BEAST) || playerManager.getBoard().equals(GuardianBoard.WIN_BEAST_FOR_RUNNERS) || playerManager.getBoard().equals(GuardianBoard.WIN_BEAST_FOR_BEAST)    ) {
                    if(bossLb) {
                        utils.sendBossBar(player, bossLobby);
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
                                message = message.replace("%runners%", playerManager.getGame().getRunners().size() + "")
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
                                message = message.replace("%runners%", playerManager.getGame().getRunners().size() + "")
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
