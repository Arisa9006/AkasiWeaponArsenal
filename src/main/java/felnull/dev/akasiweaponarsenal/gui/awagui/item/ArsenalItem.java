package felnull.dev.akasiweaponarsenal.gui.awagui.item;

import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import felnull.dev.akasiweaponarsenal.gui.core.GUIItem;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ArsenalItem extends GUIItem {

    AbstractItem abstractItem;

    public ArsenalItem(InventoryGUI gui, AbstractItem abstractItem) {
        super(gui, new ItemStack(abstractItem.itemStack));
        this.abstractItem = abstractItem;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        for(String command : abstractItem.commandList) {
            String parsedCommand = command.replace("%player%", e.getWhoClicked().getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
        }
    }
}
