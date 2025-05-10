package felnull.dev.akasiweaponarsenal.gui.awagui.item;

import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import felnull.dev.akasiweaponarsenal.gui.core.GUIItem;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ArsenalItem extends GUIItem {

    AbstractItem abstractItem;

    public ArsenalItem(InventoryGUI gui, AbstractItem abstractItem) {
        super(gui, new ItemStack(abstractItem.itemStack));
        this.abstractItem = abstractItem;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if(!abstractItem.lostItemList.isEmpty()){
            int check = 0;
            Set<Material> checkedMaterial = new HashSet<>();
            for(Material material : abstractItem.lostItemList){
                if(checkMaterialTotal((Player) e.getWhoClicked(), material, abstractItem.lostItemNumberList.get(material))){
                    check++;
                    checkedMaterial.add(material);
                }
            }
            if((abstractItem.lostItemList.size()) == check){
                for(Material material : checkedMaterial){
                    removeItems((Player) e.getWhoClicked(), material, abstractItem.lostItemNumberList.get(material));
                }
            }else {
                e.getWhoClicked().sendMessage("作成するための素材が足りません...");
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_VILLAGER_HURT, 1f,1f);
                return;
            }
        }
        for(String command : abstractItem.commandList) {
            String parsedCommand = command.replace("%player%", e.getWhoClicked().getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
        }
    }

    public boolean checkMaterialTotal(Player player, Material material, int need) {
        int total = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if(item.getType() == material) {
                total += item.getAmount();
                if (total >= need) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeItems(Player player, Material material, int amountToRemove) {
        Inventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() != material) continue;

            int stackAmount = item.getAmount();

            if (stackAmount <= amountToRemove) {
                // 全部削除
                inventory.clear(i);
                amountToRemove -= stackAmount;
            } else {
                // 一部だけ削除
                item.setAmount(stackAmount - amountToRemove);
                inventory.setItem(i, item);
                break;
            }

            if (amountToRemove <= 0) break;
        }

        player.updateInventory();
    }
}
