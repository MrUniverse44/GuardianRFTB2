package me.blueslime.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.CenterText;
import dev.mruniverse.guardianlib.core.utils.Utils;
import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.enums.GameType;
import me.blueslime.guardianrftb.multiarena.interfaces.Game;
import me.blueslime.guardianrftb.multiarena.player.GamePlayer;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayerImpl;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
public class GameUtils {

    private final GuardianRFTB plugin;
    private final ShopMenu currentShop;
    private static final Random random = new Random();

    /**
     * @param plugin main plugin
     */
    public GameUtils(GuardianRFTB plugin) {
        this.currentShop = new ShopMenu(plugin);
        this.plugin = plugin;
    }

    public ShopMenu getCurrentShop() {
        return currentShop;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public void consumeItem(Player player, int count, ItemStack itemToGet) {
        Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(itemToGet);
        int found = 0;
        for (ItemStack stack : ammo.values())
            found += stack.getAmount();
        if (count > found)
            return;

        for (Integer index : ammo.keySet()) {
            ItemStack stack = ammo.get(index);

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed)
                player.getInventory().setItem(index, null);
            else
                stack.setAmount(stack.getAmount() - removed);

            if (count <= 0)
                break;
        }

        player.updateInventory();
    }

    public void consumeItem(Player player, int count, Material material) {
        Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(material);
        int found = 0;
        for (ItemStack stack : ammo.values())
            found += stack.getAmount();
        if (count > found)
            return;

        for (Integer index : ammo.keySet()) {
            ItemStack stack = ammo.get(index);

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed)
                player.getInventory().setItem(index, null);
            else
                stack.setAmount(stack.getAmount() - removed);

            if (count <= 0)
                break;
        }

        player.updateInventory();
    }

    public String replaceVariables(String text, Player player) {

        GamePlayer gamePlayer = plugin.getStorageManager().getPlayerMap().get(
                player.getUniqueId(),
                new GamePlayerImpl(
                        plugin,
                        player
                )
        );

        text = text.replace("<player_name>",player.getName())
                .replace("[new line]","\n")
                .replace("<player_coins>", gamePlayer.getCoins() + "")
                .replace("<player_wins>", gamePlayer.getWins() + "")
                .replace("<player_kits>", gamePlayer.getKits().size() + "")
                .replace("<player_kills>", gamePlayer.getKills() + "")
                .replace("<player_deaths>", gamePlayer.getDeaths() + "")
                .replace("<player_beast_kit>","Not selected")
                .replace("<player_runner_kit>","Not selected")
                .replace("<online>",plugin.getServer().getOnlinePlayers().size() + "")
                .replace("<timeFormat>",getDateFormat());

        if (gamePlayer.getGame() != null) {
            String second;
            Game currentGame = gamePlayer.getGame();

            //int time = currentGame.getLastTimer();
            //if(time == 1) {
            //    second = plugin.getSettings().getSettings().getString("settings.timer.second","second");
            //} else {
            //    second = plugin.getSettings().getSettings().getString("settings.timer.seconds","seconds");
            //}

            text = text.replace("<arena_name>", currentGame.getName())
                    .replace("<arena_online>", "" + currentGame.getPlayers().size())
                    .replace("<arena_max>", "" + currentGame.getMax())
                    .replace("<arena_need>", "" + currentGame.getNeedPlayers())
                    .replace("<arena_beast>", getBeast(currentGame))
                    .replace("<arena_runners>","" + currentGame.getRunners().size())
                    .replace("<arena_mode>",currentGame.getType().getType())
                    .replace("<arena_timeLeft>", currentGame.getLastTimer() + "")
                    .replace("<arena_status>", currentGame.getStatus().getStatus())
                    .replace("<player_role>", gamePlayer.getCurrentRole())
                    .replace("<arena_time_number>", currentGame.getLastTimer() + "")
                    .replace("<arena_time_text>", "seconds");
        }
        if(plugin.hasPAPI()) { text = PlaceholderAPI.setPlaceholders(player,text); }
        return text;
    }

    public Player getRandomBeast(Player player) {
        GamePlayer gamePlayer = plugin.getStorageManager().getPlayerMap().get(
                player.getUniqueId(),
                new GamePlayerImpl(
                        plugin,
                        player
                )
        );

        if(gamePlayer.getGame() != null) {
            if(gamePlayer.getGame().getBeasts().size() != 0) {
                return gamePlayer.getGame().getBeasts().get(0);
            }
            return player;
        }
        return player;
    }

    public boolean isBeast(Player player) {
        GamePlayer currentData = plugin.getUser(player.getUniqueId());
        if(currentData == null) return false;
        if(currentData.getGame() != null) {
            return currentData.getGame().getBeasts().contains(player);
        }
        return false;
    }
    private String getBeast(Game game) {
        if(game.getType().equals(GameType.DOUBLE_BEAST)) {
            return game.getBeasts().size()+"";
        }
        if(game.getBeasts().size() != 0) {
            return game.getBeasts().get(0).getName();
        }
        return "none";
    }

    public String getDateFormat() {
        String dateFormat = plugin.getSettings().getSettings().getString("settings.dateFormat");
        if(dateFormat == null) dateFormat = "dd/MM/yyyy";
        return "" + (new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime()));

    }

    public void sendServer(Player player,String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Throwable ignored) { }
        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

    public void sendLeaveCountdown(final Player player, final int delay) {
        GamePlayer gamePlayerImpl = plugin.getUser(player.getUniqueId());
        int delayValue = new BukkitRunnable() {
            int countdown = delay;
            public void run() {
                if (this.countdown == 0) {
                    plugin.getServer().getScheduler().cancelTask(gamePlayerImpl.getLeaveDelay());
                    gamePlayerImpl.setLeaveDelay(0);
                    if(gamePlayerImpl.getGame() != null) {
                        gamePlayerImpl.getGame().leave(player);
                    }
                } else {
                    if(this.countdown == 2) {
                        Chunk lobbyChunk = plugin.getSettings().getLocation().getChunk();
                        if(!lobbyChunk.isLoaded()) lobbyChunk.load();
                    }
                    this.countdown--;
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L).getTaskId();
        plugin.getUser(player.getUniqueId()).setLeaveDelay(delayValue);
    }


}
