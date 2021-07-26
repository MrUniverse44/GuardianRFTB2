package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.StringType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;


@SuppressWarnings("unused")
public class settingsInfo {
    private FileConfiguration settingsConfiguration;
    private Location location;

    private final HashMap<GameTeam,String> roles;

    private final GuardianRFTB plugin;

    private boolean secondSpectator;

    private GameMode gameMode;

    public settingsInfo(GuardianRFTB plugin) {
        this.plugin = plugin;
        settingsConfiguration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        location = GuardianLIB.getControl().getUtils().getLocationFromString(getString(settingsConfiguration.getString("settings.lobby.location"),"notSet"));
        secondSpectator = settingsConfiguration.getBoolean("settings.game.use-second-spectator-system",false);
        roles = new HashMap<>();

        try {
            this.gameMode = GameMode.valueOf(
                    StringType.fromText(getString(settingsConfiguration.getString("settings.lobby.join.gamemode"),"ADVENTURE"))
                            .setType(StringType.UPPER_CASE)
                            .getText()
            );
        }catch (Throwable ignored) {
            this.gameMode = GameMode.ADVENTURE;
        }
        roles.put(GameTeam.KILLER,getString(settingsConfiguration.getString("settings.game.roles.killer"),"Killer"));
        roles.put(GameTeam.BEASTS,getString(settingsConfiguration.getString("settings.game.roles.beast"),"Beast"));
        roles.put(GameTeam.RUNNERS,getString(settingsConfiguration.getString("settings.game.roles.runner"),"Runner"));
        roles.put(GameTeam.BEASTS2,getString(settingsConfiguration.getString("settings.game.roles.beasts"),"Beasts"));
        roles.put(GameTeam.RUNNERS2,getString(settingsConfiguration.getString("settings.game.roles.runners"),"Runners"));
        roles.put(GameTeam.KILLERS2,getString(settingsConfiguration.getString("settings.game.roles.killers"),"Killers"));
    }

    public void unload() {
        roles.clear();
    }

    private String getString(String value,String result) {
        if(value == null) value = result;
        return value;
    }

    public GameMode getGameMode() {
        if(gameMode == null) gameMode = GameMode.ADVENTURE;
        return gameMode;
    }


    public void update() {
        settingsConfiguration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        try {
            this.gameMode = GameMode.valueOf(
                    StringType.fromText(getString(settingsConfiguration.getString("settings.lobby.join.gamemode"),"ADVENTURE"))
                    .setType(StringType.UPPER_CASE)
                    .getText()
            );
        }catch (Throwable ignored) {
            this.gameMode = GameMode.ADVENTURE;
        }
        secondSpectator = settingsConfiguration.getBoolean("settings.game.use-second-spectator-system",false);
        roles.put(GameTeam.KILLER,getString(settingsConfiguration.getString("settings.game.roles.killer"),"Killer"));
        roles.put(GameTeam.BEASTS,getString(settingsConfiguration.getString("settings.game.roles.beast"),"Beast"));
        roles.put(GameTeam.RUNNERS,getString(settingsConfiguration.getString("settings.game.roles.runner"),"Runner"));
        roles.put(GameTeam.BEASTS2,getString(settingsConfiguration.getString("settings.game.roles.beasts"),"Beasts"));
        roles.put(GameTeam.RUNNERS2,getString(settingsConfiguration.getString("settings.game.roles.runners"),"Runners"));
        roles.put(GameTeam.KILLERS2,getString(settingsConfiguration.getString("settings.game.roles.killers"),"Killers"));
    }

    public boolean isSecondSpectator() {
        return secondSpectator;
    }

    public String getRole(GameTeam currentTeam) {
        return roles.get(currentTeam);
    }

    public void setLocation(Location location) { this.location = location; }

    public FileConfiguration getSettings() { return settingsConfiguration; }
    public Location getLocation() { return location; }

}
