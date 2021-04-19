package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;


@SuppressWarnings("unused")
public class settingsInfo {
    private FileConfiguration settingsConfiguration;
    private Location location;

    private final GuardianRFTB plugin;

    public settingsInfo(GuardianRFTB plugin) {
        this.plugin = plugin;
        settingsConfiguration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        String loc = settingsConfiguration.getString("settings.lobby.location");
        if(loc == null) loc = "notSet";
        location = plugin.getLib().getUtils().getLocationFromString(loc);
    }

    public void update() {
        settingsConfiguration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        String loc = settingsConfiguration.getString("settings.lobby.location");
        if(loc == null) loc = "notSet";
        location = plugin.getLib().getUtils().getLocationFromString(loc);
    }

    public void setLocation(Location location) { this.location = location; }

    public FileConfiguration getSettings() { return settingsConfiguration; }
    public Location getLocation() { return location; }
}
