package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.CenterText;
import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
public class GuardianUtils {

    private final GuardianRFTB plugin;
    private final Utils utils = GuardianLIB.getControl().getUtils();
    private final ShopMenu currentShop;

    public GuardianUtils(GuardianRFTB plugin) {
        this.plugin = plugin;
        currentShop = new ShopMenu(plugin);
    }

    public ShopMenu getCurrentShop() {
        return currentShop;
    }

    public void sendMessage(Player player,String message) {
        utils.sendMessage(player,replaceVariables(message,player));
    }
    public void sendActionbar(Player player,String message) {
        utils.sendActionbar(player,replaceVariables(message,player));
    }
    public void sendBossbar(Player player,String message) {
        utils.sendBossBar(player,replaceVariables(message,player));
    }

    public void sendGameList(Player player, List<String> list, GameTeam winnerTeam) {
        if(list == null) list = new ArrayList<>();
        String runnerRole = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("roles.runners");
        String beastRole = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("roles.beasts");
        String wT,lT;
        if(runnerRole == null) runnerRole = "Runners";
        if(beastRole == null) beastRole = "Beasts";
        if(winnerTeam.equals(GameTeam.RUNNERS)) {
            wT = runnerRole;
            lT = beastRole;
        } else {
            wT = beastRole;
            lT = runnerRole;
        }
        UUID uuid = player.getUniqueId();
        GameInfo game = plugin.getPlayerData(uuid).getGame();
        String gameType = game.getType().getType();
        String gameName = game.getName();
        boolean playerBeast = game.getBeasts().contains(player);
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

    public void rewardInfo(Player player,List<String> list,boolean winnerTeamIsRunners) {
        if(list == null) list = new ArrayList<>();
        UUID uuid = player.getUniqueId();
        GameInfo game = plugin.getPlayerData(uuid).getGame();
        String gameType = game.getType().getType();
        String gameName = game.getName();
        boolean playerBeast = game.getBeasts().contains(player);
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
        plugin.getPlayerData(uuid).updateCoins(winOrLoss);
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
        PlayerManager currentData = plugin.getPlayerData(player.getUniqueId());
        if(currentData.getGame() != null) {
            UUID uuid = player.getUniqueId();
            GameInfo game = currentData.getGame();
            String gameType = game.getType().getType();
            String gameName = game.getName();
            boolean playerBeast = game.getBeasts().contains(player);
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
        PlayerManager playerManager = plugin.getPlayerData(player.getUniqueId());
        if(playerManager == null) {
            plugin.addPlayer(player);
            playerManager = plugin.getPlayerData(player.getUniqueId());
        }

        text = text.replace("<player_name>",player.getName())
                .replace("[new line]","\n")
                .replace("<player_coins>",playerManager.getCoins() + "")
                .replace("<player_wins>",playerManager.getKits().size() + "")
                .replace("<player_kills>",playerManager.getKills() + "")
                .replace("<player-deaths>",playerManager.getDeaths() + "")
                .replace("<player_beast_kit>","Not selected")
                .replace("<player_runner_kit>","Not selected")
                .replace("<online>",plugin.getServer().getOnlinePlayers().size() + "")
                .replace("<timeFormat>",getDateFormat());

        if (playerManager.getGame() != null) {
            GameInfo currentGame = playerManager.getGame();
            text = text.replace("<arena_name>",currentGame.getName())
                    .replace("<arena_online>","" + currentGame.getPlayers().size())
                    .replace("<arena_max>","" + currentGame.getMax())
                    .replace("<arena_need>","" + currentGame.getNeedPlayers())
                    .replace("<arena_beast>","")
                    .replace("<arena_runners>","" + currentGame.getRunners().size())
                    .replace("<arena_mode>",currentGame.getType().getType())
                    .replace("<arena_timeLeft>",currentGame.getLastTimer() + "")
                    .replace("<arena_status>",currentGame.getStatus().getStatus())
                    .replace("<player_role>","")
                    .replace("<arena_time_number>", currentGame.getLastTimer() + "");
        }
        if(plugin.hasPAPI()) { text = PlaceholderAPI.setPlaceholders(player,text); }
        return text;
    }

    public Player getRandomBeast(Player player) {
        PlayerManager currentData = plugin.getPlayerData(player.getUniqueId());
        if(currentData.getGame() != null) {
            if(currentData.getGame().getBeasts().size() != 0) {
                return currentData.getGame().getBeasts().get(0);
            }
            return player;
        }
        return player;
    }

    public boolean isBeast(Player player) {
        PlayerManager currentData = plugin.getPlayerData(player.getUniqueId());
        if(currentData == null) return false;
        if(currentData.getGame() != null) {
            return currentData.getGame().getBeasts().contains(player);
        }
        return false;
    }
    private String getBeast(GameInfo game) {
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
        PlayerManager playerManager = plugin.getPlayerData(player.getUniqueId());
        int delayValue = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int countdown = delay;
            public void run() {
                if (this.countdown == 0) {
                    plugin.getServer().getScheduler().cancelTask(playerManager.getLeaveDelay());
                    playerManager.setLeaveDelay(0);
                    if(playerManager.getGame() != null) {
                        playerManager.getGame().leave(player);
                    }
                } else {
                    this.countdown--;
                }
            }
        }, 0L, 20L);
        plugin.getPlayerData(player.getUniqueId()).setLeaveDelay(delayValue);
    }


}
