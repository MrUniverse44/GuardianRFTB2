package me.blueslime.guardianrftb.multiarena;

import me.blueslime.guardianrftb.multiarena.storage.StorageManager;
import dev.mruniverse.slimelib.SlimePlatform;
import dev.mruniverse.slimelib.SlimePlugin;
import dev.mruniverse.slimelib.SlimePluginInformation;
import dev.mruniverse.slimelib.control.Control;
import dev.mruniverse.slimelib.input.InputManager;
import dev.mruniverse.slimelib.loader.BaseSlimeLoader;
import dev.mruniverse.slimelib.logs.SlimeLogger;
import dev.mruniverse.slimelib.logs.SlimeLogs;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class GuardianRFTB extends JavaPlugin implements SlimePlugin<JavaPlugin> {

    private final StorageManager storageManager = new StorageManager();

    private PluginLoader loader;

    private SlimePluginInformation information;

    private boolean hasPAPI = false;

    private SlimePlatform platform;

    private SlimeLogs logger;

    @Override
    public void onEnable() {

        this.platform = SlimePlatform.SPIGOT;

        this.logger = SlimeLogger.createLogs(
                platform,
                this,
                "GuardianRFTB"
        );

        this.information = new SlimePluginInformation(platform, this);

        this.loader = new PluginLoader(
                this,
                this,
                InputManager.create(
                        platform,
                        this
                )
        );

        this.loader.setFiles(SlimeFile.class);

        this.loader.init();

        this.hasPAPI = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

    }


    @Override
    public SlimePluginInformation getPluginInformation() {
        return information;
    }

    public Control getConfigurationHandler(SlimeFile file) {
        return getLoader().getFiles().getControl(file);
    }

    @Override
    public BaseSlimeLoader<JavaPlugin> getLoader() {
        return loader;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    @Override
    public SlimePlatform getServerType() {
        return platform;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public SlimeLogs getLogs() {
        return logger;
    }

    public boolean hasPAPI() {
        return hasPAPI;
    }

    @Override
    public void reload() {
        loader.reload();
    }
}
