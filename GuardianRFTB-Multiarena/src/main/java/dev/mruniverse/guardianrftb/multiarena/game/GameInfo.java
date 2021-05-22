package dev.mruniverse.guardianrftb.multiarena.game;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.runnables.*;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"unused", "deprecation"})
public class GameInfo implements Game {

    private final GuardianRFTB plugin;
    private final Utils utils = GuardianLIB.getControl().getUtils();

    private final Random random = new Random();

    private final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<Player> runners = new ArrayList<>();
    private final ArrayList<Player> spectators = new ArrayList<>();
    private final ArrayList<Player> beasts = new ArrayList<>();
    private final ArrayList<Player> killers = new ArrayList<>();

    private final ArrayList<String> chestTypes = new ArrayList<>();

    private final ArrayList<Location> signs = new ArrayList<>();

    private final HashMap<String,List<Location>> chestLocations = new HashMap<>();

    private final String configName;
    private final String path;

    private Location waiting;
    private Location selecting;
    private Location beastSpawn;
    private Location runnerSpawn;
    private Location killerSpawn;

    private String name;

    private GameType gameType;
    private GameStatus gameStatus;

    private int max;
    private int min;
    private int gameMaxTime;
    private int lastTimer;
    private int worldTime;
    private int menuSlot = -1;
    private int lastListener;

    private boolean invincible = true;
    public boolean doubleCountPrevent = false;


    /**
     * @param plugin main class of the GuardianRFTB
     * @param configName the configuration name of the game (name in games.yml)
     * @param gameName the name of the game (this can be used in different games)
     */
    public GameInfo(GuardianRFTB plugin, String configName, String gameName) {
        this.plugin = plugin;
        this.configName = configName;
        this.name = gameName;
        this.lastTimer = 30;
        this.path = "games." + configName + ".";
        setup();
    }
    @Override
    public void setup() {
        try {
            FileConfiguration gameFile = plugin.getStorage().getControl(GuardianFiles.GAMES);
            gameMaxTime = gameFile.getInt(path + "time");
            worldTime = gameFile.getInt(path + "worldTime");
            max = gameFile.getInt(path + "max");
            min = gameFile.getInt(path + "min");
            if (min == 1) min = 2;
            gameType = GameType.valueOf(gameFile.getString(path + "gameType"));
            selecting = getLocation(gameFile.getString(path + "locations.selected-beast"));
            beastSpawn = getLocation(gameFile.getString(path + "locations.beast"));
            waiting = getLocation(gameFile.getString(path + "locations.waiting"));
            runnerSpawn = getLocation(gameFile.getString(path + "locations.runners"));
            invincible = true;
            if (beastSpawn == null || runnerSpawn == null || waiting == null || selecting == null) {
                gameStatus = GameStatus.ERROR;
            } else {
                gameStatus = GameStatus.PREPARING;
            }
            loadSigns();
            loadChests();
            loadStatus();
        }catch (Throwable throwable) {
            plugin.getLogs().error("Unexpected issue when the game was loading");
            plugin.getLogs().error("Game of the issue: " + configName);
            plugin.getLogs().error(throwable);
        }
    }
    @Override
    public void loadStatus() {
        gameStatus = GameStatus.WAITING;
        updateSigns();
    }
    @Override
    public void loadChests() {
        try {
            FileConfiguration gameFile = plugin.getStorage().getControl(GuardianFiles.GAMES);
            if(gameFile.get(path + "chests") != null) {
                for(String chestType : gameFile.getStringList(path + "chests")) {
                    chestTypes.add(chestType);
                    loadChestType(chestType);
                }
            }
        }catch (Throwable throwable){
            plugin.getLogs().error("Can't load game Chests");
            plugin.getLogs().error(throwable);
        }
    }
    @Override
    public void setInvincible(boolean status) {
        invincible = status;
    }
    @Override
    public int getNeedPlayers() {
        int RealMin = min;
        if((gameType == GameType.DOUBLE_BEAST || gameType == GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST) && min == 2) {
            RealMin = 3;
        }
        if((RealMin - players.size()) <= 0 && gameStatus.equals(GameStatus.WAITING)) {
            gameStatus = GameStatus.STARTING;
            for(Player player : players) {
                PlayerManager playerData = plugin.getPlayerData(player.getUniqueId());
                playerData.setBoard(GuardianBoard.STARTING);
            }
        }
        if(players.size() < RealMin) {
            return (RealMin - players.size());
        }
        return 0;
    }
    @Override
    public void loadChestType(String chestName) {
        try {
            FileConfiguration gameFile = plugin.getStorage().getControl(GuardianFiles.GAMES);
            if(gameFile.get(path + "chests-location." + chestName) != null) {
                List<Location> newLocations = new ArrayList<>();
                for(String locations : gameFile.getStringList(path + "chests-location." + chestName)) {
                    newLocations.add(utils.getLocationFromString(locations));
                }
                chestLocations.put(chestName,newLocations);
            }
        }catch (Throwable throwable){
            plugin.getLogs().error("Can't load game Chests");
            plugin.getLogs().error(throwable);
        }
    }
    @Override
    public void loadSigns() {
        signs.clear();
        FileConfiguration gameFile = plugin.getStorage().getControl(GuardianFiles.GAMES);
        for(String sign : gameFile.getStringList(path + "signs")) {
            Location signLocation = utils.getLocationFromString(sign);
            if(signLocation != null) {
                if(signLocation.getBlock().getState() instanceof Sign) {
                    signs.add(signLocation);
                }
            }
        }
    }

