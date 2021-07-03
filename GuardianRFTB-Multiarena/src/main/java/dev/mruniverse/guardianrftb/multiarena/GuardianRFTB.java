package dev.mruniverse.guardianrftb.multiarena;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.ExternalLogger;
import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianlib.core.utils.xseries.XMaterial;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.game.GameManager;
import dev.mruniverse.guardianrftb.multiarena.interfaces.DataStorage;
import dev.mruniverse.guardianrftb.multiarena.interfaces.FileStorage;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.kits.KitInfo;
import dev.mruniverse.guardianrftb.multiarena.kits.KitLoader;
import dev.mruniverse.guardianrftb.multiarena.listeners.ListenerController;
import dev.mruniverse.guardianrftb.multiarena.runnables.PlayerRunnable;
import dev.mruniverse.guardianrftb.multiarena.runnables.TitleRunnable;
import dev.mruniverse.guardianrftb.multiarena.scoreboard.BoardController;
import dev.mruniverse.guardianrftb.multiarena.storage.DataStorageImpl;
import dev.mruniverse.guardianrftb.multiarena.storage.FileStorageImpl;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManagerImpl;
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
    private final HashMap<UUID, PlayerManagerImpl> guardianPlayers = new HashMap<>();
    private GuardianUtils guardianUtils;

    public static GuardianRFTB instance;
    public static GuardianRFTB getInstance() { return instance; }

    private ListenerController listenerController;

    private boolean hasPAPI = false;

    private String serverVersion;

    private FileStorage fileStorageImpl;
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
    private DataStorage dataStorageImpl;

    public String getServerVersion() { return serverVersion; }
    public ExternalLogger getLogs() { return logger; }
    public ListenerController getListener() { return listenerController; }
    public ItemsInfo getItemsInfo() { return itemsInfo; }
    public FileStorage getStorage() { return fileStorageImpl; }
    public GuardianLIB getLib() { return GuardianLIB.getControl(); }
    public settingsInfo getSettings() { return sInfo;}
    public GameManager getGameManager() { return gameManager; }
    public GuardianUtils getUtils() { return guardianUtils; }
    public BoardController getScoreboards() { return boardController; }
    public PlayerRunnable getRunnable() { return runnable; }
    public TitleRunnable getTitleRunnable() { return titleRunnable; }
    public SoundsInfo getSoundsInfo() { return soundsInfo; }
    public DataStorage getData() { return dataStorageImpl; }
    public boolean hasPAPI() { return hasPAPI; }
    public boolean isOldVersion() { return (serverVersion.contains("v1_8") || serverVersion.contains("v1_9") || serverVersion.contains("v1_10") || serverVersion.contains("v1_11") || serverVersion.contains("v1_12")); }



    public void addPlayer(Player player){
        if(!existPlayer(player)) {
            guardianPlayers.put(player.getUniqueId(),new PlayerManagerImpl(this,player));
        }
    }
    public boolean existPlayer(Player player) { return guardianPlayers.containsKey(player.getUniqueId()); }
    public void removePlayer(Player player) { guardianPlayers.remove(player.getUniqueId()); }
    public HashMap<UUID, PlayerManagerImpl> getRigoxPlayers() { return guardianPlayers; }
    public PlayerManagerImpl getUser(UUID uuid) {
        if(guardianPlayers.get(uuid) == null) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                addPlayer(player);
            }
        }
        return guardianPlayers.get(uuid);
    }
    public KitLoader getKitLoader() { return kitLoader; }
    public GuardianPlaceholders getGuardianPlaceholders() { return guardianPlaceholders; }

    @Override
    public void onDisable() {
        for(Game game : getGameManager().getGames() ) {
            game.unload();
        }
        getSettings().unload();
        getSoundsInfo().unload();
        getItemsInfo().unload();
        getKitLoader().unload();
        getUtils().getCurrentShop().unload();
        getGameManager().unload();
        if(dataStorageImpl == null) return;
        dataStorageImpl.disableDatabase();
    }

    public void setDataStorage(DataStorage storage) {
        this.dataStorageImpl = storage;
    }

    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorageImpl = fileStorage;
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

                String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
                serverVersion = version.substring(0, version.lastIndexOf("_"));

                fileStorageImpl = new FileStorageImpl(instance);
                fileStorageImpl.save(SaveMode.ALL);
                sInfo = new settingsInfo(instance);

                hasPAPI = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
                listenerController = new ListenerController(instance);
                listenerController.loadListeners();
                boardController = new BoardController(instance);
                getListener().loadCommand("rftb");
                getListener().loadCommand("grftb");
                String lang = fileStorageImpl.getControl(GuardianFiles.SETTINGS).getString("settings.language");
                if(lang == null) lang = "en";
                instance.fileStorageImpl.setMessages(lang);
                logger.info("language (code) loaded: " + lang);

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
                dataStorageImpl = new DataStorageImpl(instance);
                dataStorageImpl.loadDatabase();
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
    public void loadRunnable() {
        runnable = new PlayerRunnable(this);
        if (getStorage().getControl(GuardianFiles.SCOREBOARD).getBoolean("scoreboards.animatedTitle.toggle")) {
            titleRunnable = new TitleRunnable(instance);
            titleRunnable.runTaskTimer(instance,0L, getStorage().getControl(GuardianFiles.SCOREBOARD).getLong("scoreboards.animatedTitle.repeatTime"));
        }
        runnable.runTaskTimer(instance,0L,20L);
    }


    public void getItems(GameEquip gameEquipment, Player player) {
        if(gameEquipment != GameEquip.BEAST_KIT) {
            PlayerManagerImpl user = getUser(player.getUniqueId());
            KitType type = gameEquipment.getKitType();
            if(user.hasSelectedKit()) {
                String kit = user.getSelectedKit();
                KitInfo k = getKitLoader().getKitsUsingID(type).get(kit);

                if(k == null) return;

                giveKit(k,player);
            }
            return;
        }
        for(Map.Entry<ItemStack,Integer> data : itemsInfo.getBeastInventory().entrySet()) {
            player.getInventory().setItem(data.getValue(),data.getKey());
        }
        player.getInventory().setHelmet(itemsInfo.getBeastHelmet());
        player.getInventory().setChestplate(itemsInfo.getBeastChestplate());
        player.getInventory().setLeggings(itemsInfo.getBeastLeggings());
        player.getInventory().setBoots(itemsInfo.getBeastBoots());

        PlayerManagerImpl user = getUser(player.getUniqueId());
        KitType type = gameEquipment.getKitType();
        if(user.hasSelectedKit()) {
            String kit = user.getSelectedKit();
            KitInfo k = getKitLoader().getKitsUsingID(type).get(kit);

            if(k == null) return;

            giveKit(k,player);
        }

    }

    public void giveKit(KitInfo kit,Player player) {
        for(Map.Entry<ItemStack,Integer> data : kit.getInventoryItems().entrySet()) {
            player.getInventory().setItem(data.getValue(),data.getKey());
        }

        if(kit.getArmor(GuardianArmor.HELMET) != null) player.getInventory().setHelmet(kit.getArmor(GuardianArmor.HELMET));
        if(kit.getArmor(GuardianArmor.CHESTPLATE) != null) player.getInventory().setChestplate(kit.getArmor(GuardianArmor.CHESTPLATE));
        if(kit.getArmor(GuardianArmor.LEGGINGS) != null) player.getInventory().setLeggings(kit.getArmor(GuardianArmor.LEGGINGS));
        if(kit.getArmor(GuardianArmor.BOOTS) != null) player.getInventory().setBoots(kit.getArmor(GuardianArmor.BOOTS));

    }

}
