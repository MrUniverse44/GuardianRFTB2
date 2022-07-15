package me.blueslime.guardianrftb.multiarena.enums;

import dev.mruniverse.slimelib.control.Control;
import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.SlimeFile;
import org.apache.commons.lang.StringUtils;

public enum GameTeam {
    RUNNER,
    RUNNERS,
    KILLER,
    KILLERS,
    BEAST,
    BEASTS;

    public String getRole() {
        Control control = GuardianRFTB.getFileStorage().getControl(SlimeFile.SETTINGS);

        return control.getColoredString("settings.game.roles." + this, toCapitalized());
    }

    public String toCapitalized() {
        return StringUtils.capitalize(
                toString()
        );
    }

    public String toString() {
        return super.toString().toLowerCase();
    }
}
