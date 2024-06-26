package dev.mruniverse.guardianrftb.bungeegame.game.runnables;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianrftb.bungeegame.GuardianRFTB;
import dev.mruniverse.guardianrftb.bungeegame.enums.*;
import dev.mruniverse.guardianrftb.bungeegame.utils.GuardianUtils;
import dev.mruniverse.guardianrftb.bungeegame.utils.SoundsInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class StartRunnable  extends BukkitRunnable {
    private final GuardianRFTB plugin;
    private final GuardianUtils guardianUtils;
    private String enough;
    private String prefix;
    private String starting;
    private String second;
    private String seconds;
    public StartRunnable(GuardianRFTB plugin) {
        this.plugin = plugin;
        guardianUtils = plugin.getUtils();
        FileConfiguration configuration = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        FileConfiguration secondConfiguration = plugin.getSettings().getSettings();
        enough = configuration.getString("messages.game.game-count.enough-players");
        starting = configuration.getString("messages.game.game-count.start");
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
        if(plugin.getGame().getPlayers().size() >= plugin.getGame().getMin()) {
            int time = plugin.getGame().getLastTimer();
            if(time != 0) {
                SoundsInfo sounds = plugin.getSoundsInfo();
                if(time == 30 || time == 25 || time == 20 || time == 15 || time == 10 || time == 5 || time == 4 || time == 3 || time == 2) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        guardianUtils.sendMessage(player,prefix + starting.replace("%current_time%",time + "").replace("%current_time_letter%",seconds));
                        if(sounds.getStatus(GuardianSounds.STARTING_COUNT)) player.playSound(player.getLocation(),sounds.getSound(GuardianSounds.STARTING_COUNT),sounds.getVolume(GuardianSounds.STARTING_COUNT),sounds.getPitch(GuardianSounds.STARTING_COUNT));
                    }
                }
                if(time == 1) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        guardianUtils.sendMessage(player,prefix + starting.replace("%current_time%",time + "").replace("%current_time_letter%",second));
                        if(sounds.getStatus(GuardianSounds.STARTING_COUNT)) player.playSound(player.getLocation(),sounds.getSound(GuardianSounds.STARTING_COUNT),sounds.getVolume(GuardianSounds.STARTING_COUNT),sounds.getPitch(GuardianSounds.STARTING_COUNT));
                    }
                }
                plugin.getGame().setLastTimer(time - 1);
            } else {
                FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
                String title = messages.getString("messages.game.others.titles.runnersGo.toRunners.title");
                String subtitle = messages.getString("messages.game.others.titles.runnersGo.toRunners.subtitle");
                String bTitle = messages.getString("messages.game.others.titles.runnersGo.toBeasts.title");
                String bSubtitle = messages.getString("messages.game.others.titles.runnersGo.toBeasts.subtitle");
                List<String> startInfo = messages.getStringList("messages.game.gameInfo.startGame");
                plugin.getGame().setInvincible(false);
                for(Player player : plugin.getGame().getRunners()) {
                    player.teleport(plugin.getGame().getRunnerSpawn());
                    player.getInventory().clear();
                    player.setGameMode(GameMode.SURVIVAL);
                    plugin.getItems(GameEquip.RUNNER_KIT,player);
                    guardianUtils.sendList(player,startInfo);
                    GuardianLIB.getControl().getUtils().sendTitle(player, 0, 20, 10, title, subtitle);

                }
                for(Player player : plugin.getGame().getBeasts()) {
                    GuardianLIB.getControl().getUtils().sendTitle(player, 0, 20, 10, bTitle, bSubtitle);
                }
                World world = plugin.getGame().getRunnerSpawn().getWorld();
                if(world != null) world.setTime(plugin.getGame().getWorldTime());
                plugin.getGame().cancelTask();
                plugin.getGame().beastCount();

            }
        } else {
            plugin.getGame().setDoubleCountPrevent(false);
            plugin.getGame().setGameStatus(GameStatus.WAITING);
            plugin.getGame().updateSignsBlocks();
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.setGameMode(GameMode.ADVENTURE);
                guardianUtils.sendMessage(player,prefix + enough);
            }
            plugin.setCurrentBoard(GuardianBoard.WAITING);
            for(Player beasts : plugin.getGame().getBeasts()) {
                plugin.getGame().getRunners().add(beasts);
                beasts.getInventory().clear();
                beasts.getInventory().setItem(plugin.getItemsInfo().getRunnerSlot(), plugin.getItemsInfo().getKitRunner());
                beasts.getInventory().setItem(plugin.getItemsInfo().getExitSlot(), plugin.getItemsInfo().getExit());
                plugin.getUser(beasts.getUniqueId()).setCurrentRole(GameTeam.RUNNERS);
                beasts.teleport(plugin.getGame().getWaiting());
            }
            plugin.getGame().cancelTask();
            plugin.getGame().getBeasts().clear();
        }
    }
}

