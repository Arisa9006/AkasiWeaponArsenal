package felnull.dev.akasiweaponarsenal.data;

import lombok.Getter;
import org.bukkit.Sound;
@Getter
public class SoundData {
    public Sound sound;
    public float volume;
    public float pitch;
    public int delay;

    public SoundData(Sound sound, float volume, float pitch, int delay) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.delay = delay;
    }
}
