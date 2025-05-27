package felnull.dev.akasiweaponarsenal.gui.awagui.item;

import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import felnull.dev.akasiweaponarsenal.data.PageData;
import felnull.dev.akasiweaponarsenal.data.SoundData;
import felnull.dev.akasiweaponarsenal.gui.awagui.page.SoundType;
import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import felnull.dev.akasiweaponarsenal.gui.core.GUIItem;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import felnull.dev.akasiweaponarsenal.task.PlaySoundTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArsenalItem extends GUIItem {

    AbstractItem abstractItem;
    PageData pageData;
    boolean trueCheck;

    public ArsenalItem(InventoryGUI gui, AbstractItem abstractItem, PageData pageData) {
        super(gui, new ItemStack(abstractItem.itemStack));
        this.abstractItem = abstractItem;
        this.pageData = pageData;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        trueCheck = false;
        Set<Material> checkedMaterial = new HashSet<>();
        if(!abstractItem.lostItemList.isEmpty()){
            int check = 0;
            for(Material material : abstractItem.lostItemList){
                if(checkMaterialTotal((Player) e.getWhoClicked(), material, abstractItem.lostItemNumberList.get(material))){
                    check++;
                    checkedMaterial.add(material);
                }
            }
            if((abstractItem.lostItemList.size()) == check){
                trueCheck = true;
            }else {
                trueCheck = false;
                return;
            }
        }
        Set<ItemStack> checkedItemStack = new HashSet<>();
        if(!abstractItem.lostCSItemNumberList.isEmpty()){
            int check = 0;
            for(ItemStack weapon : abstractItem.lostCSItemList){
                if(checkItemStackTotal((Player) e.getWhoClicked(), weapon, abstractItem.lostCSItemNumberList.get(weapon))){
                    check++;
                    checkedItemStack.add(weapon);
                }
            }
            if((abstractItem.lostCSItemList.size()) == check){
                trueCheck = true;
            }else {
                trueCheck = false;
                return;
            }
        }
        if(trueCheck){
            for(ItemStack weapon : checkedItemStack){
                removeItemStacks((Player) e.getWhoClicked(), weapon, abstractItem.lostCSItemNumberList.get(weapon));
            }
            for(Material material : checkedMaterial){
                removeItems((Player) e.getWhoClicked(), material, abstractItem.lostItemNumberList.get(material));
            }
        }else {
            if(abstractItem.falseMessage != null) {
                e.getWhoClicked().sendMessage(abstractItem.falseMessage);
            }
            for(SoundData soundData : pageData.soundDataMap.getOrDefault(SoundType.CLICK_FALSE, Collections.emptyList())){
                new PlaySoundTask(gui.player, soundData.getSound(), soundData.getVolume(), soundData.getPitch()).runTaskLater(AkasiWeaponArsenal.getINSTANCE(), soundData.delay);
            }
        }

        for(String command : abstractItem.commandList) {
            if(abstractItem.trueMessage != null) {
                e.getWhoClicked().sendMessage(abstractItem.trueMessage);
            }
            for(SoundData soundData : pageData.soundDataMap.getOrDefault(SoundType.CLICK_TRUE, Collections.emptyList())){
                new PlaySoundTask(gui.player, soundData.getSound(), soundData.getVolume(), soundData.getPitch()).runTaskLater(AkasiWeaponArsenal.getINSTANCE(), soundData.delay);
            }
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
    public boolean checkItemStackTotal(Player player, ItemStack itemStack, int need) {
        int total = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if(item.isSimilar(itemStack)) {
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

            if(AkasiWeaponArsenal.getCsUtility().getWeaponTitle(item) != null){
                continue;
            }

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

    public void removeItemStacks(Player player, ItemStack itemStack, int amountToRemove) {
        Inventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || !item.isSimilar(itemStack)) continue;

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
