package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;


@SuppressWarnings("unused")
public class settingsInfo {
    private FileConfiguration settingsConfiguration;
    private Location location;

    private final HashMap<GameTeam,String> roles;

    private final GuardianRFTB plugin;

    public settingsInfo(GuardianRFTB plugin) {
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
    }

    public String getRole(GameTeam currentTeam) {
        return roles.get(currentTeam);
    }

    public void setLocation(Location location) { this.location = location; }

    public FileConfiguration getSettings() { return settingsConfiguration; }
    public Location getLocation() { return location; }
}
