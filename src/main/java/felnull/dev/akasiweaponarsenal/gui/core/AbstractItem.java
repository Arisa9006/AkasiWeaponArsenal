package felnull.dev.akasiweaponarsenal.gui.core;

import felnull.dev.akasiweaponarsenal.gui.awagui.item.ActionType;
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
    public List<ItemStack> lostCSItemList = new ArrayList<>();
    @Getter
    public Map<ItemStack, Integer> lostCSItemNumberList = new HashMap<>();
    @Getter
    public Map<Material, Integer> lostItemNumberList = new HashMap<>();
    @Getter
    public String falseMessage;
    @Getter
    public String trueMessage;
    @Getter
    public ActionType action;

    public AbstractItem(ItemStack itemStack, List<String> commandList, String trueMessage , String falseMessage) {
        this.itemStack = itemStack;
        this.commandList = commandList;
        this.trueMessage = trueMessage;
        this.falseMessage = falseMessage;
    }
    public AbstractItem(ItemStack itemStack, List<String> commandList, String trueMessage , String falseMessage, ActionType action) {
        this.itemStack = itemStack;
        this.commandList = commandList;
        this.trueMessage = trueMessage;
        this.falseMessage = falseMessage;
        this.action = action;
    }
}
