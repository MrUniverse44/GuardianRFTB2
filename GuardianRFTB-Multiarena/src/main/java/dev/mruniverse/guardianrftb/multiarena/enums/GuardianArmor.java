package dev.mruniverse.guardianrftb.multiarena.enums;

public enum GuardianArmor {
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS;

    public String getPartName() {
        switch (this) {
            case HELMET:
                return "Helmet";
            case BOOTS:
                return "Boots";
            case LEGGINGS:
                return "Leggings";
            case CHESTPLATE:
            default:
                return "Chestplate";
        }
    }
}