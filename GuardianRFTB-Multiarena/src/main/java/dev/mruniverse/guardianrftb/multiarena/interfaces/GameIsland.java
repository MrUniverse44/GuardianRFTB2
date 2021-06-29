package dev.mruniverse.guardianrftb.multiarena.interfaces;

import dev.mruniverse.guardianrftb.multiarena.enums.GameStatus;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public interface GameIsland {

    /**
     * Setup the game
     */
    void setup();

    /**
     * Load game status
     */
    void loadStatus();

    /**
     * Load game-chests
     */
    void loadChests();

    /**
     * Load game-chests types
     * @param chestName - Example: armor
     */
    void loadChestType(String chestName);

    /**
     * Load game-signs
     */
    void loadSigns();

    /**
     * Set invincible damage in game
     * @param status Boolean
     */
    void setInvincible(boolean status);

    /**
     * Add a player to the game
     * @param player - Player to add
     */
    void join(Player player);

    /**
     * Remove a player from the game
     * @param player - Player to leave
     */
    void leave(Player player);

    /**
     * Leave without sending to the lobby (this is for the playerAgain Button)
     * @param player - Player to leave
     */
    void leaveWithoutSending(Player player);

    void checkPlayers();

    void spawnBeastBridge();

    void spawnRunnerBridge();

    void changeDetective();

    boolean isDetective(Player player);

    void cancelTask();

    void spawnBeastBridgeCount();

    void spawnRunnerBridgeCount();

    void startCount();

    void beastCount();

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

    Random getRandom();

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
