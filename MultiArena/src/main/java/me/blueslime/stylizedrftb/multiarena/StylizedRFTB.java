package me.blueslime.stylizedrftb.multiarena;

import me.blueslime.stylizedrftb.multiarena.loader.PluginLoader;
import me.blueslime.stylizedrftb.multiarena.loader.PluginLoaderDelay;
import me.blueslime.stylizedrftb.multiarena.storage.StorageManager;
import dev.mruniverse.slimelib.SlimePlatform;
import dev.mruniverse.slimelib.SlimePlugin;
import dev.mruniverse.slimelib.SlimePluginInformation;
import dev.mruniverse.slimelib.logs.SlimeLogger;
import dev.mruniverse.slimelib.logs.SlimeLogs;
import me.blueslime.stylizedrftb.multiarena.metrics.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class StylizedRFTB extends JavaPlugin implements SlimePlugin<JavaPlugin> {

    private final StorageManager storageManager = new StorageManager();
    private static boolean PLACE_HOLDER_SUPPORT = false;
    private SlimePluginInformation information;
    private SlimePlatform platform;
    private PluginLoader loader;
    private SlimeLogs logs;

    @Override
    public void onEnable() {

        this.platform = SlimePlatform.SPIGOT;

        this.logs = SlimeLogger.createLogs(
                platform,
                this,
                "GuardianRFTB"
        );

        logs.getPrefixes().getDebug().setPrefix("&9STYLIZED RFTB | &f");
        logs.getPrefixes().getInfo().setPrefix("&bSTYLIZED RFTB | &f");
        logs.getPrefixes().getIssue().setPrefix("&6STYLIZED RFTB | &f");
        logs.getPrefixes().getWarn().setPrefix("&eSTYLIZED RFTB | &f");

        this.information = new SlimePluginInformation(
                platform,
                this
        );

        this.loader = new PluginLoader(
                this,
                this
        );

        new PluginLoaderDelay(
                this
        ).runTaskLater(this,  20L);

        PLACE_HOLDER_SUPPORT = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        new Metrics(this, 11302);



    }


    @Override
    public SlimePluginInformation getPluginInformation() {
        return information;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    @Override
    public SlimePlatform getServerType() {
        return platform;
    }

    @Override
    public PluginLoader getLoader() {
        return loader;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public SlimeLogs getLogs() {
        return logs;
    }

    public static boolean hasPAPI() {
        return PLACE_HOLDER_SUPPORT;
    }

    @Override
    public void reload() {
        loader.reload();
    }
}
