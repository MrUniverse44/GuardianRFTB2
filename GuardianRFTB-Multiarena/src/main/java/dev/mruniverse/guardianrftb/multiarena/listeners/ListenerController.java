package dev.mruniverse.guardianrftb.multiarena.listeners;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.listeners.game.BlocksListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.game.DamagesListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.game.DeathListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.lobby.BlockListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.lobby.DamageListener;
import dev.mruniverse.guardianrftb.multiarena.listeners.lobby.SignListener;
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
    private final BlockListener blockListener;
    public ListenerController(GuardianRFTB plugin) {
        this.plugin = plugin;
        blockListener = new BlockListener();
        damageListener = new DamageListener();
        deathListener = new DeathListener();
        damagesListener = new DamagesListener();
        blocksListener = new BlocksListener();
        signListener = new SignListener();
        joinListener = new JoinListener();
        quitListener = new QuitListener();
    }
    public void loadListeners() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        pluginManager.registerEvents(blockListener,plugin);
        pluginManager.registerEvents(quitListener,plugin);
        pluginManager.registerEvents(joinListener,plugin);
        pluginManager.registerEvents(signListener,plugin);
        pluginManager.registerEvents(damageListener,plugin);
        pluginManager.registerEvents(damagesListener,plugin);
        pluginManager.registerEvents(deathListener,plugin);
        pluginManager.registerEvents(blocksListener,plugin);
    }
    public void loadCommand(String command) {
        PluginCommand cmd = plugin.getCommand(command);
        if(cmd == null) return;
        //cmd.setExecutor(new MainCommand(plugin));
    }
    public void reloadListeners() {
        blocksListener.updateAll();
        blockListener.updateAll();
        quitListener.updateAll();
        joinListener.updateAll();
        damagesListener.updateAll();
        damageListener.updateAll();
        deathListener.updateAll();
        signListener.updateAll();
    }
}
