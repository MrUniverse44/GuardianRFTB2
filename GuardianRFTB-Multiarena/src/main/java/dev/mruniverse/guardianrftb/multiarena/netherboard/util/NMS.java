package dev.mruniverse.guardianrftb.multiarena.netherboard.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
@SuppressWarnings("unused")
public class NMS {

    private static final String packageName;
    private static final Version version;

    public static final Field PLAYER_SCORES;

    public static final Constructor<?> PACKET_SCORE_REMOVE;
    public static final Constructor<?> PACKET_SCORE;

    public static final Object ENUM_SCORE_ACTION_CHANGE;
    public static final Object ENUM_SCORE_ACTION_REMOVE;

    public static final Constructor<?> SB_SCORE;
    public static final Method SB_SCORE_SET;

    public static final Constructor<?> PACKET_OBJ;
    public static final Constructor<?> PACKET_DISPLAY;

    public static final Field PLAYER_CONNECTION;
    public static final Method SEND_PACKET;

    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();

        String ver = name.substring(name.lastIndexOf('.') + 1);
        version = new Version(ver);

        packageName = "net.minecraft.server." + ver;

        Field playerScores = null;

        Constructor<?> packetScoreRemove = null;
        Constructor<?> packetScore = null;

        Object enumScoreActionChange = null;
        Object enumScoreActionRemove = null;

        Constructor<?> sbScore = null;
        Method sbScoreSet = null;

        Constructor<?> packetObj = null;
        Constructor<?> packetDisplay = null;

        Field playerConnection = null;
        Method sendPacket = null;

        try {
            Class<?> packetScoreClass = getClass("PacketPlayOutScoreboardScore");
            Class<?> packetDisplayClass = getClass("PacketPlayOutScoreboardDisplayObjective");
            Class<?> packetObjClass = getClass("PacketPlayOutScoreboardObjective");

            Class<?> scoreClass = getClass("ScoreboardScore");
            Class<?> scoreActionClass = getClass("ScoreboardServer$Action");

            Class<?> sbClass = getClass("Scoreboard");
            Class<?> objClass = getClass("ScoreboardObjective");

            Class<?> playerClass = getClass("EntityPlayer");
            Class<?> playerConnectionClass = getClass("PlayerConnection");
            Class<?> packetClass = getClass("Packet");
            if(sbClass != null && scoreClass != null && packetObjClass != null & packetScoreClass != null) {
                playerScores = sbClass.getDeclaredField("playerScores");
                playerScores.setAccessible(true);

                sbScore = scoreClass.getConstructor(sbClass, objClass, String.class);
                sbScoreSet = scoreClass.getMethod("setScore", int.class);

                packetObj = packetObjClass.getConstructor(objClass, int.class);

                switch (version.getMajor()) {
                    case "1.7":
                        packetScore = packetScoreClass.getConstructor(scoreClass, int.class);
                        break;
                    case "1.8":
                    case "1.9":
                    case "1.10":
                    case "1.11":
                    case "1.12":
                        packetScore = packetScoreClass.getConstructor(scoreClass);
                        packetScoreRemove = packetScoreClass.getConstructor(String.class, objClass);
                        break;
                    default:
                        packetScore = packetScoreClass.getConstructor(scoreActionClass,
                                String.class, String.class, int.class);
                        if(scoreActionClass != null) {
                            enumScoreActionChange = scoreActionClass.getEnumConstants()[0];
                            enumScoreActionRemove = scoreActionClass.getEnumConstants()[1];
                        }
                        break;
                }
                if(packetDisplayClass != null) packetDisplay = packetDisplayClass.getConstructor(int.class, objClass);

                if(playerClass != null) playerConnection = playerClass.getField("playerConnection");
                if(playerConnectionClass != null) sendPacket = playerConnectionClass.getMethod("sendPacket", packetClass);
            }
        } catch(Throwable ignored) {}

        PLAYER_SCORES = playerScores;

        PACKET_SCORE_REMOVE = packetScoreRemove;
        PACKET_SCORE = packetScore;

        ENUM_SCORE_ACTION_CHANGE = enumScoreActionChange;
        ENUM_SCORE_ACTION_REMOVE = enumScoreActionRemove;

        SB_SCORE = sbScore;
        SB_SCORE_SET = sbScoreSet;

        PACKET_OBJ = packetObj;
        PACKET_DISPLAY = packetDisplay;

        PLAYER_CONNECTION = playerConnection;
        SEND_PACKET = sendPacket;
    }

    public static Version getVersion() {
        return version;
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(packageName + "." + name);
        } catch(ClassNotFoundException e) {
            return null;
        }
    }

    private static final Map<Class<?>, Method> handles = new HashMap<>();
    public static Object getHandle(Object obj) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        Class<?> clazz = obj.getClass();

        if(!handles.containsKey(clazz)) {
            Method method = clazz.getDeclaredMethod("getHandle");

            if(!method.isAccessible())
                method.setAccessible(true);

            handles.put(clazz, method);
        }

        return handles.get(clazz).invoke(obj);
    }

    public static void sendPacket(Object packet, Player... players) {
        for(Player p : players) {
            try {
                Object playerConnection = PLAYER_CONNECTION.get(getHandle(p));
                SEND_PACKET.invoke(playerConnection, packet);
            } catch(Throwable ignored) {}
        }
    }

    public static class Version {

        private final String name;

        private final String major;
        private final String minor;

        Version(String name) {
            this.name = name;

            String[] splitted = name.split("_");

            this.major = splitted[0].substring(1) + "." + splitted[1];
            this.minor = splitted[2].substring(1);
        }

        public String getName() { return name; }

        public String getMajor() { return major; }
        public String getMinor() { return minor; }

    }

}
