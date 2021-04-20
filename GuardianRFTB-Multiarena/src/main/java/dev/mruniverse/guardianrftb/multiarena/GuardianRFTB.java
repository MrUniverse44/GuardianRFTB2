package dev.mruniverse.guardianrftb.multiarena;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.ExternalLogger;
import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianlib.core.utils.xseries.XMaterial;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.ItemFunction;
import dev.mruniverse.guardianrftb.multiarena.enums.SaveMode;
import dev.mruniverse.guardianrftb.multiarena.game.GameManager;
import dev.mruniverse.guardianrftb.multiarena.kits.KitLoader;
import dev.mruniverse.guardianrftb.multiarena.listeners.ListenerController;
import dev.mruniverse.guardianrftb.multiarena.runnables.PlayerRunnable;
import dev.mruniverse.guardianrftb.multiarena.runnables.TitleRunnable;
import dev.mruniverse.guardianrftb.multiarena.scoreboard.BoardController;
import dev.mruniverse.guardianrftb.multiarena.storage.FileStorage;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import dev.mruniverse.guardianrftb.multiarena.utils.GuardianUtils;
import dev.mruniverse.guardianrftb.multiarena.utils.ItemsInfo;
import dev.mruniverse.guardianrftb.multiarena.utils.settingsInfo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public void onEnable() {
        instance = this;
        itemsInfo = new ItemsInfo();
        guardianUtils = new GuardianUtils(this);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                instance.logger = new ExternalLogger(instance,"GuardianRFTB","dev.mruniverse.guardianrftb.multiarena");

                getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");

                instance.fileStorage = new FileStorage(instance);
                instance.fileStorage.save(SaveMode.ALL);
                instance.sInfo = new settingsInfo(instance);

                instance.hasPAPI = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
                instance.listenerController = new ListenerController(instance);
                instance.listenerController.loadListeners();
                instance.boardController = new BoardController(instance);
                getListener().loadCommand("rftb");
                getListener().loadCommand("grftb");
                String lang = instance.sInfo.getSettings().getString("settings.language");
                if(lang == null) lang = "en";
                if (!lang.equalsIgnoreCase("en") && !lang.equalsIgnoreCase("default")) {
                    instance.fileStorage.setMessages(lang);
                }

                gameManager = new GameManager(instance);
                gameManager.loadChests();
                gameManager.loadGames();

                kitLoader = new KitLoader(instance);


                loadLobbyItems();
                loadGameItems();
                loadBeastKit();
                loadRunnable();
            }
        };
        runnable.runTaskLater(this, 1L);
    }

    public void loadGameItems() {
        FileConfiguration items = getStorage().getControl(GuardianFiles.ITEMS);
        ConfigurationSection section;
        try {
            Utils utils = instance.getLib().getUtils();
            String ItemMaterial, ItemName;
            List<String> ItemLore;
            int ItemSlot;
            ItemStack item;
            Optional<XMaterial> optionalXMaterial;
            XMaterial m;
            ItemMaterial = items.getString("InGame.RunnerKit.item");
            ItemName = items.getString("InGame.RunnerKit.name");
            ItemLore = items.getStringList("InGame.RunnerKit.lore");
            ItemSlot = items.getInt("InGame.RunnerKit.slot");
            if (ItemMaterial == null) ItemMaterial = "PAPER";
            optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
            if (optionalXMaterial.isPresent()) {
                m = optionalXMaterial.get();
                if (m.parseMaterial() != null) {
                    item = utils.getItem(m, ItemName, ItemLore);
                    if (items.get("InGame.RunnerKit.enchantments") != null) {
                        item = utils.getEnchantmentList(item, items.getStringList("InGame.RunnerKit.enchantments"),"none");
                    }
                    itemsInfo.setKitRunner(item);
                    itemsInfo.setRunnerSlot(ItemSlot);
                    itemsInfo.getCurrentItem().put(item,ItemFunction.KIT_RUNNERS);
                }
            } else {
                getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
            }
            ItemMaterial = items.getString("InGame.backCheckpointItem.item");
            ItemName = items.getString("InGame.backCheckpointItem.name");
            ItemLore = items.getStringList("InGame.backCheckpointItem.lore");
            if (ItemMaterial == null) ItemMaterial = "NETHER_STAR";
            optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
            if(ItemName == null) ItemName = "&b&lCHECKPOINT";
            if (optionalXMaterial.isPresent()) {
                m = optionalXMaterial.get();
                if (m.parseMaterial() != null) {
                    item = utils.getItem(m, ItemName, ItemLore);
                    if (items.get("InGame.backCheckpointItem.enchantments") != null) {
                        item = utils.getEnchantmentList(item, items.getStringList("InGame.backCheckpointItem.enchantments"),"none");
                    }
                    itemsInfo.setCheckPoint(item);
                    itemsInfo.getCurrentItem().put(item, ItemFunction.CHECKPOINT);
                }
            } else {
                getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
            }
            ItemMaterial = items.getString("InGame.BeastKit.item");
            ItemName = items.getString("InGame.BeastKit.name");
            ItemLore = items.getStringList("InGame.BeastKit.lore");
            ItemSlot = items.getInt("InGame.BeastKit.slot");
            if (ItemMaterial == null) ItemMaterial = "PAPER";
            optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
            if (optionalXMaterial.isPresent()) {
                m = optionalXMaterial.get();
                if (m.parseMaterial() != null) {
                    item = utils.getItem(m, ItemName, ItemLore);
                    if (items.get("InGame.BeastKit.enchantments") != null) {
                        item = utils.getEnchantmentList(item, items.getStringList("InGame.BeastKit.enchantments"),"none");
                    }
                    itemsInfo.setKitBeast(item);
                    itemsInfo.setBeastSlot(ItemSlot);
                    itemsInfo.getCurrentItem().put(item, ItemFunction.KIT_BEASTS);
                }
            } else {
                getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
            }
            ItemMaterial = items.getString("InGame.Exit.item");
            ItemName = items.getString("InGame.Exit.name");
            ItemLore = items.getStringList("InGame.Exit.lore");
            ItemSlot = items.getInt("InGame.Exit.slot");
            if (ItemMaterial == null) ItemMaterial = "RED_BED";
            optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
            if (optionalXMaterial.isPresent()) {
                m = optionalXMaterial.get();
                if (m.parseMaterial() != null) {
                    item = utils.getItem(m, ItemName, ItemLore);
                    if (items.get("InGame.Exit.enchantments") != null) {
                        item = utils.getEnchantmentList(item, items.getStringList("InGame.Exit.enchantments"),"none");
                    }
                    itemsInfo.setExit(item);
                    itemsInfo.setExitSlot(ItemSlot);
                    itemsInfo.getCurrentItem().put(item, ItemFunction.EXIT_GAME);
                }
            } else {
                getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
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
                if (items.getBoolean("lobby." + lItems + ".toggle")) {
                    String material = items.getString("lobby." + lItems + ".item");
                    if (material == null) material = "BED";
                    Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(material);
                    XMaterial m;
                    if (optionalXMaterial.isPresent()) {
                        m = optionalXMaterial.get();
                        if (m.parseMaterial() != null) {
                            String itemName = items.getString("lobby." + lItems + ".name");
                            Integer slot = items.getInt("lobby." + lItems + ".slot");
                            List<String> lore = items.getStringList("lobby." + lItems + ".lore");
                            ItemStack item = utils.getItem(m, itemName, lore);
                            if (items.get("lobby." + lItems + ".enchantments") != null) {
                                item = utils.getEnchantmentList(item, items.getStringList("lobby." + lItems + ".enchantments"),"none");
                            }
                            itemsInfo.getLobbyItems().put(item, slot);
                            itemsInfo.getCurrentItem().put(item, getCurrent(lItems));
                        }
                    } else {
                        getLogs().error("Item: " + material + " doesn't exists.");
                    }
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
            ConfigurationSection section;
            section = items.getConfigurationSection("playing.beast-inventory");
            String ItemMaterial;
            String ItemName;
            int slot;
            List<String> ItemLore;
            ItemStack item;
            XMaterial material;
            Optional<XMaterial> optionalXMaterial;
            Utils utils = getLib().getUtils();
            if (section == null) throw new Throwable("Can't found beast inventory section in items.yml");
            for (String beastDefaultInv : section.getKeys(false)) {
                ItemMaterial = items.getString("playing.beast-inventory." + beastDefaultInv + ".item");
                if (ItemMaterial == null) ItemMaterial = "BED";
                optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
                if(optionalXMaterial.isPresent()) {
                    ItemName = items.getString("playing.beast-inventory." + beastDefaultInv + ".name");
                    slot = items.getInt("playing.beast-inventory." + beastDefaultInv + ".slot");
                    ItemLore = items.getStringList("playing.beast-inventory." + beastDefaultInv + ".lore");
                    item = utils.getItem(optionalXMaterial.get(),ItemName,ItemLore);
                    if(sInfo.getSettings().get("playing.beast-inventory." + beastDefaultInv + ".enchantments") != null) {
                        item = utils.getEnchantmentList(item,items.getStringList("playing.beast-inventory." + beastDefaultInv + ".enchantments"),"none");
                    }
                    itemsInfo.getBeastInventory().put(item,slot);
                } else {
                    getLogs().info("Item " + ItemMaterial + " doesn't exists.");
                }
            }
            ItemMaterial = items.getString("playing.beast-armor.Helmet.item");
            ItemName = items.getString("playing.beast-armor.Helmet.name");
            ItemLore = items.getStringList("playing.beast-armor.Helmet.lore");
            if (ItemMaterial == null) ItemMaterial = "DIAMOND_HELMET";
            optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
            if (optionalXMaterial.isPresent()) {
                material = optionalXMaterial.get();
                if (material.parseMaterial() != null) {
                    item = utils.getItem(material, ItemName, ItemLore);
                    if (items.get("playing.beast-armor.Helmet.enchantments") != null) {
                        item = utils.getEnchantmentList(item, items.getStringList("playing.beast-armor.Helmet.enchantments"), "none");
                    }
                    itemsInfo.setBeastHelmet(item);
                }
            } else {
                getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
            }
            ItemMaterial = items.getString("playing.beast-armor.Chestplate.item");
            ItemName = items.getString("playing.beast-armor.Chestplate.name");
            ItemLore = items.getStringList("playing.beast-armor.Chestplate.lore");
            if (ItemMaterial == null) ItemMaterial = "DIAMOND_CHESTPLATE";
            optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
            if (optionalXMaterial.isPresent()) {
                material = optionalXMaterial.get();
                if (material.parseMaterial() != null) {
                    item = utils.getItem(material, ItemName, ItemLore);
                    if (items.get("playing.beast-armor.Helmet.enchantments") != null) {
                        item = utils.getEnchantmentList(item, items.getStringList("playing.beast-armor.Chestplate.enchantments"),"none");
                    }
                    itemsInfo.setBeastChestplate(item);
                }
            } else {
                getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
            }
            ItemMaterial = items.getString("playing.beast-armor.Leggings.item");
            ItemName = items.getString("playing.beast-armor.Leggings.name");
            ItemLore = items.getStringList("playing.beast-armor.Leggings.lore");
            if (ItemMaterial == null) ItemMaterial = "DIAMOND_LEGGINGS";
            optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
            if (optionalXMaterial.isPresent()) {
                material = optionalXMaterial.get();
                if (material.parseMaterial() != null) {
                    item = utils.getItem(material, ItemName, ItemLore);
                    if (items.get("playing.beast-armor.Helmet.enchantments") != null) {
                        item = utils.getEnchantmentList(item, items.getStringList("playing.beast-armor.Leggings.enchantments"),"none");
                    }
                    itemsInfo.setBeastLeggings(item);
                }
            } else {
                getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
            }
            ItemMaterial = items.getString("playing.beast-armor.Boots.item");
            ItemName = items.getString("playing.beast-armor.Boots.name");
            ItemLore = items.getStringList("playing.beast-armor.Boots.lore");
            if (ItemMaterial == null) ItemMaterial = "DIAMOND_BOOTS";
            optionalXMaterial = XMaterial.matchXMaterial(ItemMaterial);
            if (optionalXMaterial.isPresent()) {
                material = optionalXMaterial.get();
                if (material.parseMaterial() != null) {
                    item = utils.getItem(material, ItemName, ItemLore);
                    if (items.get("playing.beast-armor.Helmet.enchantments") != null) {
                        item = utils.getEnchantmentList(item,items.getStringList("playing.beast-armor.Boots.enchantments"),"none");
                    }
                    itemsInfo.setBeastBoots(item);
                }
            } else {
                getLogs().error("Item: " + ItemMaterial + " doesn't exists.");
            }
        } catch (Throwable throwable) {
            getLogs().error("Can't load beast Default Armor");
        }
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
}
