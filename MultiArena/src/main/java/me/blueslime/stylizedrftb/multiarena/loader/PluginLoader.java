package me.blueslime.stylizedrftb.multiarena.loader;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.listeners.ListenerManager;
import dev.mruniverse.slimelib.SlimePlugin;
import dev.mruniverse.slimelib.loader.BaseSlimeLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginLoader extends BaseSlimeLoader<JavaPlugin> {
    private final ListenerManager listenerManager;

    public PluginLoader(SlimePlugin<JavaPlugin> instance, StylizedRFTB plugin) {
        super(instance);

        this.listenerManager = new ListenerManager(plugin);
    }

    @Override
    public void init() {
        if (getFiles() != null) {
            getFiles().init();
        }
        listenerManager.loadListeners();
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    @Override
    public void shutdown() {
        getCommands().unregister();
    }

    @Override
    public void reload() {
        getFiles().reloadFiles();
        listenerManager.reloadListeners();
    }
}
