package cn.ltcraft.item.items;

import cn.LTCraft.core.entityClass.Additional;
import cn.LTCraft.core.entityClass.RandomValue;
import cn.LTCraft.core.utils.GameUtils;
import cn.LTCraft.core.utils.ItemUtils;
import cn.ltcraft.item.base.*;
import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.items.gemsstones.*;
import cn.ltcraft.item.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GemsStone extends AbstractAttribute implements ConfigurableLTItem {
    private static Map<String, Class<? extends GemsStone>> gemsStones = new HashMap<>();
    public static void initItems(){
        gemsStones.put("永恒水晶", EternalCrystal.class);
        gemsStones.put("高级永恒水晶", EternalCrystal.class);
    }
    public static GemsStone get(MemoryConfiguration configuration){
        if (gemsStones.containsKey(configuration.getString("名字"))){
            Class<? extends GemsStone> aClass = gemsStones.get(configuration.getString("名字"));
            Constructor<? extends GemsStone> constructor = null;
            try {
                constructor = aClass.getConstructor(MemoryConfiguration.class);
                return constructor.newInstance(configuration);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return new GemsStone(configuration);
    }
    private String name;
    private ItemStack itemStack = null;
    private List<String> lore;
    protected boolean binding = false;
    private final MemoryConfiguration config;

    public GemsStone(MemoryConfiguration configuration){
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
        String pvpAD = config.getString("PVP.伤害");
        if (pvpAD.contains("-")){
            String[] split = pvpAD.split("-");
            PVPDamage = new RandomValue(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
        }else {
            PVPDamage = new RandomValue(Double.parseDouble(pvpAD));
        }
        PVESuckingBlood = config.getDouble("PVE.吸血");
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
        PVENailPiercing = config.getDouble("PVE.穿甲");
        PVPNailPiercing = config.getDouble("PVP.穿甲");
        PVESelfPotion = GameUtils.analyticalPotion(config.getString("PVE.自身药水"));
        PVEEntityPotion = GameUtils.analyticalPotion(config.getString("PVE.对方药水"));
        PVPSelfPotion = GameUtils.analyticalPotion(config.getString("PVP.自身药水"));
        PVPEntityPotion = GameUtils.analyticalPotion(config.getString("PVP.对方药水"));
        PVEAttackSkill = GameUtils.analyticalSkill(config.getString("PVE.攻击技能"));
        PVPAttackSkill = GameUtils.analyticalSkill(config.getString("PVP.攻击技能"));
        armor = config.getInt("护甲");
        healthValue = config.getInt("生命值");
        lucky = config.getInt("幸运");
        tenacity = config.getDouble("韧性");
        dodge = config.getDouble("闪避");
        backInjury = config.getDouble("反伤");
        PVEInjuredSkill = GameUtils.analyticalSkill(config.getString("PVE.受伤技能"));
        PVPInjuredSkill = GameUtils.analyticalSkill(config.getString("PVP.受伤技能"));
        String PVPCriticalStr = config.getString("PVP.暴击", "");
        if (!PVPCriticalStr.isEmpty()){
            String[] criticalSplit = PVPCriticalStr.split(":");
            PVPCritical.addAdditional(new Additional.Value(criticalSplit[0]));
            PVPCriticalRate = Double.parseDouble(criticalSplit[1]);
        }
        String PVECriticalStr = config.getString("PVE.暴击", "");
        if (!PVECriticalStr.isEmpty()){
            String[] criticalSplit = PVECriticalStr.split(":");
            PVECritical.addAdditional(new Additional.Value(criticalSplit[0]));
            PVECriticalRate = Double.parseDouble(criticalSplit[1]);
        }
        lore = config.getStringList("说明");
    }
    @Override
    public ItemTypes getType() {
        return ItemTypes.Gemstone;
    }

    @Override
    public String getName() {
        return name;
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
    public MemoryConfiguration getConfig() {
        return config;
    }

    @Override
    public boolean binding() {
        return binding;
    }

    /**
     * 为什么提供两个参数？
     * 第一个参数已经包括了第二个参数，处理宝石逻辑 的时候 处理的代码块已经有NBT对象了
     * 直接传递过来不用这里再去获取了
     * @param itemStack 物品堆
     * @param nbtTagCompound nbt
     * @return 处理后的 如果返回null则不用处理
     */
    public Object generalInstallOn(ItemStack itemStack, NBTTagCompound nbtTagCompound){
        Class<? extends GemsStone> aClass = this.getClass();
        Class<?> superclass = aClass.getSuperclass();
        if (superclass == GemsStone.class) {
            NBTTagCompound nbt = installOn(nbtTagCompound);
            if (nbtTagCompound != nbt){
                return nbt;
            }
            return installOn(itemStack);
        }
        return null;
    }
    protected ItemStack installOn(ItemStack itemStack){
        return itemStack;
    }

    /**
     * 一个物品安装了它
     * 用于子类处理逻辑
     * @param nbtTagCompound nbt
     * @return 安装完成后的
     */
    protected NBTTagCompound installOn(NBTTagCompound nbtTagCompound){
        return nbtTagCompound;
    }
}
