package dev.mruniverse.guardianrftb.multiarena.utils.command;

import dev.mruniverse.guardianlib.core.utils.EnumUtils;
import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.interfaces.PlayerManager;
import dev.mruniverse.guardianrftb.multiarena.utils.command.sub.CoinCommand;
import dev.mruniverse.guardianrftb.multiarena.utils.command.sub.GameCommand;
import dev.mruniverse.guardianrftb.multiarena.utils.command.sub.HoloCommand;
import dev.mruniverse.guardianrftb.multiarena.utils.command.sub.NPCCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class MainCommand implements CommandExecutor {

    private final GuardianRFTB plugin;
    private final String cmdPrefix;
    private final GameCommand gameCommand;
    private final NPCCommand npcCommand;
    private final HoloCommand holoCommand;
    private final CoinCommand coinCommand;

    public MainCommand(GuardianRFTB plugin, String command) {
        this.plugin = plugin;
        this.cmdPrefix = "&e/" + command;
        gameCommand = new GameCommand(plugin,command);
        npcCommand = new NPCCommand(plugin,command);
        holoCommand = new HoloCommand(plugin,command);
        coinCommand = new CoinCommand(plugin,command);
    }

    private boolean hasPermission(CommandSender sender,String permission,boolean sendMessage) {
        boolean check = true;
        if(sender instanceof Player) {
            Player player = (Player)sender;
            check = player.hasPermission(permission);
            if(sendMessage) {
                String permissionMsg = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.no-perms");
                if (permissionMsg == null) permissionMsg = "&cYou need permission &7%permission% &cfor this action.";
                if (!check)
                    plugin.getLib().getUtils().sendMessage(player, permissionMsg.replace("%permission%", permission));
            }
        }
        return check;
    }


    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            Utils utils = plugin.getLib().getUtils();
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(" ");
                utils.sendMessage(sender,"&b------------ &aGuardian RFTB &b------------");
                utils.sendMessage(sender,cmdPrefix + " join (name) &e- &fJoin Arena");
                utils.sendMessage(sender,cmdPrefix + " randomJoin &e- &fRandom Join");
                utils.sendMessage(sender,cmdPrefix + " leave &e- &fLeave CMD");
                if(hasPermission(sender,"grftb.command.joinAll",false)) utils.sendMessage(sender,cmdPrefix + " joinAll (name) &e- &fJoin all server-players to an arena");
                if(hasPermission(sender,"grftb.admin.help",false)) utils.sendMessage(sender,cmdPrefix + " admin &e- &fAdmin commands");
                utils.sendMessage(sender,"&b------------ &aGuardian RFTB &b------------");
                return true;
            }
            if (args[0].equalsIgnoreCase("join")) {
                if(args.length == 1) {
                    if(hasPermission(sender,"grftb.menu.join",true)) ((Player)sender).openInventory(plugin.getGameManager().getGameMainMenu().getInventory());
                    return true;
                }
                plugin.getGameManager().joinGame((Player)sender,args[1]);
                return true;
            }
            if (args[0].equalsIgnoreCase("autoPlay")) {
                if(sender instanceof Player) {
                    Player player = (Player)sender;
                    PlayerManager pM = plugin.getUser(player.getUniqueId());
                    String buttonMessage;
                    if(pM.toggleAutoplay()) {
                        buttonMessage = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.buttons.autoPlay.on");
                        if(buttonMessage == null) buttonMessage = "&aNow you &lENABLED&a the autoPlay option";
                    } else {
                        buttonMessage = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.others.buttons.autoPlay.off");
                        if(buttonMessage == null) buttonMessage = "&cNow you &lDISABLED&c the autoPlay option";
                    }
                    utils.sendMessage(player,buttonMessage);
                    return true;
                }
                utils.sendMessage(sender,"&cThis command only can be used by players.");
                return true;
            }
            if (args[0].equalsIgnoreCase("leave")) {
                if(sender instanceof Player) {
                    Player player = (Player)sender;
                    if (plugin.getUser(player.getUniqueId()).getGame() != null) {
                        plugin.getUser(player.getUniqueId()).getGame().leave(player);
                        return true;
                    }
                    utils.sendMessage(sender, "&cYou aren't playing");
                    return true;
                }
                utils.sendMessage(sender,"&cThis command only can be used by players.");
                return true;
            }
            if (args[0].equalsIgnoreCase("randomJoin")) {
                if(sender instanceof Player) {
                    for (Game game : plugin.getGameManager().getGames()) {
                        if (game.getStatus() == GameStatus.WAITING || game.getStatus() == GameStatus.SELECTING || game.getStatus() == GameStatus.STARTING) {
                            if (game.getPlayers().size() < game.getMax()) {
                                plugin.getGameManager().joinGame((Player) sender, game.getConfigName());
                                return true;
                            }
                        }
                    }
                    utils.sendMessage(sender, "&cAll games are in game or full");
                    return true;
                }
                utils.sendMessage(sender, "&cThis command only can be used for players");
                return true;
            }

            if (args[0].equalsIgnoreCase("playAgain")) {
                if(sender instanceof Player) {
                    if(plugin.getUser(((Player)sender).getUniqueId()).getGame() == null) return true;
                    for (Game game : plugin.getGameManager().getGames()) {
                        if (game.getStatus() == GameStatus.WAITING || game.getStatus() == GameStatus.SELECTING || game.getStatus() == GameStatus.STARTING) {
                            if (game.getPlayers().size() < game.getMax()) {
                                plugin.getUser(((Player)sender).getUniqueId()).getGame().leaveWithoutSending((Player)sender);
                                plugin.getGameManager().joinGame((Player) sender, game.getConfigName());
                                return true;
                            }
                        }
                    }
                    utils.sendMessage(sender, "&cAll games are in game or full");
                    return true;
                }
                utils.sendMessage(sender, "&cThis command only can be used for players");
                return true;
            }

            if (args[0].equalsIgnoreCase("joinAll")) {
                if(hasPermission(sender,"grftb.command.joinAll",true)) {
                    if (args.length != 1) {
                        String game = args[1];
                        utils.sendMessage(sender,"&aSearching game named: &b" + game);
                        Game currentGame = plugin.getGameManager().getGame(game);
                        if (currentGame == null) {
                            utils.sendMessage(sender, "&c&lThis game doesn't exists!");
                            return true;
                        }
                        utils.sendMessage(sender,"&a&lGame Found! &aTrying to send all players to game &b" + game);
                        utils.sendMessage(sender,"&aGame Info:");
                        utils.sendMessage(sender,"&6- &eMax: &f" + currentGame.getMax());
                        utils.sendMessage(sender,"&6- &eOnline: &f" + currentGame.getPlayers().size());
                        utils.sendMessage(sender,"&6- &eStatus: &f" + currentGame.getStatus());
                        utils.sendMessage(sender,"&aPriority Players: ");
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            if (player.hasPermission("guardianrftb.game.joinPriority")) {
                                utils.sendMessage(sender,"&6- &e" + player.getName());
                                currentGame.join(player);
                            }
                        }


                        utils.sendMessage(sender,"&aNormal Players: ");
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            if(!player.hasPermission("guardianrftb.game.joinPriority")) {
                                utils.sendMessage(sender,"&6- &e" + player.getName());
                                if (currentGame.getPlayers().size() != currentGame.getMax()) {
                                    currentGame.join(player);
                                }
                            }
                        }
                        return true;
                    }
                    utils.sendMessage(sender,"&aDude this command is incomplete, please check the correct usage.");
                    return true;
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("admin")) {
                if(args.length == 1 || args[1].equalsIgnoreCase("1")) {
                    if(hasPermission(sender,"grftb.admin.help.game",true)) {
                        sender.sendMessage(" ");
                        utils.sendMessage(sender,"&b------------ &aGuardian RFTB &b------------");
                        utils.sendMessage(sender,"&6Admin - Game Commands Page#1:");
                        utils.sendMessage(sender,cmdPrefix + " admin game create (game) &e- &fCreate Arena");
                        utils.sendMessage(sender,cmdPrefix + " admin game delete (game) &e- &fDelete Arena");
                        utils.sendMessage(sender,cmdPrefix + " admin game setName (game) (name) &e- &fSet game name");
                        utils.sendMessage(sender,cmdPrefix + " admin game setMin (game) (min) &e- &fSet Min Players");
                        utils.sendMessage(sender,cmdPrefix + " admin game setMax (game) (max) &e- &fSet Max Players");
                        utils.sendMessage(sender,cmdPrefix + " admin game addChest (game) (chest) &e- &fAdd chest to your game");
                        utils.sendMessage(sender,cmdPrefix + " admin game delChest (game) (chest) &e- &fRemove chest from your game");
                        utils.sendMessage(sender,cmdPrefix + " admin game addChestLoc (game) (chest) &e- &fAdd chest-loc to your game");
                        utils.sendMessage(sender,cmdPrefix + " admin game delChestLoc (game) (chest) &e- &fDel chest-loc of your game");
                        utils.sendMessage(sender,cmdPrefix + " admin game setChestLimiter (game) (chest) (number) &e- &fSet chest limit of all the game");
                        utils.sendMessage(sender,cmdPrefix + " admin game setBeast (game) (player) &e- &fSet an specific player to be the beast");
                        utils.sendMessage(sender,"&b------------ &a(Page 1&l/4&a) &b------------");
                    }
                    return true;
                }
                if(args[1].equalsIgnoreCase("changeSound")) {
                    if(args.length == 2 || args.length == 3) {
                        utils.sendMessage(sender,"&b------------ &aGuardian RFTB &b------------");
                        utils.sendMessage(sender,"&6Invalid Usage - changeSound usages:");
                        for(GuardianSounds sounds : GuardianSounds.values()) {
                            utils.sendMessage(sender, cmdPrefix + " admin changeSound " + sounds.toString().toUpperCase() + " <your sound>");
                        }
                        utils.sendMessage(sender,"&b------------ &aGuardian RFTB &b------------");
                        return true;
                    }
                    String guardianSound = args[2].toUpperCase();
                    String sound = args[3].toUpperCase();
                    try {
                        if (utils.checkValidSound(sound) && EnumUtils.isValidEnum(GuardianSounds.class,guardianSound)) {
                            Sound sound1 = Sound.valueOf(sound);
                            GuardianSounds sound2 = GuardianSounds.valueOf(guardianSound);
                            plugin.getSoundsInfo().changeSound(sound2,sound1);
                            utils.sendMessage(sender,"&aSound of &b" + sound2.getName() + "&a now is &b" + sound);
                        } else {
                            utils.sendMessage(sender,"&cThe sound or the GuardianSound is incorrect, please check your command.");
                        }
                    }catch (Throwable ignored) { utils.sendMessage(sender,"&cThis sound &e" + sound + " &cdoesn't exists");}
                    return true;
                }
                if(args[1].equalsIgnoreCase("2")) {
                    if(hasPermission(sender,"grftb.admin.help.game",true)) {
                        sender.sendMessage(" ");
                        utils.sendMessage(sender,"&b------------ &aGuardian RFTB &b------------");
                        utils.sendMessage(sender,"&6Admin - Game Commands Page#2:");
                        utils.sendMessage(sender,cmdPrefix + " admin game setMode (game) (mode) &e- &fChange Gamemode of your game");
                        utils.sendMessage(sender,cmdPrefix + " admin game setWaiting (game) &e- &fSet Waiting Location");
                        utils.sendMessage(sender,cmdPrefix + " admin game setBeastSpawn (game) &e- &fBeast Spawn Location");
                        utils.sendMessage(sender,cmdPrefix + " admin game setRunnerSpawn (game) &e- &fRunner Spawn Location");
                        utils.sendMessage(sender,cmdPrefix + " admin game setSelectedBeast (game) &e- &fSelected Beast Location");
                        utils.sendMessage(sender,cmdPrefix + " admin game chests (game) &e- &fSee all chest of your game");
                        utils.sendMessage(sender,cmdPrefix + " admin game enable (game) &e- &fEnable game to play");
                        utils.sendMessage(sender,cmdPrefix + " admin game disable (game) &e- &fDisable game to config");
                        utils.sendMessage(sender,"&b------------ &a(Page 2&l/4&a) &b------------");
                    }
                    return true;
                }

                if(args[1].equalsIgnoreCase("3")) {
                    if(hasPermission(sender,"grftb.admin.help.others",true)) {
                        sender.sendMessage(" ");
                        utils.sendMessage(sender,"&b------------ &aGuardian SkyWars &b------------");
                        utils.sendMessage(sender,"&6Admin - Holograms Commands:");
                        utils.sendMessage(sender,cmdPrefix + " admin holo setHolo (kills-wins-stats) &e- &fSet Holo");
                        utils.sendMessage(sender,cmdPrefix + " admin holo delHolo (kills-wins-stats) &e- &fDel Holo");
                        utils.sendMessage(sender,cmdPrefix + " admin holo list &e- &fList of holograms");
                        utils.sendMessage(sender,"&6Admin - Plugin Commands:");
                        utils.sendMessage(sender,cmdPrefix + " admin reload &e- &fReload the plugin");
                        utils.sendMessage(sender,cmdPrefix + " admin setlobby &e- &fSet Main Lobby");
                        utils.sendMessage(sender,cmdPrefix + " admin modes &e- &fView all modes of the plugin");
                        utils.sendMessage(sender,"&b------------ &a(Page 3&l/4&a) &b------------");
                    }
                    return true;
                }
                if(args[1].equalsIgnoreCase("4")) {
                    if(hasPermission(sender,"grftb.admin.help.others",true)) {
                        sender.sendMessage(" ");
                        utils.sendMessage(sender,"&b------------ &aGuardian SkyWars &b------------");
                        utils.sendMessage(sender,"&6Admin - NPC Commands:");
                        utils.sendMessage(sender,cmdPrefix + " admin npc setNPC (Mode) &e- &fSet NPC");
                        utils.sendMessage(sender,cmdPrefix + " admin npc delNPC (Mode) &e- &fDel NPC");
                        utils.sendMessage(sender,cmdPrefix + " admin npc list &e- &fList of NPCs");
                        utils.sendMessage(sender,"&6Admin - Coins Commands:");
                        utils.sendMessage(sender,cmdPrefix + " admin coins set (player) (coins) &e- &fSet coins of a player");
                        utils.sendMessage(sender,cmdPrefix + " admin coins add (player) (coins) &e- &fAdd coins to a player");
                        utils.sendMessage(sender,cmdPrefix + " admin coins remove (player) (coins) &e- &fRemove coins of a player");
                        utils.sendMessage(sender,"&b------------ &a(Page 4&l/4&a) &b------------");
                    }
                    return true;
                }

                if(args[1].equalsIgnoreCase("setlobby")) {
                    if(hasPermission(sender,"grftb.admin.cmd.lobby",true)) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            String location = utils.getStringFromLocation(player.getLocation());
                            plugin.getSettings().setLocation(player.getLocation());
                            plugin.getStorage().getControl(GuardianFiles.SETTINGS).set("settings.lobby.location", location);
                            plugin.getStorage().save(SaveMode.SETTINGS);
                            utils.sendMessage(sender, "&aLocation now is &b" + location + ".");
                        }
                    }
                }

                if(args[1].equalsIgnoreCase("reload")) {
                    if(hasPermission(sender,"grftb.admin.cmd.reload",true)) {
                        plugin.getGameManager().update();
                        plugin.getListener().reloadListeners();
                        plugin.getStorage().reloadFile(SaveMode.ALL);
                        plugin.getGameManager().getGameMenu(GameType.CLASSIC).reloadMenu();
                        plugin.getGameManager().getGameMenu(GameType.ISLAND_OF_THE_BEAST_KILLER).reloadMenu();
                        plugin.getGameManager().getGameMenu(GameType.KILLER).reloadMenu();
                        plugin.getGameManager().getGameMenu(GameType.ISLAND_OF_THE_BEAST).reloadMenu();
                        plugin.getGameManager().getGameMenu(GameType.ISLAND_OF_THE_BEAST_DOUBLE_BEAST).reloadMenu();
                        plugin.getGameManager().getGameMenu(GameType.INFECTED).reloadMenu();
                        plugin.getGameManager().getGameMenu(GameType.DOUBLE_BEAST).reloadMenu();
                        plugin.getRunnable().update();
                        String lang = plugin.getStorage().getControl(GuardianFiles.SETTINGS).getString("settings.language");
                        if(lang == null) lang = "en";
                        plugin.getStorage().setMessages(lang);
                        plugin.getLogs().info("language (code) loaded: " + lang);
                        if(plugin.getTitleRunnable() != null) plugin.getTitleRunnable().update();
                        utils.sendMessage(sender, "&3» &aReload completed!");
                        plugin.getSettings().update();
                    }
                }

                if(args[1].equalsIgnoreCase("game") && args.length >= 4) {
                    if(hasPermission(sender,"grftb.admin.cmd.game",true)) {
                        gameCommand.usage(sender,getArguments(args));

                    }
                    return true;
                }
                if(args[1].equalsIgnoreCase("holo") && args.length >= 4) {
                    if(hasPermission(sender,"grftb.admin.cmd.holo",true)) {
                        holoCommand.usage(sender,getArguments(args));
                    }
                    return true;
                }
                if(args[1].equalsIgnoreCase("coins") && args.length >= 4) {
                    if(hasPermission(sender,"grftb.admin.cmd.coins",true)) {
                        coinCommand.usage(sender,getArguments(args));
                    }
                    return true;
                }
                if(args[1].equalsIgnoreCase("npc") && args.length >= 4) {
                    if(hasPermission(sender,"grftb.admin.cmd.npc",true)) {
                        npcCommand.usage(sender,getArguments(args));
                    }
                    return true;
                }
            }
            return true;
        } catch (Throwable throwable) {
            plugin.getLogs().error(throwable);
        }
        return true;
    }
    private String[] getArguments(String[] args){
        String[] arguments = new String[args.length - 2];
        int argID = 0;
        int aID = 0;
        for(String arg : args) {
            if(aID != 0 && aID != 1) {
                arguments[argID] = arg;
                argID++;
            }
            aID++;
        }
        return arguments;
    }
}

