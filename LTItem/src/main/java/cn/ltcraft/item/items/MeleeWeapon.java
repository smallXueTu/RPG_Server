package cn.ltcraft.item.items;

import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.items.materials.Infernal;
import cn.ltcraft.item.items.weapon.MyriadPeopleDeath;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MeleeWeapon extends BaseWeapon{
    private static Map<String, Class<? extends MeleeWeapon>> materials = new HashMap<>();

    public static void initItems(){
        materials.put("万人斩-末界的咆哮", MyriadPeopleDeath.class);
        materials.put("死亡之舞", MeleeWeapon.class);
        materials.put("尼尔巴斯的饮血镰", MeleeWeapon.class);
        materials.put("死亡黑狼图腾", MeleeWeapon.class);
        materials.put("流星锤", MeleeWeapon.class);
        materials.put("幽灵手弩", MeleeWeapon.class);
        materials.put("流光星陨刃", MeleeWeapon.class);
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
