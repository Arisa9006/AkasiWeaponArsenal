package felnull.dev.akasiweaponarsenal.gui.awagui.page;

import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import felnull.dev.akasiweaponarsenal.data.PageData;
import felnull.dev.akasiweaponarsenal.data.SoundData;
import felnull.dev.akasiweaponarsenal.gui.awagui.item.ArsenalItem;
import felnull.dev.akasiweaponarsenal.gui.awagui.item.pageButton_LeftPage;
import felnull.dev.akasiweaponarsenal.gui.awagui.item.pageButton_RightPage;
import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import felnull.dev.akasiweaponarsenal.gui.core.GUIItem;
import felnull.dev.akasiweaponarsenal.gui.core.GUIPage;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import felnull.dev.akasiweaponarsenal.task.PlaySoundTask;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collections;

public class ArsenalPage extends GUIPage {

    public PageData pageData;
    public int invStartPosition;
    public InventoryGUI gui;

    public ArsenalPage(InventoryGUI gui, PageData pageData) {
        super(gui, ChatColor.translateAlternateColorCodes('&', pageData.pageName), pageData.getInventorySize());
        this.pageData = pageData;
        this.invStartPosition = 0;
        this.gui = gui;
        for(SoundData soundData : pageData.soundDataMap.getOrDefault(SoundType.GUI_OPEN, Collections.emptyList())){
            new PlaySoundTask(gui.player, soundData.getSound(), soundData.getVolume(), soundData.getPitch()).runTaskLater(AkasiWeaponArsenal.getINSTANCE(), soundData.delay);
        }
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
                setItem(slotPosition, checkActionButton(pageData.itemSlot.get(slot)));
            }
        }
    }

    @Override
    public void back() {

    }

    @Override
    public void close() {
        super.close();
        for(SoundData soundData : pageData.soundDataMap.getOrDefault(SoundType.GUI_CLOSE, Collections.emptyList())){
            new PlaySoundTask(gui.player, soundData.getSound(), soundData.getVolume(), soundData.getPitch()).runTaskLater(AkasiWeaponArsenal.getINSTANCE(), soundData.delay);
        }
    }

    @Override
    public Inventory getInventory() {
        return super.getInventory();
    }

    public void changeSlotStartPosition(int startPosition) {
        this.invStartPosition = startPosition;

        int displayableLines = super.getInventory().getSize() / 9;
        int maxStartLine = pageData.maxLine - displayableLines;
        int maxStartPosition = maxStartLine * 9;

        if (this.invStartPosition > maxStartPosition){
            this.invStartPosition = maxStartPosition;
        } else if (this.invStartPosition < 0) {
            this.invStartPosition = 0;
        }
        this.setUp();
    }

    public boolean addSlotStartPosition(int plusPosition) {
        this.invStartPosition += plusPosition;
        boolean success = true;
        int displayableLines = super.getInventory().getSize() / 9;
        int maxStartLine = pageData.maxLine - displayableLines;
        int maxStartPosition = maxStartLine * 9;

        if (this.invStartPosition > maxStartPosition){
            this.invStartPosition = maxStartPosition;
            success = false;
        }
        this.setUp();
        return success;
    }

    public boolean subtractSlotStartPosition(int minusPosition) {
        this.invStartPosition -= minusPosition;
        boolean success = true;
        if (this.invStartPosition < 0){
            this.invStartPosition = 0;
            success = false;
        }
        this.setUp();
        return success;
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

    public GUIItem checkActionButton(AbstractItem abstractItem) {
        if(abstractItem.action == null){
            return new ArsenalItem(gui, abstractItem, pageData);
        }
        switch (abstractItem.action) {
            case LEFT_PAGE:
                return new pageButton_LeftPage(gui, abstractItem, this);
            case RIGHT_PAGE:
                return new pageButton_RightPage(gui, abstractItem, this);
            default:
                return new ArsenalItem(gui, abstractItem, pageData);
        }
    }
}
