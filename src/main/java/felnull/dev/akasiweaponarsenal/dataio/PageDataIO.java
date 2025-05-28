package felnull.dev.akasiweaponarsenal.dataio;

import com.shampaggon.crackshot.CSUtility;
import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import felnull.dev.akasiweaponarsenal.data.PageData;
import felnull.dev.akasiweaponarsenal.data.SoundData;
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
        //ArsenalPage内のyaml全読み込み
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        //配列内のyamlを１つ１つ読み込み
        if (files != null) {
            for (File file : files) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                for(String pageName : config.getKeys(false)){
                    //-----    Page     -----

                    Map<Integer, AbstractItem> itemSlot = new HashMap<>();
                    ConfigurationSection pageSection = config.getConfigurationSection(pageName);
                    if (pageSection == null) continue;

                    boolean scrollEnable = pageSection.getBoolean("ScrollEnable", false);
                    boolean windowClickEnable = pageSection.getBoolean("WindowClickEnable", false);
                    int maxLine = pageSection.getInt("MaxLine", 1);

                    //-----    Item     -----

                    ConfigurationSection itemSection = pageSection.getConfigurationSection("Item");
                    if (itemSection == null) continue;

                    for (String itemName : itemSection.getKeys(false)) {
                        ConfigurationSection itemData = itemSection.getConfigurationSection(itemName);
                        if (itemData == null) continue;

                        Material material = Material.getMaterial(itemData.getString("Material", Material.DIRT.name()));
                        List<String> loreList = itemData.getStringList("Lore");
                        List<String> coloredLoreList = loreList.stream()
                                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                                .collect(Collectors.toList());
                        ItemStack item = new ItemStack(material);

                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemData.getString("Name", itemName)));
                        meta.setLore(coloredLoreList);
                        meta.spigot().setUnbreakable(true);
                        if(itemData.getBoolean("HideNBT", true)){
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

                        if(itemData.isInt("Damage")){
                            short damage = (short) itemData.getInt("Damage");
                            item.setDurability((short) (damage));
                        }
                        List<String> commandList = itemData.getStringList("Commands");
                        String trueMessage =  itemData.getString("TrueMessage", null);
                        if(trueMessage != null){
                            trueMessage = ChatColor.translateAlternateColorCodes('&', trueMessage);
                        }
                        String falseMessage = itemData.getString("FalseMessage", null);
                        if(falseMessage != null){
                            falseMessage = ChatColor.translateAlternateColorCodes('&', falseMessage);
                        }
                        AbstractItem abstractItem;
                        String action = itemData.getString("Action", null);
                        if(action != null){
                            abstractItem = new AbstractItem(item, commandList, trueMessage, falseMessage, action);
                        }else {
                            abstractItem = new AbstractItem(item, commandList, trueMessage, falseMessage);
                        }

                        ConfigurationSection needItemSection = itemData.getConfigurationSection("NeedItem");
                        Map<Material, Integer> costMap = new HashMap<>();
                        if (needItemSection != null) {
                            for (String needItemName : needItemSection.getKeys(false)) {
                                String matName = needItemName.toUpperCase();
                                Material mat = Material.getMaterial(matName);
                                if (mat != null) {
                                    abstractItem.lostItemList.add(mat);
                                    costMap.put(mat, needItemSection.getInt(needItemName + ".Cost", 1));
                                }
                            }
                        }
                        ConfigurationSection needCSItemSection = itemData.getConfigurationSection("NeedCSItem");
                        Map<ItemStack, Integer> csCostMap = new HashMap<>();
                        if (needCSItemSection != null) {
                            for (String needCSItemName : needCSItemSection.getKeys(false)) {
                                CSUtility csu = AkasiWeaponArsenal.getCsUtility();
                                ItemStack csWeapon = csu.generateWeapon(needCSItemName);
                                if(csWeapon == null){
                                    continue;
                                }else {
                                    abstractItem.lostCSItemList.add(csWeapon);
                                    csCostMap.put(csWeapon, needCSItemSection.getInt(needCSItemName + ".Cost", 1));
                                }
                            }
                        }
                        abstractItem.lostItemNumberList = costMap;
                        abstractItem.lostCSItemNumberList = csCostMap;
                        for(Integer slot : itemData.getIntegerList("Slot")) {
                            itemSlot.put(slot, abstractItem);
                        }
                    }
                    //----------- SOUND ------------

                    ConfigurationSection soundSection = pageSection.getConfigurationSection("Sound");
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

                            ConfigurationSection soundDataSection = pageSection.getConfigurationSection(soundTypeName);
                            if(soundDataSection == null){
                                Bukkit.getLogger().warning("SoundDataを記述してください");
                                continue;
                            }
                            for(String soundName : soundDataSection.getKeys(false)){
                                Sound sound;
                                try {
                                    sound = Sound.valueOf(soundName);
                                }catch (IllegalArgumentException | NullPointerException e) {
                                    Bukkit.getLogger().warning("無効なサウンドName: " + soundTypeName.toUpperCase());
                                    continue;
                                }
                                float volume = (float) soundDataSection.getDouble("Volume");
                                float pitch = (float) soundDataSection.getDouble("Pitch");
                                int delay = soundDataSection.getInt("Delay");

                                soundDataList.add(new SoundData(sound, volume, pitch, delay));
                            }
                            soundTypeListMap.put(soundType, soundDataList);
                        }
                    }

                    //------------------------------
                    PageData pageData = new PageData(pageName, scrollEnable, maxLine, itemSlot, windowClickEnable, soundTypeListMap);
                    AkasiWeaponArsenal.getPageDataMap().put(pageName, pageData);
                }
                AkasiWeaponArsenal.getINSTANCE().getLogger().info("Loaded config: " + file.getName());
            }
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
}
