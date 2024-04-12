package dev.mruniverse.guardianrftb.multiarena.storage;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.storage.result.DataResult;
import dev.mruniverse.guardianrftb.multiarena.storage.types.DataStorage;
import dev.mruniverse.guardianrftb.multiarena.storage.types.MySQLStorage;
import dev.mruniverse.guardianrftb.multiarena.storage.types.YamlStorage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unused")
public class PluginDatabase {
    private final Map<Class<? extends DataStorage>, DataStorage> storageMap = new HashMap<>();
    private final Map<String, Class<? extends DataStorage>> idStorageMap = new HashMap<>();

    private String currentDatabase = null;

    public PluginDatabase(GuardianRFTB plugin) {

        registerStorage(
            new MySQLStorage(plugin),
            new YamlStorage(plugin)
        );

        setCurrentDatabase(
            plugin.getStorage().getControl(GuardianFiles.MYSQL).getString("database", "yaml").toLowerCase(Locale.ENGLISH)
        );
    }

    public void setCurrentDatabase(String currentDatabase) {
        if (this.currentDatabase != null) {
            Class<? extends DataStorage> clazz = idStorageMap.get(this.currentDatabase);

            if (clazz != null) {
                DataStorage storage = storageMap.get(clazz);
                if (storage != null) {
                    if (!this.currentDatabase.equalsIgnoreCase(currentDatabase)) {
                        storage.saveAllPlayers();
                        storage.shutdown();
                    } else {
                        storage.reload();
                    }
                }
            }
        }

        this.currentDatabase = currentDatabase;

        Class<? extends DataStorage> clazz = idStorageMap.get(currentDatabase);

        if (clazz != null) {
            DataStorage storage = storageMap.get(clazz);
            if (storage != null) {
                storage.initialize();
                storage.loadAllPlayers();
            }
        }
    }

    public void reload() {

    }

    public void save(GamePlayer gamePlayer) {
        if (currentDatabase != null) {
            Class<? extends DataStorage> clazz = idStorageMap.get(currentDatabase);

            if (clazz != null) {
                DataStorage storage = storageMap.get(clazz);
                if (storage != null) {
                    storage.save(gamePlayer);
                }
            }
        }
    }

    public void saveAllPlayers() {
        if (currentDatabase != null) {
            Class<? extends DataStorage> clazz = idStorageMap.get(currentDatabase);

            if (clazz != null) {
                DataStorage storage = storageMap.get(clazz);
                if (storage != null) {
                    storage.saveAllPlayers();
                }
            }
        }
    }

    public DataResult load(Player player) {
        if (currentDatabase != null) {
            Class<? extends DataStorage> clazz = idStorageMap.get(currentDatabase);

            if (clazz != null) {
                DataStorage storage = storageMap.get(clazz);
                if (storage != null) {
                    return storage.load(player);
                }
            }
        }
        return DataResult.empty();
    }

    @SuppressWarnings("unchecked")
    public <T extends DataStorage> T getStorage(Class<? extends T> clazz) {
        return (T) storageMap.get(clazz);
    }

    public void registerStorage(DataStorage... storages) {
        for (DataStorage storage : storages) {
            idStorageMap.put(storage.getIdentifier().toLowerCase(Locale.ENGLISH), storage.getClass());
            storageMap.put(storage.getClass(), storage);
        }
    }

    public Map<Class<? extends DataStorage>, DataStorage> getStorageMap() {
        return storageMap;
    }

    public void shutdown() {
        if (currentDatabase != null) {
            Class<? extends DataStorage> clazz = idStorageMap.get(currentDatabase);

            if (clazz != null) {
                DataStorage storage = storageMap.get(clazz);
                if (storage != null) {
                    storage.saveAllPlayers();
                    storage.shutdown();
                }
            }
        }
    }
}