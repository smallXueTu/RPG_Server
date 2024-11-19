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
 * Created by Angel、 on 2024/8/17 下午2:53
 * 死亡黑狼图腾
 */
public class BlackWolfTotem extends MeleeWeapon implements Upgradeable {
    public BlackWolfTotem(MemoryConfiguration configuration) {
        super(configuration);
    }


    /**
     * 武器升级
     */
    @Override
    public Attribute calculateAttributes(NBTTagCompound nbtTagCompound) {
        int level = Utils.getEquipmentLevel(nbtTagCompound);
        BaseAttribute baseAttribute = new BaseAttribute();
        baseAttribute.setDamage(Type.PVE, new RandomValue(level / 1.5));
        baseAttribute.setSuckingBlood(Type.PVE, level / 1000d);
        baseAttribute.setGroupGyrus((int) Math.ceil(level / 2d));
        return baseAttribute;
    }
}