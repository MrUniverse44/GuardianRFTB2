package me.blueslime.guardianrftb.multiarena.enums;

import dev.mruniverse.slimelib.control.Control;
import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.SlimeFile;
import org.apache.commons.lang.StringUtils;

public enum GameType {
    DOUBLE_BEAST,
    INFECTED,
    KILLER,
    ISLAND_OF_THE_BEAST,
    ISLAND_OF_THE_BEAST_DOUBLE_BEAST,
    ISLAND_OF_THE_BEAST_KILLER,
    CLASSIC;

    public String getType() {

        Control file = GuardianRFTB.getFileStorage().getControl(SlimeFile.SETTINGS);

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

    public static GameType fromText(String text) {
        switch (text.toLowerCase()) {
            case "killer":
            case "kill":
            case "assassin":
                return GameType.KILLER;
            case "island_of_the_beast_killer":
            case "island_killer":
            case "i_o_t_b_k":
                return GameType.ISLAND_OF_THE_BEAST_KILLER;
            case "island_of_the_beast_double_beast":
            case "island_of_the_beast_double":
            case "island_double":
                return GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST;
            case "double":
            case "doubles":
            case "double_beast":
            case "double_beasts":
                return GameType.DOUBLE_BEAST;
            case "infect":
            case "infected":
                return GameType.INFECTED;
            default:
            case "classic":
            case "default":
            case "normal":
                return GameType.CLASSIC;
        }
    }

    public String toUpper() {
        return super.toString().toUpperCase();
    }

    public String toString() {
        return StringUtils.capitalize(
                super.toString().toLowerCase()
        );
    }
}
