package dev.mruniverse.guardianrftb.multiarena.game;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.SaveMode;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("unused")
public class GameManager {
    private final ArrayList<Game> games = new ArrayList<>();
    private final HashMap<World, Game> gamesWorlds = new HashMap<>();

    public HashMap<String, GameChests> gameChests = new HashMap<>();
    public HashMap<GameType, GameMenu> gameMenu = new HashMap<>();

    private final GameMainMenu gameMainMenu;
    private final GuardianRFTB plugin;

    private String gameSwitchChest = "swords";

    private boolean chestLimit;

    private boolean chestChange;

    public GameChests getSwitchChest() {
        return getGameChest(gameSwitchChest);
    }

    public GameManager(GuardianRFTB plugin) {
        this.plugin = plugin;
        gameMainMenu = new GameMainMenu(plugin);
        FileConfiguration config = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        chestLimit = config.getBoolean("settings.game.chest-limit-settings.toggle",true);
        chestChange = config.getBoolean("settings.game.chest-limit-settings.change-chest.toggle",false);
        if(chestLimit && chestChange) {
            gameSwitchChest = config.getString("settings.game.chest-limit-settings.change-chest.chest", "swords");
        }
    }

    public boolean isChestLimitEnabled() {
        return chestLimit;
    }

    public boolean hasChestChangeEnabled() {
        return chestChange;
    }

    public void update() {
        FileConfiguration config = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        chestLimit = config.getBoolean("settings.game.chest-limit-settings.toggle",true);
        chestChange = config.getBoolean("settings.game.chest-limit-settings.change-chest.toggle",false);
        if(getSwitchChest() != null) {
            String switchChest = config.getString("settings.game.chest-limit-settings.change-chest.chest", "swords");
            if(!getSwitchChest().getChestID().equalsIgnoreCase(switchChest)) {
                gameSwitchChest = switchChest;
            }
        }
    }

    public void loadChests() {
        ConfigurationSection section = plugin.getStorage().getControl(GuardianFiles.CHESTS).getConfigurationSection("chests");
        if(section == null) return;
        for(String chest : section.getKeys(false)) {
            gameChests.put(chest,new GameChests(plugin,chest));
        }
    }
    public void unload() {
        for(GameChests chests : gameChests.values()) {
            chests.unload();
        }
        gamesWorlds.clear();
        gameChests.clear();
        gameMenu.clear();
        games.clear();
    }
    public GameChests getGameChest(String chestName) {
        return gameChests.get(chestName);
    }
    public Game getGame(String gameName) {
        if (this.games.size() < 1)
            return null;
        for (Game game : this.games) {
            if (game.getConfigName().equalsIgnoreCase(gameName))
                return game;
        }
        return null;
    }

