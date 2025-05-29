package felnull.dev.akasiweaponarsenal.gui.awagui.item;

import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import felnull.dev.akasiweaponarsenal.data.SoundData;
import felnull.dev.akasiweaponarsenal.gui.awagui.page.ArsenalPage;
import felnull.dev.akasiweaponarsenal.gui.awagui.page.SoundType;
import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import felnull.dev.akasiweaponarsenal.gui.core.GUIItem;
import felnull.dev.akasiweaponarsenal.gui.core.InventoryGUI;
import felnull.dev.akasiweaponarsenal.task.PlaySoundTask;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;

public class pageButton_RightPage extends GUIItem {

    public ArsenalPage page;

    public pageButton_RightPage(InventoryGUI gui, AbstractItem abstractItem, ArsenalPage arsenalPage) {
        super(gui, abstractItem.itemStack);
        this.page = arsenalPage;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        boolean success = page.addSlotStartPosition(54);
        if(success){
            for(SoundData soundData : page.pageData.soundDataMap.getOrDefault(SoundType.RIGHT_PAGE_TRUE, Collections.emptyList())){
                new PlaySoundTask(gui.player, soundData.getSound(), soundData.getVolume(), soundData.getPitch()).runTaskLater(AkasiWeaponArsenal.getINSTANCE(), soundData.delay);
            }
        }else{
            for(SoundData soundData : page.pageData.soundDataMap.getOrDefault(SoundType.RIGHT_PAGE_FALSE, Collections.emptyList())){
                new PlaySoundTask(gui.player, soundData.getSound(), soundData.getVolume(), soundData.getPitch()).runTaskLater(AkasiWeaponArsenal.getINSTANCE(), soundData.delay);
            }
        }
    }
}
