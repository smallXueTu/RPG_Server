package cn.ltcraft.item.base.interfaces;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.inventory.ItemStack;

/**
 * 可升级武器
 */
public interface Upgradeable extends Cloneable{
    /**
     * 返回升级计算的属性
     * @return BaseAttribute
     */
    Attribute calculateAttributes(NBTTagCompound nbtTagCompound);
}
