package cn.ltcraft.item;

import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.FileUtil;
import cn.LTCraft.core.utils.ItemUtils;
import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.items.*;
import cn.ltcraft.item.objs.ItemObjs;
import cn.ltcraft.item.objs.PlayerAttribute;
import cn.ltcraft.item.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LTItemSystem extends JavaPlugin {
    //
    private static LTItemSystem instance = null;
    private static final Map<String, Class<? extends LTItem>> allType = new HashMap<String, Class<? extends LTItem>>(){
        {
            put("近战", MeleeWeapon.class);
            put("材料", Material.class);
            put("盔甲", Armor.class);
            put("宝石", GemsStone.class);
            put("饰品", Ornament.class);
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
    }
    public void init(){
        allType.keySet().forEach(k -> new File(getDataFolder() + File.separator + "items" + File.separator + k).mkdirs());
        allType.values().forEach(aClass -> {
            try {
                aClass.getMethod("initItems").invoke(null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }
    public void loadItems(){
        allType.forEach((k, v) -> {
            List<File> files = new ArrayList<>();
            File file = new File(getDataFolder() + File.separator + "items"+ File.separator + k);
            FileUtil.getFiles(file, files, true);
            for (File f : files) {
                if (f.getName().endsWith(".yml")){
                    try {
                        Method method = v.getMethod("get", MemoryConfiguration.class);
                        ItemObjs.putLTItem(f.getName().substring(0, f.getName().length() - 4), (LTItem) method.invoke(null, YamlConfiguration.loadConfiguration(f)));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
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
