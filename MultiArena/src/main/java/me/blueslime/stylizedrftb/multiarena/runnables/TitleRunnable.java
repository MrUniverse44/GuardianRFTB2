package me.blueslime.stylizedrftb.multiarena.runnables;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.enums.GuardianFiles;
import me.blueslime.stylizedrftb.multiarena.player.GamePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
@SuppressWarnings("unused")
public class TitleRunnable extends BukkitRunnable {
    private final StylizedRFTB plugin;
    private boolean isEnabled;
    private int showingTitle = 0;
    private List<String> titles;
    public TitleRunnable(StylizedRFTB main) {
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
            GamePlayer gamePlayerImpl = plugin.getUser(uuid);
            String currentTitle = titles.get(showingTitle);
            plugin.getScoreboards().setTitle(gamePlayerImpl.getPlayer(),currentTitle);
            if(showingTitle == (titles.size() - 1)) {
                showingTitle = 0;
            } else {
                showingTitle++;
            }
        }
    }
}
