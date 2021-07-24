package dev.mruniverse.guardianrftb.multiarena.utils.command.sub;

import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.SaveMode;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.listeners.api.GameSelectedBeastEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameCommand {
    private final GuardianRFTB main;
    private final String command;
    public GameCommand(GuardianRFTB main,String command) {
        this.main = main;
        this.command = command;
    }
    public void usage(CommandSender sender, String[] arguments) {
        Utils utils = main.getLib().getUtils();
        if (arguments[0].equalsIgnoreCase("create")) {
            if (arguments.length == 2) {
                String game = arguments[1];
                if (main.getGameManager().getGame(game) == null) {
                    if (!main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                        main.getGameManager().createGameFiles(game);
                        String message = main.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.admin.create");
                        if (message == null) message = "&aArena &b%arena_id% &acreated correctly!";
                        utils.sendMessage(sender, message.replace("%arena_id%", game));
                        return;
                    }
                    utils.sendMessage(sender, "&cThis game already exists in &6games.yml");
                    return;
                }
                utils.sendMessage(sender, "&cThis game already exists");
                return;
            }
            return;
        }
        if (arguments[0].equalsIgnoreCase("delete")) {
            if (arguments.length == 2) {
                String game = arguments[1];
                if (main.getGameManager().getGame(game) != null || main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                    String message = main.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.admin.delete");
                    if (message == null) message = "&aArena &b%arena_id% &aremoved correctly!";
                    utils.sendMessage(sender, message.replace("%arena_id%", game));
                    main.getStorage().getControl(GuardianFiles.GAMES).set("games." + game, null);
                    return;
                }
                arenaIssue(sender, game);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("setMin")) {
            if (arguments.length == 3) {
                String game = arguments[1];
                if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                    main.getGameManager().setMin(game, Integer.valueOf(arguments[2]));
                    utils.sendMessage(sender, "&aMin now is &b" + arguments[2]);
                    return;
                }
                arenaIssue(sender, game);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("setMax")) {
            if (arguments.length == 3) {
                String game = arguments[1];
                if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                    main.getGameManager().setMax(game, Integer.valueOf(arguments[2]));
                    utils.sendMessage(sender, "&aMax now is &b" + arguments[2]);
                    return;
                }
                arenaIssue(sender, game);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("chests")) {
            if (arguments.length == 2) {
                String game = arguments[1];
                if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                    List<String> chests = main.getStorage().getControl(GuardianFiles.GAMES).getStringList("games." + game + ".chests");
                    if(chests.size() != 0) {
                        for (String chest : chests) {
                            utils.sendMessage(sender, "&b- &e" + chest);
                        }
                        utils.sendMessage(sender, "&aThis game has " + chests.size() + " chest(s).");
                        return;
                    }
                    utils.sendMessage(sender,"&cThis game doesn't have chests yet.");
                    return;
                }
                arenaIssue(sender, game);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("enable")) {
            if (arguments.length == 2) {
                String game = arguments[1];
                if (main.getGameManager().getGame(game) == null) {
                    if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                        main.getStorage().getControl(GuardianFiles.GAMES).set("games." + game + ".enabled",true);
                        main.getGameManager().addGame(game,main.getStorage().getControl(GuardianFiles.GAMES).getString("games." + game + ".gameName"));
                        main.getStorage().save(SaveMode.GAMES_FILES);
                        utils.sendMessage(sender,"&aGame &b" + game + "&a enabled.");
                        return;
                    }
                    arenaIssue(sender, game);
                    return;
                }
                utils.sendMessage(sender,"&cThis game is already enabled!");
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("disable")) {
            if (arguments.length == 2) {
                String game = arguments[1];
                if (main.getGameManager().getGame(game) != null) {
                    if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                        main.getStorage().getControl(GuardianFiles.GAMES).set("games." + game + ".enabled",false);
                        main.getGameManager().delGame(game);
                        main.getStorage().save(SaveMode.GAMES_FILES);
                        utils.sendMessage(sender,"&aGame &b" + game + "&a disabled.");
                        return;
                    }
                    main.getGameManager().delGame(game);
                    arenaIssue(sender, game);
                    return;
                }
                utils.sendMessage(sender,"&cThis game is already disabled!");
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("setName")) {
            if (arguments.length == 3) {
                String game = arguments[1];
                if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                    main.getGameManager().setGameName(game, arguments[2]);
                    if (main.getGameManager().getConfigGame(game) != null)
                        main.getGameManager().getConfigGame(game).setName(arguments[2]);
                    utils.sendMessage(sender, "&aGame name now is &b" + arguments[2]);
                    return;
                }
                arenaIssue(sender, game);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("setBeast")) {
            if(arguments.length == 3) {
                String game = arguments[1];
                String playerString = arguments[2];
                Player player = Bukkit.getPlayer(playerString);
                if(player == null) {
                    utils.sendMessage(sender,"&aThis current player &b" + playerString + " &ais not online.");
                    return;
                }
                if(main.getGameManager().existGame(game)) {
                    Game currentGame = main.getGameManager().getGame(game);
                    if(currentGame.getRunners().contains(player)) {
                        setBeast(currentGame,player,utils);

                    }
                }
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("addChest")) {
            if (arguments.length == 3) {
                String game = arguments[1];
                if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                    if (main.getStorage().getControl(GuardianFiles.GAMES).get("games." + game + ".chests") != null) {
                        if (main.getStorage().getControl(GuardianFiles.GAMES).getStringList("games." + game + ".chests").contains(arguments[2])) {
                            utils.sendMessage(sender, "&cThis chest already exists in game '&e" + game + "&c'");
                            return;
                        }
                        List<String> chests = main.getStorage().getControl(GuardianFiles.GAMES).getStringList("games." + game + ".chests");
                        chests.add(arguments[2]);
                        utils.sendMessage(sender, "&aChest &b" + arguments[2] + " &aadded to game &b" + game + "&a.");
                        main.getStorage().getControl(GuardianFiles.GAMES).set("games." + game + ".chests", chests);
                        main.getStorage().save(SaveMode.GAMES_FILES);
                        return;
                    }
                    List<String> chests = new ArrayList<>();
                    chests.add(arguments[2]);
                    utils.sendMessage(sender, "&aChest &b" + arguments[2] + " &aadded to game &b" + game + "&a.");
                    main.getStorage().getControl(GuardianFiles.GAMES).set("games." + game + ".chests", chests);
                    main.getStorage().save(SaveMode.GAMES_FILES);
                    return;
                }
                arenaIssue(sender, game);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("delChest")) {
            if (arguments.length == 3) {
                String game = arguments[1];
                String chest = arguments[2];
                if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                    if (main.getStorage().getControl(GuardianFiles.GAMES).get("games." + game + ".chests") != null) {
                        if (!main.getStorage().getControl(GuardianFiles.GAMES).getStringList("games." + game + ".chests").contains(chest)) {
                            utils.sendMessage(sender, "&cThis chest doesn't exists in game '&e" + game + "&c'");
                            return;
                        }
                        List<String> chests = main.getStorage().getControl(GuardianFiles.GAMES).getStringList("games." + game + ".chests");
                        chests.remove(chest);
                        utils.sendMessage(sender, "&aChest &b" + chest + " &aremoved from game &b" + game + "&a.");
                        main.getStorage().getControl(GuardianFiles.GAMES).set("games." + game + ".chests", chests);
                        main.getStorage().save(SaveMode.GAMES_FILES);
                        return;
                    }
                    if (!main.getStorage().getControl(GuardianFiles.GAMES).getStringList("games." + game + ".chests").contains(chest)) {
                        utils.sendMessage(sender, "&cThis chest doesn't exists in game '&e" + game + "&c'");
                        return;
                    }
                }
                arenaIssue(sender, game);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if(arguments[0].equalsIgnoreCase("setMode")) {
            if(arguments.length == 3) {
                String game = arguments[1];
                String mode = arguments[2];
                if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                    GameType gameType = GameType.CLASSIC;
                    if(mode.equalsIgnoreCase("INFECTED")) gameType = GameType.INFECTED;
                    if(mode.equalsIgnoreCase("DOUBLE_BEAST")) gameType = GameType.DOUBLE_BEAST;
                    main.getGameManager().setMode(game, gameType);
                    utils.sendMessage(sender,"&aMode now is &b" + gameType.toString().toUpperCase());
                    return;
                }
                arenaIssue(sender,game);
                return;
            }
        }
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (arguments[0].equalsIgnoreCase("setWaiting")) {
                if (arguments.length == 2) {
                    String game = arguments[1];
                    if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                        main.getGameManager().setWaiting(game,player.getLocation());
                        utils.sendMessage(sender, getSpawnMessage(game,"Waiting",player.getLocation()));
                        return;
                    }
                    arenaIssue(sender, game);
                    return;
                }
                argumentsIssue(sender);
                return;
            }
            if (arguments[0].equalsIgnoreCase("setBeastSpawn")) {
                if (arguments.length == 2) {
                    String game = arguments[1];
                    if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                        main.getGameManager().setBeast(game,player.getLocation());
                        utils.sendMessage(sender, getSpawnMessage(game,"Beast",player.getLocation()));
                        return;
                    }
                    arenaIssue(sender, game);
                    return;
                }
                argumentsIssue(sender);
                return;
            }
            if (arguments[0].equalsIgnoreCase("setSelectedBeast")) {
                if (arguments.length == 2) {
                    String game = arguments[1];
                    if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                        main.getGameManager().setSelectedBeast(game,player.getLocation());
                        utils.sendMessage(sender, getSpawnMessage(game,"SelectedBeast",player.getLocation()));
                        return;
                    }
                    arenaIssue(sender, game);
                    return;
                }
                argumentsIssue(sender);
                return;
            }
            if (arguments[0].equalsIgnoreCase("setRunnerSpawn")) {
                if (arguments.length == 2) {
                    String game = arguments[1];
                    if (main.getStorage().getControl(GuardianFiles.GAMES).contains("games." + game)) {
                        main.getGameManager().setRunners(game,player.getLocation());
                        utils.sendMessage(sender, getSpawnMessage(game,"Runners",player.getLocation()));
                        return;
                    }
                    arenaIssue(sender, game);
                    return;
                }
                argumentsIssue(sender);
                return;
            }
            if (arguments[0].equalsIgnoreCase("addChestLoc")) {
                if (arguments.length == 3) {
                    String game = arguments[1];
                    String chest = arguments[2];
                    FileConfiguration file = main.getStorage().getControl(GuardianFiles.GAMES);
                    if (file.contains("games." + game)) {
                        String path = "games." + game + ".chests-location." + chest;
                        String toAdd = utils.getStringFromLocation(player.getTargetBlock(null, 5).getLocation());
                        if (falseChest(player.getTargetBlock(null, 5).getType())) {
                            utils.sendMessage(sender, "&cThis block is not a chest.");
                            return;
                        }
                        if (file.get("games." + game + ".chests") == null) {
                            utils.sendMessage(sender, "&cThe chest &f" + chest + " &cis not added in chest list of game &f" + game);
                            return;
                        }
                        if (!file.getStringList("games." + game + ".chests").contains(chest)) {
                            utils.sendMessage(sender, "&cThe chest &f" + chest + " &cis not added in chest list of game &f" + game);
                            return;
                        }
                        if (file.get(path) != null) {
                            if (file.getStringList(path).contains(chest)) {
                                utils.sendMessage(sender, "&cThis chest location already exists in game '&e" + game + "&c'");
                                return;
                            }
                            List<String> chests = file.getStringList(path);
                            chests.add(toAdd);
                            utils.sendMessage(sender, "&aChest Location added to chest &b" + chest + " &ain game&b " + game + "&a.");
                            main.getStorage().getControl(GuardianFiles.GAMES).set(path, chests);
                            main.getStorage().save(SaveMode.GAMES_FILES);
                            return;
                        }
                        List<String> chests = new ArrayList<>();
                        chests.add(toAdd);
                        utils.sendMessage(sender, "&aChest Location added to chest &b" + chest + " &ain game&b " + game + "&a.");
                        main.getStorage().getControl(GuardianFiles.GAMES).set(path, chests);
                        main.getStorage().save(SaveMode.GAMES_FILES);
                        return;
                    }
                    arenaIssue(sender, game);
                    return;
                }
                argumentsIssue(sender);
                return;
            }
            if (arguments[0].equalsIgnoreCase("delChestLoc")) {
                if (arguments.length == 3) {
                    String game = arguments[1];
                    String chest = arguments[2];
                    FileConfiguration file = main.getStorage().getControl(GuardianFiles.GAMES);
                    if (file.contains("games." + game)) {
                        if(falseChest(player.getTargetBlock(null,5).getType())) {
                            utils.sendMessage(sender,"&cThis block is not a chest.");
                            return;
                        }
                        String path = "games." + game + ".chests-location." + chest;
                        String toRemove = utils.getStringFromLocation(player.getTargetBlock(null,5).getLocation());
                        if(file.get("games." + game + ".chests") == null) {
                            utils.sendMessage(sender,"&cThe chest &f" + chest + " &cis not added in chest list of game &f" + game);
                            return;
                        }
                        if(!file.getStringList("games." + game + ".chests").contains(chest)) {
                            utils.sendMessage(sender,"&cThe chest &f" + chest + " &cis not added in chest list of game &f" + game);
                            return;
                        }
                        if(file.get(path) != null) {
                            if(!file.getStringList(path).contains(chest)) {
                                utils.sendMessage(sender,"&cThis chest location already doesn't exists in game '&e" + game + "&c'");
                                return;
                            }
                            List<String> chests = file.getStringList(path);
                            chests.remove(toRemove);
                            utils.sendMessage(sender,"&aChest Location removed from chest &b" + chest + " &ain game&b " + game + "&a.");
                            main.getStorage().getControl(GuardianFiles.GAMES).set(path,chests);
                            main.getStorage().save(SaveMode.GAMES_FILES);
                            return;
                        }
                        utils.sendMessage(sender,"&cThis chest location already doesn't exists in game '&e" + game + "&c'");
                        return;
                    }
                    arenaIssue(sender, game);
                    return;
                }
                argumentsIssue(sender);
                return;
            }
        }
        argumentsIssue(sender);
    }

    private void setBeast(Game currentGame,Player player,Utils utils) {

        FileConfiguration configuration = main.getStorage().getControl(GuardianFiles.MESSAGES);
        String chosenBeast = configuration.getString("messages.game.chosenBeast","&eThe player &b%player% &enow is a beast!");
        String prefix = configuration.getString("messages.prefix","");
        currentGame.getBeasts().add(player);
        currentGame.getRunners().remove(player);
        GameSelectedBeastEvent event = new GameSelectedBeastEvent(currentGame,player);
        Bukkit.getPluginManager().callEvent(event);
        for(Player game : currentGame.getPlayers()) {
            utils.sendMessage(game,prefix + chosenBeast.replace("%player%",player.getName()));
        }
        player.getInventory().clear();
        player.getInventory().setItem(main.getItemsInfo().getBeastSlot(), main.getItemsInfo().getKitBeast());
        player.getInventory().setItem(main.getItemsInfo().getExitSlot(), main.getItemsInfo().getExit());
        player.teleport(currentGame.getSelecting());
        main.getUser(player.getUniqueId()).setCurrentRole(GameTeam.BEASTS);
    }

    private boolean falseChest(Material evalMaterial) {
        if(evalMaterial.equals(Material.CHEST)) return false;
        if(evalMaterial.equals(Material.TRAPPED_CHEST)) return false;
        return (!evalMaterial.equals(Material.ENDER_CHEST));
    }

    private void arenaIssue(CommandSender sender,String game) {
        String message = main.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.admin.arenaError");
        if(message == null) message = "&7%arena_id% &cdoesn't exists";
        main.getLib().getUtils().sendMessage(sender, message.replace("%arena_id%", game));
    }
    private void argumentsIssue(CommandSender sender) {
        main.getLib().getUtils().sendMessage(sender,"&aInvalid arguments, please use &b/" + command + " admin &ato see all commands.");
    }
    private String getSpawnMessage(String game,String location, Location loc) {
        String message = main.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.admin.setSpawn");
        if(message == null) message = "&aSpawn-%spawnType% now is in &b%location%";
        return message.replace("%arena_id%", game).replace("%spawnType%",location).replace("%location%","X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ());

    }
}
