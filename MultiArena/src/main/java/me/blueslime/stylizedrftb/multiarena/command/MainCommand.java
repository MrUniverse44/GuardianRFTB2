package me.blueslime.stylizedrftb.multiarena.command;

import dev.mruniverse.slimelib.commands.command.SlimeCommand;
import dev.mruniverse.slimelib.source.SlimeSource;

import me.blueslime.stylizedrftb.multiarena.command.modules.HoloCommand;
import me.blueslime.stylizedrftb.multiarena.command.modules.CoinCommand;
import me.blueslime.stylizedrftb.multiarena.command.modules.GameCommand;
import me.blueslime.stylizedrftb.multiarena.command.modules.NPCCommand;
import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;

public class MainCommand implements SlimeCommand {

    private final CoinCommand coinCommand;
    private final HoloCommand holoCommand;
    private final GameCommand gameCommand;
    private final NPCCommand npcCommand;
    private final StylizedRFTB plugin;
    private final String command;

    public MainCommand(StylizedRFTB plugin, String command) {
        this.gameCommand = new GameCommand(plugin, command);
        this.holoCommand = new HoloCommand(plugin, command);
        this.coinCommand = new CoinCommand(plugin, command);
        this.npcCommand  = new NPCCommand(plugin, command);
        this.command = "&e/" + command;
        this.plugin = plugin;

    }

    @Override
    public String getCommand() {
        return command.replace("&e/", "");
    }

    @Override
    public void execute(SlimeSource sender, String commandLabel, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            if (sender.hasPermission("stylizedrftb.command.usage")) {

            }
        }
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

