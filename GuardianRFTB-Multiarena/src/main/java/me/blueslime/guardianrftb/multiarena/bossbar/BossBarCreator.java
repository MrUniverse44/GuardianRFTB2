package me.blueslime.guardianrftb.multiarena.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarCreator {

    public static void send(Player player, String message, float percentage) {
        BossBar bar = (BossBar) LegacyBossBar.WITHER_MAP.get(
                player.getUniqueId(),
                Bukkit.createBossBar(
                        message,
                        BarColor.BLUE,
                        BarStyle.SOLID
                )
        );

        float calculate = percentage / 100;

        bar.removePlayer(player);

        bar.addPlayer(player);

        bar.setProgress(calculate);

        bar.setTitle(message);

        bar.setVisible(true);
    }

    public static void remove(Player player) {
        BossBar bar = (BossBar) LegacyBossBar.WITHER_MAP.get(player.getUniqueId());

        if (bar != null) {
            bar.removePlayer(player);

            LegacyBossBar.WITHER_MAP.remove(
                    player.getUniqueId()
            );
        }
    }

}
