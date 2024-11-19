package cn.ltcraft.item.items.gemsstones;

import cn.LTCraft.core.utils.ItemUtils;
import cn.ltcraft.item.base.AICLA;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.base.interfaces.Upgradeable;
import cn.ltcraft.item.enums.ItemQuality;
import cn.ltcraft.item.items.BaseWeapon;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.item.objs.StoneInstallResult;
import cn.ltcraft.item.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * 武器经验水晶
 * Created by Angel、 on 2024/4/27 21:53
 */
public class WeaponExpCrystal extends GemsStone implements ConsumeGemstones {
    public WeaponExpCrystal(MemoryConfiguration configuration) {
        super(configuration);
    }
    @Override
    public StoneInstallResult installOn(ItemStack itemStack, int installCount) {
        if (itemStack.hasItemMeta()) {
            NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
            LTItem ltItems = Utils.getLTItems(nbt);
            if (ltItems instanceof BaseWeapon && ltItems instanceof Upgradeable){
                int exp = this.getConfig().getInt("增加经验");

                int totalExp = exp * installCount;
                int redundantExp = Utils.addEquipmentExp(nbt, totalExp);
                return new StoneInstallResult((int) Math.ceil((totalExp - redundantExp) * 1d / exp), ItemUtils.setNBT(itemStack, nbt));
            } //不属于可镶嵌物品
        }
        return StoneInstallResult.fail;
    }
}
