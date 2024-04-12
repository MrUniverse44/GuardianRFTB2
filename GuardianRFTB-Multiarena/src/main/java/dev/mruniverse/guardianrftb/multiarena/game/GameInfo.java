package dev.mruniverse.guardianrftb.multiarena.game;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianlib.core.utils.xseries.XMaterial;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.listeners.api.*;
import dev.mruniverse.guardianrftb.multiarena.runnables.*;
import dev.mruniverse.guardianrftb.multiarena.scoreboard.PluginScoreboard;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import dev.mruniverse.guardianrftb.multiarena.utils.PlayerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings({"unused", "deprecation"})
public class GameInfo implements Game {

    private final GuardianRFTB plugin;
    private final Utils utils = GuardianLIB.getControl().getUtils();

    private final Random random = new Random();

    private final ArrayList<UUID> players = new ArrayList<>();
    private final ArrayList<UUID> runners = new ArrayList<>();
    private final ArrayList<UUID> spectators = new ArrayList<>();
    private final ArrayList<UUID> beasts = new ArrayList<>();
    private final ArrayList<UUID> killers = new ArrayList<>();

    private final ArrayList<String> chestTypes = new ArrayList<>();

    private final ArrayList<Location> signs = new ArrayList<>();

    private final HashMap<String,List<Location>> chestLocations = new HashMap<>();

    private final HashMap<String,Boolean> chestLimitVerifier = new HashMap<>();

    private final HashMap<String,Integer> chestLimitViewer = new HashMap<>();

    private final HashMap<String,Integer> chestLimitDeadValues = new HashMap<>();

    private final HashMap<String,List<Player>> chestLimitUsers = new HashMap<>();

