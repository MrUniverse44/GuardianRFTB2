package dev.mruniverse.guardianrftb.multiarena.storage;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.holograms.PersonalHologram;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.kits.KitMenu;
import dev.mruniverse.guardianrftb.multiarena.scoreboard.PluginScoreboard;
import dev.mruniverse.guardianrftb.multiarena.storage.result.DataResult;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GamePlayer {

    private final GuardianRFTB plugin;

    private final KitMenu runnerMenu;
    private final KitMenu killerMenu;
    private final KitMenu beastMenu;

    private DataResult statistics = DataResult.empty();

    private final UUID uuid;

    private boolean autoPlay = false;
    private int leaveDelay = 0;

    private PersonalHologram playerHologram = null;

    private PluginScoreboard pluginScoreboard = PluginScoreboard.LOBBY;

    private Location lastCheckpoint = null;
    private Game game = null;

    public GamePlayer(GuardianRFTB plugin, Player player) {
        this.plugin = plugin;

        this.uuid = player.getUniqueId();

        runnerMenu = new KitMenu(plugin, KitType.RUNNER, player);
        killerMenu = new KitMenu(plugin, KitType.KILLER, player);
        beastMenu = new KitMenu(plugin, KitType.BEAST, player);
    }

    public boolean hasSelectedKit(KitType type) {
        return !getSelectedKit(type).isEmpty();
    }

    public String getCurrentRole() {
        if (game != null) {
            if (game.getBeasts().contains(uuid)) {
                return plugin.getSettings().getRole(GameTeam.BEASTS);
            } else if (game.getKillers().contains(uuid)) {
                return plugin.getSettings().getRole(GameTeam.KILLER);
            } else {
                return plugin.getSettings().getRole(GameTeam.RUNNERS);
            }
        } else {
            return plugin.getSettings().getRole(GameTeam.RUNNERS);
        }
    }

    public boolean getAutoPlayStatus() { return autoPlay; }


    public boolean toggleAutoplay() {
        autoPlay = !autoPlay;
        return autoPlay;
    }

    public KitMenu getKitMenu(KitType kitType) {
        switch (kitType) {
            case KILLER:
                return killerMenu;
            case BEAST:
                return beastMenu;
            case RUNNER:
            default:
                return runnerMenu;
        }
    }

    public void setLeaveDelay(int delay) {
        this.leaveDelay = delay;
    }

    public void setScoreboard(PluginScoreboard board) {
        this.pluginScoreboard = board;
    }

    public void setGame(Game game) {
        this.game = game;
        setLastCheckpoint(null);
    }

    public PluginScoreboard getScoreboard() {
        return pluginScoreboard;
    }

    public PlayerStatus getStatus() {
        return game == null ? PlayerStatus.IN_LOBBY : PlayerStatus.IN_GAME;
    }

    public String getName() {
        Player player = getPlayer();

        if (player == null) {
            return "";
        }

        return player.getName();
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return plugin.getServer().getPlayer(uuid);
    }

    public int getLeaveDelay() {
        return leaveDelay;
    }

    public DataResult getStatistics() {
        return statistics;
    }

    public Location getLastCheckpoint() {
        return lastCheckpoint;
    }

    public void setLastCheckpoint(Location location) {
        this.lastCheckpoint = location;

        FileConfiguration holograms = plugin.getStorage().getControl(GuardianFiles.HOLOGRAMS);

        if (holograms.getBoolean("holograms.checkpoint.toggle", true) && holograms.getBoolean("holograms.checkpoint.forPlayer.toggle", true)) {
            if (location != null) {
                if(playerHologram == null) {
                    List<String> list = new ArrayList<>();
                    for(String line : holograms.getStringList("holograms.checkpoint.forPlayer.value")) {
                        list.add(ChatColor.translateAlternateColorCodes('&',line));
                    }
                    playerHologram = new PersonalHologram(GuardianLIB.getControl(), getPlayer(), location, uuid.toString(), list);
                } else {
                    playerHologram.delete();
                    playerHologram.setLocation(location);
                }
                playerHologram.spawn();
            } else {
                if(playerHologram == null) return;
                playerHologram.delete();
            }
        }
    }

    public String getSelectedKit(KitType type) {
        if (type == KitType.BEAST) {
            return statistics.getBeastKit();
        } else if (type == KitType.RUNNER) {
            return statistics.getRunnerKit();
        } else {
            return statistics.getKillerKit();
        }
    }

    public String getId() {
        return uuid.toString().replace("-","");
    }

    public void load(Player player) {
        setData(
            plugin.getDatabase().load(
                player
            )
        );
    }

    public void setData(DataResult data) {
        this.statistics = data;
    }

    public boolean isPlaying() {
        return game != null;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setSelectedKit(KitType type, String kitID) {
        if (type == KitType.BEAST) {
            statistics.setBeastKit(kitID);
        } else if (type == KitType.RUNNER) {
            statistics.setRunnerKit(kitID);
        } else {
            statistics.setKillerKit(kitID);
        }
    }
}
