package felnull.dev.akasiweaponarsenal.gui.core;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractItem {
    public ItemStack itemStack;
    public List<String> commandList;
    @Getter
    public List<Material> lostItemList = new ArrayList<>();
    @Getter
    public Map<Material, Integer> lostItemNumberList = new HashMap<>();

    public AbstractItem(ItemStack itemStack, List<String> commandList) {
        this.itemStack = itemStack;
        this.commandList = commandList;
    }
}
