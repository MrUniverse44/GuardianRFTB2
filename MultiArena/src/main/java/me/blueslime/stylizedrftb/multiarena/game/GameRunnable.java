package me.blueslime.stylizedrftb.multiarena.game;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;

public abstract class GameRunnable implements Runnable {

    private final StylizedRFTB plugin;

    private boolean executed = false;

    private int timer = 30;

    private int id;

    public GameRunnable(StylizedRFTB plugin) {
        this.plugin = plugin;
    }

    @Override
    public abstract void run();

    public void execute() {
        id = plugin.getServer().getScheduler().runTaskTimer(
                plugin,
                this,
                0L,
                20L
        ).getTaskId();

        executed = true;
    }

    public void cancel() {
        plugin.getServer().getScheduler().cancelTask(
                id
        );
        executed = false;
    }

    public void setTimer(int value) {
        this.timer = value;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public boolean isExecuted() {
        return executed;
    }

    public int getId() {
        return id;
    }

    public int getTimer() {
        return timer;
    }
}
