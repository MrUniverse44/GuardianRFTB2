package dev.mruniverse.guardianrftb.multiarena.storage;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.kits.KitMenu;
import dev.mruniverse.guardianrftb.multiarena.enums.KitType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerManager {
    private GuardianRFTB plugin;
    private PlayerStatus playerStatus;
    private GuardianBoard guardianBoard;
    private final Player player;
    private boolean pointStatus;
    private Location lastCheckpoint;
    private GameInfo currentGame;
    private final KitMenu beastMenu;
    private final KitMenu runnerMenu;
    private final KitMenu killerMenu;
    private String selectedKit;
    private String kits;
    private int leaveDelay;
    private int kills = 0;
    private int wins = 0;
    private int coins = 0;
    private int deaths = 0;

    public PlayerManager(GuardianRFTB plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
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
    public void setGame(GameInfo game) { currentGame = game; }
    public GuardianBoard getBoard() {
        return guardianBoard;
    }
    public PlayerStatus getStatus() {
        return playerStatus;
    }
    public String getName() {
        return player.getName();
    }
    public GameInfo getGame() { return currentGame; }
    public Player getPlayer() {
        return player;
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
    }

    public int getCoins() {
        return coins;
    }
    public void updateCoins(int addOrRemove) {
        setCoins(getCoins() + addOrRemove);
    }
    public void setCoins(int coinCounter) {
        coins = coinCounter;
        plugin.getData().getData().setCoins(player.getUniqueId(),coinCounter);
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setSelectedKit(String kitID) {
        selectedKit = kitID;
        plugin.getData().getData().setSelectedKit(player.getUniqueId(),kitID);
    }

    public String getSelectedKit() {
        return selectedKit;
    }

    public void addKit(String kitID) {
        if (plugin.getStorage().getControl(GuardianFiles.MYSQL).getBoolean("mysql.enabled")) {
            if(!kits.equalsIgnoreCase("")) {
                plugin.getData().getData().setKits(player.getUniqueId(),kits + ",K" + kitID);
            } else {
                plugin.getData().getData().setKits(player.getUniqueId(),"K" + kitID);
                kits = "K" + kitID;
            }
        }
        if(plugin.getData().getSQL().kits.get(getID()) != null) {
            String lastResult = plugin.getData().getSQL().kits.get(getID());
            if(!lastResult.equalsIgnoreCase("")) {
                plugin.getData().getSQL().kits.put(getID(), lastResult + ",K" + kitID);
            } else {
                plugin.getData().getSQL().kits.put(getID(), "K" + kitID);
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
        return player.getUniqueId().toString().replace("-","");
    }

    public void addDeaths() {
        setDeaths(getDeaths() + 1);
    }

    public void create() {
        String table = plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("mysql.table");
        if (!plugin.getData().isRegistered(table, "Player", getID())) {
            List<String> values = new ArrayList<>();
            values.add("Player-" + getID());
            values.add("Coins-0");
            values.add("Kits-K" + plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.defaultKitID"));
            values.add("SelectedKit-NONE");
            plugin.getData().register(table, values);
        }
        kits = "K" + plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.defaultKitID");
        selectedKit = "NONE";
        coins = 0;
        plugin.getData().getData().addPlayer(player.getUniqueId());
    }
}
