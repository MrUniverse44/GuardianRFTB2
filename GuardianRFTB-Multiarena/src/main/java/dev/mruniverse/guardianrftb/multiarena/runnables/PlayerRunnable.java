package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameBossFormat;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import dev.mruniverse.guardianrftb.multiarena.utils.FloatConverter;
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
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        bossLb = plugin.getSettings().getSettings().getBoolean("settings.options.lobby-bossBar");
        bossLobby = messages.getString("messages.lobby.bossBar");
        actionLb = plugin.getSettings().getSettings().getBoolean("settings.options.lobby-actionBar");
        actionLobby = messages.getString("messages.lobby.actionBar");
        bossGm = plugin.getSettings().getSettings().getBoolean("settings.ShowBeastDistance.toggle");
        bossGameRunners = messages.getString("messages.inGame.others.bossBar.toRunners");
        bossGameBeast = messages.getString("messages.inGame.others.bossBar.toBeasts");
        String format = plugin.getSettings().getSettings().getString("settings.ShowBeastDistance.Format");
        if(format == null) format = "BOSSBAR";
        if(format.equalsIgnoreCase("ACTIONBAR") || format.equalsIgnoreCase("ACTION BAR") || format.equalsIgnoreCase("ACTION_BAR")) {
            gameBossFormat = GameBossFormat.ACTIONBAR;
        } else {
            gameBossFormat = GameBossFormat.BOSSBAR;
        }
    }
    public void update() {
        bossLb = plugin.getSettings().getSettings().getBoolean("settings.options.lobby-bossBar");
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        bossLobby = messages.getString("messages.lobby.bossBar");
        actionLb = plugin.getSettings().getSettings().getBoolean("settings.options.lobby-actionBar");
        actionLobby = messages.getString("messages.lobby.actionBar");
        bossGm = plugin.getSettings().getSettings().getBoolean("settings.ShowBeastDistance.toggle");
        bossGameRunners = messages.getString("messages.inGame.others.bossBar.toRunners");
        bossGameBeast = messages.getString("messages.inGame.others.bossBar.toBeasts");
        String format = plugin.getSettings().getSettings().getString("settings.ShowBeastDistance.Format");
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
                            boolean changeLife = false;
                            if(!plugin.getUtils().isBeast(player)) {
                                changeLife = true;
                            }
                            Player beast = plugin.getUtils().getRandomBeast(player);
                            if(Objects.requireNonNull(beast.getLocation().getWorld()).equals(player.getLocation().getWorld())) {
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
                            if(Objects.requireNonNull(beast.getLocation().getWorld()).equals(player.getLocation().getWorld())) {
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
