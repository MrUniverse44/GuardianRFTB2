package dev.mruniverse.guardianrftb.multiarena.storage;

import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    private PlayerStatus playerStatus;
    private GuardianBoard guardianBoard;
    private final Player player;
    private boolean pointStatus;
    private Location lastCheckpoint;
    private GameInfo currentGame;
    private int leaveDelay;
    private int kills = 0;
    private int wins = 0;
    private int coins = 0;
    private int deaths = 0;

    public PlayerManager(Player p) {
        player = p;
        leaveDelay = 0;
        guardianBoard = GuardianBoard.LOBBY;
        playerStatus = PlayerStatus.IN_LOBBY;
        pointStatus = false;
        lastCheckpoint = null;
        currentGame = null;
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
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setSelectedKit(String kitID) {
        //
    }

    public String getSelectedKit() {
        return "NONE";
    }

    public void addKit(String kitID) {
        //
    }

    public List<String> getKits() {
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
        registerDefault();
        setDeaths(getDeaths() + 1);
    }


    public void registerDefault() {
        //
    }
}
