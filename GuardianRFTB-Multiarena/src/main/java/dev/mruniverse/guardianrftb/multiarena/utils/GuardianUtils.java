package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.CenterText;
import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.storage.GamePlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
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
public class GuardianUtils {

    private final GuardianRFTB plugin;
    private final Utils utils = GuardianLIB.getControl().getUtils();
    private final ShopMenu currentShop;
    private static final Random random = new Random();

    /**
     * @param plugin main plugin
     */
    public GuardianUtils(GuardianRFTB plugin) {
        this.plugin = plugin;
        currentShop = new ShopMenu(plugin);
    }

    public ShopMenu getCurrentShop() {
        return currentShop;
    }

    /**
     * @param player player to send
     * @param message current message
     */
    public void sendMessage(Player player,String message) {
        utils.sendMessage(player,replaceVariables(message,player));
    }

    /**
     * @param player player to send
     * @param message current message
     * @param chatPlayer player from the player.
     */
    public void sendMessage(Player player,String message,Player chatPlayer) {
        message = replaceVariables(message,chatPlayer);
        utils.sendMessage(player, ChatColor.translateAlternateColorCodes('&',message));
    }

    /**
     * @param player player to send
     * @param message current message
     * @param chatPlayer player from the player.
     * @param chatMessage message from the chat.
     */
    public void sendMessage(Player player,String message,Player chatPlayer,String chatMessage) {
        message = replaceVariables(message,chatPlayer);
        if(chatPlayer.hasPermission("grftb.chat.color")) {
            utils.sendMessage(player, message.replace("%message%", ChatColor.translateAlternateColorCodes('&',chatMessage)));
        } else {
            utils.sendMessage(player, message.replace("%message%", ChatColor.stripColor(chatMessage)));
        }
    }
    /**
     * @param player player to send
     * @param message current message
     */
    public void sendActionbar(Player player,String message) {
        utils.sendActionbar(player,replaceVariables(message,player));
    }
    /**
     * @param player player to send
     * @param message current message
     */
    public void sendBossbar(Player player,String message) {
        utils.sendBossBar(player,replaceVariables(message,player));
    }

