package dev.mruniverse.guardianrftb.multiarena.interfaces;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.SaveMode;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public interface FileStorage {

    void setPlugin(GuardianRFTB plugin);

    void setMessages(String code);

    File getFile(GuardianFiles fileToGet);

    /**
     * Creates a config File if it doesn't exists,
     * reloads if specified file exists.
     *
     * @param configName config to create/reload.
     */
    FileConfiguration loadConfig(String configName);
    /**
     * Creates a config File if it doesn't exists,
     * reloads if specified file exists.
     *
     * @param rigoxFile config to create/reload.
     */
    FileConfiguration loadConfig(File rigoxFile);

    /**
     * Reload plugin file(s).
     *
     * @param Mode mode of reload.
     */
    void reloadFile(SaveMode Mode);

    /**
     * Save config File using FileStorageImpl
     *
     * @param fileToSave config to save/create with saveMode.
     */
    void save(SaveMode fileToSave);
    /**
     * Save config File Changes & Paths
     *
     * @param configName config to save/create.
     */
    void saveConfig(String configName);
    /**
     * Save config File Changes & Paths
     *
     * @param fileToSave config to save/create.
     */
    void saveConfig(File fileToSave);

    /**
     * Control a file, getControl() will return a FileConfiguration.
     *
     * @param fileToControl config to control.
     */
    FileConfiguration getControl(GuardianFiles fileToControl);

    List<String> getContent(GuardianFiles file, String path, boolean getKeys);
}
