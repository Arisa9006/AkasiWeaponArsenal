package felnull.dev.akasiweaponarsenal.gui.awagui.page;

import felnull.dev.akasiweaponarsenal.data.PageData;
import felnull.dev.akasiweaponarsenal.gui.awagui.item.ArsenalItem;
import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import felnull.dev.akasiweaponarsenal.gui.core.GUIPage;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class ArsenalPage extends GUIPage {

    public PageData pageData;
    public int invStartPosition;
    public InventoryGUI gui;

    public ArsenalPage(InventoryGUI gui, PageData pageData) {
        super(gui, pageData.pageName, pageData.getInventorySize());
        this.pageData = pageData;
        this.invStartPosition = 0;
        this.gui = gui;
        gui.player.playSound(gui.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
    }

    @Override
    public void setUp() {
        super.inventory.clear();

        for(int slot = 0; slot < pageData.maxLine * 9; slot++) {
            int slotPosition = slot - this.invStartPosition; //インベントリの参照範囲を動かす
            if(slotPosition > 53 || slotPosition < 0){
                continue;
            }
            if(pageData.itemSlot.containsKey(slot)) {
                setItem(slotPosition, new ArsenalItem(gui, pageData.itemSlot.get(slot), pageData));
            }
        }
    }

    @Override
    public void back() {

    }

    @Override
    public Inventory getInventory() {
        return super.getInventory();
    }

    public void changeSlotStartPosition(int startPosition) {
        this.invStartPosition = startPosition;
        if (this.invStartPosition > (pageData.maxLine - (pageData.maxLine % 6)) * 9){
            this.invStartPosition = 225;
        } else if (this.invStartPosition < 0) {
            this.invStartPosition = 0;
        }
        this.setUp();
    }

    public void addSlotStartPosition(int plusPosition) {
        this.invStartPosition += plusPosition;
        if (this.invStartPosition > (pageData.maxLine - (pageData.maxLine / 2)) * 9){
            this.invStartPosition = (pageData.maxLine - (pageData.maxLine / 2)) * 9;
        }
        this.setUp();
    }

    public void subtractSlotStartPosition(int minusPosition) {
        this.invStartPosition -= minusPosition;
        if (this.invStartPosition < 0){
            this.invStartPosition = 0;
        }
        this.setUp();
    }

    @Override
    public void onOutsideWindowRightClick(InventoryClickEvent e) {
        if(!pageData.windowClickEnable){
            return;
        }
        this.addSlotStartPosition(9);
    }

    @Override
    public void onOutsideWindowLeftClick(InventoryClickEvent e) {
        if(!pageData.windowClickEnable){
            return;
        }
        this.subtractSlotStartPosition(9);
    }
}
