package dev.mruniverse.guardianrftb.multiarena.listeners;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.listeners.game.BlocksListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.game.DamagesListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.game.DeathListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.lobby.DamageListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.lobby.ExtrasListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.lobby.SignListener;
import dev.mruniverse.guardianrftb.multiarena.utils.command.MainCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

@SuppressWarnings("unused")
public class ListenerController {
    private final GuardianRFTB plugin;
    private final BlocksListener blocksListener;
    private final DamagesListener damagesListener;
    private final DeathListener deathListener;
    private final DamageListener damageListener;
    private final SignListener signListener;
    private final JoinListener joinListener;
    private final QuitListener quitListener;
    private final ExtrasListener extrasListener;
    private final ChatListener chatListener;
    private final ChunkListener chunkListener;
    private final InteractListener interactListener;
    public ListenerController(GuardianRFTB plugin) {
        this.plugin = plugin;
        chunkListener = new ChunkListener(plugin);
        extrasListener = new ExtrasListener(plugin);
        damageListener = new DamageListener(plugin);
        deathListener = new DeathListener(plugin);
        damagesListener = new DamagesListener(plugin);
        blocksListener = new BlocksListener(plugin);
        signListener = new SignListener(plugin);
        joinListener = new JoinListener(plugin);
        quitListener = new QuitListener(plugin);
        chatListener = new ChatListener(plugin);
        interactListener = new InteractListener(plugin);
    }
    public void loadListeners() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        pluginManager.registerEvents(quitListener,plugin);
        pluginManager.registerEvents(extrasListener,plugin);
        pluginManager.registerEvents(joinListener,plugin);
        pluginManager.registerEvents(chunkListener,plugin);
        pluginManager.registerEvents(signListener,plugin);
        pluginManager.registerEvents(damageListener,plugin);
        pluginManager.registerEvents(damagesListener,plugin);
        pluginManager.registerEvents(deathListener,plugin);
        pluginManager.registerEvents(blocksListener,plugin);
        pluginManager.registerEvents(chatListener,plugin);
        pluginManager.registerEvents(interactListener,plugin);
    }
    public void loadCommand(String command) {
        PluginCommand cmd = plugin.getCommand(command);
        if(cmd == null) return;
        cmd.setExecutor(new MainCommand(plugin,command));
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
}
