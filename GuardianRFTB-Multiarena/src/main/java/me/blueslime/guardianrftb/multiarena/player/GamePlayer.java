package me.blueslime.guardianrftb.multiarena.player;

import me.blueslime.guardianrftb.multiarena.enums.GameTeam;
import me.blueslime.guardianrftb.multiarena.scoreboard.PluginScoreboard;
import me.blueslime.guardianrftb.multiarena.game.Game;
import me.blueslime.guardianrftb.multiarena.utils.MessageHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GamePlayer {


    private PluginScoreboard scoreboard;

    private Data data = Data.empty();

    private final Player player;

    private GameTeam team = GameTeam.RUNNERS;

    private Game game = null;

    public GamePlayer(Player player) {
        this.player = player;
    }

    public GamePlayer(Data data, Player player) {
        this.player = player;
        this.data = data;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void sendMessage(String message) {
        MessageHandler.sendMessage(this, message);
    }

    public boolean isPlaying() {
        return game != null;
    }

    public String getName() {
        return player.getName();
    }

    public GameTeam getTeam() {
        return team;
    }

    public Game getGame() {
        return game;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public Data getData() {
        return data;
    }

    public Player getBukkitPlayer() {
        return player;
    }


    public static class Data {

        private String selectedKit;

        private List<String> kits;

        private int deaths;

        private int coins;

        private int wins;

        public Data(String selectedKit, List<String> kits, int coins, int wins, int deaths) {
            this.selectedKit = selectedKit;
            this.deaths = deaths;
            this.coins = coins;
            this.kits = kits;
            this.wins = wins;
        }

        public void setSelectedKit(String kit) {
            this.selectedKit = kit;
        }

        public void setKits(List<String> kits) {
            this.kits = kits;
        }

        public void setCoins(int coins) {
            this.coins = coins;
        }

        public void removeCoins(int value) {
            coins -= value;
        }

        public void removeWins(int value) {
            wins -= value;
        }

        public void removeDeaths(int value) {
            deaths -= value;
        }

        public void addCoins(int value) {
            coins += value;
        }

        public void addWins(int value) {
            wins += value;
        }

        public void addDeaths(int value) {
            deaths += value;
        }

        public void setWins(int wins) {
            this.wins = wins;
        }

        public void setDeaths(int deaths) {
            this.deaths = deaths;
        }

        public int getCoins() {
            return coins;
        }

        public int getDeaths() {
            return deaths;
        }

        public int getWins() {
            return wins;
        }

        public int getGamesPlayed() {
            return wins + deaths;
        }

        public List<String> getKits() {
            return kits;
        }

        public String getSelectedKit() {
            return selectedKit;
        }

        public void reset() {
            selectedKit = "";
            deaths      = 0;
            coins       = 0;
            wins        = 0;
            kits        = new ArrayList<>();
        }

        public static Data empty() {
            return new Data("", new ArrayList<>(), 0, 0 ,0 );
        }

    }

}
