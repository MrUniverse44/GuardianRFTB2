package dev.mruniverse.guardianrftb.multiarena.utils.command.sub;

import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.interfaces.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinCommand {
    private final GuardianRFTB main;
    private final String command;
    public CoinCommand(GuardianRFTB main,String command) {
        this.main = main;
        this.command = command;
    }
    public void usage(CommandSender sender, String[] arguments) {
        Utils utils = main.getLib().getUtils();
        if (arguments[0].equalsIgnoreCase("set")) {
            if (arguments.length == 3) {
                String playerName = arguments[1];
                Player player = Bukkit.getPlayer(playerName);
                if(player != null) {
                    PlayerManager manager = main.getUser(player.getUniqueId());
                    if(manager != null) {
                        int number = Integer.parseInt(arguments[2]);
                        manager.setCoins(number);
                        utils.sendMessage(sender,"&aCoins of &b" + playerName + "&a now is &b" + number);
                        return;
                    }
                    playerIssue(sender,playerName);
                    return;
                }
                playerIssue(sender, playerName);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("add")) {
            if (arguments.length == 3) {
                String playerName = arguments[1];
                Player player = Bukkit.getPlayer(playerName);
                if(player != null) {
                    PlayerManager manager = main.getUser(player.getUniqueId());
                    if(manager != null) {
                        int number = Integer.parseInt(arguments[2]);
                        manager.addCoins(number);
                        utils.sendMessage(sender,"&Added &b" + number + "&a coins to &b" + playerName + "&a.");
                        return;
                    }
                    playerIssue(sender,playerName);
                    return;
                }
                playerIssue(sender, playerName);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("remove")) {
            if (arguments.length == 3) {
                String playerName = arguments[1];
                Player player = Bukkit.getPlayer(playerName);
                if(player != null) {
                    PlayerManager manager = main.getUser(player.getUniqueId());
                    if(manager != null) {
                        int number = Integer.parseInt(arguments[2]);
                        manager.removeCoins(number);
                        utils.sendMessage(sender,"&aRemoved &b" + number + "&a coins to &b" + playerName + "&a.");
                        return;
                    }
                    playerIssue(sender,playerName);
                    return;
                }
                playerIssue(sender, playerName);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        help(sender,utils);
    }
    private void help(CommandSender sender,Utils utils) {
        String cmdPrefix = "&e/" + command;
        sender.sendMessage(" ");
        utils.sendMessage(sender,"&b------------ &aGuardian RFTB &b------------");
        utils.sendMessage(sender,"&6Admin - Coins Commands:");
        utils.sendMessage(sender,cmdPrefix + " admin coins set (player) (coins) &e- &fSet coins of a player");
        utils.sendMessage(sender,cmdPrefix + " admin coins add (player) (coins) &e- &fAdd coins to a player");
        utils.sendMessage(sender,cmdPrefix + " admin coins remove (player) (coins) &e- &fRemove coins of a player");
        utils.sendMessage(sender,"&b------------ &aGuardian RFTB &b------------");
    }
    private void argumentsIssue(CommandSender sender) {
        main.getLib().getUtils().sendMessage(sender,"&aInvalid arguments, please use &b/" + command + " admin &ato see all commands.");
    }
    private void playerIssue(CommandSender sender,String playerName) {
        main.getLib().getUtils().sendMessage(sender,"&aInvalid player, the player &b" + playerName + "&a is not online.");
    }
}
