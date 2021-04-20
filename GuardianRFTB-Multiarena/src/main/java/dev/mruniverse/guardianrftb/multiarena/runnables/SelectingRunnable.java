package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameStatus;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.utils.GuardianUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SelectingRunnable extends BukkitRunnable {
    private final GameInfo currentGame;
    private final GuardianRFTB instance = GuardianRFTB.getInstance();
    private final GuardianUtils guardianUtils = instance.getUtils();
    private String enough;
    private String prefix;
    private String selecting;
    private String second;
    private String seconds;
    private String chosenBeast;
    public SelectingRunnable(GameInfo game) {
        this.currentGame = game;
        FileConfiguration configuration = instance.getStorage().getControl(GuardianFiles.MESSAGES);
        FileConfiguration secondConfiguration = instance.getSettings().getSettings();
        enough = configuration.getString("messages.game-count.enough-players");
        selecting = configuration.getString("messages.game-count.selecting");
        chosenBeast = configuration.getString("messages.game.chosenBeast");
        prefix = configuration.getString("messages.prefix");
        second = secondConfiguration.getString("settings.timer.second");
        seconds = secondConfiguration.getString("settings.timer.seconds");
        if(selecting == null) selecting = "&eThe beast will be chosen in &c%current_time% &e%current_time_letter%";
        if(enough == null) enough = "&cThis game can't start, not enough players";
        if(second == null) second = "second";
        if(chosenBeast == null) chosenBeast = "&eThe player &b%player% &enow is a beast!";
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
                        guardianUtils.sendMessage(player,prefix + selecting.replace("%current_time%",time + "").replace("%current_time_letter%",seconds));
                    }
                }
                if(time == 1) {
                    for(Player player : currentGame.getPlayers()) {
                        guardianUtils.sendMessage(player,prefix + selecting.replace("%current_time%",time + "").replace("%current_time_letter%",second));
                    }
                }
                currentGame.setLastTimer(time - 1);
            } else {
                int player = currentGame.getRandom().nextInt(currentGame.getRunners().size());
                setBeast(currentGame.getRunners().get(player));
                if(currentGame.getType().equals(GameType.DOUBLE_BEAST) || currentGame.getType().equals(GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST)) {
                    player = currentGame.getRandom().nextInt(currentGame.getRunners().size());
                    setBeast(currentGame.getRunners().get(player));
                }
                currentGame.cancelTask();
                currentGame.startCount();
            }
        } else {
            currentGame.doubleCountPrevent = false;
            currentGame.setGameStatus(GameStatus.WAITING);
            for(Player player : currentGame.getPlayers()) {
                guardianUtils.sendMessage(player,prefix + enough);
                instance.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.WAITING);
            }
            currentGame.cancelTask();
        }
    }

    private void setBeast(Player player) {
        currentGame.getBeasts().add(player);
        currentGame.getRunners().remove(player);
        for(Player game : currentGame.getPlayers()) {
            guardianUtils.sendMessage(game,prefix + chosenBeast.replace("%player%",player.getName()));
        }
        player.getInventory().clear();
        player.getInventory().setItem(instance.getItemsInfo().getBeastSlot(), instance.getItemsInfo().getKitBeast());
        player.getInventory().setItem(instance.getItemsInfo().getExitSlot(), instance.getItemsInfo().getExit());
        player.teleport(currentGame.getSelecting());
    }
}
