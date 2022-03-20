package cn.ltcraft.item.items;

import cn.LTCraft.core.utils.GameUtils;
import cn.ltcraft.item.base.*;
import cn.ltcraft.item.utils.Utils;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Armor extends AICLA {
    private String name;
    private ItemStack itemStack;
    private List<String> lore;
    protected int maxSet = 0;
    private final MemoryConfiguration config;
    protected boolean binding;
    public Armor(MemoryConfiguration configuration){
        config = configuration;
        init();
    }
    public void init(){
        name = config.getString("名字");
        binding = config.getBoolean("绑定");
        armor = config.getInt("护甲");
        healthValue = config.getInt("生命值");
        lucky = config.getInt("幸运");
        tenacity = config.getDouble("韧性");
        dodge = config.getDouble("闪避");
        backInjury = config.getDouble("反伤");
        lore = config.getStringList("说明");
        maxSet = config.getInt("宝石槽位", 3);
        PVEInjuredSkill = GameUtils.analyticalSkill(config.getString("PVE受伤技能"));
        PVPInjuredSkill = GameUtils.analyticalSkill(config.getString("PVP受伤技能"));
        potion = GameUtils.analyticalPotion(config.getString("药水效果"));
    }
    @Override
    public ItemTypes getType() {
        return ItemTypes.Armor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getItemStack() {
        if (!config.isBoolean("不可叠加")) {
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
        ItemStack itemStack = getItemStack();
        itemStack.setAmount(count);
        return itemStack;
    }

    @Override
    public int getMaxSet() {
        return maxSet;
    }

    @Override
    public List<String> getLore() {
        return lore;
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
