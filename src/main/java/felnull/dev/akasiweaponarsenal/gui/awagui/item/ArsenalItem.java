package felnull.dev.akasiweaponarsenal.gui.awagui.item;

import com.shampaggon.crackshot.CSUtility;
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
import org.bukkit.Sound;
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
        Player player = (Player) e.getWhoClicked();

        if (checkRequirements(player)) {
            consumeRequiredItems(player);
            executeSuccessActions(player);
        } else {
            executeFailureActions(player);
        }
    }

    // ----------------ItemCheck-------------------
    private boolean checkRequirements(Player player) {
        Set<Material> checkedMaterials = checkVanillaItems(player);
        Set<String> checkedCSWeapons = checkCustomWeapons(player);

        boolean vanillaMaterialCheck = abstractItem.lostItemList.isEmpty() || checkedMaterials.size() == abstractItem.lostItemList.size();
        boolean crackshotItemCheck = abstractItem.lostCSItemList.isEmpty() || checkedCSWeapons.size() == abstractItem.lostCSItemList.size();

        return vanillaMaterialCheck && crackshotItemCheck;
    }

    private Set<Material> checkVanillaItems(Player player) {
        Set<Material> matched = new HashSet<>();
        for (Material material : abstractItem.lostItemList) {
            int required = abstractItem.lostItemNumberList.get(material);
            if (hasEnoughMaterial(player, material, required)) {
                matched.add(material);
            }
        }
        return matched;
    }

    private boolean hasEnoughMaterial(Player player, Material material, int need) {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material && AkasiWeaponArsenal.getCsUtility().getWeaponTitle(item) == null) {
                total += item.getAmount();
                if (total >= need) return true;
            }
        }
        return false;
    }

    private Set<String> checkCustomWeapons(Player player) {
        Set<String> matched = new HashSet<>();
        CSUtility csu = AkasiWeaponArsenal.getCsUtility();

        for (ItemStack item : abstractItem.lostCSItemList) {
            String weaponTitle = csu.getWeaponTitle(item);
            int required = abstractItem.lostCSItemNumberList.get(item);
            if (weaponTitle != null && hasEnoughCSItem(player, weaponTitle, required)) {
                matched.add(weaponTitle);
            }
        }
        return matched;
    }

    private boolean hasEnoughCSItem(Player player, String weaponTitle, int need) {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            String title = AkasiWeaponArsenal.getCsUtility().getWeaponTitle(item);
            if (weaponTitle.equals(title)) {
                total += item.getAmount();
                if (total >= need) return true;
            }
        }
        return false;
    }
    // -------------------------------------------

    // ----------------ItemRemove-----------------
    private void consumeRequiredItems(Player player) {
        for (Material material : abstractItem.lostItemList) {
            int amount = abstractItem.lostItemNumberList.get(material);
            removeItems(player, material, amount);
        }

        for (ItemStack item : abstractItem.lostCSItemList) {
            String weaponTitle = AkasiWeaponArsenal.getCsUtility().getWeaponTitle(item);
            if (weaponTitle != null) {
                int amount = abstractItem.lostCSItemNumberList.get(item);
                removeCSItemStacks(player, weaponTitle, amount);
            }
        }
    }

    private void removeItems(Player player, Material material, int amountToRemove) {
        Inventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length && amountToRemove > 0; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() != material) continue;
            if (AkasiWeaponArsenal.getCsUtility().getWeaponTitle(item) != null) continue;

            int stackAmount = item.getAmount();
            if (stackAmount <= amountToRemove) {
                inventory.clear(i);
                amountToRemove -= stackAmount;
            } else {
                item.setAmount(stackAmount - amountToRemove);
                inventory.setItem(i, item);
                amountToRemove = 0;
            }
        }

        player.updateInventory();
    }

    private void removeCSItemStacks(Player player, String weaponTitle, int amountToRemove) {
        Inventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length && amountToRemove > 0; i++) {
            ItemStack item = contents[i];
            if (item == null) continue;
            String title = AkasiWeaponArsenal.getCsUtility().getWeaponTitle(item);
            if (!weaponTitle.equals(title)) continue;

            int stackAmount = item.getAmount();
            if (stackAmount <= amountToRemove) {
                inventory.clear(i);
                amountToRemove -= stackAmount;
            } else {
                item.setAmount(stackAmount - amountToRemove);
                inventory.setItem(i, item);
                amountToRemove = 0;
            }
        }

        player.updateInventory();
    }
    // ----------------------------------------

    // ------------executeAction---------------
    private void executeSuccessActions(Player player) {
        if (abstractItem.trueMessage != null) {
            player.sendMessage(abstractItem.trueMessage);
        }

        if (!abstractItem.silentMode) {
            playItemSounds(SoundType.CLICK_TRUE, player);
        }

        for (String command : abstractItem.commandList) {
            String parsed = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        }
    }

    private void executeFailureActions(Player player) {
        if (abstractItem.falseMessage != null) {
            player.sendMessage(abstractItem.falseMessage);
        }

        if (!abstractItem.silentMode) {
            playItemSounds(SoundType.CLICK_FALSE, player);
        }
    }

    private void playItemSounds(SoundType type, Player player) {
        if(abstractItem.itemSoundMap.isEmpty()){
            for (SoundData data : pageData.soundDataMap.getOrDefault(type, Collections.emptyList())) {
                new PlaySoundTask(player, data.getSound(), data.getVolume(), data.getPitch())
                        .runTaskLater(AkasiWeaponArsenal.getINSTANCE(), data.delay);
            }
        }else {
            for (SoundData data : abstractItem.itemSoundMap.getOrDefault(type, Collections.emptyList())) {
                new PlaySoundTask(player, data.getSound(), data.getVolume(), data.getPitch())
                        .runTaskLater(AkasiWeaponArsenal.getINSTANCE(), data.delay);
            }
        }

    }
}
