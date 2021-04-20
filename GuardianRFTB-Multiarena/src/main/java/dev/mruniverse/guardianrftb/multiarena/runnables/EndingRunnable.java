package dev.mruniverse.guardianrftb.multiarena.runnables;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameTeam;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianBoard;
import dev.mruniverse.guardianrftb.multiarena.enums.PlayerStatus;
import dev.mruniverse.guardianrftb.multiarena.game.GameInfo;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingRunnable extends BukkitRunnable {
    private final GameInfo currentGame;
    private final GameTeam currentWinner;
    private final GuardianRFTB instance = GuardianRFTB.getInstance();
    public EndingRunnable(GameInfo game, GameTeam winnerTeam) {
        this.currentGame = game;
        this.currentWinner = winnerTeam;
    }
    @Override
    public void run() {
        int time = currentGame.getLastTimer();
        if(time != 0 || currentGame.getPlayers().size() > 0) {
            instance.getServer().getScheduler().runTask(instance, () -> {
                if (currentWinner.equals(GameTeam.RUNNERS)) {
                    for (Player player : currentGame.getRunners()) {
                        firework(player, timing(time));
                    }
                } else {
                    for (Player player : currentGame.getBeasts()) {
                        firework(player, timing(time));
                    }
                }
            });
            currentGame.setLastTimer(time - 1);
        } else {
            for (Player player : currentGame.getPlayers()) {
                Location location = instance.getSettings().getLocation();
                if(location != null) {
                    player.teleport(location);
                }
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);
                PlayerManager playerManager = instance.getPlayerData(player.getUniqueId());
                playerManager.setStatus(PlayerStatus.IN_LOBBY);
                playerManager.setGame(null);
                playerManager.setBoard(GuardianBoard.LOBBY);
                player.getInventory().clear();
                for (ItemStack item : instance.getItemsInfo().getLobbyItems().keySet()) {
                    player.getInventory().setItem(instance.getItemsInfo().getSlot(item), item);
                }
                if (playerManager.getLeaveDelay() != 0) {
                    instance.getServer().getScheduler().cancelTask(playerManager.getLeaveDelay());
                    playerManager.setLeaveDelay(0);
                }
                player.updateInventory();
            }
            currentGame.cancelTask();
            currentGame.restart();

        }
    }

    public void firework(Player player,boolean firework) {
        if(!firework) return;
        Firework fa = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta fam = fa.getFireworkMeta();
        int fType = currentGame.getRandom().nextInt(4) + 1;
        FireworkEffect.Type fireworkType = null;
        switch (fType) {
            case 1:
                fireworkType = FireworkEffect.Type.STAR;
                break;
            case 2:
                fireworkType = FireworkEffect.Type.BALL;
                break;
            case 3:
                fireworkType = FireworkEffect.Type.BALL_LARGE;
                break;
            case 4:
                fireworkType = FireworkEffect.Type.CREEPER;
                break;
            case 5:
                fireworkType = FireworkEffect.Type.BURST;
                break;
        }
        int co1 = currentGame.getRandom().nextInt(10) + 1;
        int co2 = currentGame.getRandom().nextInt(10) + 1;
        Color c1 = fireColor(co1);
        Color c2 = fireColor(co2);
        FireworkEffect ef = FireworkEffect.builder().flicker(currentGame.getRandom().nextBoolean()).withColor(c1).withFade(c2).with(fireworkType).trail(currentGame.getRandom().nextBoolean()).build();
        fam.addEffect(ef);
        fam.setPower(1);
        fa.setFireworkMeta(fam);
    }
    public Color fireColor(int c) {
        switch (c) {
            default:
                return Color.YELLOW;
            case 2:
                return Color.RED;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.AQUA;
            case 6:
                return Color.OLIVE;
            case 7:
                return Color.WHITE;
            case 8:
                return Color.ORANGE;
            case 9:
                return Color.TEAL;
            case 10:
                break;
        }
        return Color.LIME;
    }
    public boolean timing(int i) {
        return i % 3 == 0;
    }

}