    /*
     *
     * GAME EVENTS
     *
     */
    @Override
    public void join(Player player) {
        PlayerManager currentData = plugin.getPlayerData(player.getUniqueId());
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        String prefix = messages.getString("messages.prefix");
        if (currentData.getGame() != null) {
            plugin.getUtils().sendMessage(player, prefix + messages.getString("messages.others.already"));
            return;
        }
        if (!gameStatus.equals(GameStatus.WAITING) && !gameStatus.equals(GameStatus.SELECTING) && !gameStatus.equals(GameStatus.STARTING)) {
            if (!gameStatus.equals(GameStatus.RESTARTING)) {
                plugin.getUtils().sendMessage(player, prefix + messages.getString("messages.others.gamePlaying"));
                return;
            }
            plugin.getUtils().sendMessage(player, prefix + messages.getString("messages.others.restarting"));
            return;
        }
        if (players.size() >= max) {
            plugin.getUtils().sendMessage(player, prefix + messages.getString("messages.others.full"));
            return;
        }

        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(this.waiting);
        currentData.setBoard(GuardianBoard.WAITING);
        if (gameStatus.equals(GameStatus.SELECTING)) {
            currentData.setBoard(GuardianBoard.SELECTING);
        } else if(!gameStatus.equals(GameStatus.WAITING)) {
            currentData.setBoard(GuardianBoard.STARTING);
        }
        players.add(player);
        runners.add(player);
        currentData.setGame(this);
        currentData.setStatus(PlayerStatus.IN_GAME);
        if (gameStatus.equals(GameStatus.WAITING)) checkPlayers();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setHealth(20.0D);
        player.setFireTicks(0);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.getInventory().setItem(plugin.getItemsInfo().getRunnerSlot(), plugin.getItemsInfo().getKitRunner());
        player.getInventory().setItem(plugin.getItemsInfo().getExitSlot(), plugin.getItemsInfo().getExit());
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        String joinMsg = messages.getString("messages.game.join-game");
        if (joinMsg == null) joinMsg = "&b%player% &7has joined the game! &3(&b%game_online%&3/&b%game_max%&3)&7!";
        updateSigns();
        player.updateInventory();
        for(Player players : players) {
            plugin.getUtils().sendMessage(players,prefix + joinMsg.replace("%player%",player.getName())
                    .replace("%game_online%",this.players.size()+"")
                    .replace("%game_max%",this.max+""));
        }
    }

