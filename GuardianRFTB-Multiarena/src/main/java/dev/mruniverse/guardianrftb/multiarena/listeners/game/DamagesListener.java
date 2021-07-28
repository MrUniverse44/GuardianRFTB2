package dev.mruniverse.guardianrftb.multiarena.listeners.game;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GameStatus;
import dev.mruniverse.guardianrftb.multiarena.enums.GameType;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.SpectatorItems;
import dev.mruniverse.guardianrftb.multiarena.interfaces.Game;
import dev.mruniverse.guardianrftb.multiarena.interfaces.PlayerManager;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.util.Vector;

public class DamagesListener implements Listener {
    private final GuardianRFTB plugin;

    private String byLava;
    private String byVoid;
    private String byBow;
    private String byPvP;
    private String byDefault;

    public DamagesListener(GuardianRFTB plugin) {
        this.plugin = plugin;
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        byLava = messages.getString("messages.game.deathMessages.lava");
        if (byLava == null) byLava = "&7%victim% was on fire!";
        byVoid = messages.getString("messages.game.deathMessages.void");
        if (byVoid == null) byVoid = "&7%victim% was searching a diamond.";
        byDefault = messages.getString("messages.game.deathMessages.otherCause");
        if (byDefault == null) byDefault = "&7%victim% died";
        byPvP = messages.getString("messages.game.deathMessages.pvp");
        if (byPvP == null) byDefault = "&7%victim% was killed by %attacker%";
        byBow = messages.getString("messages.game.deathMessages.bow");
        if (byBow == null) byBow = "&7%victim% was shot by %attacker%";
    }

    @EventHandler
    public void byEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            if (event.getDamager().getType().equals(EntityType.PLAYER)) {
                Player victim = (Player) event.getEntity();
                Player attacker = (Player) event.getDamager();
                PlayerManager mng = plugin.getUser(victim.getUniqueId());
                if (mng == null) return;
                if (mng.getGame() != null) {
                    Game game = mng.getGame();
                    if (game.isInvincible()) event.setCancelled(true);
                    if (game.getRunners().contains(victim) && game.getRunners().contains(attacker))
                        event.setCancelled(true);
                    if (game.getBeasts().contains(victim) && game.getBeasts().contains(attacker))
                        event.setCancelled(true);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void damage(EntityDamageEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) return;
        Player player = (Player) event.getEntity();
        if (plugin.getUser(player.getUniqueId()) == null) return;
        if (plugin.getUser(player.getUniqueId()).getGame() == null) return;
        Game game = plugin.getUser(player.getUniqueId()).getGame();
        if (game.getSpectators().contains(player)) event.setCancelled(true);
        if (game.getStatus() == GameStatus.WAITING || game.getStatus() == GameStatus.STARTING || game.getStatus() == GameStatus.SELECTING || game.isInvincible()) {
            event.setCancelled(true);
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if (game.getBeasts().contains(player)) {
                    player.teleport(game.getSelecting());
                } else {
                    player.teleport(game.getWaiting());
                }
                player.setHealth(20);
                player.setFoodLevel(20);
            }
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        if ((player.getHealth() - event.getFinalDamage()) <= 0 && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            event.setCancelled(true);
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().setBoots(null);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            String deathMessage;
            deathMessage = getDeathMessage(player, event.getCause());
            for (Player inGamePlayer : game.getPlayers()) {
                plugin.getUtils().sendMessage(inGamePlayer, deathMessage);
            }
            if (game.getBeasts().contains(player)) {
                if (!plugin.getSettings().isSecondSpectator()) {
                    player.setGameMode(GameMode.SPECTATOR);
                } else {
                    for (Player player1 : game.getPlayers()) {
                        if (player != player1) player1.hidePlayer(player);
                    }
                    for (SpectatorItems items : SpectatorItems.values()) {
                        items.giveItem(player, plugin);
                    }
                    player.setGameMode(GameMode.ADVENTURE);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                }
                game.deathBeast(player);
                player.teleport(game.getBeastSpawn());
            } else {
                game.deathRunner(player);
                if (!game.getType().equals(GameType.INFECTED)) {
                    if (!plugin.getSettings().isSecondSpectator()) {
                        player.setGameMode(GameMode.SPECTATOR);
                    } else {
                        for (Player player1 : game.getPlayers()) {
                            if (player != player1) player1.hidePlayer(player);
                        }
                        for (SpectatorItems items : SpectatorItems.values()) {
                            items.giveItem(player, plugin);
                        }
                        player.setGameMode(GameMode.ADVENTURE);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                    }
                    player.teleport(game.getRunnerSpawn());
                }
            }
        }
    }

    @EventHandler
    public void removeFire(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player
                && event.isCancelled()
                && plugin.getUser(event.getEntity().getUniqueId()) != null
                && plugin.getUser(event.getEntity().getUniqueId()).getGame() != null) {
            event.getEntity().setFireTicks(0);
        }
    }

