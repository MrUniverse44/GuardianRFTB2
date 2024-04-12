package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerUtil {
    public static List<Player> getPlayers(GuardianRFTB plugin, Collection<UUID> players) {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : players) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                list.add(player);
            }
        }
        return list;
    }
}
