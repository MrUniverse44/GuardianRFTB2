package dev.mruniverse.guardianrftb.multiarena.utils;

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
        for(GuardianSounds sound : GuardianSounds.values()) {
            soundStatus.put(sound,file.getBoolean(sound.getStatusPath()));
            sounds.put(sound,Sound.valueOf(file.getString(sound.getSoundPath())));
            pitch.put(sound,Float.valueOf(Objects.requireNonNull(file.getString(sound.getPitchPath()))));
            volume.put(sound,Float.valueOf(Objects.requireNonNull(file.getString(sound.getVolumePath()))));
        }
    }

    public Sound getSound(GuardianSounds sound) { return sounds.get(sound); }

    public Float getPitch(GuardianSounds sound) { return pitch.get(sound); }

    public Float getVolume(GuardianSounds sound) { return volume.get(sound); }

    public boolean getStatus(GuardianSounds sound) { return soundStatus.get(sound); }

    public void update() {
        soundStatus.clear();
        sounds.clear();
        pitch.clear();
        volume.clear();
        FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.SOUNDS);
        for(GuardianSounds sound : GuardianSounds.values()) {
            soundStatus.put(sound,file.getBoolean(sound.getStatusPath()));
            sounds.put(sound,Sound.valueOf(file.getString(sound.getSoundPath())));
            pitch.put(sound,Float.valueOf(Objects.requireNonNull(file.getString(sound.getPitchPath()))));
            volume.put(sound,Float.valueOf(Objects.requireNonNull(file.getString(sound.getVolumePath()))));
        }
    }
}
