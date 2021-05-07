package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingRunnable extends BukkitRunnable {
    private final GameInfo currentGame;
    private final GuardianRFTB instance = GuardianRFTB.getInstance();
    private boolean winnerIsRunner = false;
    private final int rewardTime;
    private int time;
    public EndingRunnable(GameInfo game, GameTeam winnerTeam) {
        this.currentGame = game;
        time = 10;
        rewardTime = time - 5;
        if(winnerTeam == GameTeam.RUNNERS || winnerTeam == GameTeam.KILLER) winnerIsRunner = true;
    }
    @Override
    public void run() {
        if(time != 0 || currentGame.getPlayers().size() > 0) {
            if(time == rewardTime) {
                for(Player player : currentGame.getPlayers()) {
                    instance.getUtils().rewardInfo(player,instance.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.gameInfo.rewardSummary"),winnerIsRunner);
                }
            }
            if (winnerIsRunner) {
                for (Player player : currentGame.getRunners()) {
                    currentGame.firework(player, currentGame.timing(time));
                    currentGame.firework(player, currentGame.timing(time));
                }
            } else {
                for (Player player : currentGame.getBeasts()) {
                    currentGame.firework(player, currentGame.timing(time));
                    currentGame.firework(player, currentGame.timing(time));
                }
            }
            time--;
        } else {
            for (Player player : currentGame.getPlayers()) {
                Location location = instance.getSettings().getLocation();
                if(location != null) {
                    player.teleport(location);
                }
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);
                PlayerManager playerManager = instance.getPlayerData(player.getUniqueId());
                playerManager.setStatus(PlayerStatus.IN_LOBBY);
                playerManager.setGame(null);
                playerManager.setBoard(GuardianBoard.LOBBY);
                player.getInventory().clear();
                for (ItemStack item : instance.getItemsInfo().getLobbyItems().keySet()) {
                    player.getInventory().setItem(instance.getItemsInfo().getSlot(item), item);
                }
                if (playerManager.getLeaveDelay() != 0) {
                    instance.getServer().getScheduler().cancelTask(playerManager.getLeaveDelay());
                    playerManager.setLeaveDelay(0);
                }
                player.updateInventory();
            }
            currentGame.cancelTask();
            currentGame.restart();
        }
    }

}
