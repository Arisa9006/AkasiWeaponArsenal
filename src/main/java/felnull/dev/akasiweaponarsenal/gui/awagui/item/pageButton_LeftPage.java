package felnull.dev.akasiweaponarsenal.gui.awagui.item;

import felnull.dev.akasiweaponarsenal.gui.awagui.page.ArsenalPage;
import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import felnull.dev.akasiweaponarsenal.gui.core.GUIItem;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class pageButton_LeftPage extends GUIItem {

    public ArsenalPage page;

    public pageButton_LeftPage(InventoryGUI gui, AbstractItem abstractItem, ArsenalPage arsenalPage) {
        super(gui, abstractItem.itemStack);
        this.page = arsenalPage;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        page.addSlotStartPosition(54);
    }
}
