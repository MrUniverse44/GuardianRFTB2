package dev.mruniverse.guardianrftb.multiarena;

import dev.mruniverse.slimelib.SlimeFiles;
import dev.mruniverse.slimelib.SlimePlatform;

public enum SlimeFile implements SlimeFiles {
    MESSAGES_ES("messages_es.yml", "translations"),
    MESSAGES_EN("messages_en.yml", "translations"),
    MESSAGES("messages_en.yml", "translations"),
    SCOREBOARDS("scoreboards.yml"),
    HOLOGRAMS("holograms.yml"),
    SETTINGS("settings.yml"),
    CHESTS("chests.yml"),
    SOUNDS("sounds.yml"),
    GAMES("games.yml"),
    ITEMS("items.yml"),
    MENUS("menus.yml"),
    KITS("kits.yml");
    private final boolean differentFolder;

    private final String file;

    private final String folder;

    private final String resource;

    SlimeFile(String file) {
        this.file = file;
        this.resource = file;
        this.differentFolder = false;
        this.folder = "";
    }

    SlimeFile(String file, String folder) {
        this.file = file;
        this.resource = file;
        this.differentFolder = true;
        this.folder = folder;
    }

    @Override
    public String getFileName() {
        return this.file;
    }

    @Override
    public String getFolderName() {
        return this.folder;
    }

    @Override
    public String getResourceFileName(SlimePlatform platform) {
        if (platform == SlimePlatform.VELOCITY || platform == SlimePlatform.SPONGE) {
            return "/" + this.resource;
        }
        return this.resource;
    }

    @Override
    public boolean isInDifferentFolder() {
        return this.differentFolder;
    }
}
