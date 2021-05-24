package dev.mruniverse.guardianrftb.bungeegame.enums;

import dev.mruniverse.guardianrftb.bungeegame.GuardianRFTB;
import org.bukkit.configuration.file.FileConfiguration;

public enum GameType {
    DOUBLE_BEAST,
    INFECTED,
    KILLER,
    ISLAND_OF_THE_BEAST,
    ISLAND_OF_THE_BEAST_DOUBLE_BEAST,
    ISLAND_OF_THE_BEAST_KILLER,
    CLASSIC;
    @SuppressWarnings("unused")
    public String getType() {
        FileConfiguration file = GuardianRFTB.getInstance().getStorage().getControl(GuardianFiles.SETTINGS);
        switch (this) {
            case KILLER:
                return file.getString("settings.game.modes.KILLER");
            case ISLAND_OF_THE_BEAST:
                return file.getString("settings.game.modes.ISLAND_OF_THE_BEAST");
            case ISLAND_OF_THE_BEAST_KILLER:
                return file.getString("settings.game.modes.ISLAND_OF_THE_BEAST_KILLER");
            case ISLAND_OF_THE_BEAST_DOUBLE_BEAST:
                return file.getString("settings.game.modes.ISLAND_OF_THE_BEAST_DOUBLE");
            case DOUBLE_BEAST:
                return file.getString("settings.game.modes.DOUBLE_BEAST");
            case INFECTED:
                return file.getString("settings.game.modes.INFECTED");
            case CLASSIC:
            default:
                return file.getString("settings.game.modes.CLASSIC");
        }
    }
}
