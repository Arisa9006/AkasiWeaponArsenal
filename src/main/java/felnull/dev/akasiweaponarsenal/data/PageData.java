package felnull.dev.akasiweaponarsenal.data;

import felnull.dev.akasiweaponarsenal.gui.awagui.page.SoundType;
import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import lombok.Getter;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PageData {
    public String pageName;
    public boolean scrollEnable;
    public int maxLine;
    public Map<Integer, AbstractItem> itemSlot;
    public boolean windowClickEnable;
    public Map<SoundType, List<SoundData>> soundDataMap;

    public PageData(String pageName, boolean scrollEnable, int maxLine, Map<Integer, AbstractItem> itemSlot, boolean windowClickEnable, Map<SoundType, List<SoundData>> soundDataMap) {
        this.pageName = pageName;
        this.scrollEnable = scrollEnable;
        this.maxLine = maxLine;
        this.itemSlot = itemSlot;
        this.windowClickEnable = windowClickEnable;
        this.soundDataMap = soundDataMap;
    }

    //表示上の最大サイズ
    public int getInventorySize() {
        return Math.min(maxLine * 9, 54);
    }

    //仮想インベントリの最大サイズ
    public int getInventoryMaxSize() {
        return maxLine * 9;
    }
}
