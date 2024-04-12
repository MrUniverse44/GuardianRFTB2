package dev.mruniverse.guardianrftb.multiarena.storage.types;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import dev.mruniverse.guardianrftb.multiarena.storage.result.DataResult;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;

public class MySQLStorage implements DataStorage {
    private final GuardianRFTB plugin;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {}
    }

    private PreparedStatement savePlayerStmt;
    private PreparedStatement loadPlayerStmt;
    private Connection connection;

    public MySQLStorage(GuardianRFTB main) {
        plugin = main;
    }

    @Override
    public String getIdentifier() {
        return "mysql";
    }

    private FileConfiguration getConfiguration() {
        return plugin.getStorage().getControl(GuardianFiles.MYSQL);
    }

    @Override
    public void initialize() {
        FileConfiguration conf = getConfiguration();

        try {
            initialize(
                conf.getString("mysql.table", "GuardianRFTB"),
                conf.getString("mysql.hostname", "localhost"),
                conf.getInt("mysql.port", 3306),
                conf.getString("mysql.database", "database"),
                conf.getString("mysql.username", "username"),
                conf.getString("mysql.password", "password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initialize(String table, String hostname, int port, String database, String user, String password) throws SQLException {
        String tableStmt = "create table if not exists `" + table + "` (" +
            " `player_id` varchar(60)," +
            " `wins` int unsigned not null," +
            " `kills` int unsigned not null," +
            " `deaths` int unsigned not null," +
            " `coins` int unsigned not null," +
            " `kits` varchar(2000)," +
            " `selected_beast` varchar(100)" +
            " `selected_killer` varchar(100)" +
            " `selected_runner` varchar(100)" +
            " primary key (`playerId`)" +
        ")";

        final Connection connection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true&useSSL=false", hostname, port, database),
                user, password
        );

        try (final Statement statement = connection.createStatement()) {
            statement.execute(tableStmt);
        }

        this.connection = connection;

        this.loadPlayerStmt = connection.prepareStatement("select * from `" + table + "` where `player_id` = ?");

        this.savePlayerStmt = connection.prepareStatement("replace into `" + table + "` values (?,?,?,?,?,?,?,?,?)");
        plugin.getLogger().info("MySQL has been connected without issues!");
    }

    @Override
    public void shutdown() {
        try {
            connection.close();
            savePlayerStmt.close();
            loadPlayerStmt.close();
        } catch (Exception ignored) {}
    }

    @Override
    public void reload() {
        shutdown();
        initialize();
    }

    @Override
    public synchronized void save(GamePlayer gamePlayer) {
        saveAttempt(gamePlayer, 0);
    }

    public synchronized void saveAttempt(GamePlayer player, int attempt) {
        final DataResult result = DataResult.builder().fromGamePlayer(player);

        if (attempt != 3) {
            try {
                savePlayerStmt.clearParameters();
                savePlayerStmt.setString(1, player.getId());
                savePlayerStmt.setInt(2, result.getWins());
                savePlayerStmt.setInt(3, result.getKills());
                savePlayerStmt.setInt(4, result.getDeaths());
                savePlayerStmt.setInt(5, result.getCoins());
                savePlayerStmt.setString(6, result.getRawKits());
                savePlayerStmt.setString(7, result.getBeastKit());
                savePlayerStmt.setString(8, result.getKillerKit());
                savePlayerStmt.setString(9, result.getRunnerKit());

                savePlayerStmt.executeUpdate();
            } catch (SQLException e) {
                initialize();
                saveAttempt(player, attempt + 1);
            }
        } else {
            try {
                savePlayerStmt.clearParameters();
                savePlayerStmt.setString(1, player.getId());
                savePlayerStmt.setInt(2, result.getWins());
                savePlayerStmt.setInt(3, result.getKills());
                savePlayerStmt.setInt(4, result.getDeaths());
                savePlayerStmt.setInt(5, result.getCoins());
                savePlayerStmt.setString(6, result.getRawKits());
                savePlayerStmt.setString(7, result.getBeastKit());
                savePlayerStmt.setString(8, result.getKillerKit());
                savePlayerStmt.setString(9, result.getRunnerKit());

                savePlayerStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void saveAllPlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            save(plugin.getGamePlayer(player));
        }
    }

    @Override
    public synchronized DataResult load(Player player) {
        return loadAttempt(player, 0);
    }

    public synchronized DataResult loadAttempt(Player player, int attempt) {
        if (attempt < 5) {
            try {
                loadPlayerStmt.clearParameters();

                String id = player.getUniqueId().toString().replace("-","");

                loadPlayerStmt.setString(1, id);

                try (ResultSet rs = loadPlayerStmt.executeQuery()) {
                    if (rs.next()) {
                        return DataResult.builder().fromRaw(
                            rs.getInt("wins"),
                            rs.getInt("kills"),
                            rs.getInt("deaths"),
                            rs.getInt("coins"),
                            rs.getString("kits"),
                            rs.getString("selected_beast"),
                            rs.getString("selected_killer"),
                            rs.getString("selected_runner")
                        );
                    }
                }
                return DataResult.empty();
            } catch (SQLException e) {

                initialize();

                return loadAttempt(player, attempt + 1);
            }
        } else {
            try {
                loadPlayerStmt.clearParameters();

                String id = player.getUniqueId().toString().replace("-","");

                loadPlayerStmt.setString(1, id);

                try (ResultSet rs = loadPlayerStmt.executeQuery()) {
                    if (rs.next()) {
                        return DataResult.builder().fromRaw(
                            rs.getInt("wins"),
                            rs.getInt("kills"),
                            rs.getInt("deaths"),
                            rs.getInt("coins"),
                            rs.getString("kits"),
                            rs.getString("selected_beast"),
                            rs.getString("selected_killer"),
                            rs.getString("selected_runner")
                        );
                    }
                }
                return DataResult.empty();
            } catch (SQLException e) {
                e.printStackTrace();
                return DataResult.empty();
            }
        }
    }

    @Override
    public synchronized void loadAllPlayers() {
        for (GamePlayer gamePlayer : plugin.getPlayers().values()) {
            gamePlayer.setData(load(gamePlayer.getPlayer()));
        }
    }
}