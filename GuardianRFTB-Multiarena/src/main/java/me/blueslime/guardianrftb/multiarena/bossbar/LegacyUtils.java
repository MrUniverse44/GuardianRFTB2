package me.blueslime.guardianrftb.multiarena.bossbar;

import me.blueslime.guardianrftb.multiarena.game.GameVersion;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

public class LegacyUtils {
    public static Class<?> WITHER;
    public static Class<?> WORLD;
    public static Class<?> ENTITY;
    public static Class<?> CRAFT_WORLD;
    public static Class<?> WORLD_SERVER;

    public static Class<?> PACKET_CLASS;

    public static MethodHandle WORLD_HANDLER;

    public static Constructor<?> ENTITY_DESTROYER;

    static {
        try {

            MethodHandles.Lookup lookup = MethodHandles.lookup();

            String version = GameVersion.getCurrent().toString();

            PACKET_CLASS = Class.forName("net.minecraft.server." + version + ".PacketPlayOutSpawnEntityLiving");

            Class<?> destroyer = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityDestroy");

            ENTITY_DESTROYER = destroyer.getConstructor(
                    int.class
            );

            WORLD_SERVER = Class.forName("net.minecraft.server." + version + ".WorldServer");

            CRAFT_WORLD = Class.forName("org.bukkit.craftbukkit." + version + ".CraftWorld");

            ENTITY = Class.forName("net.minecraft.server." + version + ".EntityLiving");

            WITHER = Class.forName("net.minecraft.server." + version + ".EntityWither");

            WORLD = Class.forName("net.minecraft.server." + version + ".World");

            WORLD_HANDLER = lookup.findVirtual(
                    CRAFT_WORLD,
                    "getHandle",
                    MethodType.methodType(
                            WORLD_SERVER
                    )
            );

        } catch (ReflectiveOperationException ignored) {}
    }

}
