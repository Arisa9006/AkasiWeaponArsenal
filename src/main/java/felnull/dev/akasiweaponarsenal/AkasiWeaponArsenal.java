package felnull.dev.akasiweaponarsenal;

import felnull.dev.akasiweaponarsenal.commands.AWAReload;
import felnull.dev.akasiweaponarsenal.commands.OpenArsenalCommand;
import felnull.dev.akasiweaponarsenal.data.PageData;
import felnull.dev.akasiweaponarsenal.dataio.PageDataIO;
import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import felnull.dev.akasiweaponarsenal.gui.listener.GUIClickListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AkasiWeaponArsenal extends JavaPlugin {

    @Getter
    public static AkasiWeaponArsenal INSTANCE;
    @Getter
    public static Map<String, PageData> pageDataMap = new HashMap<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        initCommands();
        initListener();
        PageDataIO.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void initCommands() {
        Bukkit.getPluginCommand("awaopen").setExecutor(new OpenArsenalCommand());
        Bukkit.getPluginCommand("awareload").setExecutor(new AWAReload());
    }
    public void initListener() {
    }
}