    public void loadGames() {
        try {
            for(GameType currentGameType : GameType.values()) {
                gameMenu.put(currentGameType,new GameMenu(plugin,currentGameType));
            }
            if(plugin.getStorage().getControl(GuardianFiles.GAMES).contains("games")) {
                for (String gameName : Objects.requireNonNull(plugin.getStorage().getControl(GuardianFiles.GAMES).getConfigurationSection("games")).getKeys(false)) {
                    if(plugin.getStorage().getControl(GuardianFiles.GAMES).getBoolean("games." + gameName + ".enabled")) {
                        String mapName = plugin.getStorage().getControl(GuardianFiles.GAMES).getString("games." + gameName + ".gameName");
                        if(mapName == null) {
                            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".gameName",gameName);
                            mapName = gameName;
                            plugin.getStorage().save(SaveMode.GAMES_FILES);
                        }
                        Game game = new GameInfo(plugin, gameName, mapName);
                        this.games.add(game);
                        plugin.getLogs().debug("Game " + gameName + " loaded!");
                    } else {
                        plugin.getLogs().debug("Game " + gameName + " is not enabled in games.yml, this game wasn't loaded.");
                    }
                }
                plugin.getLogs().info(this.games.size() + " game(s) loaded!");
            } else {
                plugin.getLogs().info("You don't have games created yet.");
            }
            loadGameWorlds();
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't load games plugin games :(");
            plugin.getLogs().error(throwable);
        }
    }
    public void loadGameWorlds() {
        for(Game game : getGames()) {
            gamesWorlds.put(game.getRunnerSpawn().getWorld(),game);
        }
    }
    public GameMainMenu getGameMainMenu() { return gameMainMenu; }
    public GameMenu getGameMenu(GameType gameType) {
        if(!gameMenu.containsKey(gameType)) {
            gameMenu.put(gameType,new GameMenu(plugin,gameType));
        }
        return gameMenu.get(gameType);
    }
    public void addGame(String configName,String gameName) {
        if(getConfigGame(configName) != null) {
            return;
        }
        if(gameName == null) {
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + configName + ".gameName",configName);
            gameName = configName;
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }
        GameInfo game = new GameInfo(plugin,configName,gameName);
        this.games.add(game);
        plugin.getLogs().debug("Game " + gameName + " loaded!");
    }
    public void delGame(String gameName) {
        if(getGame(gameName) != null) {
            this.games.remove(getGame(gameName));
        }
        plugin.getLogs().debug("Game " + gameName + " unloaded!");
    }
    public ArrayList<Game> getGames() {
        return games;
    }
    public HashMap<World,Game> getGameWorlds() { return gamesWorlds; }

    public Game getGame(Player player) {
        return plugin.getUser(player.getUniqueId()).getGame();
    }

    public Game getConfigGame(String name) {
        if (this.games.size() < 1)
            return null;
        for (Game game : this.games) {
            if (game.getConfigName().equalsIgnoreCase(name))
                return game;
        }
        return null;
    }

    public boolean existGame(String name) {
        return getConfigGame(name) != null;
    }

    public void joinGame(Player player,String gameName) {
        if(!existGame(gameName)) {
            plugin.getLib().getUtils().sendMessage(player, Objects.requireNonNull(plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.admin.arenaError")).replace("%arena_id%",gameName));
            return;
        }
        Game game = getGame(gameName);
        game.join(player);
    }
    public void createGameFiles(String gameName) {
        FileConfiguration gameFiles = plugin.getStorage().getControl(GuardianFiles.GAMES);
        gameFiles.set("games." + gameName + ".enabled", false);
        gameFiles.set("games." + gameName + ".time", 500);
        gameFiles.set("games." + gameName + ".gameName", gameName);
        gameFiles.set("games." + gameName + ".disableRain", true);
        gameFiles.set("games." + gameName + ".max", 10);
        gameFiles.set("games." + gameName + ".min", 2);
        gameFiles.set("games." + gameName + ".worldTime", 0);
        gameFiles.set("games." + gameName + ".gameType","CLASSIC");
        gameFiles.set("games." + gameName + ".locations.waiting", "notSet");
        gameFiles.set("games." + gameName + ".locations.selected-beast", "notSet");
        gameFiles.set("games." + gameName + ".locations.beast", "notSet");
        gameFiles.set("games." + gameName + ".locations.runners", "notSet");
        gameFiles.set("games." + gameName + ".signs", new ArrayList<>());
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }
    public void setWaiting(String gameName, Location location) {
        try {
            String gameLoc = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".locations.waiting", gameLoc);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set waiting lobby for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setChestLimiter(String game,String chest,String limit) {
        try {
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + game + ".chest-limits." + chest, limit);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set chest limit for game: " + game);
            plugin.getLogs().error(throwable);
        }
    }
    public void setGameName(String configName, String gameName) {
        try {
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + configName + ".gameName", gameName);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set game name for game: " + configName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setSelectedBeast(String gameName, Location location) {
        try {
            String gameLoc = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".locations.selected-beast", gameLoc);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set selected beast location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setBeast(String gameName, Location location) {
        try {
            String gameLoc = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".locations.beast", gameLoc);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set beast spawn location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setRunners(String gameName, Location location) {
        try {
            String gameLoc = Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
            plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".locations.runners", gameLoc);
            plugin.getStorage().save(SaveMode.GAMES_FILES);
        }catch (Throwable throwable) {
            plugin.getLogs().error("Can't set runners spawn location for game: " + gameName);
            plugin.getLogs().error(throwable);
        }
    }
    public void setMax(String gameName,Integer max) {
        plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".max", max);
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }
    public void setMin(String gameName,Integer min) {
        plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".min", min);
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }
    public void setMode(String gameName,GameType type) {
        plugin.getStorage().getControl(GuardianFiles.GAMES).set("games." + gameName + ".gameType", type.toString().toUpperCase());
        plugin.getStorage().save(SaveMode.GAMES_FILES);
    }






}
