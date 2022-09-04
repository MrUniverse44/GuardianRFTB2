package me.blueslime.stylizedrftb.multiarena.game.type;


import me.blueslime.stylizedrftb.multiarena.StylizedRFTB;
import me.blueslime.stylizedrftb.multiarena.enums.GameTeam;
import me.blueslime.stylizedrftb.multiarena.game.Game;
import me.blueslime.stylizedrftb.multiarena.player.GamePlayer;

public class NormalGame extends Game {

    public NormalGame(StylizedRFTB plugin, String name) {
        super(plugin, name);
    }

    public NormalGame(StylizedRFTB plugin, String name, String customName) {
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

