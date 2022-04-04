package cn.ltcraft.item.items;

import cn.LTCraft.core.entityClass.RandomValue;
import cn.LTCraft.core.utils.GameUtils;
import cn.ltcraft.item.base.*;
import cn.ltcraft.item.utils.Utils;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class BaseWeapon extends AICLA {
    private String name;
    private ItemStack itemStack;
    private String killMessage;
    private List<String> lore;
    protected int maxSet = 0;
    protected boolean binding = false;
    private final MemoryConfiguration config;
    public BaseWeapon(MemoryConfiguration configuration){
        config = configuration;
        init();
    }
    public void init(){
        name = config.getString("名字");
        binding = config.getBoolean("绑定");
        String pveAD = config.getString("PVE.伤害");
        if (pveAD.contains("-")){
            String[] split = pveAD.split("-");
            PVEDamage = new RandomValue(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
        }else {
            PVEDamage = new RandomValue(Double.parseDouble(pveAD));
        }
        PVESuckingBlood = config.getDouble("PVE.吸血");
        String pvpAD = config.getString("PVP.伤害");
        if (pvpAD.contains("-")){
            String[] split = pvpAD.split("-");
            PVPDamage = new RandomValue(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
        }else {
            PVPDamage = new RandomValue(Double.parseDouble(pvpAD));
        }
        PVPSuckingBlood = config.getDouble("PVP.吸血");
        PVEBurning = config.getInt("PVE.燃烧");
        PVPBurning = config.getInt("PVP.燃烧");
        PVEAttackRecovery = config.getInt("PVE.攻击恢复");
        PVPAttackRecovery = config.getInt("PVP.攻击恢复");
        PVPLightning = config.getDouble("PVP.雷击");
        PVELightning = config.getDouble("PVE.雷击");
        groupGyrus = config.getInt("PVE.群回");
        PVPStrikeFly = config.getInt("PVP.击飞");
        PVPRepel = config.getInt("PVP.击退");
        PVEStrikeFly = config.getInt("PVE.击飞");
        PVERepel = config.getInt("PVE.击退");
        killMessage = config.getString("PVE.击杀信息");
        PVENailPiercing = config.getDouble("PVE.穿甲");
        PVPNailPiercing = config.getDouble("PVP.穿甲");
        PVESelfPotion = GameUtils.analyticalPotion(config.getString("PVE.自身药水"));
        PVEEntityPotion = GameUtils.analyticalPotion(config.getString("PVE.对方药水"));
        PVPSelfPotion = GameUtils.analyticalPotion(config.getString("PVP.自身药水"));
        PVPEntityPotion = GameUtils.analyticalPotion(config.getString("PVP.对方药水"));
        PVEAttackSkill = GameUtils.analyticalSkill(config.getString("PVE.攻击技能"));
        PVPAttackSkill = GameUtils.analyticalSkill(config.getString("PVP.攻击技能"));
        lore = config.getStringList("说明");
        maxSet = config.getInt("宝石槽位", Utils.getMaxSet(config.getString("quality", "")));
    }
    @Override
    public String getName() {
        return name;
    }

    public String getKillMessage() {
        return killMessage;
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
    public List<String> getLore() {
        return lore;
    }

    @Override
    public ItemStack generate(int count) {
        ItemStack itemStack = this.getItemStack().clone();
        itemStack.setAmount(count);
        return itemStack;
    }

    @Override
    public int getMaxSet() {
        return maxSet;
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
