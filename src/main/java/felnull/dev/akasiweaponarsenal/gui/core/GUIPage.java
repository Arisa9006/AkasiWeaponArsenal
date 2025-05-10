package felnull.dev.akasiweaponarsenal.gui.core;

import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import felnull.dev.akasiweaponarsenal.gui.listener.GUIClickListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public abstract class GUIPage implements InventoryHolder {
    private final AkasiWeaponArsenal plugin = AkasiWeaponArsenal.getINSTANCE();
    public InventoryGUI gui;
    public Inventory inventory;
    public GUIClickListener listener;
    private HashMap<Integer, GUIItem> items = new HashMap<>();

    public GUIPage(InventoryGUI gui, String name, int size) {
        inventory = Bukkit.createInventory(this, size, name);
        this.gui = gui;
        listener = new GUIClickListener(this);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public abstract void setUp();

    public abstract void back();

    public void setItem(int slot, GUIItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.itemStack);
    }

    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    public GUIItem getItem(int slot) {
        return items.get(slot);
    }

    public ItemStack getItemStack(int slot){
        return items.get(slot).itemStack;
    }

    public void close(){
        HandlerList.unregisterAll(listener);
    }

    public void onOutsideWindowRightClick(InventoryClickEvent e){}

    public void onOutsideWindowLeftClick(InventoryClickEvent e){}

    public void onOutsideWindowClick(InventoryClickEvent e){}

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
