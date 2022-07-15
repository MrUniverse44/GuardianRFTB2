package me.blueslime.guardianrftb.multiarena.game;

import dev.mruniverse.slimelib.control.Control;
import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.SlimeFile;
import me.blueslime.guardianrftb.multiarena.enums.GameStatus;
import me.blueslime.guardianrftb.multiarena.enums.GameType;
import me.blueslime.guardianrftb.multiarena.utils.LocationUtils;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class Game {

    private static boolean NO_GAME_RULE_SUPPORT = false;

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

        loadStatus();

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
    }

    private void loadSigns() {
        //TODO: SIGN LOADER
    }

    private void loadChests() {
        //TODO: CHEST LOADING
    }

    private void loadStatus() {
        //TODO: STATUS CHANGE TO WAITING
    }

    public void updateSigns() {
        //TODO: SIGN UPDATE
    }

    public void setWaiting(Location waiting) {
        this.waiting = waiting;
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

}
