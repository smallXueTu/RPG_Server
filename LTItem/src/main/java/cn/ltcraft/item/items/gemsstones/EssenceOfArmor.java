package cn.ltcraft.item.items.gemsstones;

import cn.LTCraft.core.utils.ItemUtils;
import cn.ltcraft.item.base.AICLA;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.enums.ItemQuality;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.item.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * 盔甲精髓
 * Created by Angel、 on 2024/4/26 23:53
 */
public class EssenceOfArmor extends GemsStone implements ConsumeGemstones {
    //增加等级
    private static final int INCREASE_LEVEL = 10;
    // 成功几率
    public final float SUCCESS_RATE = 0.1f;
    public EssenceOfArmor(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public ItemStack installOn(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
            LTItem ltItems = Utils.getLTItems(nbt);
            if (ltItems instanceof AICLA){
                AICLA aicla = (AICLA) ltItems;
                int equipmentMaxLevel = Utils.getEquipmentMaxLevel(nbt);
                ItemQuality quality = ItemQuality.ofQuality(aicla.getConfig().getString("品质", ""));
                //未知品质
                if (Objects.isNull(quality))return null;
                int maxLevel = quality.getMaxLevel();
                //不支持此物品
                if (maxLevel < 0)return null;
                //已经到最大等级
                if (equipmentMaxLevel >= maxLevel)return null;
                Utils.setEquipmentMaxLevel(nbt, equipmentMaxLevel + INCREASE_LEVEL);
                return ItemUtils.setNBT(itemStack, nbt);
            }else {//不属于可镶嵌物品
                return null;
            }
        }
        return null;
    }

    @Override
    public float getRate() {
        return SUCCESS_RATE;
    }
}
