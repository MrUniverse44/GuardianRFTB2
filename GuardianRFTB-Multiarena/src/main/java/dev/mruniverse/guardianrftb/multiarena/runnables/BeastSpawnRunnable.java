package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.utils.GuardianUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BeastSpawnRunnable extends BukkitRunnable {
    private final GameInfo currentGame;
    private final GuardianRFTB instance = GuardianRFTB.getInstance();
    private final GuardianUtils guardianUtils = instance.getUtils();
    private String prefix;
    private String spawn;
    private String second;
    private String seconds;
    public BeastSpawnRunnable(GameInfo game) {
        this.currentGame = game;
        FileConfiguration configuration = instance.getStorage().getControl(GuardianFiles.MESSAGES);
        FileConfiguration secondConfiguration = instance.getSettings().getSettings();
        spawn = configuration.getString("messages.game-count.beast");
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
                if(time == 30 || time == 25 || time == 20 || time == 15 || time == 10 || time == 5 || time == 4 || time == 3 || time == 2) {
                    for(Player player : currentGame.getPlayers()) {
                        guardianUtils.sendMessage(player,prefix + spawn.replace("%current_time%",time + "").replace("%current_time_letter%",seconds));
                    }
                }
                if(time == 1) {
                    for(Player player : currentGame.getPlayers()) {
                        guardianUtils.sendMessage(player,prefix + spawn.replace("%current_time%",time + "").replace("%current_time_letter%",second));
                    }
                }
                currentGame.setLastTimer(time - 1);
            } else {
                for(Player player : currentGame.getBeasts()) {
                    instance.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.PLAYING);
                    player.teleport(currentGame.getBeastSpawn());
                    player.getInventory().clear();
                    /*
                     * GIVE KIT HERE
                     */
                }
                currentGame.cancelTask();
                currentGame.playCount();

            }
        } else {
            if(currentGame.getBeasts().size() == 0) {
                currentGame.setGameStatus(GameStatus.RESTARTING);
                currentGame.setWinner(GameTeam.RUNNERS);
                currentGame.cancelTask();
                return;
            }
            currentGame.setGameStatus(GameStatus.RESTARTING);
            currentGame.cancelTask();
            currentGame.setWinner(GameTeam.BEASTS);
        }
    }
}
