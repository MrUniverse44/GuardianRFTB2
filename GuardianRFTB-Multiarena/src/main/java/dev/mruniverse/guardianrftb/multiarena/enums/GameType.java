package dev.mruniverse.guardianrftb.multiarena.enums;

public enum GameType {
    DOUBLE_BEAST,
    INFECTED,
    KILLER,
    ISLAND_OF_THE_BEAST,
    ISLAND_OF_THE_BEAST_DOUBLE_BEAST,
    ISLAND_OF_THE_BEAST_KILLER,
    CLASSIC;

    public String toPath() {
        return this == ISLAND_OF_THE_BEAST_DOUBLE_BEAST ? "ISLAND_OF_THE_BEAST_DOUBLE" : toString();
    }

    public String getPath() {
        return "settings.game.modes." + toPath();
    }
}
