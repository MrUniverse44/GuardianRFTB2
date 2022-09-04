package me.blueslime.stylizedrftb.multiarena.listeners;

import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.listeners.game.BlocksListener;
import me.blueslime.stylizedrftb.multiarena.listeners.game.DamagesListener;
import me.blueslime.stylizedrftb.multiarena.listeners.game.DeathListener;
import me.blueslime.stylizedrftb.multiarena.listeners.lobby.DamageListener;
import me.blueslime.stylizedrftb.multiarena.listeners.lobby.ExtrasListener;
import me.blueslime.stylizedrftb.multiarena.listeners.lobby.SignListener;
import me.blueslime.stylizedrftb.multiarena.command.MainCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

@SuppressWarnings("unused")
public class ListenerManager {
    private final InteractListener interactListener;
    private final DamagesListener damagesListener;
    private final ExtrasListener extrasListener;
    private final BlocksListener blocksListener;
    private final DamageListener damageListener;
    private final ChunkListener chunkListener;
    private final DeathListener deathListener;
    private final SignListener signListener;
    private final JoinListener joinListener;
    private final QuitListener quitListener;
    private final ChatListener chatListener;
    private final StylizedRFTB plugin;

    public ListenerManager(StylizedRFTB plugin) {
        this.plugin = plugin;

        interactListener = new InteractListener(plugin);
        damagesListener  = new DamagesListener(plugin);
        blocksListener   = new BlocksListener(plugin);
        extrasListener   = new ExtrasListener(plugin);
        damageListener   = new DamageListener(plugin);
        chunkListener    = new ChunkListener(plugin);
        deathListener    = new DeathListener(plugin);
        signListener     = new SignListener(plugin);
        joinListener     = new JoinListener(plugin);
        quitListener     = new QuitListener(plugin);
        chatListener     = new ChatListener(plugin);
    }
    public void loadListeners() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

        pluginManager.registerEvents(
                quitListener,
                plugin
        );
        pluginManager.registerEvents(
                extrasListener,
                plugin
        );
        pluginManager.registerEvents(
                joinListener,
                plugin
        );
        pluginManager.registerEvents(
                chunkListener,
                plugin
        );
        pluginManager.registerEvents(
                signListener,
                plugin
        );
        pluginManager.registerEvents(
                damageListener,
                plugin
        );
        pluginManager.registerEvents(
                damagesListener,
                plugin
        );
        pluginManager.registerEvents(
                deathListener,
                plugin
        );
        pluginManager.registerEvents(
                blocksListener,
                plugin
        );
        pluginManager.registerEvents(
                chatListener,
                plugin
        );
        pluginManager.registerEvents(
                interactListener,
                plugin
        );
    }

    public void reloadListeners() {
        blocksListener.updateAll();
        quitListener.updateAll();
        joinListener.updateAll();
        chunkListener.updateAll();
        damagesListener.updateAll();
        damageListener.updateAll();
        deathListener.updateAll();
        signListener.updateAll();
        chatListener.updateAll();
        interactListener.updateAll();
    }

    public void loadCommand(String command) {
        PluginCommand pluginCommand = plugin.getCommand(command);

        if (pluginCommand == null) {
            return;
        }

        pluginCommand.setExecutor(
                new MainCommand(
                        plugin,
                        command
                )
        );
    }
}
