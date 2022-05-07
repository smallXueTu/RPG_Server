package cn.ltcraft.item.items;

import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.base.interfaces.actions.TickItem;
import cn.ltcraft.item.items.materials.Infernal;
import cn.ltcraft.item.utils.Utils;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Material implements ConfigurableLTItem {
    private static Map<String, Class<? extends Material>> materials = new HashMap<>();
    /**
     * todo 对于包含动作的物品，全部替换为面向对象的方式 见：
     * @see TickItem
     */
    public static void initItems(){
        materials.put("火魔", Infernal.class);
    }
    public static Material get(MemoryConfiguration configuration){
        if (materials.containsKey(configuration.getString("名字"))){
            Class<? extends Material> aClass = materials.get(configuration.getString("名字"));
            Constructor<? extends Material> constructor = null;
            try {
                constructor = aClass.getConstructor(MemoryConfiguration.class);
                return constructor.newInstance(configuration);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return new Material(configuration);
    }
    private String name;
    private ItemStack itemStack;
    private List<String> lore;
    private final MemoryConfiguration config;
    protected boolean binding = false;
    public Material(MemoryConfiguration configuration){
        config = configuration;
        init();
    }
    public void init(){
        name = config.getString("名字");
        binding = config.getBoolean("绑定", false);
        lore = config.getStringList("说明");
    }
    @Override
    public cn.ltcraft.item.base.ItemTypes getType() {
        return ItemTypes.Material;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    @Override
    public ItemStack getItemStack() {
        if (!config.getBoolean("不可叠加")) {
            if (this.itemStack == null) {
                itemStack = Utils.getItem(this);
            }
            return itemStack.clone();
        }else {
            return Utils.getItem(this);
        }
    }

    @Override
    public ItemStack generate(int count) {
        ItemStack itemStack = this.getItemStack();
        itemStack.setAmount(count);
        return itemStack;
    }

    @Override
    public MemoryConfiguration getConfig() {
        return config;
    }

    @Override
    public boolean binding() {
        return binding;
    }
}
