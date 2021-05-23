package dev.mruniverse.guardianrftb.multiarena;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.ExternalLogger;
import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianlib.core.utils.xseries.XMaterial;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.game.GameManager;
import dev.mruniverse.guardianrftb.multiarena.kits.KitInfo;
import dev.mruniverse.guardianrftb.multiarena.kits.KitLoader;
import dev.mruniverse.guardianrftb.multiarena.listeners.ListenerController;
import dev.mruniverse.guardianrftb.multiarena.runnables.PlayerRunnable;
import dev.mruniverse.guardianrftb.multiarena.runnables.TitleRunnable;
import dev.mruniverse.guardianrftb.multiarena.scoreboard.BoardController;
import dev.mruniverse.guardianrftb.multiarena.storage.DataStorage;
import dev.mruniverse.guardianrftb.multiarena.storage.FileStorage;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import dev.mruniverse.guardianrftb.multiarena.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public final class GuardianRFTB extends JavaPlugin {
    private final HashMap<UUID, PlayerManager> guardianPlayers = new HashMap<>();
    private GuardianUtils guardianUtils;

    public static GuardianRFTB instance;
    public static GuardianRFTB getInstance() { return instance; }

    private ListenerController listenerController;

    private boolean hasPAPI = false;

    private FileStorage fileStorage;
    private ExternalLogger logger;
    private ItemsInfo itemsInfo;
    private settingsInfo sInfo;
    private GameManager gameManager;
    private BoardController boardController;
    private PlayerRunnable runnable;
    private KitLoader kitLoader;
    private TitleRunnable titleRunnable;
    private SoundsInfo soundsInfo;
    private GuardianPlaceholders guardianPlaceholders;
    private DataStorage dataStorage;

    public ExternalLogger getLogs() { return logger; }
    public ListenerController getListener() { return listenerController; }
    public ItemsInfo getItemsInfo() { return itemsInfo; }
    public FileStorage getStorage() { return fileStorage; }
    public GuardianLIB getLib() { return GuardianLIB.getControl(); }
    public settingsInfo getSettings() { return sInfo;}
    public GameManager getGameManager() { return gameManager; }
    public GuardianUtils getUtils() { return guardianUtils; }
    public BoardController getScoreboards() { return boardController; }
    public PlayerRunnable getRunnable() { return runnable; }
    public TitleRunnable getTitleRunnable() { return titleRunnable; }
    public SoundsInfo getSoundsInfo() { return soundsInfo; }
    public DataStorage getData() { return dataStorage; }
    public boolean hasPAPI() { return hasPAPI; }


    public void addPlayer(Player player){
        if(!existPlayer(player)) {
            guardianPlayers.put(player.getUniqueId(),new PlayerManager(this,player));
        }
    }
    public boolean existPlayer(Player player) { return guardianPlayers.containsKey(player.getUniqueId()); }
    public void removePlayer(Player player) { guardianPlayers.remove(player.getUniqueId()); }
    public HashMap<UUID, PlayerManager> getRigoxPlayers() { return guardianPlayers; }
    public PlayerManager getPlayerData(UUID uuid) { return guardianPlayers.get(uuid); }
    public KitLoader getKitLoader() { return kitLoader; }
    public GuardianPlaceholders getGuardianPlaceholders() { return guardianPlaceholders; }

    @Override
    public void onDisable() {
        if(dataStorage == null) return;
        dataStorage.disableDatabase();
    }

    @Override
    public void onEnable() {
        instance = this;
        itemsInfo = new ItemsInfo();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                logger = new ExternalLogger(instance,"GuardianRFTB","dev.mruniverse.guardianrftb.multiarena");

                getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");

                fileStorage = new FileStorage(instance);
                fileStorage.save(SaveMode.ALL);
                sInfo = new settingsInfo(instance);

                hasPAPI = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
                listenerController = new ListenerController(instance);
                listenerController.loadListeners();
                boardController = new BoardController(instance);
                getListener().loadCommand("rftb");
                getListener().loadCommand("grftb");
                String lang = fileStorage.getControl(GuardianFiles.MESSAGES).getString("settings.language");
                if(lang == null) lang = "en";
                if (!lang.equalsIgnoreCase("en") && !lang.equalsIgnoreCase("default")) {
                    instance.fileStorage.setMessages(lang);
                    logger.info("Changed language to code: " + lang);
                }

                gameManager = new GameManager(instance);
                gameManager.loadChests();
                gameManager.loadGames();

                kitLoader = new KitLoader(instance);
                BukkitMetrics bukkitMetrics = new BukkitMetrics(instance, 11302);
                getLogs().debug(String.format("Spigot metrics has been enabled &7(%s)", bukkitMetrics.isEnabled()));

                loadLobbyItems();
                loadGameItems();
                loadBeastKit();
                loadRunnable();
                if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    guardianPlaceholders = new GuardianPlaceholders(instance);
                    guardianPlaceholders.register();
                }
                soundsInfo = new SoundsInfo(instance);
                dataStorage = new DataStorage(instance);
                dataStorage.loadDatabase();
                guardianUtils = new GuardianUtils(instance);

                if (getStorage().getControl(GuardianFiles.SETTINGS).getBoolean("settings.update-check")) {
                    Updater updater = new Updater(instance, 88817);
                    String updaterResult = updater.getUpdateResult();
                    switch (updaterResult.toUpperCase()) {
                        case "UPDATED":
                            getLogs().info("&aYou're using latest version of GuardianRFTB, You're Awesome!");
                            break;
                        case "NEW_VERSION":
                            getLogs().info("&aA new update is available: &bhttps://www.spigotmc.org/resources/88817/");
                            break;
                        case "BETA_VERSION":
                            getLogs().info("&aYou are Running a Pre-Release version, please report bugs ;)");
                            break;
                        case "RED_PROBLEM":
                            getLogs().info("&aGuardianRFTB can't connect to WiFi to check plugin version.");
                            break;
                        case "ALPHA_VERSION":
                            getLogs().info("&bYou are Running a &aAlpha version&b, it is normal to find several errors, please report these errors so that they can be solved.");
                            break;
                        case "PRE_ALPHA_VERSION":
                            getLogs().info("&cYou are Running a &aPre Alpha version&c, it is normal to find several errors, please report these errors so that they can be solved. &eWARNING: &cI (MrUniverse) recommend a Stable version, PreAlpha aren't stable versions!");
                            break;
                        default:
                            break;
                    }
                }

            }
        };
        runnable.runTaskLater(this, 1L);
    }

    public void loadGameItems() {
        FileConfiguration items = getStorage().getControl(GuardianFiles.ITEMS);
        ConfigurationSection section;
        try {
            Utils utils = instance.getLib().getUtils();
            for(GameItems currentItem : GameItems.values()) {
                String path = currentItem.getItemPath();
                ItemStack currentIStack;
                if (items.get(path + "enchantments") == null) {
                    currentIStack = getItemWithData(
                            items.getString(path + "item"),
                            items.getString(path + "name"),
                            items.getStringList(path + "lore")
                    );
                } else {
                    currentIStack = getItemWithData(
                            items.getString(path + "item"),
                            items.getString(path + "name"),
                            items.getStringList(path + "lore"),
                            items.getStringList(path + "enchantments")
                    );
                }
                itemsInfo.getCurrentItem().put(currentIStack,currentItem.getItemFunction());
                currentItem.set(this,currentIStack);
                currentItem.slot(this,items.getInt(path + "slot"));
            }
        } catch (Throwable throwable) {
            getLogs().error("Can't get game items on startup");
            getLogs().error(throwable);
        }
    }

    public void loadLobbyItems() {
        FileConfiguration items = getStorage().getControl(GuardianFiles.ITEMS);
        ConfigurationSection section;
        try {
            Utils utils = getLib().getUtils();
            section = items.getConfigurationSection("lobby");
            if (section == null) throw new Throwable("Can't found beast inventory section in items.yml");
            for (String lItems : section.getKeys(false)) {
                String path = "lobby." + lItems + ".";
                if (items.getBoolean(path + "toggle")) {
                    ItemStack currentItem = getItemWithData(
                            items.getString(path + "item"),
                            items.getString(path + "name"),
                            items.getStringList(path + "lore")
                    );
                    itemsInfo.getLobbyItems().put(currentItem,items.getInt(path + "slot"));
                    itemsInfo.getCurrentItem().put(currentItem,getCurrent(lItems));
                }
            }
        } catch (Throwable throwable) {
            getLogs().error("Can't get lobby items on startup");
            getLogs().error(throwable);
        }
    }

    public void loadBeastKit() {
        try {
            FileConfiguration items = getStorage().getControl(GuardianFiles.ITEMS);
            for (String beastDefaultInv : getStorage().getContent(GuardianFiles.ITEMS,"playing.beast-inventory",false)) {
                String path = "playing.beast-inventory." + beastDefaultInv + ".";
                if (items.get(path + "enchantments") == null) {
                    itemsInfo.getBeastInventory().put(getItemWithData(
                            items.getString(path + "item"),
                            items.getString(path + "name"),
                            items.getStringList(path + "lore")
                    ),items.getInt(path + "slot"));
                } else {
                    itemsInfo.getBeastInventory().put(getItemWithData(
                            items.getString(path + "item"),
                            items.getString(path + "name"),
                            items.getStringList(path + "lore"),
                            items.getStringList(path + "enchantments")
                    ),items.getInt(path + "slot"));
                }


            }
            if (items.get("playing.beast-armor.Helmet.enchantments") == null) {
                itemsInfo.setBeastChestplate(getItemWithData(
                        items.getString("playing.beast-armor.Helmet.item"),
                        items.getString("playing.beast-armor.Helmet.name"),
                        items.getStringList("playing.beast-armor.Helmet.lore")
                ));
            } else {
                itemsInfo.setBeastChestplate(getItemWithData(
                        items.getString("playing.beast-armor.Helmet.item"),
                        items.getString("playing.beast-armor.Helmet.name"),
                        items.getStringList("playing.beast-armor.Helmet.lore"),
                        items.getStringList("playing.beast-armor.Helmet.enchantments")
                ));
            }
            if (items.get("playing.beast-armor.Chestplate.enchantments") == null) {
                itemsInfo.setBeastChestplate(getItemWithData(
                        items.getString("playing.beast-armor.Chestplate.item"),
                        items.getString("playing.beast-armor.Chestplate.name"),
                        items.getStringList("playing.beast-armor.Chestplate.lore")
                ));
            } else {
                itemsInfo.setBeastChestplate(getItemWithData(
                        items.getString("playing.beast-armor.Chestplate.item"),
                        items.getString("playing.beast-armor.Chestplate.name"),
                        items.getStringList("playing.beast-armor.Chestplate.lore"),
                        items.getStringList("playing.beast-armor.Chestplate.enchantments")
                ));
            }
            if (items.get("playing.beast-armor.Leggings.enchantments") == null) {
                itemsInfo.setBeastLeggings(getItemWithData(
                        items.getString("playing.beast-armor.Leggings.item"),
                        items.getString("playing.beast-armor.Leggings.name"),
                        items.getStringList("playing.beast-armor.Leggings.lore")
                ));
            } else {
                itemsInfo.setBeastLeggings(getItemWithData(
                        items.getString("playing.beast-armor.Leggings.item"),
                        items.getString("playing.beast-armor.Leggings.name"),
                        items.getStringList("playing.beast-armor.Leggings.lore"),
                        items.getStringList("playing.beast-armor.Leggings.enchantments")
                ));
            }
            if (items.get("playing.beast-armor.Boots.enchantments") == null) {
                itemsInfo.setBeastBoots(getItemWithData(
                        items.getString("playing.beast-armor.Boots.item"),
                        items.getString("playing.beast-armor.Boots.name"),
                        items.getStringList("playing.beast-armor.Boots.lore")
                ));
            } else {
                itemsInfo.setBeastBoots(getItemWithData(
                        items.getString("playing.beast-armor.Boots.item"),
                        items.getString("playing.beast-armor.Boots.name"),
                        items.getStringList("playing.beast-armor.Boots.lore"),
                        items.getStringList("playing.beast-armor.Boots.enchantments")
                ));
            }
        } catch (Throwable throwable) {
            getLogs().error("Can't load beast Default Armor");
        }
    }

    private ItemStack getItemWithData(@Nullable String material,@Nullable String name, List<String> lore) {
        Utils utils = getLib().getUtils();
        if(material == null) material = "BEDROCK";
        if(name == null) name = "INVALID_NAME";
        Optional<XMaterial> optional = XMaterial.matchXMaterial(material);
        if(optional.isPresent()) {
            return utils.getItem(optional.get(), name, lore);
        }
        getLogs().error("Item " + material + " doesn't exists!");
        return null;
    }

    private ItemStack getItemWithData(@Nullable String material,@Nullable String name,List<String> lore,List<String> enchantments) {
        Utils utils = getLib().getUtils();
        if(material == null) material = "BEDROCK";
        if(name == null) name = "INVALID_NAME";
        Optional<XMaterial> optional = XMaterial.matchXMaterial(material);
        if(optional.isPresent()) {
            ItemStack currentItem = utils.getItem(optional.get(), name, lore);
            return utils.getEnchantmentList(currentItem,enchantments,"none");
        }
        getLogs().error("Item " + material + " doesn't exists!");
        return null;
    }

    public ItemFunction getCurrent(String path) {
        if(path.equalsIgnoreCase("gameSelector")) {
            return ItemFunction.GAME_SELECTOR;
        }
        if(path.equalsIgnoreCase("Shop")) {
            return ItemFunction.SHOP;
        }
        if(path.equalsIgnoreCase("PlayerSettings")) {
            return ItemFunction.PLAYER_SETTINGS;
        }
        if(path.equalsIgnoreCase("LobbySelector")) {
            return ItemFunction.LOBBY_SELECTOR;
        }
        return ItemFunction.EXIT_LOBBY;
    }
    @SuppressWarnings("deprecation")
    public void loadRunnable() {
        runnable = new PlayerRunnable(this);
        if (getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.animatedTitle.toggle")) {
            titleRunnable = new TitleRunnable(instance);
            getServer().getScheduler().runTaskTimerAsynchronously(instance, instance.titleRunnable, 0L, getStorage().getControl(GuardianFiles.SCOREBOARD).getLong("scoreboards.animatedTitle.repeatTime"));
        }
        getServer().getScheduler().runTaskTimerAsynchronously(instance, instance.runnable, 0L, 20L);
    }


    public void getItems(GameEquip gameEquipment, Player player) {
        switch (gameEquipment) {
            case BEAST_KIT:
                String kitID = getPlayerData(player.getUniqueId()).getSelectedKit();
                if(kitID.equalsIgnoreCase("NONE")) {
                    for(Map.Entry<ItemStack,Integer> data : itemsInfo.getBeastInventory().entrySet()) {
                        player.getInventory().setItem(data.getValue(),data.getKey());
                    }
                    player.getInventory().setHelmet(itemsInfo.getBeastHelmet());
                    player.getInventory().setChestplate(itemsInfo.getBeastChestplate());
                    player.getInventory().setLeggings(itemsInfo.getBeastLeggings());
                    player.getInventory().setBoots(itemsInfo.getBeastBoots());
                    return;
                }
                KitInfo kitInfo = getKitLoader().getKitsUsingID(KitType.BEAST).get(kitID);
                if(kitInfo == null) {
                    for(Map.Entry<ItemStack,Integer> data : itemsInfo.getBeastInventory().entrySet()) {
                        player.getInventory().setItem(data.getValue(),data.getKey());
                    }
                    player.getInventory().setHelmet(itemsInfo.getBeastHelmet());
                    player.getInventory().setChestplate(itemsInfo.getBeastChestplate());
                    player.getInventory().setLeggings(itemsInfo.getBeastLeggings());
                    player.getInventory().setBoots(itemsInfo.getBeastBoots());
                    return;
                }
                for(Map.Entry<ItemStack,Integer> data : kitInfo.getInventoryItems().entrySet()) {
                    player.getInventory().setItem(data.getValue(),data.getKey());
                }
                if(kitInfo.getArmor(GuardianArmor.HELMET) != null) player.getInventory().setHelmet(kitInfo.getArmor(GuardianArmor.HELMET));
                if(kitInfo.getArmor(GuardianArmor.CHESTPLATE) != null) player.getInventory().setChestplate(kitInfo.getArmor(GuardianArmor.CHESTPLATE));
                if(kitInfo.getArmor(GuardianArmor.LEGGINGS) != null) player.getInventory().setLeggings(kitInfo.getArmor(GuardianArmor.LEGGINGS));
                if(kitInfo.getArmor(GuardianArmor.BOOTS) != null) player.getInventory().setBoots(kitInfo.getArmor(GuardianArmor.BOOTS));
                return;
            case KILLER_KIT:
                String killerID = getPlayerData(player.getUniqueId()).getSelectedKit();
                if(killerID.equalsIgnoreCase("NONE")) return;
                KitInfo killerInfo = getKitLoader().getKitsUsingID(KitType.KILLER).get(killerID);
                if(killerInfo == null) return;
                for(Map.Entry<ItemStack,Integer> data : killerInfo.getInventoryItems().entrySet()) {
                    player.getInventory().setItem(data.getValue(),data.getKey());
                }
                if(killerInfo.getArmor(GuardianArmor.HELMET) != null) player.getInventory().setHelmet(killerInfo.getArmor(GuardianArmor.HELMET));
                if(killerInfo.getArmor(GuardianArmor.CHESTPLATE) != null) player.getInventory().setChestplate(killerInfo.getArmor(GuardianArmor.CHESTPLATE));
                if(killerInfo.getArmor(GuardianArmor.LEGGINGS) != null) player.getInventory().setLeggings(killerInfo.getArmor(GuardianArmor.LEGGINGS));
                if(killerInfo.getArmor(GuardianArmor.BOOTS) != null) player.getInventory().setBoots(killerInfo.getArmor(GuardianArmor.BOOTS));
                return;
            case RUNNER_KIT:
            default:
                String runnerID = getPlayerData(player.getUniqueId()).getSelectedKit();
                if(runnerID.equalsIgnoreCase("NONE")) return;
                KitInfo runnerInfo = getKitLoader().getKitsUsingID(KitType.RUNNER).get(runnerID);
                if(runnerInfo == null) return;
                for(Map.Entry<ItemStack,Integer> data : runnerInfo.getInventoryItems().entrySet()) {
                    player.getInventory().setItem(data.getValue(),data.getKey());
                }
                if(runnerInfo.getArmor(GuardianArmor.HELMET) != null) player.getInventory().setHelmet(runnerInfo.getArmor(GuardianArmor.HELMET));
                if(runnerInfo.getArmor(GuardianArmor.CHESTPLATE) != null) player.getInventory().setChestplate(runnerInfo.getArmor(GuardianArmor.CHESTPLATE));
                if(runnerInfo.getArmor(GuardianArmor.LEGGINGS) != null) player.getInventory().setLeggings(runnerInfo.getArmor(GuardianArmor.LEGGINGS));
                if(runnerInfo.getArmor(GuardianArmor.BOOTS) != null) player.getInventory().setBoots(runnerInfo.getArmor(GuardianArmor.BOOTS));
        }
    }

}
