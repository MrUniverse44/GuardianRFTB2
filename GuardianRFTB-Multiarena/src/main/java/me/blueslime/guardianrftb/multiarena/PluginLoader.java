package me.blueslime.guardianrftb.multiarena;

import me.blueslime.guardianrftb.multiarena.game.GameManager;
import me.blueslime.guardianrftb.multiarena.listeners.ListenerManager;
import dev.mruniverse.slimelib.SlimePlugin;
import dev.mruniverse.slimelib.SlimeStorage;
import dev.mruniverse.slimelib.input.InputManager;
import dev.mruniverse.slimelib.loader.BaseSlimeLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginLoader extends BaseSlimeLoader<JavaPlugin> {
    private final ListenerManager listenerManager;

    private final GameManager gameManager;

    public PluginLoader(SlimePlugin<JavaPlugin> instance, GuardianRFTB plugin, InputManager inputManager) {
        super(instance);

        super.storage(new SlimeStorage(
                plugin.getServerType(),
                plugin.getLogs(),
                inputManager
        ));

        this.listenerManager = new ListenerManager(plugin);

        this.gameManager = new GameManager(plugin);
    }

    @Override
    public void init() {
        if (getFiles() != null) {
            getFiles().init();
        }
        gameManager.initialize();
        listenerManager.loadListeners();
    }

    public GameManager getGameManager() {
        return gameManager;
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
