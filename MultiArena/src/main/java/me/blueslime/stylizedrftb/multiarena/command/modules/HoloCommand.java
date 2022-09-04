package me.blueslime.stylizedrftb.multiarena.command.modules;

import dev.mruniverse.guardianlib.core.utils.Utils;
import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import org.bukkit.command.CommandSender;

public class HoloCommand {
    private final StylizedRFTB main;
    private final String command;
    public HoloCommand(StylizedRFTB main, String command) {
        this.main = main;
        this.command = command;
    }
    public void usage(CommandSender sender, String[] arguments) {
        Utils utils = main.getLib().getUtils();
        utils.sendMessage(sender,"&cThis system is under maintenance.");
    }
}
