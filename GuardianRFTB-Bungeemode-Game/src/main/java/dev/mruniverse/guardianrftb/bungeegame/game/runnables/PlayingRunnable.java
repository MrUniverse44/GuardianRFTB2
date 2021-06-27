package dev.mruniverse.guardianrftb.bungeegame.game.runnables;

import dev.mruniverse.guardianrftb.bungeegame.GuardianRFTB;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayingRunnable extends BukkitRunnable {
    private final GuardianRFTB main;

    public PlayingRunnable(GuardianRFTB main) {
        this.main = main;
    }

    @Override
    public void run() {
        int time = main.getGame().getLastTimer();
        if(time != 0 && main.getGame().getRunners().size() >= 1 && main.getGame().getBeasts().size() >= 1) {
            main.getGame().setLastTimer(time - 1);
        } else {
            main.getGame().cancelTask();
            if(main.getGame().getRunners().size() >= 1) {
                main.getGame().winRunners();
            } else {
                main.getGame().winBeasts();
            }
        }
    }
}
