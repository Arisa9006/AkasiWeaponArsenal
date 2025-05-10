package felnull.dev.akasiweaponarsenal.data;

import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import lombok.Getter;

import java.util.Map;

@Getter
public class PageData {
    public String pageName;
    public boolean scrollEnable;
    public int maxLine;
    public Map<Integer, AbstractItem> itemSlot;
    public boolean windowClickEnable;

    public PageData(String pageName, boolean scrollEnable, int maxLine, Map<Integer, AbstractItem> itemSlot, boolean windowClickEnable) {
        this.pageName = pageName;
        this.scrollEnable = scrollEnable;
        this.maxLine = maxLine;
        this.itemSlot = itemSlot;
        this.windowClickEnable = windowClickEnable;
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
