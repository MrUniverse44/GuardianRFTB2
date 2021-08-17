package dev.mruniverse.guardianrftb.multiarena.enums;

import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import org.bukkit.inventory.ItemStack;

public enum GameItems {
    RUNNER_KIT,
    BEAST_KIT,
    KILLER_KIT,
    LEAVE_ITEM,
    CHECKPOINT;

    public ItemFunction getItemFunction() {
        switch (this) {
            case BEAST_KIT:
                return ItemFunction.KIT_BEASTS;
            case CHECKPOINT:
                return ItemFunction.CHECKPOINT;
            case LEAVE_ITEM:
                return ItemFunction.EXIT_GAME;
            case KILLER_KIT:
                return ItemFunction.KIT_KILLERS;
            default:
            case RUNNER_KIT:
                return ItemFunction.KIT_RUNNERS;
        }
    }

    public String getItemPath() {
        switch (this) {
            case BEAST_KIT:
                return "InGame.BeastKit.";
            case CHECKPOINT:
                return "InGame.backCheckpointItem.";
            case LEAVE_ITEM:
                return "InGame.Exit.";
            case KILLER_KIT:
                return "InGame.KillerKit.";
            default:
            case RUNNER_KIT:
                return "InGame.RunnerKit.";
        }
    }

    public void slot(GuardianRFTB main,int slot) {
        switch (this) {
            case KILLER_KIT:
                main.getItemsInfo().setKillerSlot(slot);
                break;
            case BEAST_KIT:
                main.getItemsInfo().setBeastSlot(slot);
                break;
            case LEAVE_ITEM:
                main.getItemsInfo().setExitSlot(slot);
                break;
            case RUNNER_KIT:
                main.getItemsInfo().setRunnerSlot(slot);
                break;
        }
    }

    public void set(GuardianRFTB main, ItemStack item) {
        switch (this) {
            case BEAST_KIT:
                main.getItemsInfo().setKitBeast(item);
                if(item == null) main.getItemsInfo().setKitBeastStatus(false);
            case CHECKPOINT:
                main.getItemsInfo().setCheckPoint(item);
                break;
            case LEAVE_ITEM:
                main.getItemsInfo().setExit(item);
                if(item == null) main.getItemsInfo().setExitStatus(false);
                break;
            case KILLER_KIT:
                main.getItemsInfo().setKitKiller(item);
                if(item == null) main.getItemsInfo().setKitKillerStatus(false);
                break;
            default:
            case RUNNER_KIT:
                main.getItemsInfo().setKitRunner(item);
                if(item == null) main.getItemsInfo().setKitRunnerStatus(false);
                break;
        }
    }
}
