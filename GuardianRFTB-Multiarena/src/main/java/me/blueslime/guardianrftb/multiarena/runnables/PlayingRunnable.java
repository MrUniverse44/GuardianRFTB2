package me.blueslime.guardianrftb.multiarena.runnables;

import me.blueslime.guardianrftb.multiarena.interfaces.Game;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayingRunnable extends BukkitRunnable {
    private final Game currentGame;

    public PlayingRunnable(Game game) {
        this.currentGame = game;
    }
    @Override
    public void run() {
        int time = currentGame.getLastTimer();
        if(time != 0 && currentGame.getRunners().size() >= 1 && currentGame.getBeasts().size() >= 1) {
            currentGame.setLastTimer(time - 1);
        } else {
            currentGame.cancelTask();
            if(currentGame.getRunners().size() >= 1) {
                currentGame.winRunners();
            } else {
                currentGame.winBeasts();
            }
        }
    }
}
