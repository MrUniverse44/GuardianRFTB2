package dev.mruniverse.guardianrftb.multiarena.enums;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import org.bukkit.configuration.file.FileConfiguration;

@SuppressWarnings("unused")
public enum GameStatus {
    PREPARING,
    WAITING,
    SELECTING,
    STARTING,
    PLAYING,
    IN_GAME,
    ERROR,
    RESTARTING;

    public String getBlock() {
        FileConfiguration settings = GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS);
        switch (this) {
            case ERROR:
                return "&cError";
            case WAITING:
                return settings.getString("settings.status.blocks.waiting");
            case IN_GAME:
                return settings.getString("settings.status.blocks.InGame");
            case SELECTING:
            case STARTING:
                return settings.getString("settings.status.blocks.starting");
            case PREPARING:
                return settings.getString("settings.status.blocks.preparing");
            case PLAYING:
                return settings.getString("settings.status.blocks.playing");
            case RESTARTING:
            default:
                return settings.getString("settings.status.blocks.ending");
        }
    }

    public String getStatus() {
        FileConfiguration settings = GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS);
        switch (this) {
            case ERROR:
                return "&cError";
            case WAITING:
                return settings.getString("settings.status.color.waiting");
            case IN_GAME:
                return settings.getString("settings.status.color.InGame");
            case SELECTING:
            case STARTING:
                return settings.getString("settings.status.color.starting");
            case PREPARING:
                return settings.getString("settings.status.color.preparing");
            case PLAYING:
                return settings.getString("settings.status.color.playing");
            case RESTARTING:
            default:
                return settings.getString("settings.status.color.ending");
        }
    }

    public String getStatusName() {
        FileConfiguration settings = GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS);
        switch (this) {
            case ERROR:
                return "Setup-Error";
            case WAITING:
                return settings.getString("settings.status.names.waiting");
            case IN_GAME:
                return settings.getString("settings.status.names.InGame");
            case SELECTING:
            case STARTING:
                return settings.getString("settings.status.names.starting");
            case PREPARING:
                return settings.getString("settings.status.names.preparing");
            case PLAYING:
                return settings.getString("settings.status.names.playing");
            case RESTARTING:
            default:
                return settings.getString("settings.status.names.ending");
        }
    }
}
