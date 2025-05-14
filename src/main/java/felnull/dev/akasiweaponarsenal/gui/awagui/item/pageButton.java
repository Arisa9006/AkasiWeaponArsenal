package felnull.dev.akasiweaponarsenal.gui.awagui.item;

import felnull.dev.akasiweaponarsenal.gui.core.GUIItem;
import felnull.dev.akasiweaponarsenal.gui.core.GUIUtil;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class pageButton extends GUIItem {
    public pageButton(InventoryGUI gui, String buttonName, List<String> buttonLore, Short damage) {
        super(gui, new ItemStack(Material.FEATHER));
        ItemMeta meta = super.itemStack.getItemMeta();
        meta.setDisplayName(GUIUtil.fColorStr(buttonName));
        meta.setLore(buttonLore);
        meta.spigot().setUnbreakable(true);
        super.itemStack.setItemMeta(meta);
        if(damage != null){
            super.itemStack.setDurability(damage);
        }
    }
}
