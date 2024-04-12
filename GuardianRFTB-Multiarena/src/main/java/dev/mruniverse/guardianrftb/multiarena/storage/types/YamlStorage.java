package dev.mruniverse.guardianrftb.multiarena.storage.types;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import dev.mruniverse.guardianrftb.multiarena.storage.result.DataResult;
import dev.mruniverse.guardianrftb.multiarena.utils.FileUtilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class YamlStorage implements DataStorage{
    private final GuardianRFTB plugin;

    public YamlStorage(GuardianRFTB plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getIdentifier() {
        return "yaml";
    }

    @Override
    public void initialize() {
        File file = new File(plugin.getDataFolder(), "users");
        if (!file.exists() && file.mkdirs()) {
            plugin.getLogger().info("Initialized Files Storage for first time, welcome to GuardianRFTB!");
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void save(GamePlayer gamePlayer) {
        String id = gamePlayer.getId();

        File folder = new File(plugin.getDataFolder(), "users");
        File file = new File(folder, id + ".yml");

        FileUtilities.createFile(
            file,
            YamlStorage.class.getResourceAsStream("/user-template.yml")
        );

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        final DataResult result = DataResult.builder().fromGamePlayer(gamePlayer);

        configuration.set("wins", result.getWins());
        configuration.set("kills", result.getKills());
        configuration.set("deaths", result.getDeaths());
        configuration.set("coins", result.getCoins());
        configuration.set("kits", result.getRawKits());
        configuration.set("selected.beast", result.getBeastKit());
        configuration.set("selected.killer", result.getKillerKit());
        configuration.set("selected.runner", result.getRunnerKit());

        try {
            configuration.save(
                    file
            );
        } catch (IOException exception) {
            plugin.getLogger().info("Can't save player statistics");
            exception.printStackTrace();
        }
    }

    @Override
    public void saveAllPlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            save(plugin.getGamePlayer(player));
        }
    }

    @Override
    public DataResult load(Player player) {
        String id = player.getUniqueId().toString().replace("-","");

        File folder = new File(plugin.getDataFolder(), "users");
        File file = new File(folder, id + ".yml");

        FileUtilities.createFile(
            file,
            YamlStorage.class.getResourceAsStream("/user-template.yml")
        );

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        return DataResult.builder().fromConfiguration(
            configuration
        );
    }

    @Override
    public void loadAllPlayers() {
        for (GamePlayer gamePlayer : plugin.getPlayers().values()) {
            gamePlayer.setData(load(gamePlayer.getPlayer()));
        }
    }
}
