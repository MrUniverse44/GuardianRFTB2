package me.blueslime.guardianrftb.multiarena.interfaces;

import me.blueslime.guardianrftb.multiarena.enums.GameStatus;
import me.blueslime.guardianrftb.multiarena.enums.GameTeam;
import me.blueslime.guardianrftb.multiarena.enums.GameType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public abstract class Game {

    private static final Random RANDOM = new Random();

    public Random getRandom() {
        return RANDOM;
    }

    public abstract void setup();

    void loadStatus();

    void loadChests();

    void loadChestType(String chestName);

    void loadSigns();

    void setInvincible(boolean status);

    void join(Player player);

    void leave(Player player);

    void leaveWithoutSending(Player player);

    void checkPlayers();

    void cancelTask();

    void startCount();

    void beastCount();

    void unload();

    void playCount();

    void setWinner(GameTeam gameTeam);

    void winRunners();

    void winBeasts();

    void restart();

    void deathBeast(Player beast);

    void deathRunner(Player player);

    void deathKiller(Player player);

    void updateSignsBlocks();

    void updateSigns();

    void giveBeastInv(Player beast);

    int getNeedPlayers();

    void setName(String name);

    void setGameType(GameType gameType);

    void setGameStatus(GameStatus gameStatus);

    void setLastTimer(int time);

    void setMenuSlot(int slot);

    void addChestLimit(String chest,Player player);

    void addDeadPlayerChest(String chest);

    void setChestLimiter(String chest,String limit);

    boolean isChestLimitParsed(String chestName);

    boolean isChestLimited(String chestName);

    boolean isChestOf(String chestName,Player player);

    String getConfigName();
    String getName();

    ArrayList<Player> getPlayers();
    ArrayList<Player> getRunners();
    ArrayList<Player> getBeasts();
    ArrayList<Player> getKillers();
    ArrayList<Player> getSpectators();
    ArrayList<Location> getSigns();
    ArrayList<String> getChestTypes();

    GameType getType();
    GameStatus getStatus();

    Location getWaiting();
    Location getSelecting();
    Location getBeastSpawn();
    Location getRunnerSpawn();
    Location getKillerSpawn();

    HashMap<String, List<Location>> getChestLocations();

    boolean isInvincible();

    void firework(Player player,boolean firework);

    void setDoubleCountPrevent(boolean toggle);

    boolean timing(int i);

    int getMax();
    int getMin();
    int getLastTimer();
    int getGameMaxTime();
    int getWorldTime();



}
