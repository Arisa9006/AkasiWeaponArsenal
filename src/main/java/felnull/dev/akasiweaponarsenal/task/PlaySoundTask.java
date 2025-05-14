package felnull.dev.akasiweaponarsenal.task;

import felnull.dev.akasiweaponarsenal.data.PageData;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaySoundTask extends BukkitRunnable {

    private final Player player;
    private final PageData pageData;

    public PlaySoundTask(Player player, PageData pageData){
        this.player = player;
        this.pageData = pageData;
    }
    @Override
    public void run() {
        player.playSound(player.getLocation(), pageData.openSound, 1f, 1f);
    }
}
