package dev.mruniverse.guardianrftb.multiarena.enums;

public enum ShopAction {
    KIT_RUNNERS,
    KIT_BEASTS,
    KIT_KILLERS,
    BOOST,
    FILL,
    CUSTOM;
    public String getPath() {
        switch (this) {
            case KIT_KILLERS:
                return "menus.shop.items.KitRunner";
            case KIT_RUNNERS:
                return "menus.shop.items.KitKiller";
            case KIT_BEASTS:
                return "menus.shop.items.KitBeast";
            case BOOST:
                return "menus.shop.items.Boost";
            case FILL:
                return "fill-inventory.shop";
            case CUSTOM:
            default:
                return "none";
        }
    }
}
