package felnull.dev.akasiweaponarsenal.task;

import felnull.dev.akasiweaponarsenal.data.PageData;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaySoundTask extends BukkitRunnable {

    private final Player player;
    private final Sound sound;
    private final float volume;
    private final float pitch;

    public PlaySoundTask(Player player, Sound sound,float volume, float pitch){
        this.player = player;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }
    @Override
    public void run() {
        if(player != null){
            player.playSound(player.getLocation(), sound, pitch, volume);
        }
    }
}
