package dev.mruniverse.guardianrftb.multiarena.listeners.game;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameStatus;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.storage.PlayerManager;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamagesListener implements Listener {
    private final GuardianRFTB plugin;

    private String byLava;
    private String byVoid;
    private String byBow;
    private String byDefault;

    public DamagesListener(GuardianRFTB plugin) {
        this.plugin = plugin;
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        byLava = messages.getString("messages.game.deathMessages.lava");
        if(byLava == null) byLava = "&7%victim% was on fire!";
        byVoid = messages.getString("messages.game.deathMessages.void");
        if(byVoid == null) byVoid = "&7%victim% was searching a diamond.";
        byDefault = messages.getString("messages.game.deathMessages.otherCause");
        if(byDefault == null) byDefault = "&7%victim% died";
        byBow = messages.getString("messages.game.deathMessages.bow");
        if(byBow == null) byBow = "&7%victim% was shot by %attacker%";
    }
    @EventHandler
    public void byEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            if(event.getDamager().getType().equals(EntityType.PLAYER)) {
                Player victim = (Player)event.getEntity();
                Player attacker = (Player)event.getDamager();
                PlayerManager mng = plugin.getPlayerData(victim.getUniqueId());
                if(mng.getGame() != null) {
                    Game game = mng.getGame();
                    if(game.isInvincible()) event.setCancelled(true);
                    if(game.getRunners().contains(victim) && game.getRunners().contains(attacker)) event.setCancelled(true);
                    if(game.getBeasts().contains(victim) && game.getBeasts().contains(attacker)) event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent event) {
        if(!event.getEntity().getType().equals(EntityType.PLAYER)) return;
        Player player = (Player)event.getEntity();
        if(plugin.getPlayerData(player.getUniqueId()) == null) return;
        if(plugin.getPlayerData(player.getUniqueId()).getGame() == null) return;
        Game game = plugin.getPlayerData(player.getUniqueId()).getGame();
        if(game.getStatus() == GameStatus.WAITING || game.getStatus() == GameStatus.STARTING || game.getStatus() == GameStatus.SELECTING || game.isInvincible()) {
            event.setCancelled(true);
            if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if(game.getBeasts().contains(player)) {
                    player.teleport(game.getSelecting());
                } else {
                    player.teleport(game.getWaiting());
                }
                player.setHealth(20);
                player.setFoodLevel(20);
            }
            return;
        }
        if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        if((player.getHealth() - event.getFinalDamage()) <= 0) {
            event.setCancelled(true);
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().setBoots(null);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            String deathMessage;
            deathMessage = getDeathMessage(player,event.getCause());
            for(Player inGamePlayer : game.getPlayers()) {
                plugin.getUtils().sendMessage(inGamePlayer,deathMessage);
            }
            if(game.getBeasts().contains(player)) {
                player.setGameMode(GameMode.SPECTATOR);
                game.deathBeast(player);
                player.teleport(game.getBeastSpawn());
            } else {
                game.deathRunner(player);
                if(!game.getType().equals(GameType.INFECTED)) {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(game.getRunnerSpawn());
                }
            }
        }
    }

    @EventHandler
    public void GameProjectile(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player victim = (Player)event.getEntity();
            if(event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();
                Player shooter = (Player) arrow.getShooter();
                if (plugin.getPlayerData(victim.getUniqueId()).getGame() == null) return;
                Game game = plugin.getPlayerData(victim.getUniqueId()).getGame();
                if (game.getRunners().contains(victim) && game.getRunners().contains(shooter)) {
                    event.setCancelled(true);
                }
                if (game.getBeasts().contains(victim) && game.getBeasts().contains(shooter)) {
                    event.setCancelled(true);
                }
                if(!event.isCancelled()) {
                    if((victim.getHealth() - event.getFinalDamage()) <= 0) {
                        String deathMessage;
                        if(shooter != null) {
                            deathMessage = byBow.replace("%victim%", victim.getName()).replace("%attacker%", shooter.getName());
                        } else {
                            deathMessage = byBow.replace("%victim%", victim.getName()).replace("%attacker%", "Unknown Player");
                        }
                        for(Player inGamePlayer : game.getPlayers()) {
                            plugin.getUtils().sendMessage(inGamePlayer,deathMessage);
                        }
                    }
                }
            }
        }
    }


    private String getDeathMessage(Player player, EntityDamageEvent.DamageCause cause) {
        switch (cause) {
            case FIRE:
            case FIRE_TICK:
            case LAVA:
                return byLava.replace("%victim%",player.getName());
            case DROWNING:
            case SUICIDE:
            case VOID:
                return byVoid.replace("%victim%",player.getName());
            default:
                return byDefault.replace("%victim%",player.getName());
        }
    }

    public void updateAll() {
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        byLava = messages.getString("messages.game.deathMessages.lava");
        if(byLava == null) byLava = "&7%victim% was on fire!";
        byVoid = messages.getString("messages.game.deathMessages.void");
        if(byVoid == null) byVoid = "&7%victim% was searching a diamond.";
        byDefault = messages.getString("messages.game.deathMessages.otherCause");
        if(byDefault == null) byDefault = "&7%victim% died";
        byBow = messages.getString("messages.game.deathMessages.bow");
        if(byBow == null) byBow = "&7%victim% was shot by %attacker%";
    }
}
