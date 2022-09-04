package me.blueslime.stylizedrftb.multiarena.bossbar;

import com.cryptomorin.xseries.ReflectionUtils;
import me.blueslime.stylizedrftb.multiarena.player.GamePlayer;
import me.blueslime.stylizedrftb.multiarena.player.PluginStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("unused")
public class LegacyBossBar {

    public static final PluginStorage<UUID, Object> WITHER_MAP = PluginStorage.initAsHash();

    private static boolean bossBarMethodNotFound = false;

    public static void sendBossBar(GamePlayer player, String message) {
        sendBossBar(player.getBukkitPlayer(), message);
    }

    public static void sendBossBar(Player player, String message) {
        sendBossBar(player, message, 100);
    }

    public static void sendBossBar(Player player, String message, float percentage) {
        try {
            if (!bossBarMethodNotFound) {
                BossBarCreator.send(player, message, percentage);
                return;
            }
        } catch (Exception ignored) {
            bossBarMethodNotFound = true;
        }

        try {
            if (percentage <= 0) {
                percentage = (float) 0.001;
            }

            Object world = LegacyUtils.WORLD_HANDLER.invoke(
                    player.getLocation().getWorld()
            );

            Object wither = WITHER_MAP.get(
                    player.getUniqueId(),
                    LegacyUtils.WITHER.getConstructor(
                            LegacyUtils.WORLD
                    ).newInstance(
                            world
                    )
            );

            float health = (float) LegacyUtils.WITHER.getMethod(
                    "getMaxHealth"
            ).invoke(
                    wither
            );

            float life = (percentage * health);

            Location location = obtainWitherLocation(
                    player.getLocation()
            );

            LegacyUtils.WITHER.getMethod(
                    "setCustomName",
                    String.class
            ).invoke(
                    wither,
                    message
            );

            LegacyUtils.WITHER.getMethod(
                    "setHealth",
                    float.class
            ).invoke(
                    wither,
                    life
            );

            LegacyUtils.WITHER.getMethod(
                    "setInvisible",
                    boolean.class
            ).invoke(
                    wither,
                    true
            );

            LegacyUtils.WITHER.getMethod(
                    "setLocation",
                    double.class,
                    double.class,
                    double.class,
                    float.class,
                    float.class
            ).invoke(
                    wither,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    0,
                    0
            );

            Object packet = LegacyUtils.PACKET_CLASS.getConstructor(
                    LegacyUtils.ENTITY
            ).newInstance(
                    wither
            );

            ReflectionUtils.sendPacket(
                    player,
                    packet
            );
        } catch (Throwable ignored) { }
    }

    public static void removeBossBar(GamePlayer player) {
        removeBossBar(player.getBukkitPlayer());
    }

    public static void removeBossBar(Player player) {
        try {
            if (!bossBarMethodNotFound) {
                BossBarCreator.remove(player);
                return;
            }
        } catch (Throwable ignored) {
            bossBarMethodNotFound = true;
        }

        if (WITHER_MAP.toMap().containsKey(player.getUniqueId())) {
            Object wither = WITHER_MAP.get(
                    player.getUniqueId()
            );

            try {
                int id = (int)LegacyUtils.WITHER.getMethod(
                        "getId"
                ).invoke(
                        wither
                );

                Object packet = LegacyUtils.ENTITY_DESTROYER.newInstance(
                        id
                );

                ReflectionUtils.sendPacket(
                        player,
                        packet
                );
            } catch (Throwable ignored) { }
        }
    }

    private static Location obtainWitherLocation(Location location) {
        return location.add(
                location.getDirection().multiply(60)
        );
    }

}