    /**
     * @param player player to send
     * @param list list of the message
     * @param winnerTeam winner team of the game
     */
    public void sendGameList(Player player, List<String> list, GameTeam winnerTeam) {
        if(list == null) list = new ArrayList<>();
        String runnerRole = plugin.getSettings().getRole(GameTeam.RUNNERS2);
        String beastRole = plugin.getSettings().getRole(GameTeam.BEASTS2);
        String wT,lT;
        if(winnerTeam.equals(GameTeam.RUNNERS) || winnerTeam.equals(GameTeam.KILLER)) {
            wT = runnerRole;
            lT = beastRole;
        } else {
            wT = beastRole;
            lT = runnerRole;
        }
        Game game = plugin.getUser(player.getUniqueId()).getGame();
        String gameType = plugin.getSettings().getSettings().getString(game.getType().toPath(), "");
        String gameName = game.getName();
        boolean playerBeast = game.getBeasts().contains(player.getUniqueId());
        for(String line : list) {
            if(playerBeast) line = line.replace("<isBeast>","");
            if(!playerBeast) line = line.replace("<isRunner>","");
            line = line.replace("%gameType%",gameType)
                    .replace("%map_name%",gameName)
                    .replace("%winner_team%",wT)
                    .replace("%looser_team%",lT)
                    .replace("[px]","⚫")
                    .replace("%game%","+5")
                    .replace("[bx]","▄");
            if(line.contains("<center>")) {
                line = CenterText.sendToCenter(line.replace("<center>",""));
            }
            if(playerBeast) {
                if(!line.contains("<isRunner>")) utils.sendMessage(player,line);
            } else {
                if(!line.contains("<isBeast>")) utils.sendMessage(player,line);
            }
        }
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public void rewardInfo(Player player,List<String> list,boolean winnerTeamIsRunners) {
        if(list == null) list = new ArrayList<>();
        UUID uuid = player.getUniqueId();
        Game game = plugin.getUser(uuid).getGame();
        String gameType = plugin.getSettings().getSettings().getString(game.getType().toPath(), "");
        String gameName = game.getName();
        boolean playerBeast = game.getBeasts().contains(player.getUniqueId());
        int winOrLoss;
        if(playerBeast) {
            if(winnerTeamIsRunners) {
                winOrLoss = plugin.getSettings().getSettings().getInt("settings.pointSystem.beasts.death");
            } else {
                winOrLoss = plugin.getSettings().getSettings().getInt("settings.pointSystem.beasts.win");
            }
        } else {
            if(winnerTeamIsRunners) {
                winOrLoss = plugin.getSettings().getSettings().getInt("settings.pointSystem.runners.win");
            } else {
                winOrLoss = plugin.getSettings().getSettings().getInt("settings.pointSystem.runners.death");
            }
        }
        GamePlayer gamePlayer = plugin.getGamePlayer(uuid);
        if (gamePlayer != null) {
            gamePlayer.getStatistics().addCoins(winOrLoss);
        }
        String coins;
        if(winOrLoss >= 0) {
            coins = "+" + winOrLoss;
        } else {
            coins = "" + winOrLoss;
        }
        for(String line : list) {
            if(playerBeast) line = line.replace("<isBeast>","");
            if(!playerBeast) line = line.replace("<isRunner>","");
            line = line.replace("%gameType%",gameType)
                    .replace("%map_name%",gameName)
                    .replace("%coins%",coins)
                    .replace("[px]","⚫")
                    .replace("%game%","+5")
                    .replace("[bx]","▄");
            if(line.contains("<center>")) {
                line = CenterText.sendToCenter(line.replace("<center>",""));
            }
            if(playerBeast) {
                if(!line.contains("<isRunner>")) utils.sendMessage(player,line);
            } else {
                if(!line.contains("<isBeast>")) utils.sendMessage(player,line);
            }
        }
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

    public void sendList(Player player,List<String> list) {
        if(list == null) list = new ArrayList<>();
        GamePlayer currentData = plugin.getGamePlayer(player);
        if(currentData.getGame() != null) {
            UUID uuid = player.getUniqueId();
            Game game = currentData.getGame();
            String gameType = plugin.getSettings().getSettings().getString(game.getType().toPath(), "");
            String gameName = game.getName();
            boolean playerBeast = game.getBeasts().contains(player.getUniqueId());
            for(String line : list) {
                if(playerBeast) line = line.replace("<isBeast>","");
                if(!playerBeast) line = line.replace("<isRunner>","");
                line = line.replace("%gameType%",gameType)
                        .replace("%map_name%",gameName)
                        .replace("[px]","⚫")
                        .replace("%game%","+5")
                        .replace("[bx]","▄");
                if(line.contains("<center>")) {
                    line = CenterText.sendToCenter(line.replace("<center>",""));
                }
                if(playerBeast) {
                    if(!line.contains("<isRunner>")) utils.sendMessage(player,line);
                } else {
                    if(!line.contains("<isBeast>")) utils.sendMessage(player,line);
                }
            }
        } else {
            for(String line : list) {
                line = line.replace("[bx]","▄");
                utils.sendMessage(player,line);
            }
        }
    }

    public String replaceVariables(String text,Player player) {
        GamePlayer playerManagerImpl = plugin.getGamePlayer(player);

        text = text.replace("<player_name>",player.getName())
            .replace("[new line]","\n")
            .replace("<player_coins>", playerManagerImpl.getStatistics().getCoins() + "")
            .replace("<player_wins>",playerManagerImpl.getStatistics().getWins() + "")
            .replace("<player_kits>", playerManagerImpl.getStatistics().getKits().size() + "")
            .replace("<player_kills>", playerManagerImpl.getStatistics().getKills() + "")
            .replace("<player_deaths>", playerManagerImpl.getStatistics().getDeaths() + "")
            .replace("<player_beast_kit>","Not selected")
            .replace("<player_runner_kit>","Not selected")
            .replace("<online>",plugin.getServer().getOnlinePlayers().size() + "")
            .replace("<timeFormat>",getDateFormat());

        if (playerManagerImpl.getGame() != null) {
            String second;
            Game currentGame = playerManagerImpl.getGame();
            int time = currentGame.getLastTimer();
            if(time == 1) {
                second = plugin.getSettings().getSettings().getString("settings.timer.second","second");
            } else {
                second = plugin.getSettings().getSettings().getString("settings.timer.seconds","seconds");
            }
            text = text.replace("<arena_name>", currentGame.getName())
                .replace("<arena_online>", "" + currentGame.getPlayers().size())
                .replace("<arena_max>", "" + currentGame.getMax())
                .replace("<arena_need>", "" + currentGame.getNeedPlayers())
                .replace("<arena_beast>", getBeast(currentGame))
                .replace("<arena_runners>", "" + currentGame.getRunners().size())
                .replace("<arena_mode>", plugin.getSettings().getSettings().getString(currentGame.getType().toPath(), ""))
                .replace("<arena_timeLeft>", currentGame.getLastTimer() + "")
                .replace("<arena_status>", currentGame.getStatus().getStatus())
                .replace("<player_role>", playerManagerImpl.getCurrentRole())
                .replace("<arena_time_number>",  currentGame.getLastTimer() + "")
                .replace("<arena_time_text>", second);
        }
        if(plugin.hasPAPI()) { text = PlaceholderAPI.setPlaceholders(player,text); }
        return text;
    }

    public Player getRandomBeast(Player player) {
        GamePlayer currentData = plugin.getGamePlayer(player);
        if (currentData.getGame() != null) {
            if (!currentData.getGame().getBeasts().isEmpty()) {
                Player usedPlayer = plugin.getServer().getPlayer(currentData.getGame().getBeasts().get(0));
                if (usedPlayer != null) {
                    return usedPlayer;
                }
                return player;
            }
            return player;
        }
        return player;
    }

    public boolean isBeast(Player player) {
        GamePlayer currentData = plugin.getGamePlayer(player);
        if (currentData == null) {
            return false;
        }
        if (currentData.getGame() != null) {
            return currentData.getGame().getBeasts().contains(player.getUniqueId());
        }
        return false;
    }
    private String getBeast(Game game) {
        if (game.getType().equals(GameType.DOUBLE_BEAST)) {
            return String.valueOf(game.getBeasts().size());
        }
        if (!game.getBeasts().isEmpty()) {
            Player usedPlayer = plugin.getServer().getPlayer(game.getBeasts().get(0));
            if (usedPlayer != null) {
                return usedPlayer.getName();
            }
            return "none";
        }
        return "none";
    }

    public String getDateFormat() {
        String dateFormat = plugin.getSettings().getSettings().getString("settings.dateFormat");
        if (dateFormat == null) {
            dateFormat = "dd/MM/yyyy";
        }
        return new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());

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
        GamePlayer playerManagerImpl = plugin.getGamePlayer(player);
        int delayValue = new BukkitRunnable() {
            int countdown = delay;
            public void run() {
                if (this.countdown == 0) {
                    plugin.getServer().getScheduler().cancelTask(playerManagerImpl.getLeaveDelay());
                    playerManagerImpl.setLeaveDelay(0);
                    if(playerManagerImpl.getGame() != null) {
                        playerManagerImpl.getGame().leave(player);
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
