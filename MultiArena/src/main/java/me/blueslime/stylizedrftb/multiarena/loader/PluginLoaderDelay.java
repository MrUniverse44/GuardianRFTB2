package me.blueslime.stylizedrftb.multiarena.loader;

import me.blueslime.stylizedrftb.multiarena.SlimeFile;
import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.command.MainCommand;
import org.bukkit.scheduler.BukkitRunnable;

public class PluginLoaderDelay extends BukkitRunnable {

    private final StylizedRFTB main;

    public PluginLoaderDelay(StylizedRFTB main) {
        this.main = main;
    }

    @Override
    public void run() {
        main.getLoader().setFiles(SlimeFile.class);

        main.getLoader().init();

        main.getCommands().register(
                new MainCommand(
                        main,
                        "rftb"
                )
        );
    }
}
