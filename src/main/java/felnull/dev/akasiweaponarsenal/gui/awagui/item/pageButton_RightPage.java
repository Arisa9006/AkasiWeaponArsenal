package felnull.dev.akasiweaponarsenal.gui.awagui.item;

import felnull.dev.akasiweaponarsenal.gui.awagui.page.ArsenalPage;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class pageButton_RightPage extends pageButton{

    public ArsenalPage page;

    public pageButton_RightPage(InventoryGUI gui, String buttonName, List<String> buttonLore, Short damage, ArsenalPage arsenalPage) {
        super(gui, buttonName, buttonLore, damage);
        this.page = arsenalPage;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        page.subtractSlotStartPosition(54);
    }
}
