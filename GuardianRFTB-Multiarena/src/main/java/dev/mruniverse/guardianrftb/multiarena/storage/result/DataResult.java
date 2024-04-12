package dev.mruniverse.guardianrftb.multiarena.storage.result;

import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataResult {
    private final List<String> kits = new ArrayList<>();

    private String runnerKit;
    private String killerKit;
    private String beastKit;

    private int deaths;
    private int coins;
    private int kills;
    private int wins;

    private DataResult(
        int wins, int kills, int deaths,
        int coins, List<String> kits, String beastKit,
        String killerKit, String runnerKit
    ) {
        this.runnerKit = runnerKit;
        this.killerKit = killerKit;
        this.beastKit  = beastKit;

        this.kits.addAll(kits);

        this.deaths = deaths;
        this.coins = coins;
        this.kills = kills;
        this.wins = wins;
    }

    public static DataResultBuilder builder() {
        return new DataResultBuilder();
    }

    public List<String> getKits() {
        return kits;
    }

    public void addKit(String kit) {
        this.kits.add(kit);
    }

    public void addDeaths(int deaths) {
        this.deaths += deaths;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    public void addKills(int kills) {
        this.kills += kills;
    }

    public void addWins(int wins) {
        this.wins += wins;
    }

    public void removeKit(String kit) {
        this.kits.remove(kit);
    }

    public void removeDeaths(int deaths) {
        this.deaths -= deaths;
    }

    public void removeCoins(int coins) {
        this.coins -= coins;
    }

    public void removeKills(int kills) {
        this.kills -= kills;
    }

    public boolean containsKit(String kit) {
        return this.kits.contains(kit);
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWins() {
        return wins;
    }

    public int getCoins() {
        return coins;
    }

    public int getKills() {
        return kills;
    }

    public String getRunnerKit() {
        return runnerKit;
    }

    public String getKillerKit() {
        return killerKit;
    }

    public String getBeastKit() {
        return beastKit;
    }

    public String getRawKits() {
        return String.join(",", kits);
    }

    public void setRunnerKit(String runnerKit) {
        this.runnerKit = runnerKit;
    }

    public void setKillerKit(String killerKit) {
        this.killerKit = killerKit;
    }

    public void setBeastKit(String beastKit) {
        this.beastKit = beastKit;
    }

    public static DataResult empty() {
        return new DataResultBuilder().fromRaw(0, 0, 0, 0, "", "", "", "");
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public static class DataResultBuilder {
        public DataResult fromGamePlayer(GamePlayer gamePlayer) {
            return gamePlayer.getStatistics();
        }

        public DataResult fromConfiguration(FileConfiguration configuration) {
            return fromRaw(
                configuration.getInt("wins", 0),
                configuration.getInt("kills", 0),
                configuration.getInt("deaths", 0),
                configuration.getInt("coins", 0),
                configuration.getString("kits", ""),
                configuration.getString("selected.beast", ""),
                configuration.getString("selected.killer", ""),
                configuration.getString("selected.runner", "")
            );
        }

        public DataResult fromRaw(int wins, int kills, int deaths, int coins, String kits, String beastKit, String killerKit, String runnerKit) {
            List<String> kitList = Arrays.asList(
                kits.replace(
                    " ",
                    ""
                ).split(",")
            );

            return new DataResult(
                wins,
                kills,
                deaths,
                coins,
                kitList,
                beastKit,
                killerKit,
                runnerKit
            );
        }
    }
}
