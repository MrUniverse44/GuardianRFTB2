package dev.mruniverse.guardianrftb.bungeegame;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.ExternalLogger;
import dev.mruniverse.guardianrftb.bungeegame.enums.GameEquip;
import dev.mruniverse.guardianrftb.bungeegame.enums.GuardianArmor;
import dev.mruniverse.guardianrftb.bungeegame.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.bungeegame.enums.KitType;
import dev.mruniverse.guardianrftb.bungeegame.game.Game;
import dev.mruniverse.guardianrftb.bungeegame.kits.KitInfo;
import dev.mruniverse.guardianrftb.bungeegame.kits.KitLoader;
import dev.mruniverse.guardianrftb.bungeegame.storage.DataStorage;
import dev.mruniverse.guardianrftb.bungeegame.storage.FileStorage;
import dev.mruniverse.guardianrftb.bungeegame.storage.PlayerManager;
import dev.mruniverse.guardianrftb.bungeegame.utils.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public final class GuardianRFTB extends JavaPlugin {
    private final HashMap<UUID, PlayerManager> guardianPlayers = new HashMap<>();

    private FileStorage storage;
    private ExternalLogger logger;
    private KitLoader kitLoader;
    private GuardianPlaceholders guardianPlaceholders;
    private DataStorage dataStorage;
    private SettingsInfo settingsInfo;
    private Game currentGame;
    private GuardianUtils guardianUtils;
    private boolean hasPAPI = false;
    private GuardianBoard currentBoard = GuardianBoard.WAITING;
    private ItemsInfo itemsInfo;
    private SoundsInfo soundsInfo;


    private static GuardianRFTB instance;

    @Override
    public void onEnable() {
        instance = this;
        logger = new ExternalLogger(this,"GuardianRFTB","dev.mruniverse.guardianrftb.bungeegame");
        hasPAPI = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        storage = new FileStorage(this);
    }

    @Override
    public void onDisable() {

    }

    public void setCurrentBoard(GuardianBoard board) { currentBoard = board; }

    public GuardianBoard getCurrentBoard() { return currentBoard; }

    public SoundsInfo getSoundsInfo() { return soundsInfo; }

    public ItemsInfo getItemsInfo() { return itemsInfo; }

    public boolean hasPAPI() { return hasPAPI; }

    public void addPlayer(Player player){
        if(!existPlayer(player)) guardianPlayers.put(player.getUniqueId(),new PlayerManager(this,player));
    }
    public boolean existPlayer(Player player) { return guardianPlayers.containsKey(player.getUniqueId()); }
    public void removePlayer(Player player) { guardianPlayers.remove(player.getUniqueId()); }
    public DataStorage getData() { return dataStorage; }
    public Game getGame() { return currentGame; }
    public GuardianUtils getUtils() { return guardianUtils; }
    public HashMap<UUID, PlayerManager> getRigoxPlayers() { return guardianPlayers; }
    public PlayerManager getPlayerData(UUID uuid) { return guardianPlayers.get(uuid); }
    public KitLoader getKits() { return kitLoader; }
    public GuardianPlaceholders getGuardianPlaceholders() { return guardianPlaceholders; }

    public GuardianLIB getLib() { return GuardianLIB.getControl(); }
    public static GuardianRFTB getInstance() { return instance; }
    public FileStorage getStorage() { return storage; }
    public SettingsInfo getSettings() { return settingsInfo; }
    public ExternalLogger getLogs() { return logger; }

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
                KitInfo kitInfo = getKits().getKitsUsingID(KitType.BEAST).get(kitID);
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
                KitInfo killerInfo = getKits().getKitsUsingID(KitType.KILLER).get(killerID);
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
                KitInfo runnerInfo = getKits().getKitsUsingID(KitType.RUNNER).get(runnerID);
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

