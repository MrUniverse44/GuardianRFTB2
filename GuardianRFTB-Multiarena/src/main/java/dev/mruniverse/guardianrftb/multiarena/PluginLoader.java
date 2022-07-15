package dev.mruniverse.guardianrftb.multiarena;

import dev.mruniverse.guardianrftb.multiarena.listeners.ListenerManager;
import dev.mruniverse.slimelib.SlimePlugin;
import dev.mruniverse.slimelib.SlimeStorage;
import dev.mruniverse.slimelib.input.InputManager;
import dev.mruniverse.slimelib.loader.BaseSlimeLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginLoader extends BaseSlimeLoader<JavaPlugin> {

    private final ListenerManager listenerManager;

    public PluginLoader(SlimePlugin<JavaPlugin> instance, GuardianRFTB plugin, InputManager inputManager) {
        super(instance);

        super.storage(new SlimeStorage(
                plugin.getServerType(),
                plugin.getLogs(),
                inputManager
        ));

        this.listenerManager = new ListenerManager(plugin);

        this.listenerManager.loadListeners();
    }

    @Override
    public void init() {
        if (getFiles() != null) {
            getFiles().init();
        }
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
