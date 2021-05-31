package dev.mruniverse.guardianrftb.bungeegame.game.runnables;

import dev.mruniverse.guardianrftb.bungeegame.GuardianRFTB;
import dev.mruniverse.guardianrftb.bungeegame.enums.*;
import dev.mruniverse.guardianrftb.bungeegame.storage.PlayerManager;
import dev.mruniverse.guardianrftb.bungeegame.utils.GuardianUtils;
import dev.mruniverse.guardianrftb.bungeegame.utils.SoundsInfo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SelectingRunnable extends BukkitRunnable {
    private final GuardianRFTB plugin;
    private final GuardianUtils guardianUtils;
    private String enough;
    private String prefix;
    private String selecting;
    private String second;
    private String seconds;
    private String chosenBeast;
    public SelectingRunnable(GuardianRFTB plugin) {
        this.plugin = plugin;
        guardianUtils = plugin.getUtils();
        FileConfiguration configuration = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        FileConfiguration secondConfiguration = plugin.getSettings().getSettings();
        enough = configuration.getString("messages.game.game-count.enough-players");
        selecting = configuration.getString("messages.game.game-count.selecting");
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
        if(plugin.getGame().getPlayers().size() >= plugin.getGame().getMin()) {
            int time = plugin.getGame().getLastTimer();
            if(time != 0) {
                SoundsInfo sounds = plugin.getSoundsInfo();
                if(time == 30 || time == 25 || time == 20 || time == 15 || time == 10 || time == 5 || time == 4 || time == 3 || time == 2) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        guardianUtils.sendMessage(player,prefix + selecting.replace("%current_time%",time + "").replace("%current_time_letter%",seconds));
                        if(sounds.getStatus(GuardianSounds.GAME_COUNT)) player.playSound(player.getLocation(),sounds.getSound(GuardianSounds.GAME_COUNT),sounds.getVolume(GuardianSounds.GAME_COUNT),sounds.getPitch(GuardianSounds.GAME_COUNT));
                    }
                }
                if(time == 1) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        guardianUtils.sendMessage(player,prefix + selecting.replace("%current_time%",time + "").replace("%current_time_letter%",second));
                        if(sounds.getStatus(GuardianSounds.GAME_COUNT)) player.playSound(player.getLocation(),sounds.getSound(GuardianSounds.GAME_COUNT),sounds.getVolume(GuardianSounds.GAME_COUNT),sounds.getPitch(GuardianSounds.GAME_COUNT));
                    }
                }
                plugin.getGame().setLastTimer(time - 1);
            } else {
                int player = plugin.getGame().getRandom().nextInt(plugin.getGame().getRunners().size());
                setBeast(plugin.getGame().getRunners().get(player));
                if(plugin.getGame().getType().equals(GameType.DOUBLE_BEAST) || plugin.getGame().getType().equals(GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST)) {
                    player = plugin.getGame().getRandom().nextInt(plugin.getGame().getRunners().size());
                    setBeast(plugin.getGame().getRunners().get(player));
                }
                plugin.getGame().cancelTask();
                plugin.getGame().startCount();
            }
        } else {
            plugin.getGame().setDoubleCountPrevent(false);
            plugin.getGame().setGameStatus(GameStatus.WAITING);
            plugin.getGame().updateSignsBlocks();
            for(Player player : Bukkit.getOnlinePlayers()) {
                guardianUtils.sendMessage(player,prefix + enough);
            }
            plugin.getGame().cancelTask();
            plugin.setCurrentBoard(GuardianBoard.WAITING);
        }
    }

    private void setBeast(Player player) {
        plugin.getGame().getBeasts().add(player);
        plugin.getGame().getRunners().remove(player);
        for(Player game : Bukkit.getOnlinePlayers()) {
            guardianUtils.sendMessage(game,prefix + chosenBeast.replace("%player%",player.getName()));
        }
        player.getInventory().clear();
        player.getInventory().setItem(plugin.getItemsInfo().getBeastSlot(), plugin.getItemsInfo().getKitBeast());
        player.getInventory().setItem(plugin.getItemsInfo().getExitSlot(), plugin.getItemsInfo().getExit());
        player.teleport(plugin.getGame().getSelecting());
        PlayerManager currentPlayer = plugin.getPlayerData(player.getUniqueId());
        currentPlayer.setCurrentRole(GameTeam.BEASTS);
    }
}
