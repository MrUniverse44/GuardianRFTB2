package dev.mruniverse.guardianrftb.multiarena.enums;

public enum MainAction {
    CLASSIC,
    INFECTED,
    DOUBLE_BEAST,
    KILLER,
    ISLAND_OF_THE_BEAST,
    ISLAND_OF_THE_BEAST_KILLER,
    ISLAND_OF_THE_BEAST_DOUBLE_BEAST,
    CUSTOM,
    FILL;
    public String getPath() {
        switch (this) {
            case CLASSIC:
                return "menus.gameMain.items.Classic";
            case INFECTED:
                return "menus.gameMain.items.Infected";
            case DOUBLE_BEAST:
                return "menus.gameMain.items.Double";
            case KILLER:
                return "menus.gameMain.items.Killer";
            case ISLAND_OF_THE_BEAST:
                return "menus.gameMain.items.Island";
            case ISLAND_OF_THE_BEAST_DOUBLE_BEAST:
                return "menus.gameMain.items.IslandDouble";
            case ISLAND_OF_THE_BEAST_KILLER:
                return "menus.gameMain.items.IslandKiller";
            case FILL:
                return "fill-inventory.gameMain";
            case CUSTOM:
            default:
                return "none";
        }
    }
}
