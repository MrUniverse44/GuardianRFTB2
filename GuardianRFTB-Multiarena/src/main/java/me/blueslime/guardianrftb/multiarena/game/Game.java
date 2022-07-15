package me.blueslime.guardianrftb.multiarena.game;

import dev.mruniverse.slimelib.control.Control;
import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.SlimeFile;
import me.blueslime.guardianrftb.multiarena.enums.GameStatus;
import me.blueslime.guardianrftb.multiarena.enums.GameTeam;
import me.blueslime.guardianrftb.multiarena.enums.GameType;
import me.blueslime.guardianrftb.multiarena.player.GamePlayer;
import me.blueslime.guardianrftb.multiarena.storage.players.PluginStorage;
import me.blueslime.guardianrftb.multiarena.utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class Game {

    private final PluginStorage<String, List<Location>> chestLocations = PluginStorage.initAsHash();

    private final ArrayList<Player> spectators = new ArrayList<>();

    private final ArrayList<Location> signList = new ArrayList<>();

    private final ArrayList<Player> runnerList = new ArrayList<>();

    private final ArrayList<String> chestTypes = new ArrayList<>();

    private final ArrayList<Player> beastList = new ArrayList<>();

    private final ArrayList<Location> signs = new ArrayList<>();

    private static boolean NO_GAME_RULE_SUPPORT = false;


    private GameRunnable runnable = null;

    private final GuardianRFTB plugin;

    private Location selection;

    private final String name;

    private GameStatus status;

    private String customName;

    private GameType gameType;

    private Location waiting;

    private Location runners;

    private Location beasts;

    private int worldTime;

    private int maxTime;

    private int min;

    private int max;

    private int slot = -1;

    public Game(GuardianRFTB plugin, String name) {
        this.customName = name;
        this.plugin = plugin;
        this.name = name;

        load();
    }

    public Game(GuardianRFTB plugin, String name, String customName) {
        this.customName = customName;
        this.plugin = plugin;
        this.name = name;

        load();
    }

    public void load() {

        Control gameSettings = plugin.getConfigurationHandler(SlimeFile.GAMES);

        String path = "games." + name + ".";

        this.worldTime = gameSettings.getInt(path + "worldTime");
        this.maxTime = gameSettings.getInt(path + "time");
        this.max = gameSettings.getInt(path + "max");
        this.min = gameSettings.getInt(path + "min");

        if (min == 1) {
            this.min = 2;

            plugin.getLogs().error("Min players of a map can't be 1, changed to 2, game-id: " + name + " (" + customName + ")");
        }

        this.gameType = GameType.fromText(
                gameSettings.getString(
                        path + "gameType",
                        "CLASSIC"
                )
        );

        this.selection = LocationUtils.fromString(
                plugin.getLogs(),
                gameSettings.getString(path + "locations.selected-beast", "notSet")
        );

        this.beasts = LocationUtils.fromString(
                plugin.getLogs(),
                gameSettings.getString(path + "locations.beast", "notSet")
        );

        this.waiting = LocationUtils.fromString(
                plugin.getLogs(),
                gameSettings.getString(path + "locations.waiting", "notSet")
        );

        this.runners = LocationUtils.fromString(
                plugin.getLogs(),
                gameSettings.getString(path + "locations.runners", "notSet")
        );

        this.status = GameStatus.PREPARING;

        if (beasts == null || runners == null || waiting == null || selection == null) {
            this.status = GameStatus.ERROR;
        }

        loadSigns();

        loadChests();

        this.status = GameStatus.WAITING;

        updateSigns();

        if(runners != null) {

            World gameWorld = runners.getWorld();

            if(gameWorld != null) {
                gameWorld.setTime(worldTime);
                gameWorld.setDifficulty(Difficulty.NORMAL);
                gameWorld.setSpawnFlags(false, false);

                if (!NO_GAME_RULE_SUPPORT) {
                    try {
                        gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    } catch (Throwable ignored) {
                        NO_GAME_RULE_SUPPORT = true;
                    }
                }
            }
        }
        onInitialize();
    }

    public void onInitialize() {
        //TODO: This method can be used by class extending this class,
        //TODO: This method will be empty, but if another class use this method
        //TODO: This method will be called on Game Setup Finish
    }

    private void loadSigns() {
        signList.clear();

        Control gameFile = plugin.getConfigurationHandler(SlimeFile.GAMES);

        for(String sign : gameFile.getStringList("games." + name + ".signs")) {

            Location location = LocationUtils.fromString(plugin.getLogs(), sign);

            if(location != null) {

                if(location.getBlock().getState() instanceof Sign) {
                    signList.add(location);
                }

            }
        }
    }

    private void loadChests() {
        Control gameSettings = plugin.getConfigurationHandler(SlimeFile.GAMES);

        chestLocations.clear();
        chestTypes.clear();

        if (gameSettings.get("games." + name + ".chests") != null) {
            for (String name : gameSettings.getStringList("games." + name + ".chests")) {

                chestTypes.add(name);

                loadChest(name);

            }
        }
    }

    private void loadChest(String name) {
        Control gameSettings = plugin.getConfigurationHandler(SlimeFile.GAMES);

        if (gameSettings.get("games." + this.name + ".chests-locations." + name) != null) {
            ArrayList<Location> locationList = new ArrayList<>();

            for (String location : gameSettings.getStringList("games." + this.name + ".chests-locations." + name)) {
                locationList.add(
                        LocationUtils.fromString(
                                plugin.getLogs(),
                                location
                        )
                );
            }
            chestLocations.add(name, locationList);
        }
    }

    public void updateSigns() {
        Control settings = plugin.getConfigurationHandler(SlimeFile.SETTINGS);

        String line1 = settings.getString("settings.signs.line1", "&l%arena%");
        String line2 = settings.getString("settings.signs.line2", "%status%");
        String line3 = settings.getString("settings.signs.line3", "%online%/%max%");
        String line4 = settings.getString("settings.signs.line4", "&nClick to join");

        GameMenu menu = plugin.getGameManager().getGameMenu(gameType);

        //TODO: if(menu != null) {
        //TODO:     menu.updateSlot(slot, this);
        //TODO: }


        for (Location sign : signList) {

            World world = sign.getWorld();

            if (world == null) {
                continue;
            }

            if (!(sign.getBlock().getState() instanceof Sign)) {
                continue;
            }

            if (!world.getChunkAt(sign).isLoaded()) {
                world.loadChunk(
                        world.getChunkAt(sign)
                );

                if (!world.getChunkAt(sign).isLoaded()) {
                    continue;
                }
            }

            Sign gameSign = (Sign) sign.getBlock().getState();

            gameSign.setLine(0, replaceVariables(line1));
            gameSign.setLine(1, replaceVariables(line2));
            gameSign.setLine(2, replaceVariables(line3));
            gameSign.setLine(3, replaceVariables(line4));
            gameSign.update();
        }
    }

    public void join(Player player) {
        join(
                plugin.getStorageManager().getPlayerStorage().get(
                        player.getUniqueId(),
                        new GamePlayer(
                                player
                        )
                )
        );
    }

    public abstract void join(GamePlayer player);

    public void quit(Player player) {
        quit(
                plugin.getStorageManager().getPlayerStorage().get(
                        player.getUniqueId(),
                        new GamePlayer(
                                player
                        )
                )
        );
    }

    public abstract void quit(GamePlayer player);

    public abstract void win(GameTeam team);

    public abstract void checkPlayers();

    public abstract int getNeedPlayers();

    public void restart() {
        this.runnerList.clear();
        this.spectators.clear();
        this.beastList.clear();
        this.status = GameStatus.WAITING;

        onRestart();
    }

    public void onRestart() {
        //TODO: This method can be used by class extending this class,
        //TODO: This method will be empty, but if another class use this method
        //TODO: This method will be called on Game Restart Event
    }

    public void setBeastSpawn(Location beasts) {
        this.beasts = beasts;
    }

    public void setRunnerSpawn(Location runners) {
        this.runners = runners;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public void setSelection(Location selection) {
        this.selection = selection;
    }

    public void setWaiting(Location waiting) {
        this.waiting = waiting;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setWorldTime(int worldTime) {
        this.worldTime = worldTime;
    }

    public void setRunnable(GameRunnable runnable) {
        this.runnable = runnable;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getMin() {
        return min;
    }

    public int getSlot() {
        return slot;
    }

    public int getWorldTime() {
        return worldTime;
    }

    public String getName() {
        return name;
    }

    public String getCustomName() {
        return customName;
    }

    public GameType getGameType() {
        return gameType;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameRunnable getRunnable() {
        return runnable;
    }

    public GuardianRFTB getPlugin() {
        return plugin;
    }



    public Location getWaiting() {
        return waiting;
    }

    public Location getSelection() {
        return selection;
    }

    public Location getSpawnRunners() {
        return runners;
    }

    public Location getSpawnBeasts() {
        return beasts;
    }

    public ArrayList<Location> getSignList() {
        return signList;
    }

    public ArrayList<Player> getBeastList() {
        return beastList;
    }

    public ArrayList<Player> getRunnerList() {
        return runnerList;
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }

    public PluginStorage<String, List<Location>> getChestLocations() {
        return chestLocations;
    }

    public ArrayList<Player> getPlayerList() {
        ArrayList<Player> playerList = new ArrayList<>();

        playerList.addAll(getRunnerList());
        playerList.addAll(getBeastList());
        playerList.addAll(getSpectators());

        return playerList;
    }

    public String replaceVariables(String message) {
        int online = getPlayerList().size();

        return message.replace("%on%", online + "")
                .replace("%online%", online + "")
                .replace("%min%", min + "")
                .replace("%max%", max + "")
                .replace("%name%", name)
                .replace("%configName%", name)
                .replace("%gameName%", customName)
                .replace("%customName%", customName)
                .replace("%runners%", runnerList.size() + "")
                .replace("%beasts%", beastList.size() + "");
    }

}
