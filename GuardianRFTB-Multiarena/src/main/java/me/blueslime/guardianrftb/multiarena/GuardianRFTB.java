package me.blueslime.guardianrftb.multiarena;

import dev.mruniverse.slimelib.logs.SlimeLoggerProperties;
import dev.mruniverse.slimelib.storage.FileStorage;
import me.blueslime.guardianrftb.multiarena.game.GameManager;
import me.blueslime.guardianrftb.multiarena.storage.StorageManager;
import dev.mruniverse.slimelib.SlimePlatform;
import dev.mruniverse.slimelib.SlimePlugin;
import dev.mruniverse.slimelib.SlimePluginInformation;
import dev.mruniverse.slimelib.control.Control;
import dev.mruniverse.slimelib.input.InputManager;
import dev.mruniverse.slimelib.logs.SlimeLogger;
import dev.mruniverse.slimelib.logs.SlimeLogs;
import me.blueslime.guardianrftb.multiarena.utils.BukkitMetrics;
import me.blueslime.guardianrftb.multiarena.utils.GameUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class GuardianRFTB extends JavaPlugin implements SlimePlugin<JavaPlugin> {

    private static FileStorage FILE_STORAGE;

    private final StorageManager storageManager = new StorageManager();

    private static boolean PLACE_HOLDER_SUPPORT = false;

    private SlimePluginInformation information;

    private GameUtils gameUtils;

    private SlimePlatform platform;

    private PluginLoader loader;

    private SlimeLogs logger;

    @Override
    public void onEnable() {

        this.platform = SlimePlatform.SPIGOT;

        this.logger = SlimeLogger.createLogs(
                platform,
                this,
                "GuardianRFTB"
        );

        SlimeLoggerProperties properties = logger.getProperties();

        properties.getPrefixes().getDebug().setPrefix("&9GUARDIAN RFTB | &f");
        properties.getPrefixes().getInfo().setPrefix("&bGUARDIAN RFTB | &f");
        properties.getPrefixes().getIssue().setPrefix("&6GUARDIAN RFTB | &f");
        properties.getPrefixes().getWarn().setPrefix("&eGUARDIAN RFTB | &f");

        this.logger.getSlimeLogger().setProperties(properties);

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

        this.gameUtils = new GameUtils(this);

        FILE_STORAGE = loader.getFiles();

        PLACE_HOLDER_SUPPORT = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        new BukkitMetrics(this, 11302);



    }

    public GameManager getGameManager() {
        return loader.getGameManager();
    }

    public GameUtils getGameUtils() {
        return gameUtils;
    }

    @Override
    public SlimePluginInformation getPluginInformation() {
        return information;
    }

    public Control getConfigurationHandler(SlimeFile file) {
        return getLoader().getFiles().getControl(file);
    }

    @Override
    public PluginLoader getLoader() {
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

    public static boolean hasPAPI() {
        return PLACE_HOLDER_SUPPORT;
    }

    public static FileStorage getFileStorage() {
        return FILE_STORAGE;
    }

    @Override
    public void reload() {
        loader.reload();
    }
}
