package dev.mruniverse.guardianrftb.multiarena.utils.command.sub;

import dev.mruniverse.guardianlib.core.utils.Utils;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import org.bukkit.command.CommandSender;

public class NPCCommand {
    private final GuardianRFTB main;
    private final String command;
    public NPCCommand(GuardianRFTB main,String command) {
        this.main = main;
        this.command = command;
    }

    public void usage(CommandSender sender, String[] arguments) {
        Utils utils = main.getLib().getUtils();
        utils.sendMessage(sender,"&cThis system is under maintenance.");
    }
}
