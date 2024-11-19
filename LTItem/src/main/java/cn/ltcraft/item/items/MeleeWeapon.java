package cn.ltcraft.item.items;

import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.items.weapon.ultimate.*;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MeleeWeapon extends BaseWeapon{
    private static Map<String, Class<? extends MeleeWeapon>> materials = new HashMap<>();

    public static void initItems(){
        materials.put("万人斩-末界的咆哮", MyriadPeopleDeath.class);
        materials.put("尼尔巴斯的饮血镰", BloodDrinkingScytheOfNilbas.class);
        materials.put("死亡之舞", DanceOfDeath.class);
        materials.put("死亡黑狼图腾", BlackWolfTotem.class);
        materials.put("流星锤", MeteorHammer.class);
        materials.put("幽灵手弩", GhostHandCrossbow.class);
        materials.put("流光星陨刃", StarfallBlade.class);
    }
    public static MeleeWeapon get(MemoryConfiguration configuration){
        if (materials.containsKey(configuration.getString("名字"))){
            Class<? extends MeleeWeapon> aClass = materials.get(configuration.getString("名字"));
            Constructor<? extends MeleeWeapon> constructor = null;
            try {
                constructor = aClass.getConstructor(MemoryConfiguration.class);
                return constructor.newInstance(configuration);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return new MeleeWeapon(configuration);
    }
    public MeleeWeapon(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public cn.ltcraft.item.base.ItemTypes getType() {
        return ItemTypes.Melee;
    }
}
