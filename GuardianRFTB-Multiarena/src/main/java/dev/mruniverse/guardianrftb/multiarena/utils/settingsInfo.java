package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import org.bukkit.ChatColor;
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

    private GameMode gameMode;

    public settingsInfo(GuardianRFTB plugin) {
        this.plugin = plugin;
        settingsConfiguration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        location = GuardianLIB.getControl().getUtils().getLocationFromString(getString(settingsConfiguration.getString("settings.lobby.location"),"notSet"));
        roles = new HashMap<>();

        try {
            this.gameMode = GameMode.valueOf(getString(settingsConfiguration.getString("settings.lobby.join.gamemode"),"ADVENTURE",StringType.UPPER_CASE));
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

    private String getString(String value,String result) {
        if(value == null) value = result;
        return value;
    }

    public GameMode getGameMode() {
        if(gameMode == null) gameMode = GameMode.ADVENTURE;
        return gameMode;
    }

    public String getString(String value,String result,StringType type) {
        if(value == null) value = result;
        if(type == StringType.UPPER_CASE) return value.toUpperCase();
        if(type == StringType.LOWER_CASE) return value.toLowerCase();
        if(type == StringType.COLORED) return ChatColor.translateAlternateColorCodes('&',value);
        return value;
    }

    public void update() {
        settingsConfiguration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        String loc = settingsConfiguration.getString("settings.lobby.location");
        if(loc == null) loc = "notSet";
        location = plugin.getLib().getUtils().getLocationFromString(loc);

        String role = settingsConfiguration.getString("settings.game.roles.killer");
        if(role == null) role = "Killer";
        roles.put(GameTeam.KILLER,role);
        role = settingsConfiguration.getString("settings.game.roles.beast");
        if(role == null) role = "Beast";
        roles.put(GameTeam.BEASTS,role);
        role = settingsConfiguration.getString("settings.game.roles.runner");
        if(role == null) role = "Runner";
        roles.put(GameTeam.RUNNERS,role);

        role = settingsConfiguration.getString("settings.game.roles.beasts");
        if(role == null) role = "Beasts";
        roles.put(GameTeam.BEASTS2,role);
        role = settingsConfiguration.getString("settings.game.roles.runners");
        if(role == null) role = "Runners";
        roles.put(GameTeam.RUNNERS2,role);
        role = settingsConfiguration.getString("settings.game.roles.killers");
        if(role == null) role = "Killers";
        roles.put(GameTeam.KILLERS2,role);
    }

    public String getRole(GameTeam currentTeam) {
        return roles.get(currentTeam);
    }

    public void setLocation(Location location) { this.location = location; }

    public FileConfiguration getSettings() { return settingsConfiguration; }
    public Location getLocation() { return location; }

    private enum StringType {
        UPPER_CASE,
        LOWER_CASE,
        COLORED
    }
}
