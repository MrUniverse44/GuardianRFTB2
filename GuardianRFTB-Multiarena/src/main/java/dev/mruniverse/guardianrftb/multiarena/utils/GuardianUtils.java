package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianlib.core.GuardianLIB;
import dev.mruniverse.guardianlib.core.utils.CenterText;
import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.game.Game;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class GuardianUtils {
    private final GuardianRFTB plugin;
    private final Utils utils = GuardianLIB.getControl().getUtils();
    public GuardianUtils(GuardianRFTB plugin) {
        this.plugin = plugin;
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
        Game game = plugin.getPlayerData(uuid).getGame();
        String gameType = game.getGameType().getType();
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
        Game game = plugin.getPlayerData(uuid).getGame();
        String gameType = game.getGameType().getType();
        String gameName = game.getName();
        boolean playerBeast = game.getBeasts().contains(player);
        int winOrLoss;
        if(playerBeast) {
            if(winnerTeamIsRunners) {
                winOrLoss = plugin.getSettings().getSettings().getInt("settings.pointSystem.beasts.death");
            } else {
                winOrLoss = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getInt("settings.pointSystem.beasts.win");
            }
        } else {
            if(winnerTeamIsRunners) {
                winOrLoss = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getInt("settings.pointSystem.runners.win");
            } else {
                winOrLoss = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getInt("settings.pointSystem.runners.death");
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
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            UUID uuid = player.getUniqueId();
            Game game = plugin.getPlayerData(uuid).getGame();
            String gameType = game.getGameType().getType();
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

    public Player getRandomBeast(Player player) {
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            if(plugin.getPlayerData(player.getUniqueId()).getGame().getBeasts().size() != 0) {
                return plugin.getPlayerData(player.getUniqueId()).getGame().getBeasts().get(0);
            }
            return player;
        }
        return player;
    }

    public boolean isBeast(Player player) {
        if(plugin.getPlayerData(player.getUniqueId()) == null) return false;
        if(plugin.getPlayerData(player.getUniqueId()).getGame() != null) {
            return plugin.getPlayerData(player.getUniqueId()).getGame().getBeasts().contains(player);
        }
        return false;
    }
    private String getBeast(Game game) {
        if(game.getGameType().equals(GameType.DOUBLE_BEAST)) {
            return game.getBeasts().size()+"";
        }
        if(game.getBeasts().size() != 0) {
            return game.getBeasts().get(0).getName();
        }
        return "none";
    }

}