    private final HashMap<String,Integer> chestLimitValues = new HashMap<>();

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
            updateSignsBlocks();
            if(runnerSpawn != null) {
                World gameWorld = runnerSpawn.getWorld();
                if(gameWorld != null) {
                    gameWorld.setTime(worldTime);
                    gameWorld.setDifficulty(Difficulty.NORMAL);
                    gameWorld.setSpawnFlags(false, false);
                }
            }
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
    public void unload() {
        players.clear();
        beasts.clear();
        runners.clear();
        spectators.clear();
        killers.clear();
        chestLocations.clear();
        this.gameStatus = GameStatus.PREPARING;
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
            updateSignsBlocks();

            for (UUID uniqueId : players) {

                GamePlayer gamePlayer = plugin.getGamePlayer(uniqueId);

                if (gamePlayer == null) {
                    continue;
                }

                gamePlayer.setScoreboard(PluginScoreboard.STARTING);
            }
        }
        if(players.size() < RealMin) {
            return (RealMin - players.size());
        }
        return 0;
    }
    @Override
    public void addChestLimit(String chestName,Player player) {
        int value = chestLimitViewer.get(chestName);
        List<Player> players = chestLimitUsers.get(chestName);
        if(!players.contains(player)) {
            players.add(player);
            chestLimitUsers.put(chestName, players);
            chestLimitViewer.put(chestName, value + 1);
        }
    }

    @Override
    public boolean isChestLimitParsed(String chestName) {
        chestLimitViewer.putIfAbsent(chestName, 0);
        chestLimitValues.putIfAbsent(chestName, 10);
        int current = chestLimitViewer.get(chestName);
        int max = chestLimitValues.get(chestName);
        if(chestLimitVerifier.get(chestName)) {
            return (current >= max);
        }
        return chestLimitVerifier.get(chestName);
    }

    @Override
    public void addDeadPlayerChest(String chest) {
        int value = chestLimitDeadValues.get(chest);
        int max = chestLimitValues.get(chest);
        chestLimitDeadValues.put(chest,value + 1);

        if(value >= max) {
            chestLimitViewer.put(chest,0);
            chestLimitDeadValues.put(chest,0);
            chestLimitUsers.put(chest,new ArrayList<>());
        }
    }

    @Override
    public void setChestLimiter(String chest,String limit) {
        if(limit.equalsIgnoreCase("NONE")) {
            chestLimitValues.put(chest,0);
            chestLimitVerifier.put(chest,false);
            return;
        }
        chestLimitValues.put(chest,Integer.parseInt(limit));
        chestLimitVerifier.put(chest,true);
    }

    @Override
    public boolean isChestLimited(String chestName) {
        return chestLimitVerifier.get(chestName);
    }

    public void loadChestLimiter() {
        try {
            FileConfiguration gameFile = plugin.getStorage().getControl(GuardianFiles.GAMES);
            if(gameFile.get(path + "chests") != null) {
                for(String chestType : gameFile.getStringList(path + "chests")) {
                    loadChestLimit(chestType);
                }
            }
        }catch (Throwable throwable){
            plugin.getLogs().error("Can't load game Chests Limiters");
            plugin.getLogs().error(throwable);
        }
    }

    @Override
    public boolean isChestOf(String chestName,Player player) {
        return chestLimitUsers.get(chestName).contains(player);
    }

    public void loadChestLimit(String chestName) {
        FileConfiguration gameFile = plugin.getStorage().getControl(GuardianFiles.GAMES);
        if(gameFile.get(path + "chest-limits." + chestName) != null) {
            String chestLimit = gameFile.getString(path + "chest-limits." + chestName,"NONE");
            if(chestLimit.equalsIgnoreCase("NONE")) {
                chestLimitValues.put(chestName, 0);
                chestLimitViewer.put(chestName, 0);
                chestLimitVerifier.put(chestName, false);
            } else {
                chestLimitValues.put(chestName, Integer.parseInt(chestLimit));
                chestLimitVerifier.put(chestName, true);
                chestLimitViewer.put(chestLimit,0);
            }
            chestLimitUsers.put(chestName,new ArrayList<>());
            chestLimitDeadValues.put(chestName,0);
        } else {
            chestLimitDeadValues.put(chestName,0);
            chestLimitUsers.put(chestName,new ArrayList<>());
            chestLimitViewer.put(chestName,0);
            chestLimitVerifier.put(chestName,false);
            chestLimitValues.put(chestName,0);
        }
    }

    @Override
    public void loadChestType(String chestName) {
        try {
            FileConfiguration gameFile = plugin.getStorage().getControl(GuardianFiles.GAMES);
            loadChestLimit(chestName);
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
        GamePlayer currentData = plugin.getGamePlayer(player.getUniqueId());

        if (currentData == null) {
            return;
        }

        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        String prefix = messages.getString("messages.prefix");
        if (currentData.getGame() != null) {
            plugin.getUtils().sendMessage(player, prefix + messages.getString("messages.others.already"));
            return;
        }
        if (!gameStatus.equals(GameStatus.WAITING) && !gameStatus.equals(GameStatus.SELECTING) && !gameStatus.equals(GameStatus.STARTING)) {
            if (!gameStatus.equals(GameStatus.RESTARTING)) {
                if(!plugin.getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.game.allow-external-spectators")) {
                    plugin.getUtils().sendMessage(player, prefix + messages.getString("messages.others.gamePlaying"));
                    return;
                }
                Chunk lobbyChunk = plugin.getSettings().getLocation().getChunk();
                if(!lobbyChunk.isLoaded()){
                    lobbyChunk.load();
                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            spectatorJoin(player);
                        }
                    };
                    runnable.runTaskLaterAsynchronously(plugin,20L);
                    return;
                }
                spectatorJoin(player);
                return;
            }
            plugin.getUtils().sendMessage(player, prefix + messages.getString("messages.others.restarting"));
            return;
        }
        if (players.size() >= max) {
            plugin.getUtils().sendMessage(player, prefix + messages.getString("messages.others.full"));
            return;
        }
        Chunk lobbyChunk = plugin.getSettings().getLocation().getChunk();
        if(!lobbyChunk.isLoaded()){
            lobbyChunk.load();
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    fastJoin(player);
                }
            };
            runnable.runTaskLaterAsynchronously(plugin,20L);
            return;
        }
        fastJoin(player);
    }

    private void spectatorJoin(Player player) {
        GamePlayer currentData = plugin.getGamePlayer(player.getUniqueId());

        if (currentData == null) {
            return;
        }

        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        String prefix = messages.getString("messages.prefix");
        String nowSpectating = messages.getString("messages.spectating");
        player.getInventory().clear();
        if(!plugin.getSettings().isSecondSpectator()) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            for (Player player1 : getPlayers(getPlayers())) {
                if (player != player1) {
                    player1.hidePlayer(player);
                }
            }
            player.setGameMode(GameMode.ADVENTURE);
            for(SpectatorItems items : SpectatorItems.values()) {
                items.giveItem(player,plugin);
            }
        }
        player.teleport(this.runnerSpawn);
        currentData.setScoreboard(PluginScoreboard.PLAYING);
        players.add(player.getUniqueId());
        spectators.add(player.getUniqueId());
        currentData.setGame(this);
        currentData.setLastCheckpoint(null);
        player.setAllowFlight(true);
        player.setFlying(true);
        if (currentData.getLeaveDelay() != 0) {
            plugin.getServer().getScheduler().cancelTask(currentData.getLeaveDelay());
            currentData.setLeaveDelay(0);
        }
        if(!plugin.getSettings().isSecondSpectator()) {
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            for(Player player1 : getPlayers(getPlayers())) {
                if (player != player1) {
                    player1.hidePlayer(player);
                }
            }
            for(SpectatorItems items : SpectatorItems.values()) {
                items.giveItem(player,plugin);
            }
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
        }
        player.setAllowFlight(true);
        player.setFlying(true);
        GameJoinEvent event = new GameJoinEvent(this,player);
        Bukkit.getPluginManager().callEvent(event);
        player.setHealth(20.0D);
        player.setFireTicks(0);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.updateInventory();
    }

    private void fastJoin(Player player) {
        GamePlayer currentData = plugin.getGamePlayer(player.getUniqueId());

        if (currentData == null) {
            return;
        }

        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        String prefix = messages.getString("messages.prefix");
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.teleport(this.waiting);
        currentData.setScoreboard(PluginScoreboard.WAITING);
        if (gameStatus.equals(GameStatus.SELECTING)) {
            currentData.setScoreboard(PluginScoreboard.SELECTING);
        } else if(!gameStatus.equals(GameStatus.WAITING)) {
            currentData.setScoreboard(PluginScoreboard.STARTING);
        }
        players.add(player.getUniqueId());
        runners.add(player.getUniqueId());
        currentData.setGame(this);
        currentData.setLastCheckpoint(null);
        if (currentData.getLeaveDelay() != 0) {
            plugin.getServer().getScheduler().cancelTask(currentData.getLeaveDelay());
            currentData.setLeaveDelay(0);
        }
        if (gameStatus.equals(GameStatus.WAITING)) checkPlayers();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setAllowFlight(false);
        GameJoinEvent event = new GameJoinEvent(this,player);
        plugin.getServer().getPluginManager().callEvent(event);
        player.setHealth(20.0D);
        player.setFireTicks(0);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        if(plugin.getItemsInfo().getKitRunnerStatus()) player.getInventory().setItem(plugin.getItemsInfo().getRunnerSlot(), plugin.getItemsInfo().getKitRunner());
        if(plugin.getItemsInfo().getExitStatus()) player.getInventory().setItem(plugin.getItemsInfo().getExitSlot(), plugin.getItemsInfo().getExit());
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        String joinMsg = messages.getString("messages.game.join-game");
        if (joinMsg == null) joinMsg = "&b%player% &7has joined the game! &3(&b%game_online%&3/&b%game_max%&3)&7!";
        updateSigns();
        player.updateInventory();

        //TODO: Show and hide players
    }

    @Override
    public void leaveWithoutSending(Player player) {
        this.players.remove(player.getUniqueId());
        this.runners.remove(player.getUniqueId());
        this.beasts.remove(player.getUniqueId());
        checkDeadChests(player);
        this.spectators.remove(player.getUniqueId());
        if(player.isOnline()) {
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            GamePlayer currentData = plugin.getGamePlayer(player);
            currentData.setGame(null);
            currentData.setScoreboard(PluginScoreboard.LOBBY);
            currentData.setLastCheckpoint(null);
        }
        GameQuitEvent event = new GameQuitEvent(this,player);

        plugin.getServer().getPluginManager().callEvent(event);

        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);

        String quitMsg;

        if(!gameStatus.equals(GameStatus.IN_GAME) && !gameStatus.equals(GameStatus.PLAYING) && !gameStatus.equals(GameStatus.RESTARTING)) {
            quitMsg = messages.getString("messages.game.leave-game");
        } else {
            quitMsg = messages.getString("messages.game.leave-game-in-game");
        }

        if(quitMsg == null) {
            quitMsg = "&b%player% &7has left the game! &3(&b%game_online%&3/&b%game_max%&3)&7!";
        }

        for (Player gamePlayers : getPlayers(this.players)) {
            plugin.getUtils().sendMessage(gamePlayers, quitMsg.replace("%player%",player.getName())
                .replace("%game_online%",this.players.size()+"")
                .replace("%game_max%",this.max+""),player);
        }
        updateSigns();
    }
    public void checkDeadChests(Player player) {
        for(Map.Entry<String,List<Player>> entry : chestLimitUsers.entrySet()) {
            if(entry.getValue().contains(player)) {
                List<Player> entryValue = entry.getValue();
                entryValue.remove(player);
                chestLimitUsers.put(entry.getKey(),entryValue);
                addDeadPlayerChest(entry.getKey());
            }
        }
    }

    @Override
    public void leave(Player player) {
        Chunk lobbyChunk = plugin.getSettings().getLocation().getChunk();
        if(!lobbyChunk.isLoaded()){
            lobbyChunk.load();
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    fastLeave(player);
                }
            };
            runnable.runTaskLaterAsynchronously(plugin,20L);
            return;
        }
        fastLeave(player);
    }

    private List<Player> getPlayers(Collection<UUID> players) {
        return PlayerUtil.getPlayers(plugin, players);
    }

    private void fastLeave(Player player) {
        this.players.remove(player.getUniqueId());
        this.runners.remove(player.getUniqueId());
        checkDeadChests(player);
        this.beasts.remove(player.getUniqueId());
        this.spectators.remove(player.getUniqueId());
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
        GameQuitEvent event = new GameQuitEvent(this,player);
        plugin.getServer().getPluginManager().callEvent(event);
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        String quitMsg;
        if(!gameStatus.equals(GameStatus.IN_GAME) && !gameStatus.equals(GameStatus.PLAYING) && !gameStatus.equals(GameStatus.RESTARTING)) {
            quitMsg = messages.getString("messages.game.leave-game");
        } else {
            quitMsg = messages.getString("messages.game.leave-game-in-game");
        }
        if(quitMsg == null) quitMsg = "&b%player% &7has left the game! &3(&b%game_online%&3/&b%game_max%&3)&7!";
        for(Player pl : getPlayers(this.players)) {
            plugin.getUtils().sendMessage(pl,quitMsg.replace("%player%",player.getName())
                    .replace("%game_online%",this.players.size()+"")
                    .replace("%game_max%",this.max+""),player);
        }
        if(player.isOnline()) {
            GamePlayer currentData = plugin.getGamePlayer(player);
            currentData.setGame(null);
            currentData.setScoreboard(PluginScoreboard.LOBBY);
            currentData.setLastCheckpoint(null);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getInventory().clear();
            for (ItemStack item : plugin.getItemsInfo().getLobbyItems().keySet()) {
                player.getInventory().setItem(plugin.getItemsInfo().getSlot(item), item);
            }
            player.updateInventory();
        }
        updateSigns();
        updateSignsBlocks();
    }

    @Override
    public void checkPlayers() {
        if (players.size() == min && !gameStatus.equals(GameStatus.STARTING) && !gameStatus.equals(GameStatus.SELECTING) && !doubleCountPrevent) {
            gameStatus = GameStatus.SELECTING;
            updateSignsBlocks();
            SelectingRunnable select = new SelectingRunnable(plugin,this);
            lastListener = select.runTaskTimer(plugin,0L,20L).getTaskId();
            lastTimer = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getInt("settings.game.start-countdown",30);
            doubleCountPrevent = true;
            for (Player gamePlayers : getPlayers(this.players)) {
                plugin.getGamePlayer(gamePlayers).setScoreboard(PluginScoreboard.SELECTING);
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
        updateSignsBlocks();
        this.lastTimer = 10;
        StartRunnable select = new StartRunnable(plugin,this);
        lastListener = select.runTaskTimer(plugin,0L,20L).getTaskId();
        for (Player player : getPlayers(players)) {
            plugin.getGamePlayer(player).setScoreboard(PluginScoreboard.STARTING);
        }
    }

    @Override
    public void beastCount() {
        this.gameStatus = GameStatus.PLAYING;
        updateSignsBlocks();
        this.lastTimer = 15;
        for (Player player : getPlayers(runners)) {
            plugin.getUser(player.getUniqueId()).setScoreboard(PluginScoreboard.PLAYING);
        }
        for (Player player : getPlayers(beasts)) {
            plugin.getUser(player.getUniqueId()).setScoreboard(PluginScoreboard.BEAST_SPAWN);
        }
        BeastSpawnRunnable select = new BeastSpawnRunnable(plugin,this);
        lastListener = select.runTaskTimer(plugin,0L,20L).getTaskId();
    }

    @Override
    public void playCount() {
        this.gameStatus = GameStatus.IN_GAME;
        updateSignsBlocks();
        this.lastTimer = getGameMaxTime();
        GameStartEvent event = new GameStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        PlayingRunnable select = new PlayingRunnable(this);
        lastListener = select.runTaskTimer(plugin,0L,20L).getTaskId();
    }

    @Override
    public void setDoubleCountPrevent(boolean toggle) { doubleCountPrevent = toggle; }

    @Override
    public void setWinner(GameTeam gameTeam) {
        plugin.getServer().getScheduler().cancelTask(lastListener);
        this.gameStatus = GameStatus.RESTARTING;
        updateSignsBlocks();
        GameEndEvent event = new GameEndEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        EndingRunnable select = new EndingRunnable(plugin,this,gameTeam);
        lastListener = select.runTaskTimer(plugin,0L,20L).getTaskId();
    }

    @Override
    public void firework(Player player,boolean firework) {
        if(!firework) return;
        Firework fa = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fam = fa.getFireworkMeta();
        int fType = getRandom().nextInt(5) + 1;
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
        for (Player player : getPlayers(this.players)) {
            if (this.spectators.contains(player.getUniqueId())) {
                if (!plugin.getSettings().isSecondSpectator()) {
                    player.setGameMode(GameMode.SPECTATOR);
                } else {
                    for (Player player1 : getPlayers(getPlayers())) {
                        if (player != player1) {
                            player1.hidePlayer(player);
                        }
                    }
                    for (SpectatorItems items : SpectatorItems.values()) {
                        items.giveItem(player,plugin);
                    }
                    player.setGameMode(GameMode.ADVENTURE);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                }
            }
            plugin.getUtils().sendGameList(player, messages.getStringList("messages.inGame.infoList.endInfo"),GameTeam.RUNNERS);
        }
        for (Player runner : getPlayers(this.runners)) {
            plugin.getGamePlayer(runner).setScoreboard(PluginScoreboard.WIN_RUNNERS_FOR_RUNNERS);
            plugin.getGamePlayer(runner).getStatistics().addWins(1);
        }
        for (Player beast : getPlayers(this.beasts)) {
            plugin.getGamePlayer(beast).setScoreboard(PluginScoreboard.WIN_RUNNERS_FOR_BEAST);
        }
        for (Player spectator : getPlayers(this.spectators)) {
            plugin.getGamePlayer(spectator).setScoreboard(PluginScoreboard.WIN_RUNNERS_FOR_BEAST);
        }
        this.invincible = true;
        this.gameStatus = GameStatus.RESTARTING;
        updateSignsBlocks();
        setWinner(GameTeam.RUNNERS);
    }

    @Override
    public void winBeasts() {
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        for (Player player : getPlayers(this.players)) {
            plugin.getUtils().sendGameList(player, messages.getStringList("messages.inGame.infoList.endInfo"),GameTeam.BEASTS);
        }
        for (Player beast : getPlayers(this.beasts)) {
            plugin.getGamePlayer(beast).setScoreboard(PluginScoreboard.WIN_BEAST_FOR_BEAST);
            plugin.getGamePlayer(beast).getStatistics().addWins(1);
        }
        for (Player runner : getPlayers(this.runners)) {
            plugin.getGamePlayer(runner).setScoreboard(PluginScoreboard.WIN_BEAST_FOR_RUNNERS);
        }
        for (Player spectator : getPlayers(this.spectators)) {
            plugin.getGamePlayer(spectator).setScoreboard(PluginScoreboard.WIN_BEAST_FOR_RUNNERS);
        }
        this.invincible = true;
        this.gameStatus = GameStatus.RESTARTING;
        updateSignsBlocks();
        setWinner(GameTeam.BEASTS);
    }

    @Override
    public void restart() {
        this.players.clear();
        this.spectators.clear();
        this.beasts.clear();
        this.runners.clear();
        this.chestLimitUsers.clear();
        this.chestLimitDeadValues.clear();
        this.chestLimitViewer.clear();
        this.chestLimitVerifier.clear();
        this.beasts.clear();
        loadChestLimiter();
        this.lastTimer = 30;
        this.gameStatus = GameStatus.WAITING;
        doubleCountPrevent = false;
        updateSignsBlocks();
        loadStatus();
    }

    @Override
    public void deathBeast(Player beast) {
        beasts.remove(beast.getUniqueId());
        spectators.add(beast.getUniqueId());
        checkDeadChests(beast);
        plugin.getGamePlayer(beast).getStatistics().addDeaths(1);
        BeastDeathEvent event = new BeastDeathEvent(this, beast);
        plugin.getServer().getPluginManager().callEvent(event);
        if (!plugin.getSettings().isSecondSpectator()) {
            beast.setGameMode(GameMode.SPECTATOR);
        } else {
            for(Player player1 : getPlayers(getPlayers())) {
                if(beast != player1) player1.hidePlayer(beast);
            }
            for(SpectatorItems items : SpectatorItems.values()) {
                items.giveItem(beast, plugin);
            }
            beast.setGameMode(GameMode.ADVENTURE);
            beast.setAllowFlight(true);
            beast.setFlying(true);
        }
        if (beasts.isEmpty()) {
            plugin.getGamePlayer(beast).setScoreboard(PluginScoreboard.WIN_RUNNERS_FOR_BEAST);
            winRunners();
        }
    }

    @Override
    public void deathKiller(Player killer) {
        KillerDeathEvent event = new KillerDeathEvent(this,killer);
        plugin.getServer().getPluginManager().callEvent(event);
    }

    @Override
    public void deathRunner(Player runner) {
        runners.remove(runner.getUniqueId());
        checkDeadChests(runner);
        RunnerDeathEvent event = new RunnerDeathEvent(this,runner);
        Bukkit.getPluginManager().callEvent(event);
        if(!gameType.equals(GameType.INFECTED)) {
            spectators.add(runner.getUniqueId());
            if (!plugin.getSettings().isSecondSpectator()) {
                runner.setGameMode(GameMode.SPECTATOR);
            } else {
                for (Player player1 : getPlayers(getPlayers())) {
                    if (runner != player1) {
                        player1.hidePlayer(runner);
                    }
                }
                for (SpectatorItems items : SpectatorItems.values()) {
                    items.giveItem(runner,plugin);
                }
                runner.setGameMode(GameMode.ADVENTURE);
                runner.setAllowFlight(true);
                runner.setFlying(true);
            }
        } else {
            beasts.add(runner.getUniqueId());
            runner.getInventory().clear();
            giveBeastInv(runner);
            runner.setGameMode(GameMode.ADVENTURE);
            runner.teleport(beastSpawn);
        }
        plugin.getGamePlayer(runner).getStatistics().addDeaths(1);
        if (runners.isEmpty()) {
            plugin.getGamePlayer(runner).setScoreboard(PluginScoreboard.WIN_BEAST_FOR_RUNNERS);
            winBeasts();
        }
    }

    /*
     *
     * GAME SECOND THINGS
     *
     */
    @Override
    public void updateSignsBlocks() {
        Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(gameStatus.getBlock());
        if(optionalXMaterial.isPresent()) {
            XMaterial material = optionalXMaterial.get();
            for (Location signLocation : this.signs) {
                boolean worldLoaded = true;
                World signWorld = signLocation.getWorld();
                if (signWorld != null) {
                    if (Bukkit.getWorld(signWorld.getName()) == null) worldLoaded = false;
                    if (!worldLoaded || !signWorld.getChunkAt(signLocation).isLoaded()) {
                        signWorld.loadChunk(signWorld.getChunkAt(signLocation));
                    }
                    if (signLocation.getBlock().getState() instanceof Sign) {
                        Block signBlock = signLocation.getBlock();
                        Sign currentSign = (Sign) signBlock.getState();
                        org.bukkit.material.Sign signMaterial = (org.bukkit.material.Sign) signBlock.getState().getData();
                        Block block = signBlock.getRelative(signMaterial.getAttachedFace());
                        block.setType(material.parseMaterial());
                        if(plugin.isOldVersion()) {
                            MaterialData data = material.parseItem().getData();
                            if(data != null) plugin.getLib().getNMS().setBlockData(block,data.getData());
                            block.getState().update(true);
                        }
                    }
                }
            }


        }
    }
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
    public ArrayList<UUID> getPlayers() { return players; }

    @Override
    public ArrayList<UUID> getRunners() { return runners; }

    @Override
    public ArrayList<UUID> getBeasts() { return beasts; }

    @Override
    public ArrayList<UUID> getKillers() { return killers; }

    @Override
    public ArrayList<UUID> getSpectators() { return spectators; }

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
