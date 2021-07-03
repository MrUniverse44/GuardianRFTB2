package dev.mruniverse.guardianrftb.multiarena.interfaces;

import dev.mruniverse.guardianrftb.multiarena.enums.*;
import dev.mruniverse.guardianrftb.multiarena.kits.KitMenu;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface PlayerManager {
    boolean hasSelectedKit();

    void setCurrentRole(GameTeam currentRole);

    String getCurrentRole();

    boolean getAutoPlayStatus();


    boolean toggleAutoplay();

    KitMenu getKitMenu(KitType kitType);

    void setLeaveDelay(int delay);
    void setStatus(PlayerStatus status);
    void setBoard(GuardianBoard board);
    void setGame(Game game);
    GuardianBoard getBoard();
    PlayerStatus getStatus();
    String getName();
    Game getGame();
    Player getPlayer();
    int getLeaveDelay();
    int getWins();

    void setWins(int wins);

    void addWins();

    void addCoins(int coins);

    void removeCoins(int coins);

    boolean getPointStatus();

    Location getLastCheckpoint();

    void setPointStatus(boolean bol);

    void setLastCheckpoint(Location location);

    int getCoins();
    void updateCoins(int addOrRemove);
    void setCoins(int coinCounter);

    int getKills();

    void setKills(int kills);

    void setSelectedKit(String kitID);

    void setKits(String kits);

    String getSelectedKit();

    void addKit(String kitID);

    List<String> getKits();

    @SuppressWarnings("unused")
    void addKills();

    int getDeaths();

    void setDeaths(int deaths);

    String getID();
sssssssssssss
    void addDeaths();

    void create();
}
