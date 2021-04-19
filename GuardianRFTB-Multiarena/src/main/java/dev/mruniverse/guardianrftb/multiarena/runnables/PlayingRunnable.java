package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayingRunnable extends BukkitRunnable {
    private final GameInfo currentGame;

    public PlayingRunnable(GameInfo game) {
        this.currentGame = game;
    }
    @Override
    public void run() {
        int time = currentGame.getLastTimer();
        if(time != 0) {
            currentGame.setLastTimer(time - 1);
        } else {
            currentGame.winRunners();
            cancel();
        }
    }
}
