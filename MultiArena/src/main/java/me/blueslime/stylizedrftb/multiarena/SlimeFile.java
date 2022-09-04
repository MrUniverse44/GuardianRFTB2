package me.blueslime.stylizedrftb.multiarena;

import dev.mruniverse.slimelib.SlimeFiles;
import dev.mruniverse.slimelib.SlimePlatform;

public enum SlimeFile implements SlimeFiles {
    SETTINGS("settings.yml");

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
        return this.resource;
    }

    @Override
    public boolean isInDifferentFolder() {
        return this.differentFolder;
    }
}
