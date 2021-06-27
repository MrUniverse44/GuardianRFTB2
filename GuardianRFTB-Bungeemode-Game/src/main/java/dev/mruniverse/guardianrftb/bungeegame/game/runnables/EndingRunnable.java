package dev.mruniverse.guardianrftb.bungeegame.game.runnables;

import dev.mruniverse.guardianrftb.bungeegame.enums.GameTeam;
import dev.mruniverse.guardianrftb.bungeegame.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.bungeegame.GuardianRFTB;
import dev.mruniverse.guardianrftb.bungeegame.cloudlytext.TextBuilder;
import dev.mruniverse.guardianrftb.bungeegame.cloudlytext.part.TextPart;
import dev.mruniverse.guardianrftb.bungeegame.cloudlytext.part.TextPartBuilder;
import dev.mruniverse.guardianrftb.bungeegame.cloudlytext.part.action.ActionClick;
import dev.mruniverse.guardianrftb.bungeegame.cloudlytext.part.hover.HoverBuilder;
import dev.mruniverse.guardianrftb.bungeegame.storage.PlayerManager;
import dev.mruniverse.guardianrftb.bungeegame.utils.GuardianText;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndingRunnable extends BukkitRunnable {
    private final GuardianRFTB plugin;
    private boolean winnerIsRunner = false;
    private final int rewardTime;
    private final int buttonTime;
    private int time;
    private final HashMap<String, GuardianText> buttons;
    public EndingRunnable(GuardianRFTB plugin, GameTeam winnerTeam) {
        this.plugin = plugin;
        plugin.getGame().setLastTimer(15);
        time = 15;
        buttons = new HashMap<>();
        if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.game.show-game-buttons-on-end")) {
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
                for(Player player : Bukkit.getOnlinePlayers()) {
                    plugin.getUtils().rewardInfo(player,plugin.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.gameInfo.rewardSummary"),winnerIsRunner);
                }
            }
            if(time == buttonTime) {
                if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.game.show-game-buttons-on-end")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        sendOptions(player, plugin.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.gameInfo.playAgain"));
                    }
                }
            }
            if (winnerIsRunner) {
                for (Player player : plugin.getGame().getRunners()) {
                    plugin.getGame().firework(player, plugin.getGame().timing(time));
                }
            } else {
                for (Player player : plugin.getGame().getBeasts()) {
                    plugin.getGame().firework(player, plugin.getGame().timing(time));
                }
            }
            this.time--;
        } else {
            for (Player player : plugin.getGame().getPlayers()) {
                back(player);
            }
            plugin.getGame().cancelTask();
            plugin.getGame().restart();
        }
    }

    private void back(Player player) {
        Location location = plugin.getSettings().getLocation();
        PlayerManager playerManager = plugin.getPlayerData(player.getUniqueId());
        if (location != null) {
            player.teleport(location);
        }
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        playerManager.setLastCheckpoint(null);
        player.setGameMode(plugin.getSettings().getGameMode());
        player.getInventory().clear();
        if (playerManager.getLeaveDelay() != 0) {
            plugin.getServer().getScheduler().cancelTask(playerManager.getLeaveDelay());
            playerManager.setLeaveDelay(0);
        }
        player.updateInventory();
    }


    public void sendOptions(Player player, List<String> list) {
        for(String line : list) {
            if(line.contains("<playAgainButton>")) {
                sendButtons(player,plugin.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.playAgainButton.value"));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',line.replace("[px]", "⚫").replace("[bx]","▄")));
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
        line = line.replace("[bx]","▄");
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
        if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.game.show-game-buttons-on-end")) {
            FileConfiguration configuration = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
            String path;
            GuardianText guardianText;
            for (String values : plugin.getStorage().getContent(GuardianFiles.MESSAGES, "messages.game.playAgainButton.customOptions", false)) {
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

