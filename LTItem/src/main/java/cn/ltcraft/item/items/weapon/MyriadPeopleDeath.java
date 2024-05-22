package cn.ltcraft.item.items.weapon;

import cn.ltcraft.item.base.BaseAttribute;
import cn.ltcraft.item.base.interfaces.Attribute;
import cn.ltcraft.item.base.interfaces.Upgradeable;
import cn.ltcraft.item.items.MeleeWeapon;
import cn.ltcraft.item.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * Created by Angel、 on 2024/4/29 23:09
 */
public class MyriadPeopleDeath extends MeleeWeapon implements Upgradeable {
    public MyriadPeopleDeath(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Attribute calculateAttributes(NBTTagCompound nbtTagCompound) {
        int level = Utils.getEquipmentLevel(nbtTagCompound);
        return new BaseAttribute();
    }
}
