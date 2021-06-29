package dev.mruniverse.guardianrftb.multiarena.game;

import dev.mruniverse.guardianrftb.multiarena.enums.GameStatus;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.interfaces.GameIsland;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class GameIslands implements GameIsland {

    /**
     * Setup the game
     */
    @Override
    public void setup() {

    }

    /**
     * Load game status
     */
    @Override
    public void loadStatus() {

    }

    /**
     * Load game-chests
     */
    @Override
    public void loadChests() {

    }

    /**
     * Load game-chests types
     *
     * @param chestName - Example: armor
     */
    @Override
    public void loadChestType(String chestName) {

    }

    /**
     * Load game-signs
     */
    @Override
    public void loadSigns() {

    }

    /**
     * Set invincible damage in game
     *
     * @param status Boolean
     */
    @Override
    public void setInvincible(boolean status) {

    }

    /**
     * Add a player to the game
     *
     * @param player - Player to add
     */
    @Override
    public void join(Player player) {

    }

    /**
     * Remove a player from the game
     *
     * @param player - Player to leave
     */
    @Override
    public void leave(Player player) {

    }

    /**
     * Leave without sending to the lobby (this is for the playerAgain Button)
     *
     * @param player - Player to leave
     */
    @Override
    public void leaveWithoutSending(Player player) {

    }

    @Override
    public void checkPlayers() {

    }

    @Override
    public void spawnBeastBridge() {

    }

    @Override
    public void spawnRunnerBridge() {

    }

    @Override
    public void changeDetective() {

    }

    @Override
    public boolean isDetective(Player player) {
        return false;
    }

    @Override
    public void cancelTask() {

    }

    @Override
    public void spawnBeastBridgeCount() {

    }

    @Override
    public void spawnRunnerBridgeCount() {

    }

    @Override
    public void startCount() {

    }

    @Override
    public void beastCount() {

    }

    @Override
    public void playCount() {

    }

    @Override
    public void setWinner(GameTeam gameTeam) {

    }

    @Override
    public void winRunners() {

    }

    @Override
    public void winBeasts() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void deathBeast(Player beast) {

    }

    @Override
    public void deathRunner(Player player) {

    }

    @Override
    public void deathKiller(Player player) {

    }

    @Override
    public void updateSignsBlocks() {

    }

    @Override
    public void updateSigns() {

    }

    @Override
    public void giveBeastInv(Player beast) {

    }

    @Override
    public int getNeedPlayers() {
        return 0;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setGameType(GameType gameType) {

    }

    @Override
    public void setGameStatus(GameStatus gameStatus) {

    }

    @Override
    public void setLastTimer(int time) {

    }

    @Override
    public void setMenuSlot(int slot) {

    }

    @Override
    public String getConfigName() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ArrayList<Player> getPlayers() {
        return null;
    }

    @Override
    public ArrayList<Player> getRunners() {
        return null;
    }

    @Override
    public ArrayList<Player> getBeasts() {
        return null;
    }

    @Override
    public ArrayList<Player> getKillers() {
        return null;
    }

    @Override
    public ArrayList<Player> getSpectators() {
        return null;
    }

    @Override
    public ArrayList<Location> getSigns() {
        return null;
    }

    @Override
    public ArrayList<String> getChestTypes() {
        return null;
    }

    @Override
    public GameType getType() {
        return null;
    }

    @Override
    public GameStatus getStatus() {
        return null;
    }

    @Override
    public Location getWaiting() {
        return null;
    }

    @Override
    public Location getSelecting() {
        return null;
    }

    @Override
    public Location getBeastSpawn() {
        return null;
    }

    @Override
    public Location getRunnerSpawn() {
        return null;
    }

    @Override
    public Location getKillerSpawn() {
        return null;
    }

    @Override
    public Random getRandom() {
        return null;
    }

    @Override
    public HashMap<String, List<Location>> getChestLocations() {
        return null;
    }

    @Override
    public boolean isInvincible() {
        return false;
    }

    @Override
    public void firework(Player player, boolean firework) {

    }

    @Override
    public void setDoubleCountPrevent(boolean toggle) {

    }

    @Override
    public boolean timing(int i) {
        return false;
    }

    @Override
    public int getMax() {
        return 0;
    }

    @Override
    public int getMin() {
        return 0;
    }

    @Override
    public int getLastTimer() {
        return 0;
    }

    @Override
    public int getGameMaxTime() {
        return 0;
    }

    @Override
    public int getWorldTime() {
        return 0;
    }
}
