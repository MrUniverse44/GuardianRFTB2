package dev.mruniverse.guardianrftb.bungeegame.utils;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianrftb.bungeegame.GuardianRFTB;
import dev.mruniverse.guardianrftb.bungeegame.enums.GameTeam;
import dev.mruniverse.guardianrftb.bungeegame.enums.GuardianFiles;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;


@SuppressWarnings("unused")
public class SettingsInfo {
    private FileConfiguration settingsConfiguration;
    private Location location;

    private final HashMap<GameTeam,String> roles;

    private final GuardianRFTB plugin;

    public SettingsInfo(GuardianRFTB plugin) {
        this.plugin = plugin;
        settingsConfiguration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        String loc = settingsConfiguration.getString("settings.lobby.location");
        if(loc == null) loc = "notSet";
        location = GuardianLIB.getControl().getUtils().getLocationFromString(loc);
        roles = new HashMap<>();

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
}
