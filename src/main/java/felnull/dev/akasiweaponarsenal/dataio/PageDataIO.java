package felnull.dev.akasiweaponarsenal.dataio;

import felnull.dev.akasiweaponarsenal.AkasiWeaponArsenal;
import felnull.dev.akasiweaponarsenal.data.PageData;
import felnull.dev.akasiweaponarsenal.gui.core.AbstractItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
        Map<String, PageData> pageDataMap = new HashMap<>();

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
                        item.setItemMeta(meta);

                        if(itemData.isInt("Damage")){
                            short damage = (short) (item.getType().getMaxDurability() - itemData.getInt("Damage"));
                            item.setDurability((short) (damage));
                        }
                        List<String> commandList = itemData.getStringList("Commands");
                        String trueMessage = itemData.getString("TrueMessage", null);
                        String falseMessage = itemData.getString("FalseMessage", null);
                        AbstractItem abstractItem = new AbstractItem(item, commandList, trueMessage, falseMessage);

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
                        abstractItem.lostItemNumberList = costMap;
                        for(Integer slot : itemData.getIntegerList("Slot")) {
                            itemSlot.put(slot, abstractItem);
                        }
                    }

                    //-----------------------

                    PageData pageData = new PageData(pageName, scrollEnable, maxLine, itemSlot, windowClickEnable);
                    AkasiWeaponArsenal.getPageDataMap().put(pageName, pageData);
                }
                AkasiWeaponArsenal.getINSTANCE().getLogger().info("Loaded config: " + file.getName());
            }
        }
    }

    public static void checkFolder(File folder) {
        if(!folder.exists()){
            if(folder.mkdirs()) {
                AkasiWeaponArsenal.getINSTANCE().getLogger().info( "フォルダ" + folder.getName() + "を生成しました");
            }else {
                AkasiWeaponArsenal.getINSTANCE().getLogger().warning("フォルダ" + folder.getName() + "の生成に失敗しました");
            }
        }
    }
}
