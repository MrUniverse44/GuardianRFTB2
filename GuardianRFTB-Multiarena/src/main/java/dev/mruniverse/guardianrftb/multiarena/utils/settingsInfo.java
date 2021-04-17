package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import org.bukkit.configuration.file.FileConfiguration;


@SuppressWarnings("unused")
public class settingsInfo {
    private FileConfiguration settingsConfiguration;

    private final GuardianRFTB plugin;

    public settingsInfo(GuardianRFTB plugin) {
        this.plugin = plugin;
        settingsConfiguration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
    }

    public void update() {
        settingsConfiguration = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
    }

    public FileConfiguration getSettings() { return settingsConfiguration; }
}
