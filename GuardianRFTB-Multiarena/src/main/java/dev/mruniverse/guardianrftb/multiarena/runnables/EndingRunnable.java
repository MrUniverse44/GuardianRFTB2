package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.TextBuilder;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.part.TextPart;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.part.TextPartBuilder;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.part.action.ActionClick;
import dev.mruniverse.guardianrftb.multiarena.cloudlytext.part.hover.HoverBuilder;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.listeners.api.GameRestartEvent;
import dev.mruniverse.guardianrftb.multiarena.scoreboard.PluginScoreboard;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import dev.mruniverse.guardianrftb.multiarena.utils.GuardianText;
import dev.mruniverse.guardianrftb.multiarena.utils.PlayerUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndingRunnable extends BukkitRunnable {
    private final Game currentGame;
    private final GuardianRFTB plugin;
    private boolean winnerIsRunner = false;
    private final int rewardTime;
    private final int buttonTime;
    private int time;
    private final HashMap<String, GuardianText> buttons;
    public EndingRunnable(GuardianRFTB plugin, Game game, GameTeam winnerTeam) {
        this.currentGame = game;
        this.plugin = plugin;
        game.setLastTimer(15);
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
            if(time == 3) {
                Chunk lobbyChunk = plugin.getSettings().getLocation().getChunk();
                if(!lobbyChunk.isLoaded()) lobbyChunk.load();
            }
            if(time == rewardTime) {
                for(Player player : PlayerUtil.getPlayers(plugin, currentGame.getPlayers())) {
                    plugin.getUtils().rewardInfo(player,plugin.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.gameInfo.rewardSummary"),winnerIsRunner);
                }
            }
            if(time == buttonTime) {
                if(plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.game.show-game-buttons-on-end")) {
                    for (Player player : PlayerUtil.getPlayers(plugin, currentGame.getPlayers())) {
                        sendOptions(player, plugin.getStorage().getControl(GuardianFiles.MESSAGES).getStringList("messages.game.gameInfo.playAgain"));
                    }
                }
            }
            if (winnerIsRunner) {
                for (Player player : PlayerUtil.getPlayers(plugin, currentGame.getRunners())) {
                    currentGame.firework(player, currentGame.timing(time));
                }
            } else {
                for (Player player : PlayerUtil.getPlayers(plugin, currentGame.getBeasts())) {
                    currentGame.firework(player, currentGame.timing(time));
                }
            }
            this.time--;
        } else {
            GameRestartEvent event = new GameRestartEvent(currentGame);
            Bukkit.getPluginManager().callEvent(event);
            for (Player player : PlayerUtil.getPlayers(plugin, currentGame.getPlayers())) {

                GamePlayer playerManagerImpl = plugin.getGamePlayer(player);

                if(!playerManagerImpl.getAutoPlayStatus()) {
                    back(player);
                } else {
                    playerManagerImpl.setGame(null);
                    if (playerManagerImpl.getLeaveDelay() != 0) {
                        plugin.getServer().getScheduler().cancelTask(playerManagerImpl.getLeaveDelay());
                        playerManagerImpl.setLeaveDelay(0);
                    }
                    if(!canJoin(player)) {
                        back(player);
                        plugin.getUtils().sendMessage(player,"&cAll games are in game or full");
                    }
                }
            }
            currentGame.cancelTask();
            currentGame.restart();
        }
    }

    private void back(Player player) {
        Location location = plugin.getSettings().getLocation();
        GamePlayer playerManagerImpl = plugin.getGamePlayer(player);
        if (location != null) {
            player.teleport(location);
        }
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setGameMode(plugin.getSettings().getGameMode());
        playerManagerImpl.setGame(null);
        playerManagerImpl.setLastCheckpoint(null);
        playerManagerImpl.setScoreboard(PluginScoreboard.LOBBY);
        player.getInventory().clear();
        for (ItemStack item : plugin.getItemsInfo().getLobbyItems().keySet()) {
            player.getInventory().setItem(plugin.getItemsInfo().getSlot(item), item);
        }
        if (playerManagerImpl.getLeaveDelay() != 0) {
            plugin.getServer().getScheduler().cancelTask(playerManagerImpl.getLeaveDelay());
            playerManagerImpl.setLeaveDelay(0);
        }
        player.updateInventory();
    }

    private boolean canJoin(Player player) {
        for(Game game : plugin.getGameManager().getGames()) {
            if(game.getStatus() == GameStatus.WAITING || game.getStatus() == GameStatus.SELECTING || game.getStatus() == GameStatus.STARTING) {
                if(game.getPlayers().size() < game.getMax()) {
                    plugin.getGameManager().joinGame(player,game.getConfigName());
                    return true;
                }
            }
        }
        return false;
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
