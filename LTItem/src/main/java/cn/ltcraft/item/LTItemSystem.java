package cn.ltcraft.item;

import cn.LTCraft.core.utils.FileUtil;
import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.items.Armor;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.item.items.Material;
import cn.ltcraft.item.items.MeleeWeapon;
import cn.ltcraft.item.objs.ItemObjs;
import cn.ltcraft.item.objs.PlayerAttribute;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LTItemSystem extends JavaPlugin {
    private static LTItemSystem instance = null;
    private static final Map<String, Class<? extends LTItem>> allType = new HashMap<String, Class<? extends LTItem>>(){
        {
            put("近战", MeleeWeapon.class);
            put("材料", Material.class);
            put("盔甲", Armor.class);
            put("宝石", GemsStone.class);
        }
    };

    public static LTItemSystem getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();
        init();
        loadItems();
        Command.init();
        Bukkit.getPluginManager().registerEvents(new AllListener(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> PlayerAttribute.getPlayerAttributeMap().forEach((s, playerAttribute) -> playerAttribute.tick()), 1, 1);
    }
    public void init(){
        allType.keySet().forEach(k -> new File(getDataFolder() + File.separator + "items" + File.separator + k).mkdirs());
    }
    public void loadItems(){
        allType.forEach((k, v) -> {
            List<File> files = new ArrayList<>();
            File file = new File(getDataFolder() + File.separator + "items"+ File.separator + k);
            FileUtil.getFiles(file, files, true);
            for (File f : files) {
                if (f.getName().endsWith(".yml")){
                    try {
                        Constructor<? extends LTItem> constructor = v.getConstructor(MemoryConfiguration.class);
                        ItemObjs.putLTItem(f.getName().substring(0, f.getName().length() - 4), constructor.newInstance(YamlConfiguration.loadConfiguration(f)));
                    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public LTItem getItem(ItemTypes itemTypes, String name){
        return ItemObjs.getLTItem(itemTypes, name);
    }
}
