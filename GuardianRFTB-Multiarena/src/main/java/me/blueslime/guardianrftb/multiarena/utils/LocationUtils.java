package me.blueslime.guardianrftb.multiarena.utils;

import dev.mruniverse.slimelib.logs.SlimeLogs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {

    public static Location fromString(SlimeLogs logs, String text) {
        if(text.equalsIgnoreCase("notSet")) {
            return null;
        }

        String[] loc = text.split(",");

        World w = Bukkit.getWorld(loc[0]);

        if(w != null) {
            double x = Double.parseDouble(loc[1]);
            double y = Double.parseDouble(loc[2]);
            double z = Double.parseDouble(loc[3]);
            float yaw = Float.parseFloat(loc[4]);
            float pitch = Float.parseFloat(loc[5]);

            return new Location(
                    w,
                    x,
                    y,
                    z,
                    yaw,
                    pitch
            );
        }

        logs.error("Can't get world named: " + loc[0]);
        return null;
    }
}
