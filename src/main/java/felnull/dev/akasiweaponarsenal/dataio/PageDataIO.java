package felnull.dev.akasiweaponarsenal.dataio;

import com.shampaggon.crackshot.CSUtility;
import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import felnull.dev.akasiweaponarsenal.data.PageData;
import felnull.dev.akasiweaponarsenal.data.SoundData;
import felnull.dev.akasiweaponarsenal.gui.awagui.item.ActionType;
import felnull.dev.akasiweaponarsenal.gui.awagui.page.SoundType;
import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PageDataIO {
    static File dataFolder = new File(AkasiWeaponArsenal.getINSTANCE().getDataFolder(), "ArsenalPage");

    public void save() {

    }

    public static void load() {
        checkFolder(dataFolder);
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String pageName : config.getKeys(false)) {
                PageData pageData = loadPageData(pageName, config.getConfigurationSection(pageName));
                if (pageData != null) {
                    AkasiWeaponArsenal.getPageDataMap().put(pageName, pageData);
                }
            }
            AkasiWeaponArsenal.getINSTANCE().getLogger().info("Loaded config: " + file.getName());
        }
    }

    public static void checkFolder(File folder) {
        if(!folder.exists()){
            if(folder.mkdirs()) {
                AkasiWeaponArsenal.getINSTANCE().getLogger().info( "フォルダ" + folder.getName() + "をk生成しました");
            }else {
                AkasiWeaponArsenal.getINSTANCE().getLogger().warning("フォルダ" + folder.getName() + "の生成に失敗しました");
            }
        }
    }

    private static PageData loadPageData(String pageName, ConfigurationSection pageSection) {
        if (pageSection == null) return null;

        boolean scrollEnable = pageSection.getBoolean("ScrollEnable", false);
        boolean windowClickEnable = pageSection.getBoolean("WindowClickEnable", false);
        int maxLine = pageSection.getInt("MaxLine", 1);

        Map<Integer, AbstractItem> itemSlot = loadItems(pageSection.getConfigurationSection("Item"));
        Map<SoundType, List<SoundData>> soundTypeListMap = loadSounds(pageSection.getConfigurationSection("Sound"));

        return new PageData(pageName, scrollEnable, maxLine, itemSlot, windowClickEnable, soundTypeListMap);
    }

    private static Map<Integer, AbstractItem> loadItems(ConfigurationSection itemSection) {
        Map<Integer, AbstractItem> itemSlot = new HashMap<>();
        if (itemSection == null) return itemSlot;

        for (String itemName : itemSection.getKeys(false)) {
            ConfigurationSection itemData = itemSection.getConfigurationSection(itemName);
            if (itemData == null) continue;

            AbstractItem abstractItem = createAbstractItem(itemData, itemName);
            if (abstractItem == null) continue;

            for (Integer slot : itemData.getIntegerList("Slot")) {
                itemSlot.put(slot, abstractItem);
            }
        }
        return itemSlot;
    }
    // ----------------Itemデータ処理---------------------
    private static AbstractItem createAbstractItem(ConfigurationSection itemData, String fallbackName) {
        Material material = Material.getMaterial(itemData.getString("Material", Material.DIRT.name()));
        if (material == null) material = Material.DIRT;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemData.getString("Name", fallbackName)));
        List<String> lore = itemData.getStringList("Lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
        meta.setLore(lore);
        meta.spigot().setUnbreakable(true);

        if (itemData.getBoolean("HideNBT", true)) {
            meta.addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES,     // 攻撃力などを隠す
                    ItemFlag.HIDE_UNBREAKABLE,    // Unbreakable を隠す
                    ItemFlag.HIDE_ENCHANTS,       // エンチャントを隠す（必要なら）
                    ItemFlag.HIDE_DESTROYS,       // "Can Destroy" を隠す
                    ItemFlag.HIDE_PLACED_ON,      // "Can Place On" を隠す
                    ItemFlag.HIDE_POTION_EFFECTS  // ポーション効果を隠す
            );
        }
        item.setItemMeta(meta);

        if (itemData.isInt("Damage")) {
            item.setDurability((short) itemData.getInt("Damage"));
        }

        ActionType action = null;
        String actionStr = itemData.getString("Action", null);
        if (actionStr != null) {
            try {
                action = ActionType.valueOf(actionStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        List<String> commands = itemData.getStringList("Commands");
        String trueMessage = translateNullableString(itemData.getString("TrueMessage"));
        String falseMessage = translateNullableString(itemData.getString("FalseMessage"));
        boolean silentMode = itemData.getBoolean("SilentMode", false);


        AbstractItem abstractItem = (action != null)
                ? new AbstractItem(item, commands, trueMessage, falseMessage, silentMode, action)
                : new AbstractItem(item, commands, trueMessage, falseMessage, silentMode);

        loadNeedItems(itemData.getConfigurationSection("NeedItem"), abstractItem);
        loadNeedCSItems(itemData.getConfigurationSection("NeedCSItem"), abstractItem);
        loadItemSounds(itemData.getConfigurationSection("ItemSound"), abstractItem);

        return abstractItem;
    }

    private static void loadNeedItems(ConfigurationSection needItemSection, AbstractItem abstractItem) {
        if (needItemSection == null) return;
        Map<Material, Integer> costMap = new HashMap<>();

        for (String needItemName : needItemSection.getKeys(false)) {
            Material mat = Material.getMaterial(needItemName.toUpperCase());
            if (mat != null) {
                abstractItem.lostItemList.add(mat);
                costMap.put(mat, needItemSection.getInt(needItemName + ".Cost", 1));
            }
        }
        abstractItem.lostItemNumberList = costMap;
    }

    private static void loadNeedCSItems(ConfigurationSection needCSItemSection, AbstractItem abstractItem) {
        if (needCSItemSection == null) return;
        Map<ItemStack, Integer> csCostMap = new HashMap<>();
        CSUtility csu = AkasiWeaponArsenal.getCsUtility();

        for (String needCSItemName : needCSItemSection.getKeys(false)) {
            ItemStack csWeapon = csu.generateWeapon(needCSItemName);
            if (csWeapon != null) {
                abstractItem.lostCSItemList.add(csWeapon);
                csCostMap.put(csWeapon, needCSItemSection.getInt(needCSItemName + ".Cost", 1));
            }
        }
        abstractItem.lostCSItemNumberList = csCostMap;
    }

    private static void loadItemSounds(ConfigurationSection soundSection, AbstractItem abstractItem) {
        if (soundSection == null) return;

        for (String key : soundSection.getKeys(false)) {
            SoundType type;
            try {
                type = SoundType.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("不明なSoundType: " + key);
                continue;
            }

            // CLICK系以外は無視する
            if (type != SoundType.CLICK_TRUE && type != SoundType.CLICK_FALSE) continue;

            List<SoundData> dataList = new ArrayList<>();
            List<Map<?, ?>> soundList = soundSection.getMapList(key); // リスト形式

            for (Map<?, ?> mapList : soundList) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) mapList;
                    Sound sound = Sound.valueOf((String) map.get("Sound"));
                    float volume = ((Number) map.getOrDefault("Volume", 1.0)).floatValue();
                    float pitch = ((Number) map.getOrDefault("Pitch", 1.0)).floatValue();
                    int delay = ((Number) map.getOrDefault("Delay", 0)).intValue();
                    dataList.add(new SoundData(sound, volume, pitch, delay));
                } catch (Exception e) {
                    Bukkit.getLogger().warning("ItemSoundの読み込み失敗: " + e.getMessage());
                }
            }
            abstractItem.itemSoundMap.put(type, dataList);
        }
    }

    private static String translateNullableString(String str) {
        if (str == null) return null;
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    // -----------------------------------------------------

    //-----------------------Sound--------------------------

    private static Map<SoundType, List<SoundData>> loadSounds(ConfigurationSection soundSection) {
        Map<SoundType, List<SoundData>> soundTypeListMap = new HashMap<>();
        if(soundSection != null){
            for (String soundTypeName : soundSection.getKeys(false)) {
                SoundType soundType;
                try {
                    soundType = SoundType.valueOf(soundTypeName.toUpperCase());
                }catch (IllegalArgumentException | NullPointerException e){
                    Bukkit.getLogger().warning("無効なサウンドType: " + soundTypeName.toUpperCase());
                    Bukkit.getLogger().info("利用可能なSoundType");
                    for(SoundType enableSoundType : SoundType.values()){
                        Bukkit.getLogger().info(enableSoundType.name());
                    }
                    continue;
                }
                List<SoundData> soundDataList = new ArrayList<>();

                List<Map<?, ?>> soundList = soundSection.getMapList(soundTypeName);
                for (Map<?, ?> soundEntry : soundList) {
                    String soundName = String.valueOf(soundEntry.get("Sound"));
                    Sound sound;
                    try {
                        sound = Sound.valueOf(soundName);
                    } catch (IllegalArgumentException | NullPointerException e) {
                        Bukkit.getLogger().warning("無効なサウンドName: " + soundName);
                        continue;
                    }

                    float volume = ((Number) soundEntry.get("Volume")).floatValue();
                    float pitch = ((Number) soundEntry.get("Pitch")).floatValue();
                    int delay = ((Number) soundEntry.get("Delay")).intValue();

                    soundDataList.add(new SoundData(sound, volume, pitch, delay));
                }
                soundTypeListMap.put(soundType, soundDataList);
            }
        }
        return soundTypeListMap;
    }
    // -----------------------------------------------------
}
