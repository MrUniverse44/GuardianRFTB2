package dev.mruniverse.guardianrftb.multiarena.enums;

public enum GameEquip {
    BEAST_KIT,
    RUNNER_KIT,
    KILLER_KIT;

    public KitType getKitType() {
        switch (this) {
            case KILLER_KIT:
                return KitType.KILLER;
            case BEAST_KIT:
                return KitType.BEAST;
            default:
            case RUNNER_KIT:
                return KitType.RUNNER;
        }
    }
}
