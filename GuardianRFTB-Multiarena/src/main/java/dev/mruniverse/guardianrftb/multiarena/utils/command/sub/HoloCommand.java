package dev.mruniverse.guardianrftb.multiarena.utils.command.sub;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import org.bukkit.command.CommandSender;

public class HoloCommand {
    private final GuardianRFTB main;
    private final String command;
    public HoloCommand(GuardianRFTB main,String command) {
        this.main = main;
        this.command = command;
    }
    public void usage(CommandSender sender, String[] arguments) {

    }
}
