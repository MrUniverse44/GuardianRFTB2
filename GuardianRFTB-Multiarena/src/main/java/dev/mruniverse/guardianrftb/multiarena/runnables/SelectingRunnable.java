package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.listeners.api.GameResetCountEvent;
import dev.mruniverse.guardianrftb.multiarena.listeners.api.GameSelectedBeastEvent;
import dev.mruniverse.guardianrftb.multiarena.scoreboard.PluginScoreboard;
import dev.mruniverse.guardianrftb.multiarena.utils.GuardianUtils;
import dev.mruniverse.guardianrftb.multiarena.utils.PlayerUtil;
import dev.mruniverse.guardianrftb.multiarena.utils.SoundsInfo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SelectingRunnable extends BukkitRunnable {
    private final Game currentGame;
    private final GuardianRFTB plugin;
    private final GuardianUtils guardianUtils;
    private String enough;
    private String prefix;
    private String selecting;
    private String second;
    private String seconds;
    private String chosenBeast;
    private final String forcedBeast;
    private final boolean xp;
    private final int max;
    public SelectingRunnable(GuardianRFTB plugin, Game game) {
        this.plugin = plugin;
        this.currentGame = game;
        max = game.getLastTimer();
        guardianUtils = plugin.getUtils();
        FileConfiguration configuration = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        FileConfiguration secondConfiguration = plugin.getSettings().getSettings();
        enough = configuration.getString("messages.game.game-count.enough-players");
        selecting = configuration.getString("messages.game.game-count.selecting");
        chosenBeast = configuration.getString("messages.game.chosenBeast");
        forcedBeast = configuration.getString("messages.game.forcedBeast");
        prefix = configuration.getString("messages.prefix");
        second = secondConfiguration.getString("settings.timer.second");
        xp = secondConfiguration.getBoolean("settings.game.xp-countdown",true);
        seconds = secondConfiguration.getString("settings.timer.seconds");
        if (selecting == null) {
            selecting = "&eThe beast will be chosen in &c%current_time% &e%current_time_letter%";
        }
        if (enough == null) {
            enough = "&cThis game can't start, not enough players";
        }
        if (second == null) {
            second = "second";
        }
        if (chosenBeast == null) {
            chosenBeast = "&eThe player &b%player% &enow is a beast!";
        }
        if (seconds == null) {
            seconds = "seconds";
        }
        if (prefix == null) {
            prefix = "&3&lG&b&lRFTB &8| ";
        }
    }

    @Override
    public void run() {
        boolean sendMessage = false;
        String message = "";
        if(currentGame.getPlayers().size() >= currentGame.getMin()) {
            if(!currentGame.getBeasts().isEmpty() && currentGame.getType() == GameType.CLASSIC) {
                currentGame.cancelTask();
                currentGame.startCount();
                for (Player player : PlayerUtil.getPlayers(plugin, currentGame.getPlayers())) {
                    guardianUtils.sendMessage(player,prefix + forcedBeast);
                }
            }
            int time = currentGame.getLastTimer();
            if(time != 0) {
                SoundsInfo sounds = plugin.getSoundsInfo();
                if(time == 30 || time == 25 || time == 20 || time == 15 || time == 10 || time == 5 || time == 4 || time == 3 || time == 2) {
                    sendMessage = true;
                    message = prefix + selecting.replace("%current_time%",time + "").replace("%current_time_letter%",seconds);
                }
                if(time == 1) {
                    sendMessage = true;
                    message = prefix + selecting.replace("%current_time%",time + "").replace("%current_time_letter%",second);
                }
                if(xp) {
                    for (Player player : PlayerUtil.getPlayers(plugin, currentGame.getPlayers())) {
                        float division = (float)time/max;
                        player.setExp(division);
                        if (sendMessage) {
                            guardianUtils.sendMessage(player, message);
                            if (sounds.getStatus(GuardianSounds.GAME_COUNT))
                                player.playSound(player.getLocation(), sounds.getSound(GuardianSounds.GAME_COUNT), sounds.getVolume(GuardianSounds.GAME_COUNT), sounds.getPitch(GuardianSounds.GAME_COUNT));
                        }
                    }
                } else {
                    if(sendMessage) {
                        for (Player player : PlayerUtil.getPlayers(plugin, currentGame.getPlayers())) {
                            guardianUtils.sendMessage(player, message);
                            if (sounds.getStatus(GuardianSounds.GAME_COUNT))
                                player.playSound(player.getLocation(), sounds.getSound(GuardianSounds.GAME_COUNT), sounds.getVolume(GuardianSounds.GAME_COUNT), sounds.getPitch(GuardianSounds.GAME_COUNT));
                        }
                    }
                }

                currentGame.setLastTimer(time - 1);
            } else {
                int player = currentGame.getRandom().nextInt(currentGame.getRunners().size());

                UUID selected = currentGame.getRunners().get(player);

                setBeast(plugin.getServer().getPlayer(selected));

                if(currentGame.getType().equals(GameType.DOUBLE_BEAST) || currentGame.getType().equals(GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST)) {
                    player = currentGame.getRandom().nextInt(currentGame.getRunners().size());
                    selected = currentGame.getRunners().get(player);
                    setBeast(plugin.getServer().getPlayer(selected));
                }
                currentGame.cancelTask();
                currentGame.startCount();
            }
        } else {
            currentGame.setDoubleCountPrevent(false);
            currentGame.setGameStatus(GameStatus.WAITING);
            currentGame.updateSignsBlocks();
            GameResetCountEvent event = new GameResetCountEvent(currentGame);
            plugin.getServer().getPluginManager().callEvent(event);
            for (Player player : PlayerUtil.getPlayers(plugin, currentGame.getPlayers())) {
                guardianUtils.sendMessage(player,prefix + enough);
                plugin.getGamePlayer(player).setScoreboard(PluginScoreboard.WAITING);
            }
            currentGame.cancelTask();
        }
    }

    private void setBeast(Player player) {
        if (player == null) {
            return;
        }
        currentGame.getBeasts().add(player.getUniqueId());
        currentGame.getRunners().remove(player.getUniqueId());
        GameSelectedBeastEvent event = new GameSelectedBeastEvent(currentGame,player);
        plugin.getServer().getPluginManager().callEvent(event);
        for (Player game : PlayerUtil.getPlayers(plugin, currentGame.getPlayers())) {
            guardianUtils.sendMessage(game,prefix + chosenBeast.replace("%player%",player.getName()),player);
        }
        player.getInventory().clear();
        if (plugin.getItemsInfo().getKitBeastStatus()) {
            player.getInventory().setItem(plugin.getItemsInfo().getBeastSlot(), plugin.getItemsInfo().getKitBeast());
        }
        if (plugin.getItemsInfo().getExitStatus()) {
            player.getInventory().setItem(plugin.getItemsInfo().getExitSlot(), plugin.getItemsInfo().getExit());
        }
        player.teleport(currentGame.getSelecting());
    }
}
