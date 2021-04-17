package dev.mruniverse.guardianrftb.multiarena.enums;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;

public enum GameStatus {
    PREPARING,
    WAITING,
    STARTING,
    PLAYING,
    IN_GAME,
    RESTARTING;

    public String getStatus() {
        switch (this) {
            case WAITING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.color.waiting");
            case IN_GAME:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.color.InGame");
            case STARTING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.color.starting");
            case PREPARING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.color.preparing");
            case PLAYING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.color.playing");
            case RESTARTING:
            default:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.color.ending");
        }
    }
    public String getStatusName() {
        switch (this) {
            case WAITING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.names.waiting");
            case IN_GAME:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.names.InGame");
            case STARTING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.names.starting");
            case PREPARING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.names.preparing");
            case PLAYING:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.names.playing");
            case RESTARTING:
            default:
                return GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.status.names.ending");
        }
    }
}
