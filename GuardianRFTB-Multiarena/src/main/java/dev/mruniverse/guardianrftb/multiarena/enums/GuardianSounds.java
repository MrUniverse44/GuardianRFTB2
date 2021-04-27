package dev.mruniverse.guardianrftb.multiarena.enums;

@SuppressWarnings("unused")
public enum GuardianSounds {
    GAME_COUNT,
    BEAST_COUNT,
    STARTING_COUNT,
    KILL_SOUND,
    WIN_SOUND,
    DEATH_SOUND;

    public String getSoundPath() {
        switch (this) {
            case WIN_SOUND:
                return "sounds.game.win.sound";
            case KILL_SOUND:
                return "sounds.game.kill.sound";
            case DEATH_SOUND:
                return "sounds.game.death.sound";
            case STARTING_COUNT:
                return "sounds.game.count.starting.sound";
            case BEAST_COUNT:
                return "sounds.game.count.beast-appear.sound";
            default:
            case GAME_COUNT:
                return "sounds.game.count.selecting.sound";
        }
    }
    public String getPitchPath() {
        switch (this) {
            case WIN_SOUND:
                return "sounds.game.win.pitch";
            case KILL_SOUND:
                return "sounds.game.kill.pitch";
            case DEATH_SOUND:
                return "sounds.game.death.pitch";
            case STARTING_COUNT:
                return "sounds.game.count.starting.pitch";
            case BEAST_COUNT:
                return "sounds.game.count.beast-appear.pitch";
            default:
            case GAME_COUNT:
                return "sounds.game.count.selecting.pitch";
        }
    }
    public String getVolumePath() {
        switch (this) {
            case WIN_SOUND:
                return "sounds.game.win.volume";
            case KILL_SOUND:
                return "sounds.game.kill.volume";
            case DEATH_SOUND:
                return "sounds.game.death.volume";
            case STARTING_COUNT:
                return "sounds.game.count.starting.volume";
            case BEAST_COUNT:
                return "sounds.game.count.beast-appear.volume";
            default:
            case GAME_COUNT:
                return "sounds.game.count.selecting.volume";
        }
    }
    public String getStatusPath() {
        switch (this) {
            case WIN_SOUND:
                return "sounds.game.win.toggle";
            case KILL_SOUND:
                return "sounds.game.kill.toggle";
            case DEATH_SOUND:
                return "sounds.game.death.toggle";
            case STARTING_COUNT:
                return "sounds.game.count.starting.toggle";
            case BEAST_COUNT:
                return "sounds.game.count.beast-appear.toggle";
            default:
            case GAME_COUNT:
                return "sounds.game.count.selecting.toggle";
        }
    }
}
