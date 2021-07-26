package dev.mruniverse.guardianrftb.multiarena.listeners;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.interfaces.PlayerManager;
import dev.mruniverse.guardianrftb.multiarena.utils.SoundsInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class InteractListener implements Listener {
    private final GuardianRFTB plugin;
    private final SoundsInfo sounds;
    private final Random random = new Random();

    private String cancelMessage;
    private String confirmMessage;

    public InteractListener(GuardianRFTB plugin) {
        this.plugin = plugin;
        sounds = plugin.getSoundsInfo();
        cancelMessage = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.leave.cancelled");
        if (cancelMessage == null) cancelMessage = "&c&lTeleport cancelled!";
        confirmMessage = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.leave.confirm");
        if (confirmMessage == null) confirmMessage = "&a&lTeleporting you to the lobby in <leaveCancelTime> seconds...[new line]&a&lRight-Click again to cancel the teleport!";
    }

    public void updateAll() {
        cancelMessage = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.leave.cancelled");
        if (cancelMessage == null) cancelMessage = "&c&lTeleport cancelled!";
        confirmMessage = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.leave.confirm");
        if (confirmMessage == null) confirmMessage = "&a&lTeleporting you to the lobby in <leaveCancelTime> seconds...[new line]&a&lRight-Click again to cancel the teleport!";
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getItem() != null) {
            if(event.getItem() == null) return;
            if(event.getItem().getItemMeta() == null) return;
            if(event.getItem().getType().equals(plugin.getItemsInfo().getExit().getType()) && event.getItem().getItemMeta().equals(plugin.getItemsInfo().getExit().getItemMeta())) {
                event.setCancelled(true);
                PlayerManager playerManagerImpl = plugin.getUser(player.getUniqueId());
                String message;
                int leaveInt = plugin.getSettings().getSettings().getInt("settings.leaveCancelTime");
                if(playerManagerImpl.getLeaveDelay() != 0) {
                    plugin.getServer().getScheduler().cancelTask(plugin.getUser(event.getPlayer().getUniqueId()).getLeaveDelay());
                    message = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.leave.cancelled");
                    if(message == null) message = "&c&lTeleport cancelled!";
                    playerManagerImpl.setLeaveDelay(0);
                    plugin.getUtils().sendMessage(player, message.replace("<leaveCancelTime>",""+leaveInt));
                    return;
                }
                message = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.leave.confirm");
                if(message == null) message = "&a&lTeleporting you to the lobby in <leaveCancelTime> seconds...[new line]&a&lRight-Click again to cancel the teleport!";
                plugin.getUtils().sendMessage(player, message.replace("<leaveCancelTime>",""+leaveInt));
                plugin.getUtils().sendLeaveCountdown(player,leaveInt);
                return;
            }
            PlayerManager pm = plugin.getUser(player.getUniqueId());
            HashMap<ItemStack, Integer> itemToChecks = new HashMap<>(plugin.getItemsInfo().getLobbyItems());
            itemToChecks.put(plugin.getItemsInfo().getKitBeast(), 0);
            itemToChecks.put(plugin.getItemsInfo().getKitRunner(),0);
            itemToChecks.put(plugin.getItemsInfo().getCheckPoint(), 0);
            itemToChecks.put(plugin.getItemsInfo().getExit(), 8);
            for(ItemStack item : itemToChecks.keySet()) {
                if(event.getItem().getType().equals(item.getType()) && event.getItem().getItemMeta().equals(item.getItemMeta())) {
                    ItemFunction itemAction = plugin.getItemsInfo().getCurrentItem().get(item);
                    event.setCancelled(true);
                    switch(itemAction) {
                        case SHOP:
                            Inventory sInv = plugin.getUtils().getCurrentShop().getInventory();
                            if(sInv == null) return;
                            player.openInventory(sInv);
                            return;
                        case KIT_KILLERS:
                            if(plugin.getKitLoader().hasKits(KitType.KILLER)) {
                                Inventory inventory = pm.getKitMenu(KitType.KILLER).getInventory();
                                if (inventory == null) return;
                                player.openInventory(inventory);
                            } else {
                                plugin.getUtils().sendMessage(
                                        player,
                                        plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.no-kits")
                                        );
                            }
                            return;
                        case KIT_BEASTS:
                            if(plugin.getKitLoader().hasKits(KitType.BEAST)) {
                                Inventory inventory = pm.getKitMenu(KitType.BEAST).getInventory();
                                if (inventory == null) return;
                                player.openInventory(inventory);
                            } else {
                                plugin.getUtils().sendMessage(
                                        player,
                                        plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.no-kits")
                                );
                            }
                            return;
                        case KIT_RUNNERS:
                            if(plugin.getKitLoader().hasKits(KitType.RUNNER)) {
                                Inventory inv = pm.getKitMenu(KitType.RUNNER).getInventory();
                                if(inv == null) return;
                                player.openInventory(inv);
                            } else {
                                plugin.getUtils().sendMessage(
                                        player,
                                        plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.no-kits")
                                );
                            }
                            return;
                        case EXIT_LOBBY:
                            List<String> servers = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getStringList("settings.lobby.Hub-servers");
                            String server = servers.get(random.nextInt(servers.size()));
                            plugin.getUtils().sendServer(player,server);
                            return;
                        case CHECKPOINT:
                            if(pm.getPointStatus() && pm.getLastCheckpoint() != null) {
                                player.teleport(pm.getLastCheckpoint());
                                pm.setLastCheckpoint(null);
                                pm.setPointStatus(false);
                                plugin.getUtils().consumeItem(player,1,plugin.getItemsInfo().getCheckPoint());
                                plugin.getUtils().sendMessage(player, plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.others.checkpoint.use"));
                                try {
                                    if (sounds.getStatus(GuardianSounds.CHECKPOINT_USE))
                                        player.playSound(player.getLocation(), sounds.getSound(GuardianSounds.CHECKPOINT_USE), sounds.getVolume(GuardianSounds.CHECKPOINT_USE), sounds.getPitch(GuardianSounds.CHECKPOINT_USE));
                                }catch (Throwable ignored) {
                                    plugin.getLogs().error("Checkpoint sound is not set in your sounds.yml");
                                }
                            }
                            return;
                        case GAME_SELECTOR:
                            player.openInventory(plugin.getGameManager().getGameMainMenu().getInventory());
                            return;
                        case LOBBY_SELECTOR:
                            plugin.getUtils().sendMessage(player,"&cLobby Selector is in maintenance");
                            return;
                        case PLAYER_SETTINGS:
                            plugin.getUtils().sendMessage(player,"&cCurrently in development");
                            return;
                        case EXIT_GAME:
                        default:
                            if(plugin.getUser(player.getUniqueId()).getGame() != null) {
                                PlayerManager playerManagerImpl = plugin.getUser(player.getUniqueId());
                                int leaveInt = plugin.getSettings().getSettings().getInt("settings.leaveCancelTime");
                                if (playerManagerImpl.getLeaveDelay() != 0) {
                                    plugin.getServer().getScheduler().cancelTask(plugin.getUser(event.getPlayer().getUniqueId()).getLeaveDelay());
                                    playerManagerImpl.setLeaveDelay(0);
                                    plugin.getUtils().sendMessage(player, cancelMessage.replace("<leaveCancelTime>", "" + leaveInt));
                                    return;
                                }
                                plugin.getUtils().sendMessage(player, confirmMessage.replace("<leaveCancelTime>", "" + leaveInt));
                                plugin.getUtils().sendLeaveCountdown(player, leaveInt);
                                return;
                            }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKitMenuClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        PlayerManager data = plugin.getUser(player.getUniqueId());
        if(event.getCurrentItem() == null) return;
        if(event.getInventory().equals(data.getKitMenu(KitType.BEAST).getInventory())) {
            HashMap<ItemStack, String> hash = data.getKitMenu(KitType.BEAST).getItems();
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (hash.containsKey(clickedItem)) {
                plugin.getKitLoader().getToSelect(KitType.BEAST, player, hash.get(clickedItem));
            }
            return;
        }
        if(event.getInventory().equals(data.getKitMenu(KitType.RUNNER).getInventory())) {
            HashMap<ItemStack, String> hash = data.getKitMenu(KitType.RUNNER).getItems();
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (hash.containsKey(clickedItem)) {
                plugin.getKitLoader().getToSelect(KitType.RUNNER,player,hash.get(clickedItem));
            }
            return;
        }
        if(event.getInventory().equals(data.getKitMenu(KitType.KILLER).getInventory())) {
            HashMap<ItemStack, String> hash = data.getKitMenu(KitType.KILLER).getItems();
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (hash.containsKey(clickedItem)) {
                plugin.getKitLoader().getToSelect(KitType.KILLER,player,hash.get(clickedItem));
            }
        }
    }

    @EventHandler
    public void onShopMenuClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if(plugin.getUser(player.getUniqueId()).getGame() != null) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getInventory().equals(plugin.getUtils().getCurrentShop().getInventory())) return;
        HashMap<ItemStack, ShopAction> hash = plugin.getUtils().getCurrentShop().getItems();
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);
        if(hash.containsKey(clickedItem)) {
            if(hash.get(clickedItem) != ShopAction.CUSTOM && hash.get(clickedItem) != ShopAction.FILL) {
                plugin.getUtils().getCurrentShop().execute(player,hash.get(clickedItem));
            }
        }
    }

    @EventHandler
    public void onGameMainClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if(plugin.getUser(player.getUniqueId()).getGame() != null) return;
        if(event.getCurrentItem() == null) return;
        if(!event.getInventory().equals(plugin.getGameManager().getGameMainMenu().getInventory())) return;
        HashMap<ItemStack, MainAction> hash = plugin.getGameManager().getGameMainMenu().getItems();
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);
        if(hash.containsKey(clickedItem)) {
            if(hash.get(clickedItem) != MainAction.CUSTOM && hash.get(clickedItem) != MainAction.FILL) {
                plugin.getGameManager().getGameMainMenu().execute(player,hash.get(clickedItem));
            }
        }

    }
    @EventHandler
    public void onGameMenuClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if(plugin.getUser(player.getUniqueId()).getGame() != null) return;
        if(event.getCurrentItem() == null) return;
        GameType gameType = null;
        for(GameType gameType1 : GameType.values()) {
            if (event.getInventory().equals(plugin.getGameManager().getGameMenu(gameType1).getInventory())) gameType = gameType1;
        }
        if(gameType == null) return;
        HashMap<ItemStack,String> hash = plugin.getGameManager().getGameMenu(gameType).getGameItems();
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);
        if(hash.containsKey(clickedItem)) {
            plugin.getGameManager().joinGame(player,hash.get(clickedItem));
        }
    }
    @EventHandler
    public void onChestClick(PlayerInteractEvent e) {
        Game game = plugin.getUser(e.getPlayer().getUniqueId()).getGame();
        if(game == null) return;
        if(game.getStatus() == GameStatus.WAITING || game.getStatus() == GameStatus.STARTING || game.getStatus() == GameStatus.RESTARTING) {
            e.setCancelled(true);
        }
        Block b = e.getClickedBlock();
        if (b == null) { return; }
        if(falseChest(b.getType())) { return; }
        if(b.getType() == Material.CHEST) e.setCancelled(true);
        if(b.getType() == Material.TRAPPED_CHEST) e.setCancelled(true);
        if(b.getType() == Material.ENDER_CHEST) e.setCancelled(true);
        Chest chest = (Chest)e.getClickedBlock().getState();
        InventoryHolder holder = chest.getInventory().getHolder();
        if (holder instanceof DoubleChest) {
            DoubleChest doubleChest = ((DoubleChest) holder);
            Chest leftChest = (Chest) doubleChest.getLeftSide();
            if (leftChest != null) {
                if (isGameChest(e.getPlayer(), leftChest.getLocation())) return;
            }
            Chest rightChest = (Chest) doubleChest.getRightSide();
            if (rightChest != null) { if (isGameChest(e.getPlayer(), rightChest.getLocation())) return; }
            return;
        }
        checkGameChest(e.getPlayer(),b.getLocation());
    }
    private void openGameChest(Player player,String chestName) {
        player.openInventory(plugin.getGameManager().getGameChest(chestName).getInventory());
    }
    private void checkGameChest(Player player, Location location) {
        Game game = plugin.getUser(player.getUniqueId()).getGame();
        if(game == null) return;
        if(game.getChestLocations() == null) return;
        for(String chests : game.getChestTypes()) {
            if(game.getChestLocations().get(chests) != null) {
                if (game.getChestLocations().get(chests).contains(location)) {
                    if(game.isChestLimited(chests) && plugin.getGameManager().isChestLimitEnabled()) {
                        if(!game.isChestLimitParsed(chests) || game.isChestOf(chests,player)) {
                            openGameChest(player,chests);
                            game.addChestLimit(chests,player);
                            return;
                        }
                        String prefix = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.prefix", "&3&lG&b&lRFTB &8| ");
                        String limitedChest = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.game.limited-chest", "&aThis chest has been empty for others users.");
                        if(plugin.getGameManager().hasChestChangeEnabled()) {
                            if(plugin.getGameManager().getSwitchChest() != null) {
                                player.openInventory(plugin.getGameManager().getSwitchChest().getInventory());
                                return;
                            }
                        }
                        plugin.getUtils().sendMessage(player, prefix + limitedChest);
                        return;
                    }
                    openGameChest(player, chests);
                    return;
                }
            }
        }
    }
    private boolean isGameChest(Player player,Location location) {
        Game game = plugin.getUser(player.getUniqueId()).getGame();
        if(game == null) return false;
        for(String chests : game.getChestTypes()) {
            if(game.getChestLocations().get(chests).contains(location)) {
                openGameChest(player,chests);
                return true;
            }
        }
        return false;
    }
    private boolean falseChest(Material evalMaterial) {
        if(evalMaterial.equals(Material.CHEST)) return false;
        if(evalMaterial.equals(Material.TRAPPED_CHEST)) return false;
        return (!evalMaterial.equals(Material.ENDER_CHEST));
    }
}
