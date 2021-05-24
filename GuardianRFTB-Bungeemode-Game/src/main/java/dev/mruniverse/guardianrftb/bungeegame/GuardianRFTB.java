package dev.mruniverse.guardianrftb.bungeegame;

import dev.mruniverse.guardianlib.core.utils.ExternalLogger;
import dev.mruniverse.guardianrftb.bungeegame.storage.FileStorage;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class GuardianRFTB extends JavaPlugin {
    private FileStorage storage;
    private ExternalLogger logger;
    private static GuardianRFTB instance;
    @Override
    public void onEnable() {
        instance = this;
        logger = new ExternalLogger(this,"GuardianRFTB","dev.mruniverse.guardianrftb.bungeegame");
        storage = new FileStorage(this);
    }

    @Override
    public void onDisable() {

    }
    public static GuardianRFTB getInstance() { return instance; }
    public FileStorage getStorage() { return storage; }
    public ExternalLogger getLogs() { return logger; }
}

