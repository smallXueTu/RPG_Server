package cn.ltcraft.item.items.gemsstones;

import cn.LTCraft.core.utils.ItemUtils;
import cn.hutool.core.util.ObjectUtil;
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
 * 武器精髓
 * Created by Angel、 on 2024/4/26 23:53
 */
public class EssenceOfWeapons extends GemsStone implements ConsumeGemstones {
    //增加等级
    private static final int INCREASE_LEVEL = 10;
    // 成功几率 10%
    public final float SUCCESS_RATE = 0.1f;
    public EssenceOfWeapons(MemoryConfiguration configuration) {
        super(configuration);
    }

    @Override
    public StoneInstallResult installOn(ItemStack itemStack, int installCount) {
        if (itemStack.hasItemMeta()) {
            NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
            LTItem ltItems = Utils.getLTItems(nbt);
            if (ltItems instanceof BaseWeapon && ltItems instanceof Upgradeable){
                int equipmentMaxLevel = Utils.getEquipmentMaxLevel(nbt);
                ItemQuality quality = ltItems.getQuality();
                //未知品质
                if (ObjectUtil.isNotEmpty(quality)) {
                    int maxLevel = quality.getMaxLevel();
                    //不支持此物品
                    if (maxLevel > 0) {
                        //已经到最大等级
                        if (equipmentMaxLevel < maxLevel) {
                            int consumption = 0;
                            for (; consumption < installCount; consumption++) {
                                if (rateCompute()) {
                                    equipmentMaxLevel = Math.min(INCREASE_LEVEL + equipmentMaxLevel, maxLevel);
                                    if (equipmentMaxLevel == maxLevel){
                                        consumption++;
                                        break;
                                    }
                                }
                            }
                            Utils.setEquipmentMaxLevel(nbt, equipmentMaxLevel);
                            return new StoneInstallResult(consumption, ItemUtils.setNBT(itemStack, nbt));
                        }
                    }
                }
            }else {//不属于可镶嵌物品
                return StoneInstallResult.fail;
            }
        }
        return StoneInstallResult.fail;
    }

    @Override
    public float getRate() {
        return SUCCESS_RATE;
    }
}
