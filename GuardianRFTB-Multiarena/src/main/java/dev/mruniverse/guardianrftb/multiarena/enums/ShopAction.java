package dev.mruniverse.guardianrftb.multiarena.enums;

public enum ShopAction {
    KIT_RUNNERS,
    KIT_BEASTS,
    CRAFT,
    BOOST,
    FILL,
    CUSTOM;
    public String getPath() {
        switch (this) {
            case CRAFT:
                return "menus.shop.items.Craft";
            case KIT_RUNNERS:
                return "menus.shop.items.KitRunner";
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
