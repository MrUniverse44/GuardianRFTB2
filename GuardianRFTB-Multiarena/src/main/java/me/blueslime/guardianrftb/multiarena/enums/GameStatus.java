package me.blueslime.guardianrftb.multiarena.enums;

import dev.mruniverse.slimelib.control.Control;
import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.SlimeFile;
import org.apache.commons.lang.StringUtils;

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
        Control settings = GuardianRFTB.getFileStorage().getControl(SlimeFile.SETTINGS);

        switch (this) {
            case ERROR:
                return "BEDROCK";
            case WAITING:
                return settings.getString("settings.status.blocks.waiting", "STAINED_GLASS:5");
            case IN_GAME:
                return settings.getString("settings.status.blocks.InGame", "STAINED_GLASS:14");
            case SELECTING:
            case STARTING:
                return settings.getString("settings.status.blocks.starting", "STAINED_GLASS:4");
            case PREPARING:
                return settings.getString("settings.status.blocks.preparing", "STAINED_GLASS:11");
            case PLAYING:
                return settings.getString("settings.status.blocks.playing", "STAINED_GLASS:14");
            case RESTARTING:
            default:
                return settings.getString("settings.status.blocks.ending", "STAINED_GLASS:0");
        }
    }

    public String getStatus() {
        Control settings = GuardianRFTB.getFileStorage().getControl(SlimeFile.SETTINGS);

        switch (this) {
            case ERROR:
                return "&cError";
            case WAITING:
                return settings.getString("settings.status.color.waiting", "&aWaiting");
            case IN_GAME:
                return settings.getString("settings.status.color.InGame", "&4InGame");
            case SELECTING:
            case STARTING:
                return settings.getString("settings.status.color.starting", "&eStarting");
            case PREPARING:
                return settings.getString("settings.status.color.preparing", "&5Config");
            case PLAYING:
                return settings.getString("settings.status.color.playing", "&cPlaying");
            case RESTARTING:
            default:
                return settings.getString("settings.status.color.ending", "&9Restarting");
        }
    }

    public String getStatusName() {
        Control settings = GuardianRFTB.getFileStorage().getControl(SlimeFile.SETTINGS);

        switch (this) {
            case ERROR:
                return "Setup-Error";
            case WAITING:
                return settings.getString("settings.status.names.waiting", toCapitalized());
            case IN_GAME:
                return settings.getString("settings.status.names.InGame", toCapitalized());
            case SELECTING:
            case STARTING:
                return settings.getString("settings.status.names.starting", toCapitalized());
            case PREPARING:
                return settings.getString("settings.status.names.preparing", toCapitalized());
            case PLAYING:
                return settings.getString("settings.status.names.playing", toCapitalized());
            case RESTARTING:
            default:
                return settings.getString("settings.status.names.ending", toCapitalized());
        }
    }

    public String toCapitalized() {
        return StringUtils.capitalize(
                toString().toLowerCase()
        );
    }
}
