package dev.mruniverse.guardianrftb.multiarena.utils;

import dev.mruniverse.guardianlib.core.utils.ExternalLogger;
import dev.mruniverse.guardianrftb.multiarena.GuardianRFTB;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianFiles;
import dev.mruniverse.guardianrftb.multiarena.enums.GuardianSounds;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("unused")
public class SoundsInfo {
    private final GuardianRFTB plugin;
    private final HashMap<GuardianSounds,Boolean> soundStatus = new HashMap<>();
    private final HashMap<GuardianSounds,Sound> sounds = new HashMap<>();
    private final HashMap<GuardianSounds,Float> pitch = new HashMap<>();
    private final HashMap<GuardianSounds,Float> volume = new HashMap<>();

    public SoundsInfo(GuardianRFTB plugin) {
        this.plugin = plugin;
        FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.SOUNDS);
        try {
            for (GuardianSounds sound : GuardianSounds.values()) {
                soundStatus.put(sound, file.getBoolean(sound.getStatusPath(),true));
                sounds.put(sound, Sound.valueOf(file.getString(sound.getSoundPath(),"NOTE_PLING")));
                pitch.put(sound, Float.valueOf(Objects.requireNonNull(file.getString(sound.getPitchPath(),"1.0F"))));
                volume.put(sound, Float.valueOf(Objects.requireNonNull(file.getString(sound.getVolumePath(),"2.0F"))));
            }
        } catch (Throwable ignored) {
            ExternalLogger logs = plugin.getLogs();
            logs.error(" ");
            logs.error("Plugin sounds in sounds.yml aren't correct in your version, please put correct sounds and restart your server");
            logs.error("To prevent issues");
            logs.error(" ");
            logs.error("Plugin sounds in sounds.yml aren't correct in your version, please put correct sounds and restart your server");
            logs.error("To prevent issues");
            logs.error(" ");
            logs.error("Plugin sounds in sounds.yml aren't correct in your version, please put correct sounds and restart your server");
            logs.error("To prevent issues");
            logs.error(" ");
            logs.error("Plugin sounds in sounds.yml aren't correct in your version, please put correct sounds and restart your server");
            logs.error("To prevent issues");
            logs.error(" ");
            logs.error("Plugin sounds in sounds.yml aren't correct in your version, please put correct sounds and restart your server");
            logs.error("To prevent issues");
            logs.error(" ");
        }
    }

    public void unload() {
        soundStatus.clear();
        sounds.clear();
        pitch.clear();
        volume.clear();
    }

    public Sound getSound(GuardianSounds sound) {
        if(sounds.get(sound) == null) updateSound(sound);
        checkForNull(sound);
        return sounds.get(sound);
    }
    public void checkForNull(GuardianSounds sound) {

        if(sounds.get(sound) == null) {
            Sound sd = GuardianUtils.randomEnum(Sound.class);
            plugin.getLogs().info("Sound of '" + sound.getName() + "' has been changed to " + sd.toString());
            plugin.getLogs().info("Because the sound doesn't exists, if you want change this sound");
            plugin.getLogs().info("Use the command &b/rftb admin changeSound " + sound.toString().toUpperCase() + " <your sound>&f.");
            sounds.put(sound,sd);
        }
        if(pitch.get(sound) == null) {
            plugin.getLogs().info("Pitch of '" + sound.getName() + "' has been changed to 2.0F");
            pitch.put(sound,2.0F);
        }
        if(volume.get(sound) == null) {
            plugin.getLogs().info("Volume of '" + sound.getName() + "' has been changed to 1.0F");
            volume.put(sound,1.0F);
        }

    }

    public void changeSound(GuardianSounds sound1,Sound sound2){
        sounds.put(sound1,sound2);
    }

    public Float getPitch(GuardianSounds sound) {
        if(pitch.get(sound) == null) updateSound(sound);
        return pitch.get(sound);
    }

    public Float getVolume(GuardianSounds sound) {
        if(volume.get(sound) == null) updateSound(sound);
        return volume.get(sound);
    }

    public boolean getStatus(GuardianSounds sound) {
        if(soundStatus.get(sound) == null) updateSound(sound);
        return soundStatus.get(sound);
    }

    public void updateSound(GuardianSounds sound) {
        FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.SOUNDS);
        soundStatus.put(sound,file.getBoolean(sound.getStatusPath(),true));
        sounds.put(sound, Sound.valueOf(file.getString(sound.getSoundPath(),"NOTE_PLING")));
        pitch.put(sound, Float.valueOf(Objects.requireNonNull(file.getString(sound.getPitchPath(),"1.0F"))));
        volume.put(sound, Float.valueOf(Objects.requireNonNull(file.getString(sound.getVolumePath(),"2.0F"))));
    }

    public void update() {
        soundStatus.clear();
        sounds.clear();
        pitch.clear();
        volume.clear();
        FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.SOUNDS);
        for(GuardianSounds sound : GuardianSounds.values()) {
            soundStatus.put(sound,file.getBoolean(sound.getStatusPath(),true));
            sounds.put(sound, Sound.valueOf(file.getString(sound.getSoundPath(),"NOTE_PLING")));
            pitch.put(sound, Float.valueOf(Objects.requireNonNull(file.getString(sound.getPitchPath(),"1.0F"))));
            volume.put(sound, Float.valueOf(Objects.requireNonNull(file.getString(sound.getVolumePath(),"2.0F"))));
        }
    }
}
