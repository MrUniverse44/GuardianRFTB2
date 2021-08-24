package dev.mruniverse.guardianrftb.multiarena.enums;

public enum CommandType {
    WIN,
    START,
    END,
    RESTART,
    BEAST_SELECTION;

    public String getPath() {
        String simply = "settings.game.game-actions.";
        switch (this) {
            case RESTART:
                return simply + "restart-actions";
            case END:
                return simply + "end-actions";
            case START:
                return simply + "start-actions";
            case BEAST_SELECTION:
                return simply + "selected-beast-actions";
            default:
            case WIN:
                return simply + "win-actions";
        }
    }
}
