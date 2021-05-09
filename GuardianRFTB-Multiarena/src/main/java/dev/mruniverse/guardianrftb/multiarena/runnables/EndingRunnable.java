package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import dev.mruniverse.guardianrftb.multiarena.utils.GuardianText;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class EndingRunnable extends BukkitRunnable {
    private final GameInfo currentGame;
    private final GuardianRFTB instance = GuardianRFTB.getInstance();
    private boolean winnerIsRunner = false;
    private final int rewardTime;
    private final int buttonTime;
    private int time;
    private final HashMap<String, GuardianText> buttons;
    public EndingRunnable(GameInfo game, GameTeam winnerTeam) {
        this.currentGame = game;
        game.setLastTimer(10);
        time = 10;
        buttons = new HashMap<>();
        loadOptions();
        rewardTime = time - 5;
        buttonTime = rewardTime - 2;
        if(winnerTeam == GameTeam.RUNNERS || winnerTeam == GameTeam.KILLER) winnerIsRunner = true;
    }
    @Override
    public void run() {
        int time = this.time;
        if(time != 0) {
            if(time == rewardTime) {
                for(Player player : currentGame.getPlayers()) {
                    instance.getUtils().rewardInfo(player,instance.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.gameInfo.rewardSummary"),winnerIsRunner);
                }
            }
            if(time == buttonTime) {
                for(Player player : currentGame.getPlayers()) {
                    sendOptions(player,instance.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.gameInfo.playAgain"));
                }
            }
            if (winnerIsRunner) {
                for (Player player : currentGame.getRunners()) {
                    currentGame.firework(player, currentGame.timing(time));
                }
            } else {
                for (Player player : currentGame.getBeasts()) {
                    currentGame.firework(player, currentGame.timing(time));
                }
            }
            this.time--;
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
    public void sendOptions(Player player, List<String> list) {
        for(String line : list) {
            // * Check vars, replace vars and send message.
        }
    }

    public void loadOptions() {
        FileConfiguration configuration = instance.getStorage().getControl(GuardianFiles.MESSAGES);
        String path;
        GuardianText guardianText;
        for(String values : instance.getStorage().getContent(GuardianFiles.MESSAGES,"messages.game.playAgainButton.customOptions",false)) {
            path = "messages.game.playAgainButton.customOptions." + values + ".";
            String message = configuration.getString(path + "value");
            String hover = configuration.getString(path + "hover");
            String action = configuration.getString(path + "customAction");
            String result = configuration.getString(path + "customResult");
            if(message == null) message = "notSet";
            if(hover == null) hover = "notSet";
            if(action == null) action = "CMD";
            if(result == null) result = "notSet";
            message = message.replace("[px]","⚫");
            guardianText = new GuardianText(message);
            guardianText.setClickEvent(action,result);
            guardianText.setHoverEvent(HoverEvent.Action.SHOW_TEXT,hover);
            buttons.put(values,guardianText);
        }
    }


}
