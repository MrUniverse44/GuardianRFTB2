package dev.mruniverse.guardianrftb.multiarena.storage;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.holograms.PersonalHologram;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.kits.KitMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    private final GuardianRFTB plugin;

    private final KitMenu beastMenu;
    private final KitMenu runnerMenu;
    private final KitMenu killerMenu;

    private final UUID uuid;

    private int leaveDelay;
    private int kills = 0;
    private int wins = 0;
    private int coins = 0;
    private int deaths = 0;

    private boolean pointStatus;
    private boolean autoPlay = false;

    private GameTeam currentRole = GameTeam.RUNNERS;

    private PlayerStatus playerStatus;

    private GuardianBoard guardianBoard;

    private PersonalHologram playerHologram = null;

    private Location lastCheckpoint;

    private Game currentGame;

    private String selectedKit;
    private String kits;

    public PlayerManager(GuardianRFTB plugin, Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
        leaveDelay = 0;
        guardianBoard = GuardianBoard.LOBBY;
        playerStatus = PlayerStatus.IN_LOBBY;
        beastMenu = new KitMenu(plugin, KitType.BEAST,player);
        runnerMenu = new KitMenu(plugin, KitType.RUNNER,player);
        killerMenu = new KitMenu(plugin, KitType.KILLER,player);
        pointStatus = false;
        lastCheckpoint = null;
        currentGame = null;
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
            if (!plugin.getData().isRegistered(table, "Player", getID())) {
                plugin.getData().getData().addPlayer(player.getUniqueId());
                coins = plugin.getData().getData().getCoins(player.getUniqueId());
                selectedKit = plugin.getData().getData().getSelectedKit(player.getUniqueId());
                kits = plugin.getData().getData().getKits(player.getUniqueId());
                coins = plugin.getData().getData().getCoins(player.getUniqueId());
            }
            return;
        }
        if(!plugin.getData().getSQL().exist(player.getUniqueId())) plugin.getData().getSQL().createPlayer(player);
        coins = plugin.getData().getSQL().getCoins(player.getUniqueId());
        selectedKit = plugin.getData().getSQL().getSelectedKit(player.getUniqueId());
        kits = plugin.getData().getSQL().getKits(player.getUniqueId());
        coins = plugin.getData().getSQL().getCoins(player.getUniqueId());
    }

    public void setCurrentRole(GameTeam currentRole) { this.currentRole = currentRole; }

    public String getCurrentRole() {
        return plugin.getSettings().getRole(currentRole);
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

    public void setLeaveDelay(int delay) { leaveDelay = delay; }
    public void setStatus(PlayerStatus status) {
        playerStatus = status;
    }
    public void setBoard(GuardianBoard board) {
        guardianBoard = board;
    }
    public void setGame(Game game) {
        currentGame = game;
        setLastCheckpoint(null);
    }
    public GuardianBoard getBoard() {
        return guardianBoard;
    }
    public PlayerStatus getStatus() {
        return playerStatus;
    }
    public String getName() { return getPlayer().getName(); }
    public Game getGame() { return currentGame; }
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    public int getLeaveDelay() { return leaveDelay; }
    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void addWins() {
        setWins(getWins() + 1);
    }

    public void addCoins(int coins) {
        setCoins(getCoins() + coins);
    }

    public void removeCoins(int coins) {
        setCoins(getCoins() - coins);
    }

    public boolean getPointStatus() {
        return pointStatus;
    }

    public Location getLastCheckpoint() {
        return lastCheckpoint;
    }

    public void setPointStatus(boolean bol) {
        pointStatus = bol;
    }

    public void setLastCheckpoint(Location location) {
        lastCheckpoint = location;
        FileConfiguration holograms = plugin.getStorage().getControl(GuardianFiles.HOLOGRAMS);
        if(holograms.getBoolean("holograms.checkpoint.toggle") && holograms.getBoolean("holograms.checkpoint.forPlayer.toggle")) {
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

    public int getCoins() {
        return coins;
    }
    public void updateCoins(int addOrRemove) {
        setCoins(getCoins() + addOrRemove);
    }
    public void setCoins(int coinCounter) {
        coins = coinCounter;
        plugin.getData().getData().setCoins(uuid,coinCounter);
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setSelectedKit(String kitID) {
        selectedKit = kitID;
        plugin.getData().getData().setSelectedKit(uuid,kitID);
    }

    public void setKits(String kits) {
        this.kits = kits;
    }

    public String getSelectedKit() {
        return selectedKit;
    }

    public void addKit(String kitID) {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            if(!kits.equalsIgnoreCase("")) {
                plugin.getData().getData().setKits(uuid,kits + ",K" + kitID);
                kits = kits + ",K" + kitID;
            } else {
                plugin.getData().getData().setKits(uuid,"K" + kitID);
                kits = "K" + kitID;
            }
        }
        if(plugin.getData().getSQL().kits.get(getID()) != null) {
            String lastResult = plugin.getData().getSQL().kits.get(getID());
            if(!lastResult.equalsIgnoreCase("")) {
                plugin.getData().getSQL().kits.put(getID(), lastResult + ",K" + kitID);
                kits = lastResult + ",K" + kitID;
            } else {
                plugin.getData().getSQL().kits.put(getID(), "K" + kitID);
                kits = "K" + kitID;
            }
        }
    }

    public List<String> getKits() {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            String[] kitShortList = kits.split(",");
            return Arrays.asList(kitShortList);
        }
        if(plugin.getData().getSQL().kits.get(getID()) != null) {
            String kitsBuy = plugin.getData().getSQL().kits.get(getID());
            kitsBuy = kitsBuy.replace(" ","");
            String[] kitShortList = kitsBuy.split(",");
            return Arrays.asList(kitShortList);
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unused")
    public void addKills() {
        setKills(getKills() + 1);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public String getID() {
        return uuid.toString().replace("-","");
    }

    public void addDeaths() {
        setDeaths(getDeaths() + 1);
    }

    public void create() {
        String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
        String defaultRunner = plugin.getSettings().getSettings().getString("settings.default-kits.runner");
        String defaultBeast = plugin.getSettings().getSettings().getString("settings.default-kits.beast");
        String defaultKiller = plugin.getSettings().getSettings().getString("settings.default-kits.killer");
        if (!plugin.getData().isRegistered(table, "Player", getID())) {
            List<String> values = new ArrayList<>();
            values.add("Player-" + getID());
            values.add("Coins-0");
            if(plugin.getSettings().getSettings().getBoolean("settings.default-kits.toggle")) {

                values.add("Kits-K" + defaultRunner + ",K" + defaultBeast + ",K" + defaultKiller);
            } else{
                values.add("Kits-NONE");
            }
            values.add("SelectedKit-NONE");
            plugin.getData().register(table, values);
            return;
        }
        if(plugin.getSettings().getSettings().getBoolean("settings.default-kits.toggle")) {
            kits = "Kits-K" + defaultRunner + ",K" + defaultBeast + ",K" + defaultKiller;
        } else {
            kits = "Kits-NONE";
        }
        selectedKit = "NONE";
        coins = 0;
        plugin.getData().getData().addPlayer(uuid);
    }
}
