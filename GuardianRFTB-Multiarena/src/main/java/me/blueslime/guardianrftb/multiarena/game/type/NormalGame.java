package me.blueslime.guardianrftb.multiarena.game.type;


import me.blueslime.guardianrftb.multiarena.GuardianRFTB;
import me.blueslime.guardianrftb.multiarena.enums.GameTeam;
import me.blueslime.guardianrftb.multiarena.game.Game;
import me.blueslime.guardianrftb.multiarena.player.GamePlayer;

public class NormalGame extends Game {

    public NormalGame(GuardianRFTB plugin, String name) {
        super(plugin, name);
    }

    public NormalGame(GuardianRFTB plugin, String name, String customName) {
        super(plugin, name, customName);
    }

    @Override
    public void join(GamePlayer player) {

    }

    @Override
    public void quit(GamePlayer player) {

    }

    @Override
    public void win(GameTeam team) {

    }

    @Override
    public void onInitialize() {
        super.onInitialize();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void checkPlayers() {

    }

    @Override
    public int getNeedPlayers() {
        return 0;
    }
}