    @EventHandler
    public void GameProjectile(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.PLAYER)) {
            Player victim = (Player) event.getEntity();
            if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getDamager();
                Player shooter = (Player) arrow.getShooter();
                if (plugin.getUser(victim.getUniqueId()) == null) return;
                if (plugin.getUser(victim.getUniqueId()).getGame() == null) return;
                Game game = plugin.getUser(victim.getUniqueId()).getGame();
                if (game.getRunners().contains(victim) && game.getRunners().contains(shooter)) {
                    event.setCancelled(true);
                    return;
                }
                if (game.getBeasts().contains(victim) && game.getBeasts().contains(shooter)) {
                    event.setCancelled(true);
                    return;
                }
                if (!event.isCancelled()) {
                    if ((victim.getHealth() - event.getFinalDamage()) <= 0) {
                        String deathMessage;
                        if (shooter != null) {
                            deathMessage = byBow.replace("%victim%", victim.getName()).replace("%attacker%", shooter.getName());
                            plugin.getUser(shooter.getUniqueId()).addKills();
                        } else {
                            deathMessage = byBow.replace("%victim%", victim.getName()).replace("%attacker%", "Unknown Player");
                        }
                        for (Player inGamePlayer : game.getPlayers()) {
                            plugin.getUtils().sendMessage(inGamePlayer, deathMessage);
                        }
                    }
                }
                return;
            }
            if (event.getDamager().getType() == EntityType.PLAYER) {
                if (plugin.getUser(victim.getUniqueId()) == null) return;
                if (plugin.getUser(victim.getUniqueId()).getGame() == null) return;
                Player player = (Player) event.getDamager();
                Game game = plugin.getUser(victim.getUniqueId()).getGame();
                if (game.getRunners().contains(victim) && game.getRunners().contains(player)) {
                    event.setCancelled(true);
                    return;
                }
                if (game.getBeasts().contains(victim) && game.getBeasts().contains(player)) {
                    event.setCancelled(true);
                    return;
                }
                if ((player.getHealth() - event.getFinalDamage()) <= 0) {
                    String deathMessage;
                    deathMessage = byPvP.replace("%victim%", victim.getName()).replace("%attacker%", player.getName());
                    plugin.getUser(player.getUniqueId()).addKills();
                    for (Player inGamePlayer : game.getPlayers()) {
                        plugin.getUtils().sendMessage(inGamePlayer, deathMessage);
                    }
                }
            }
        }
    }

    @EventHandler
    public void spectatorInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        PlayerManager manager = plugin.getUser(player.getUniqueId());
        if(manager != null && manager.getGame() != null && manager.getGame().getSpectators().contains(player)) {
            event.setCancelled(true);
        }
    }







    @SuppressWarnings("UnnecessaryLocalVariable")
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if(plugin.getSettings().isSecondSpectator()) {
            Player victim = (Player) event.getEntity();
            Game game = plugin.getUser(victim.getUniqueId()).getGame();
            if (game == null) return;
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                Player attacker = (Player) event.getDamager();
                if ((game.getStatus() == GameStatus.PLAYING || game.getStatus() == GameStatus.IN_GAME)
                        && game.getSpectators().contains(victim)
                        && (game.getRunners().contains(attacker) || game.getSpectators().contains(attacker)|| game.getBeasts().contains(attacker) || game.getKillers().contains(attacker))) {
                    event.setCancelled(true);
                }
            }
            if (event.getEntity() instanceof Player) {
                if (event.getDamager() instanceof Arrow && (
                        (Arrow) event.getDamager()).getShooter() instanceof Player &&
                        game.getSpectators().size() > 0 &&
                        game.getSpectators().contains(victim)) {
                    Arrow arrow1 = (Arrow) event.getDamager();
                    Vector vector = arrow1.getVelocity();
                    Player shooter = (Player) arrow1.getShooter();
                    Player teleport = victim;
                    teleport.teleport(victim.getLocation().add(0.0D, 2.0D, 0.0D));
                    teleport.setFlying(true);
                    if (shooter != null) {
                        Arrow arrow2 = shooter.launchProjectile(Arrow.class);
                        arrow2.setShooter(shooter);
                        arrow2.setVelocity(vector);
                        arrow2.setBounce(false);
                    }
                    event.setCancelled(true);
                    arrow1.remove();
                }
                if (event.getDamager() instanceof Snowball && (
                        (Snowball) event.getDamager()).getShooter() instanceof Player &&
                        game.getSpectators().size() > 0 &&
                        game.getSpectators().contains(victim)) {
                    Snowball snowball1 = (Snowball) event.getDamager();
                    Vector vector = snowball1.getVelocity();
                    Player shooter = (Player) snowball1.getShooter();
                    Player teleport = victim;
                    teleport.teleport(victim.getLocation().add(0.0D, 2.0D, 0.0D));
                    teleport.setFlying(true);
                    if (shooter != null) {
                        Snowball snowball2 = shooter.launchProjectile(Snowball.class);
                        snowball2.setShooter(shooter);
                        snowball2.setVelocity(vector);
                        snowball2.setBounce(false);
                    }
                    event.setCancelled(true);
                    snowball1.remove();
                }
                if (event.getDamager() instanceof EnderPearl && (
                        (EnderPearl) event.getDamager()).getShooter() instanceof Player &&
                        game.getSpectators().size() > 0 &&
                        game.getSpectators().contains(victim)) {
                    EnderPearl enderPearl1 = (EnderPearl) event.getDamager();
                    Vector vector = enderPearl1.getVelocity();
                    Player shooter = (Player) enderPearl1.getShooter();
                    Player teleport = victim;
                    teleport.teleport(victim.getLocation().add(0.0D, 2.0D, 0.0D));
                    teleport.setFlying(true);
                    if (shooter != null) {
                        EnderPearl enderPearl2 = shooter.launchProjectile(EnderPearl.class);
                        enderPearl2.setShooter(shooter);
                        enderPearl2.setVelocity(vector);
                        enderPearl2.setBounce(false);
                    }
                    event.setCancelled(true);
                    enderPearl1.remove();
                }
                if (event.getDamager() instanceof Egg && (
                        (Egg) event.getDamager()).getShooter() instanceof Player &&
                        game.getSpectators().size() > 0 &&
                        game.getSpectators().contains(victim)) {
                    Egg egg1 = (Egg) event.getDamager();
                    Vector vector = egg1.getVelocity();
                    Player shooter = (Player) egg1.getShooter();
                    Player teleport = victim;
                    teleport.teleport(victim.getLocation().add(0.0D, 2.0D, 0.0D));
                    teleport.setFlying(true);
                    if (shooter != null) {
                        Egg egg2 = shooter.launchProjectile(Egg.class);
                        egg2.setShooter(shooter);
                        egg2.setVelocity(vector);
                        egg2.setBounce(false);
                    }
                    event.setCancelled(true);
                    egg1.remove();
                }
                if (event.getDamager() instanceof ThrownExpBottle && (
                        (ThrownExpBottle) event.getDamager()).getShooter() instanceof Player &&
                        game.getSpectators().size() > 0 &&
                        game.getSpectators().contains(victim)) {
                    ThrownExpBottle thrownExpBottle1 = (ThrownExpBottle) event.getDamager();
                    Vector vector = thrownExpBottle1.getVelocity();
                    Player shooter = (Player) thrownExpBottle1.getShooter();
                    Player teleport = victim;
                    teleport.teleport(victim.getLocation().add(0.0D, 2.0D, 0.0D));
                    teleport.setFlying(true);
                    if (shooter != null) {
                        ThrownExpBottle thrownExpBottle2 = shooter.launchProjectile(ThrownExpBottle.class);
                        thrownExpBottle2.setShooter(shooter);
                        thrownExpBottle2.setVelocity(vector);
                        thrownExpBottle2.setBounce(false);
                        event.setCancelled(true);
                    }
                    thrownExpBottle1.remove();
                }

                if((victim.getHealth() - event.getFinalDamage()) <= 0) {
                    String deathMessage;
                    if(event.getDamager() instanceof Player) {
                        Player attacker = (Player) event.getDamager();
                        deathMessage = byPvP.replace("%victim%", victim.getName()).replace("%attacker%", attacker.getName());
                        plugin.getUser(attacker.getUniqueId()).addKills();
                    } else {
                        deathMessage = byDefault.replace("%victim%", victim.getName());
                    }
                    for (Player inGamePlayer : game.getPlayers()) {
                        plugin.getUtils().sendMessage(inGamePlayer, deathMessage);
                    }
                }
            }
            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                if (game.getSpectators().contains(player) || game.getSpectators().contains(victim))
                    event.setCancelled(true);
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
            case ENTITY_ATTACK:
                return byPvP.replace("%victim%",player.getName());
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
        byPvP = messages.getString("messages.game.deathMessages.pvp");
        if(byPvP == null) byDefault = "&7%victim% was killed by %attacker%";
    }
}
