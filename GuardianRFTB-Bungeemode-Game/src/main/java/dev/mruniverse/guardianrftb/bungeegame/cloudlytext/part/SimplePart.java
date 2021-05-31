package dev.mruniverse.guardianrftb.bungeegame.cloudlytext.part;

import net.md_5.bungee.api.chat.BaseComponent;

public class SimplePart implements TextPart {

    private final BaseComponent part;
    public SimplePart(BaseComponent part){
        this.part = part;
    }

    @Override
    public BaseComponent getPart() {
        return part;
    }
}