    @Override
    public void leaveWithoutSending(Player player) {
        this.players.remove(player);
        this.runners.remove(player);
        this.beasts.remove(player);
        this.spectators.remove(player);
        if(player.isOnline()) {
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            plugin.getPlayerData(player.getUniqueId()).setGame(null);
            plugin.getPlayerData(player.getUniqueId()).setLastCheckpoint(null);
        }
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        String quitMsg;
        if(!gameStatus.equals(GameStatus.IN_GAME) && !gameStatus.equals(GameStatus.PLAYING) && !gameStatus.equals(GameStatus.RESTARTING)) {
            quitMsg = messages.getString("messages.game.leave-game");
        } else {
            quitMsg = messages.getString("messages.game.leave-game-in-game");
        }
        if(quitMsg == null) quitMsg = "&b%player% &7has left the game! &3(&b%game_online%&3/&b%game_max%&3)&7!";
        for(Player pl : this.players) {
            plugin.getUtils().sendMessage(pl,quitMsg.replace("%player%",player.getName())
                    .replace("%game_online%",this.players.size()+"")
                    .replace("%game_max%",this.max+""));
        }
        updateSigns();
    }
    @Override
    public void leave(Player player) {
        this.players.remove(player);
        this.runners.remove(player);
        this.beasts.remove(player);
        this.spectators.remove(player);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        if(player.isOnline()) {
            Location location = plugin.getSettings().getLocation();
            if (location != null) {
                player.teleport(location);
                player.setGameMode(GameMode.ADVENTURE);
            }
        }
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        String quitMsg;
        if(!gameStatus.equals(GameStatus.IN_GAME) && !gameStatus.equals(GameStatus.PLAYING) && !gameStatus.equals(GameStatus.RESTARTING)) {
            quitMsg = messages.getString("messages.game.leave-game");
        } else {
            quitMsg = messages.getString("messages.game.leave-game-in-game");
        }
        if(quitMsg == null) quitMsg = "&b%player% &7has left the game! &3(&b%game_online%&3/&b%game_max%&3)&7!";
        for(Player pl : this.players) {
            plugin.getUtils().sendMessage(pl,quitMsg.replace("%player%",player.getName())
                    .replace("%game_online%",this.players.size()+"")
                    .replace("%game_max%",this.max+""));
        }
        if(player.isOnline()) {
            plugin.getPlayerData(player.getUniqueId()).setStatus(PlayerStatus.IN_LOBBY);
            plugin.getPlayerData(player.getUniqueId()).setGame(null);
            plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.LOBBY);
            plugin.getPlayerData(player.getUniqueId()).setLastCheckpoint(null);
            player.getInventory().clear();
            for (ItemStack item : plugin.getItemsInfo().getLobbyItems().keySet()) {
                player.getInventory().setItem(plugin.getItemsInfo().getSlot(item), item);
            }
            player.updateInventory();
        }
        updateSigns();
    }

    @Override
    public void checkPlayers() {
        if (players.size() == min && !gameStatus.equals(GameStatus.STARTING) && !gameStatus.equals(GameStatus.SELECTING) && !doubleCountPrevent) {
            gameStatus = GameStatus.SELECTING;
            lastListener = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new SelectingRunnable(plugin,this), 0L, 20L);
            lastTimer = 30;
            doubleCountPrevent = true;
            for(Player player : players) {
                plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.SELECTING);
            }
        }
    }

    @Override
    public void cancelTask() {
        plugin.getServer().getScheduler().cancelTask(lastListener);
    }

    @Override
    public void startCount() {
        this.gameStatus = GameStatus.STARTING;
        this.lastTimer = 10;
        for(Player player : players) {
            plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.STARTING);
        }
        lastListener = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new StartRunnable(plugin,this), 0L, 20L);
    }

    @Override
    public void beastCount() {
        this.gameStatus = GameStatus.PLAYING;
        this.lastTimer = 15;
        for(Player player : runners) {
            plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.PLAYING);
        }
        for(Player player : beasts) {
            plugin.getPlayerData(player.getUniqueId()).setBoard(GuardianBoard.BEAST_SPAWN);
        }
        lastListener = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new BeastSpawnRunnable(plugin,this), 0L, 20L);
    }

    @Override
    public void playCount() {
        this.gameStatus = GameStatus.IN_GAME;
        this.lastTimer = getGameMaxTime();
        lastListener = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new PlayingRunnable(this), 0L, 20L);
    }

    @Override
    public void setDoubleCountPrevent(boolean toggle) { doubleCountPrevent = toggle; }

    @Override
    public void setWinner(GameTeam gameTeam) {
        plugin.getServer().getScheduler().cancelTask(lastListener);
        this.gameStatus = GameStatus.RESTARTING;
        lastListener = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new EndingRunnable(plugin,this,gameTeam), 0L, 20L);
    }

    @Override
    public void firework(Player player,boolean firework) {
        if(!firework) return;
        Firework fa = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fam = fa.getFireworkMeta();
        int fType = getRandom().nextInt(4) + 1;
        FireworkEffect.Type fireworkType = null;
        switch (fType) {
            case 1:
                fireworkType = FireworkEffect.Type.STAR;
                break;
            case 2:
                fireworkType = FireworkEffect.Type.BALL;
                break;
            case 3:
                fireworkType = FireworkEffect.Type.BALL_LARGE;
                break;
            case 4:
                fireworkType = FireworkEffect.Type.CREEPER;
                break;
            case 5:
                fireworkType = FireworkEffect.Type.BURST;
                break;
        }
        int co1 = getRandom().nextInt(10) + 1;
        int co2 = getRandom().nextInt(10) + 1;
        Color c1 = fireColor(co1);
        Color c2 = fireColor(co2);
        FireworkEffect ef = FireworkEffect.builder().flicker(getRandom().nextBoolean()).withColor(c1).withFade(c2).with(fireworkType).trail(getRandom().nextBoolean()).build();
        fam.addEffect(ef);
        fam.setPower(1);
        fa.setFireworkMeta(fam);
    }
    public Color fireColor(int c) {
        switch (c) {
            default:
                return Color.YELLOW;
            case 2:
                return Color.RED;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.AQUA;
            case 6:
                return Color.OLIVE;
            case 7:
                return Color.WHITE;
            case 8:
                return Color.ORANGE;
            case 9:
                return Color.TEAL;
            case 10:
                break;
        }
        return Color.LIME;
    }

    @Override
    public boolean timing(int i) {
        return i % 3 == 0;
    }

    @Override
    public void winRunners() {
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        for(Player player : this.players) {
            if(this.spectators.contains(player)) player.setGameMode(GameMode.SPECTATOR);
            plugin.getUtils().sendGameList(player, messages.getStringList("messages.inGame.infoList.endInfo"),GameTeam.RUNNERS);
        }
        for(Player runner : this.runners) {
            plugin.getPlayerData(runner.getUniqueId()).setBoard(GuardianBoard.WIN_RUNNERS_FOR_RUNNERS);
            plugin.getPlayerData(runner.getUniqueId()).addWins();
        }
        for(Player beast : this.beasts) {
            plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.WIN_RUNNERS_FOR_BEAST);
        }
        for(Player spectator : this.spectators) {
            plugin.getPlayerData(spectator.getUniqueId()).setBoard(GuardianBoard.WIN_RUNNERS_FOR_BEAST);
        }
        this.invincible = true;
        this.gameStatus = GameStatus.RESTARTING;
        setWinner(GameTeam.RUNNERS);
    }

    @Override
    public void winBeasts() {
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        for(Player player : this.players) {
            plugin.getUtils().sendGameList(player, messages.getStringList("messages.inGame.infoList.endInfo"),GameTeam.BEASTS);
        }
        for(Player beast : this.beasts) {
            plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.WIN_BEAST_FOR_BEAST);
            plugin.getPlayerData(beast.getUniqueId()).addWins();
        }
        for(Player runner : this.runners) {
            plugin.getPlayerData(runner.getUniqueId()).setBoard(GuardianBoard.WIN_BEAST_FOR_RUNNERS);
        }
        for(Player spectator : this.spectators) {
            plugin.getPlayerData(spectator.getUniqueId()).setBoard(GuardianBoard.WIN_BEAST_FOR_RUNNERS);
        }
        this.invincible = true;
        this.gameStatus = GameStatus.RESTARTING;
        setWinner(GameTeam.BEASTS);
    }

    @Override
    public void restart() {
        this.players.clear();
        this.spectators.clear();
        this.beasts.clear();
        this.runners.clear();
        this.beasts.clear();
        this.lastTimer = 30;
        this.gameStatus = GameStatus.WAITING;
        loadStatus();
    }

    @Override
    public void deathBeast(Player beast) {
        beasts.remove(beast);
        spectators.add(beast);
        plugin.getPlayerData(beast.getUniqueId()).addDeaths();
        beast.setGameMode(GameMode.SPECTATOR);
        for(Player player : players) {
            player.hidePlayer(beast);
        }
        if(beasts.size() == 0) {
            plugin.getPlayerData(beast.getUniqueId()).setBoard(GuardianBoard.WIN_RUNNERS_FOR_BEAST);
            winRunners();
        }
    }

    @Override
    public void deathKiller(Player beast) {
        // * no killers here
    }

    @Override
    public void deathRunner(Player runner) {
        runners.remove(runner);
        if(!gameType.equals(GameType.INFECTED)) {
            spectators.add(runner);
            for(Player player : players) {
                player.hidePlayer(runner);
            }
            runner.setGameMode(GameMode.SPECTATOR);
        } else {
            beasts.add(runner);
            runner.getInventory().clear();
            giveBeastInv(runner);
            runner.setGameMode(GameMode.ADVENTURE);
            runner.teleport(beastSpawn);
        }
        plugin.getPlayerData(runner.getUniqueId()).addDeaths();
        if(runners.size() == 0) {
            plugin.getPlayerData(runner.getUniqueId()).setBoard(GuardianBoard.WIN_BEAST_FOR_RUNNERS);
            winBeasts();
        }
    }

    /*
     *
     * GAME SECOND THINGS
     *
     */
    @Override
    public void updateSigns() {
        String line1,line2,line3,line4;
        line1 = plugin.getSettings().getSettings().getString("settings.signs.line1");
        line2 = plugin.getSettings().getSettings().getString("settings.signs.line2");
        line3 = plugin.getSettings().getSettings().getString("settings.signs.line3");
        line4 = plugin.getSettings().getSettings().getString("settings.signs.line4");
        if(line1 == null) line1 = "&l%arena%";
        if(line2 == null) line2 = "%gameStatus%";
        if(line3 == null) line3 = "%on%/%max%";
        if(line4 == null) line4 = "&nClick to join";
        if(plugin.getGameManager().getGameMenu(this.gameType) != null) {
            plugin.getGameManager().getGameMenu(this.gameType).updateSlot(menuSlot, this);
        }
        for(Location signLocation : this.signs) {
            boolean worldLoaded = true;
            World signWorld = signLocation.getWorld();
            if(signWorld != null) {
                if (Bukkit.getWorld(signWorld.getName()) == null) worldLoaded = false;
                if (worldLoaded && signWorld.getChunkAt(signLocation).isLoaded()) {
                    if (signLocation.getBlock().getState() instanceof Sign) {
                        Sign currentSign = (Sign) signLocation.getBlock().getState();
                        currentSign.setLine(0, replaceGameVariable(line1));
                        currentSign.setLine(1, replaceGameVariable(line2));
                        currentSign.setLine(2, replaceGameVariable(line3));
                        currentSign.setLine(3, replaceGameVariable(line4));
                        currentSign.update();
                    }
                } else {
                    signWorld.loadChunk(signWorld.getChunkAt(signLocation));
                    if (worldLoaded && signWorld.getChunkAt(signLocation).isLoaded()) {
                        if (signLocation.getBlock().getState() instanceof Sign) {
                            Sign currentSign = (Sign) signLocation.getBlock().getState();
                            currentSign.setLine(0, replaceGameVariable(line1));
                            currentSign.setLine(1, replaceGameVariable(line2));
                            currentSign.setLine(2, replaceGameVariable(line3));
                            currentSign.setLine(3, replaceGameVariable(line4));
                            currentSign.update();
                        }
                    }
                }
            }
        }
    }

    private String replaceGameVariable(String message) {
        if(message == null) message = "";
        if(message.contains("%arena%")) message = message.replace("%arena%",name);
        if(message.contains("%gameStatus%")) message = message.replace("%gameStatus%",gameStatus.getStatus());
        if(message.contains("%on%")) message = message.replace("%on%",players.size() + "");
        if(message.contains("%max%")) message = message.replace("%max%", max + "");
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    private Location getLocation(String location) {
        if(location == null) {
            location = "notSet";
        }
        return utils.getLocationFromString(location);
    }

    private int getSlot(ItemStack item) {
        return plugin.getItemsInfo().getBeastInventory().get(item);
    }
    @Override
    public void giveBeastInv(Player beast){
        for(ItemStack inventory : plugin.getItemsInfo().getBeastInventory().keySet()) {
            beast.getInventory().setItem(getSlot(inventory),inventory);
        }
        beast.getInventory().setHelmet(plugin.getItemsInfo().getBeastHelmet());
        beast.getInventory().setChestplate(plugin.getItemsInfo().getBeastChestplate());
        beast.getInventory().setLeggings(plugin.getItemsInfo().getBeastLeggings());
        beast.getInventory().setBoots(plugin.getItemsInfo().getBeastBoots());
    }










    @Override
    public void setName(String name) { this.name = name;}

    @Override
    public void setGameType(GameType gameType) { this.gameType = gameType; }

    @Override
    public void setGameStatus(GameStatus gameStatus) { this.gameStatus = gameStatus; }

    @Override
    public void setLastTimer(int time) { lastTimer = time; }

    @Override
    public void setMenuSlot(int slot) { menuSlot = slot; }

    @Override
    public String getConfigName() { return configName; }

    @Override
    public String getName() { return name; }

    @Override
    public ArrayList<Player> getPlayers() { return players; }

    @Override
    public ArrayList<Player> getRunners() { return runners; }

    @Override
    public ArrayList<Player> getBeasts() { return beasts; }

    @Override
    public ArrayList<Player> getKillers() { return killers; }

    @Override
    public ArrayList<Player> getSpectators() { return spectators; }

    @Override
    public ArrayList<Location> getSigns() { return signs; }

    @Override
    public ArrayList<String> getChestTypes() { return chestTypes; }

    @Override
    public GameType getType() { return gameType; }

    @Override
    public GameStatus getStatus() { return gameStatus; }

    @Override
    public Location getWaiting() { return waiting; }

    @Override
    public Location getSelecting() { return selecting; }

    @Override
    public Location getBeastSpawn() { return beastSpawn; }

    @Override
    public Location getRunnerSpawn() { return runnerSpawn; }

    @Override
    public Location getKillerSpawn() { return killerSpawn; }

    @Override
    public Random getRandom() { return random; }

    @Override
    public HashMap<String,List<Location>> getChestLocations() { return chestLocations; }

    @Override
    public boolean isInvincible() { return invincible; }

    @Override
    public int getMax() { return max; }

    @Override
    public int getMin() { return min; }

    @Override
    public int getLastTimer() { return lastTimer; }

    @Override
    public int getGameMaxTime() { return gameMaxTime; }

    @Override
    public int getWorldTime() { return worldTime; }






}
