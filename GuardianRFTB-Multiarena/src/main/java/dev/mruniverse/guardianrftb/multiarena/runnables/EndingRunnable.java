package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.TextBuilder;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.part.TextPart;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.part.TextPartBuilder;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.part.action.ActionClick;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.part.hover.HoverBuilder;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import dev.mruniverse.guardianrftb.multiarena.utils.GuardianText;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if(instance.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.game.show-game-buttons-on-end")) {
            loadOptions();
        }
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
                if(instance.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.game.show-game-buttons-on-end")) {
                    for (Player player : currentGame.getPlayers()) {
                        sendOptions(player, instance.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.gameInfo.playAgain"));
                    }
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
            if(line.contains("<playAgainButton>")) {
                sendButtons(player,instance.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.playAgainButton.value"));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',line));
            }
        }
    }
    private void sendButtons(Player player, List<String> list) {
        for(String line : list) {
            checkLine(line.replace("[px]", "⚫").replace("[bx]","▄"), player);
        }
    }

    private void checkLine(String line,Player player) {
        if(line == null) return;
        String currentButtonVar;
        for(Map.Entry<String,GuardianText> currentEntry : buttons.entrySet()) {
            currentButtonVar = "<button_" + currentEntry.getKey() + ">";
            if(line.contains(currentButtonVar)) {
                line = line
                        .replace(currentButtonVar,"");
                GuardianText currentText = currentEntry.getValue();
                BaseComponent[] component = new TextBuilder()
                        .add(TextPart.of(line))
                        .add(new TextPartBuilder(currentText.getChatMessage())
                        .setHover(new HoverBuilder(currentText.getHoverMessage()))
                        .setActionClick(new ActionClick(currentText.getActionTypeClick(),currentText.getClickValue())))
                        .create();
                player.spigot().sendMessage(component);
            }
        }
    }

    public void loadOptions() {
        if(instance.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.game.show-game-buttons-on-end")) {
            FileConfiguration configuration = instance.getStorage().getControl(GuardianFiles.MESSAGES);
            String path;
            GuardianText guardianText;
            for (String values : instance.getStorage().getContent(GuardianFiles.MESSAGES, "messages.game.playAgainButton.customOptions", false)) {
                path = "messages.game.playAgainButton.customOptions." + values + ".";
                String message = configuration.getString(path + "value");
                String hover = configuration.getString(path + "hover");
                String action = configuration.getString(path + "customAction");
                String result = configuration.getString(path + "customResult");
                if (message == null) message = "notSet";
                if (hover == null) hover = "notSet";
                if (action == null) action = "CMD";
                if (result == null) result = "notSet";
                message = message.replace("[px]", "⚫");
                guardianText = new GuardianText(message);
                guardianText.setClickEvent(action, result);
                guardianText.setHoverEvent(HoverEvent.Action.SHOW_TEXT, hover);
                buttons.put(values, guardianText);
            }
            path = "messages.game.playAgainButton.play.";
            String message = configuration.getString(path + "value");
            String hover = configuration.getString(path + "hover");
            guardianText = new GuardianText(getString(message));
            guardianText.setHoverEvent("SHOW_TEXT", getString(hover));
            guardianText.setClickEvent("CMD", "/rftb playAgain");
            buttons.put("play", guardianText);
            path = "messages.game.playAgainButton.auto.";
            message = configuration.getString(path + "value");
            hover = configuration.getString(path + "hover");
            guardianText = new GuardianText(getString(message));
            guardianText.setHoverEvent("SHOW_TEXT", getString(hover));
            guardianText.setClickEvent("CMD", "/rftb autoPlay");
            buttons.put("auto", guardianText);
            path = "messages.game.playAgainButton.leave.";
            message = configuration.getString(path + "value");
            hover = configuration.getString(path + "hover");
            guardianText = new GuardianText(getString(message));
            guardianText.setHoverEvent("SHOW_TEXT", getString(hover));
            guardianText.setClickEvent("CMD", "/rftb leave");
            buttons.put("leave", guardianText);
        }
    }

    private String getString(String message) {
        if(message == null) message = "notSet";
        return message;
    }



}
