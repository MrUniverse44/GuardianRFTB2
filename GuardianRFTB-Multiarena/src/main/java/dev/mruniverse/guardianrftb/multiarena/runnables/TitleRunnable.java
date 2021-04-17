package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class TitleRunnable extends BukkitRunnable {
    private final GuardianRFTB plugin;
    private boolean isEnabled;
    private int showingTitle = 0;
    private List<String> titles;
    public TitleRunnable(GuardianRFTB main) {
        plugin = main;
        titles = main.getStorage().getControl(GuardianFiles.SCOREBOARD).getStringList("scoreboards.animatedTitle.titles");
        isEnabled = main.getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.animatedTitle.toggle");
    }
    public void update() {
        titles = plugin.getStorage().getControl(GuardianFiles.SCOREBOARD).getStringList("scoreboards.animatedTitle.titles");
        isEnabled = plugin.getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.animatedTitle.toggle");
    }
    @Override
    public void run () {
        if(!isEnabled) cancel();
        for (UUID uuid : plugin.getRigoxPlayers().keySet()) {
            PlayerManager playerManager = plugin.getPlayerData(uuid);
            String currentTitle = titles.get(showingTitle);
            plugin.getScoreboards().setTitle(playerManager.getPlayer(),currentTitle);
            if(showingTitle == (titles.size() - 1)) {
                showingTitle = 0;
            } else {
                showingTitle++;
            }
        }
    }
}
