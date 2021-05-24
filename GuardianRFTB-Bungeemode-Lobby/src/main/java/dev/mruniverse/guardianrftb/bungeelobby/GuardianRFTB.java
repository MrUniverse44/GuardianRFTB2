package dev.mruniverse.guardianrftb.bungeelobby;

import dev.mruniverse.guardianlib.core.utils.ExternalLogger;
import dev.mruniverse.guardianrftb.bungeelobby.storage.FileStorage;
import org.bukkit.plugin.java.JavaPlugin;
@SuppressWarnings("unused")
public final class GuardianRFTB extends JavaPlugin {
    private FileStorage storage;
    private ExternalLogger logger;
    @Override
    public void onEnable() {
        logger = new ExternalLogger(this,"GuardianRFTB","dev.mruniverse.guardianrftb.bungeelobby");
        storage = new FileStorage(this);
    }

    @Override
    public void onDisable() {

    }
    public FileStorage getStorage() { return storage; }
    public ExternalLogger getLogs() { return logger; }
}
