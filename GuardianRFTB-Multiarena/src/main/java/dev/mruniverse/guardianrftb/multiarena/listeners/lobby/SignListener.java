package dev.mruniverse.guardianrftb.multiarena.listeners.lobby;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.SaveMode;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SignListener implements Listener {
    private final GuardianRFTB plugin;
    public SignListener(GuardianRFTB plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("GuardianRFTB.admin.signCreate"))
            return;
        try {
            String line1 = event.getLine(0);
            if(line1 == null) return;
            if (line1.equalsIgnoreCase("[RFTB]") || line1.equalsIgnoreCase("[EDLB]")) {
                String name = event.getLine(1);
                if(name == null) name = "null";
                final Game game = plugin.getGameManager().getGame(name);
                if (game == null) {
                    String errorMsg = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.admin.arenaError");
                    if(errorMsg == null) errorMsg = "&c%arena_id% don't exists";
                    errorMsg = errorMsg.replace("%arena_id%", name);
                    plugin.getUtils().sendMessage(player,errorMsg);
                    return;
                }
                List<String> signs = plugin.getStorage().getControl(GuardianFiles.GAMES).getStringList("games." + name + ".signs");
                signs.add(plugin.getLib().getUtils().getStringFromLocation(event.getBlock().getLocation()));
                plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + name + ".signs",signs);
                plugin.getStorage().save(SaveMode.GAMES_FILES);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aSign added!"));
                game.loadSigns();
            }
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't create plugin sign");
            plugin.getLogs().error(throwable);
        }
    }

    @EventHandler
    public void SignInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = e.getPlayer();
        try {
            Block block = e.getClickedBlock();
            if(block == null) return;
            if (block.getState() instanceof Sign) {
                for (Game game : plugin.getGameManager().getGames()) {
                    if (game.getSigns().contains(e.getClickedBlock().getLocation())) {
                        game.join(player);
                        return;
                    }
                }
            }
        }catch (Throwable ignored) {}
    }
    public void updateAll() {

    }
}
