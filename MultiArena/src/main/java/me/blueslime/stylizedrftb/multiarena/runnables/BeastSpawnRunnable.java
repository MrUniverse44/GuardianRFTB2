package me.blueslime.stylizedrftb.multiarena.runnables;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import me.blueslime.stylizedrftb.multiarena.interfaces.Game;
import me.blueslime.stylizedrftb.multiarena.scoreboard.PluginScoreboard;
import me.blueslime.stylizedrftb.multiarena.utils.GameUtils;
import me.blueslime.guardianrftb.multiarena.utils.SoundsInfo;
import me.blueslime.guardianrftb.multiarena.enums.*;
import me.blueslime.stylizedrftb.multiarena.enums.*;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class BeastSpawnRunnable extends BukkitRunnable {
    private final Game currentGame;
    private final StylizedRFTB plugin;
    private final GameUtils gameUtils;
    private String prefix;
    private String spawn;
    private String second;
    private String seconds;
    public BeastSpawnRunnable(StylizedRFTB plugin, Game game) {
        this.currentGame = game;
        this.plugin = plugin;
        gameUtils = plugin.getUtils();
        FileConfiguration configuration = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        FileConfiguration secondConfiguration = plugin.getSettings().getSettings();
        spawn = configuration.getString("messages.game.game-count.beast");
        prefix = configuration.getString("messages.prefix");
        second = secondConfiguration.getString("settings.timer.second");
        seconds = secondConfiguration.getString("settings.timer.seconds");
        if(spawn == null) spawn = "&eThe beast spawns in &c%current_time% &e%current_time_letter%!";
        if(second == null) second = "second";
        if(seconds == null) seconds = "seconds";
        if(prefix == null) prefix = "&3&lG&b&lRFTB &8| ";
    }
    @Override
    public void run() {
        if(currentGame.getBeasts().size() != 0 && currentGame.getRunners().size() != 0 && currentGame.getStatus().equals(GameStatus.PLAYING)) {
            int time = currentGame.getLastTimer();
            if(time != 0) {
                SoundsInfo sounds = plugin.getSoundsInfo();
                if(time == 30 || time == 25 || time == 20 || time == 15 || time == 10 || time == 5 || time == 4 || time == 3 || time == 2) {
                    for(Player player : currentGame.getPlayers()) {
                        gameUtils.sendMessage(player,prefix + spawn.replace("%current_time%",time + "").replace("%current_time_letter%",seconds));
                        if(sounds.getStatus(GuardianSounds.BEAST_COUNT)) player.playSound(player.getLocation(),sounds.getSound(GuardianSounds.BEAST_COUNT),sounds.getVolume(GuardianSounds.BEAST_COUNT),sounds.getPitch(GuardianSounds.BEAST_COUNT));
                    }
                }
                if(time == 1) {
                    for(Player player : currentGame.getPlayers()) {
                        gameUtils.sendMessage(player,prefix + spawn.replace("%current_time%",time + "").replace("%current_time_letter%",second));
                        if(sounds.getStatus(GuardianSounds.BEAST_COUNT)) player.playSound(player.getLocation(),sounds.getSound(GuardianSounds.BEAST_COUNT),sounds.getVolume(GuardianSounds.BEAST_COUNT),sounds.getPitch(GuardianSounds.BEAST_COUNT));
                    }
                }
                currentGame.setLastTimer(time - 1);
            } else {
                FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
                String title = messages.getString("messages.game.others.titles.beastsGo.toBeasts.title");
                String subtitle = messages.getString("messages.game.others.titles.beastsGo.toBeasts.subtitle");
                String rTitle = messages.getString("messages.game.others.titles.beastsGo.toRunners.title");
                String rSubtitle = messages.getString("messages.game.others.titles.beastsGo.toRunners.subtitle");
                List<String> startInfo = messages.getStringList("messages.game.gameInfo.startGame");
                for(Player player : currentGame.getBeasts()) {
                    plugin.getUser(player.getUniqueId()).setBoard(PluginScoreboard.PLAYING);
                    player.teleport(currentGame.getBeastSpawn());
                    player.getInventory().clear();
                    if(player.getFireTicks() > 0) player.setFireTicks(0);
                    player.setGameMode(GameMode.SURVIVAL);
                    plugin.getItems(GameEquip.BEAST_KIT,player);
                    gameUtils.sendList(player,startInfo);
                    GuardianLIB.getControl().getUtils().sendTitle(player, 0, 20, 10, title, subtitle);
                }
                for(Player player : currentGame.getRunners()) {
                    GuardianLIB.getControl().getUtils().sendTitle(player, 0, 20, 10, rTitle, rSubtitle);
                }
                currentGame.cancelTask();
                currentGame.playCount();

            }
        } else {
            if(currentGame.getBeasts().size() == 0) {
                currentGame.setGameStatus(GameStatus.RESTARTING);
                currentGame.updateSignsBlocks();
                currentGame.cancelTask();
                currentGame.setWinner(GameTeam.RUNNERS);
                return;
            }
            currentGame.setGameStatus(GameStatus.RESTARTING);
            currentGame.updateSignsBlocks();
            currentGame.cancelTask();
            currentGame.setWinner(GameTeam.BEASTS);
        }
    }
}
