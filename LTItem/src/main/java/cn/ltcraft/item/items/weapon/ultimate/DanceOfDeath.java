package cn.ltcraft.item.items.weapon.ultimate;

import cn.LTCraft.core.entityClass.RandomValue;
import cn.ltcraft.item.base.BaseAttribute;
import cn.ltcraft.item.base.interfaces.Attribute;
import cn.ltcraft.item.base.interfaces.Upgradeable;
import cn.ltcraft.item.items.MeleeWeapon;
import cn.ltcraft.item.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * Created by Angel、 on 2024/8/17 下午2:48
 * 死亡之舞
 */
public class DanceOfDeath extends MeleeWeapon implements Upgradeable {
    public DanceOfDeath(MemoryConfiguration configuration) {
        super(configuration);
    }


    /**
     * 武器升级
     */
    @Override
    public Attribute calculateAttributes(NBTTagCompound nbtTagCompound) {
        int level = Utils.getEquipmentLevel(nbtTagCompound);
        BaseAttribute baseAttribute = new BaseAttribute();
        int damage = Math.min(20, level);
        damage += Math.max(0, level - 20) * 4;
        baseAttribute.setDamage(Type.PVE, new RandomValue(damage));
        baseAttribute.setSuckingBlood(Type.PVE, level / 300d);
        baseAttribute.setGroupGyrus((int) Math.ceil(level / 2d));
        return baseAttribute;
    }
}