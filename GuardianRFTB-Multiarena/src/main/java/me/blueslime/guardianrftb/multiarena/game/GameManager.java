package me.blueslime.guardianrftb.multiarena.game;

import dev.mruniverse.slimelib.control.Control;
import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.SlimeFile;
import me.blueslime.guardianrftb.multiarena.enums.GameType;
import me.blueslime.guardianrftb.multiarena.enums.GuardianFiles;
import me.blueslime.guardianrftb.multiarena.enums.SaveMode;
import me.blueslime.guardianrftb.multiarena.interfaces.Game;
import me.blueslime.guardianrftb.multiarena.storage.StorageManager;
import me.blueslime.guardianrftb.multiarena.storage.players.PluginStorage;
import me.blueslime.guardianrftb.multiarena.utils.MessageHandler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameManager {
    private final ArrayList<Game> games = new ArrayList<>();

    private String gameSwitchChest = "swords";

    private GameMainMenu gameMainMenu;

    private final GuardianRFTB plugin;

    private boolean chestChange;

    private boolean chestLimit;

    public GameChests getSwitchChest() {
        return getGameChest(gameSwitchChest);
    }

    public GameManager(GuardianRFTB plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize this class with this method
     */
    public void initialize() {
        this.gameMainMenu = new GameMainMenu(plugin);

        Control settings = plugin.getConfigurationHandler(SlimeFile.SETTINGS);

        String path = "settings.game.chest-limit-settings.";

        this.chestChange     = settings.getStatus(path + "toggle", true);
        this.chestLimit      = settings.getStatus(path + "change-chest.toggle", true);

        if (!(chestLimit && chestChange)) {
            return;
        }

        this.gameSwitchChest = settings.getString(path + "change-chest.chest", "swords");

        unload();

        loadChests();
        loadGames();

        loadGameWorlds();
    }

    /**
     * With this method you refresh all your game data,
     * including arenas, chests, configurations, and more.
     */
    public void update() {
        initialize();
    }

    public void loadChests() {

        Control section = plugin.getConfigurationHandler(SlimeFile.CHESTS).getSection("chests");

        if(section == null) {
            return;
        }

        plugin.getStorageManager().getChestStorage().clear();

        for(String chest : section.getKeys(false)) {
            plugin.getStorageManager().getChestStorage().add(
                    chest,
                    new GameChests(plugin, chest)
            );
        }
    }


    public void unload() {
        StorageManager storage = plugin.getStorageManager();

        for(GameChests chests : storage.getChestStorage().getValues()) {
            chests.unload();
        }

        storage.getGameWorldStorage().clear();
        storage.getChestStorage().clear();
        storage.getMenuStorage().clear();

        games.clear();
    }


    public GameChests getGameChest(String chestName) {
        return plugin.getStorageManager().getChestStorage().get(chestName);
    }

    /**
     * Get a game using the configuration name of that game
     * @param name The name to get the game
     * @return Game
     * Returns null if the game doesn't exists!
     */
    public Game getGame(String name) {
        if (this.games.size() < 1)
            return null;
        for (Game game : this.games) {
            if (game.getConfigName().equalsIgnoreCase(name))
                return game;
        }
        return null;
    }

    public void loadGames() {

        PluginStorage<GameType, GameMenu> menuStorage = plugin.getStorageManager().getMenuStorage();

        try {
            for (GameType gameType : GameType.values()) {
                menuStorage.add(
                        gameType,
                        new GameMenu(
                                plugin,
                                gameType
                        )
                );
            }

            Control gameSettings = plugin.getConfigurationHandler(SlimeFile.GAMES);

            if (!gameSettings.contains("games")) {
                plugin.getLogs().info("The plugin doesn't have games created yet!");
                return;
            }

            for (String name : gameSettings.getContent("games", false)) {

                String path = "games." + name + ".";

                if (gameSettings.getStatus(path + "enabled")) {

                    String customName = gameSettings.getString(path + "gameName", name);

                    this.games.add(
                            new GameInfo(
                                    plugin,
                                    name,
                                    customName
                            )
                    );

                    plugin.getLogs().info("Game " + name + " loaded, using a custom name: '" + customName + "'.");

                } else {

                    plugin.getLogs().info("Game " + name + " is not enabled in the games.yml, this game was not loaded by the plugin");

                }
            }

            plugin.getLogs().info(
                    games.size() + " game(s) loaded!"
            );

        } catch (Exception exception) {
            plugin.getLogs().error("Can't load plugin games, an error occurred", exception);
        }
    }

    public void loadGameWorlds() {

        PluginStorage<World, Game> storage = plugin.getStorageManager().getGameWorldStorage();

        for (Game game : getGames()) {

            Location location = game.getRunnerSpawn();

            World gameWorld = location.getWorld();

            if (gameWorld == null) {
                plugin.getLogs().error("Error detected with game-id: " + game.getConfigName() + " (" + game.getName() + ") ");
                plugin.getLogs().error("The plugin disabled this game, reason: Runner-Spawn-Location doesn't exist");

                game.cancelTask();

                game.unload();

                games.remove(game);

                continue;
            }

            storage.add(
                    gameWorld,
                    game
            );
        }
    }


    public GameMainMenu getGameMainMenu() { return gameMainMenu; }

    public GameMenu getGameMenu(GameType gameType) {
        return plugin.getStorageManager().getMenuStorage().get(
                gameType,
                new GameMenu(
                        plugin,
                        gameType
                )
        );
    }

    public void addGame(String name, String customName) {

        if(getConfigGame(name) != null) {
            return;
        }

        if (customName == null) {
            customName = name;
        }

        GameInfo game = new GameInfo(
                plugin,
                name,
                customName
        );
        this.games.add(game);

        plugin.getLogs().debug("Game " + customName + " loaded!");

    }
    public void removeGame(String name) {

        Game game = getGame(name);

        if(game != null) {
            this.games.remove(game);

            plugin.getLogs().debug("Game " + name + " unloaded!");
            return;
        }
        plugin.getLogs().debug("Game " + name + " is not loaded or doesn't exists!");
    }

    public Map<World,Game> getWorlds() {
        return plugin.getStorageManager().getGameWorldStorage().toMap();
    }

    public ArrayList<Game> getGames() {
        return games;
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

    public void joinGame(Player player, String name) {

        if(!existGame(name)) {
            MessageHandler.sendMessage(
                    player,
                    plugin.getConfigurationHandler(SlimeFile.MESSAGES).getString(
                            "messages.admin.arenaError",
                            ""
                    ).replace("%arena_id%", name)
            );
            return;
        }

        Game game = getGame(name);

        game.join(player);
    }
    public void createGameData(String name) {

        Control file = plugin.getConfigurationHandler(SlimeFile.GAMES);

        String path = "games." + name + ".";

        file.set(path + "enabled", false);
        file.set(path + "time", 500);
        file.set(path + "disableRain", true);
        file.set(path + "max" , 10);
        file.set(path + "min", 2);
        file.set(path + "worldTime", 0);
        file.set(path + "gameType", "CLASSIC");
        file.set(path + "locations.selected-beast", "notSet");
        file.set(path + "locations.waiting", "notSet");
        file.set(path + "locations.runners", "notSet");
        file.set(path + "locations.beast", "notSet");

        file.save();

    }

    public void setWaiting(String name, Location location) {
        setLocation(name, location, "waiting");
    }

    public void setChestLimiter(String name, String chest, String limit) {
        plugin.getConfigurationHandler(SlimeFile.GAMES).set(
                "games." + name + ".chest-limits." + chest,
                limit
        );

        plugin.getConfigurationHandler(SlimeFile.GAMES).save();
    }

    public void setGameName(String name, String customName) {

        plugin.getConfigurationHandler(SlimeFile.GAMES).set(
                "games." + name + ".gameName",
                customName
        );

        plugin.getConfigurationHandler(SlimeFile.GAMES).save();
    }

    public void setSelectedBeast(String name, Location location) {
        setLocation(name, location, "selected-beast");
    }

    private void setLocation(String name, Location location, String path) {
        World world = location.getWorld();

        if (world == null) {
            plugin.getLogs().error("This location doesn't exists! The world is not loaded or doesn't exists");
            return;
        }

        plugin.getConfigurationHandler(SlimeFile.GAMES).set(
                "games." + name + ".locations." + path,
                world.getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," +
                        location.getYaw() + "," + location.getPitch()
        );

        plugin.getConfigurationHandler(SlimeFile.GAMES).save();
    }

    public void setBeast(String name, Location location) {
        setLocation(name, location, "beast");
    }

    public void setRunners(String name, Location location) {
        setLocation(name, location, "runners");
    }

    public void setMax(String gameName, int max) {
        plugin.getConfigurationHandler(SlimeFile.GAMES).set("games." + gameName + ".max", max);
        plugin.getConfigurationHandler(SlimeFile.GAMES).save();
    }

    public void setMin(String gameName, int min) {
        plugin.getConfigurationHandler(SlimeFile.GAMES).set("games." + gameName + ".min", min);
        plugin.getConfigurationHandler(SlimeFile.GAMES).save();
    }

    public void setMode(String gameName, GameType type) {
        plugin.getConfigurationHandler(SlimeFile.GAMES).set("games." + gameName + ".gameType", type.toUpper());
        plugin.getConfigurationHandler(SlimeFile.GAMES).save();
    }






}
