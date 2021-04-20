package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameStatus;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.utils.GuardianUtils;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StartRunnable  extends BukkitRunnable {
    private final GameInfo currentGame;
    private final GuardianRFTB instance = GuardianRFTB.getInstance();
    private final GuardianUtils guardianUtils = instance.getUtils();
    private String enough;
    private String prefix;
    private String starting;
    private String second;
    private String seconds;
    public StartRunnable(GameInfo game) {
        this.currentGame = game;
        FileConfiguration configuration = instance.getStorage().getControl(GuardianFiles.MESSAGES);
        FileConfiguration secondConfiguration = instance.getSettings().getSettings();
        enough = configuration.getString("messages.game-count.enough-players");
        starting = configuration.getString("messages.game-count.start");
        prefix = configuration.getString("messages.prefix");
        second = secondConfiguration.getString("settings.timer.second");
        seconds = secondConfiguration.getString("settings.timer.seconds");
        if(starting == null) starting = "&eThe game starts in &c%current_time% &e%current_time_letter%!";
        if(enough == null) enough = "&cThis game can't start, not enough players";
        if(second == null) second = "second";
        if(seconds == null) seconds = "seconds";
        if(prefix == null) prefix = "&3&lG&b&lRFTB &8| ";
    }
    @Override
    public void run() {
        if(currentGame.getPlayers().size() >= currentGame.getMin()) {
            int time = currentGame.getLastTimer();
            if(time != 0) {
                if(time == 30 || time == 25 || time == 20 || time == 15 || time == 10 || time == 5 || time == 4 || time == 3 || time == 2) {
                    for(Player player : currentGame.getPlayers()) {
                        guardianUtils.sendMessage(player,prefix + starting.replace("%current_time%",time + "").replace("%current_time_letter%",seconds));
                    }
                }
                if(time == 1) {
                    for(Player player : currentGame.getPlayers()) {
                        guardianUtils.sendMessage(player,prefix + starting.replace("%current_time%",time + "").replace("%current_time_letter%",second));
                    }
                }
                currentGame.setLastTimer(time - 1);
            } else {
                for(Player player : currentGame.getRunners()) {
                    player.teleport(currentGame.getRunnerSpawn());
                    player.getInventory().clear();
                    /*
                     * GIVE KIT HERE
                     */
                }
                World world = currentGame.getRunnerSpawn().getWorld();
                if(world != null) world.setTime(currentGame.getWorldTime());
                currentGame.cancelTask();
                currentGame.beastCount();

            }
        } else {
            currentGame.doubleCountPrevent = false;
            currentGame.setGameStatus(GameStatus.WAITING);
            for(Player player : currentGame.getPlayers()) {
                guardianUtils.sendMessage(player,prefix + enough);
                instance.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.WAITING);
            }
            for(Player beasts : currentGame.getBeasts()) {
                currentGame.getRunners().add(beasts);
                beasts.getInventory().clear();
                beasts.getInventory().setItem(instance.getItemsInfo().getRunnerSlot(), instance.getItemsInfo().getKitRunner());
                beasts.getInventory().setItem(instance.getItemsInfo().getExitSlot(), instance.getItemsInfo().getExit());
                beasts.teleport(currentGame.getWaiting());
            }
            currentGame.cancelTask();
            currentGame.getBeasts().clear();
        }
    }
}
