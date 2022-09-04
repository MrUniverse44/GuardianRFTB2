package me.blueslime.stylizedrftb.multiarena.utils;

public class PluginUtilities {
    public static class FloatUtil {
        public static float converter(double number) {
            return (float) (number / 20.D);
        }

        public static int meters(double number) {
            return (int) number;
        }
    }
}
