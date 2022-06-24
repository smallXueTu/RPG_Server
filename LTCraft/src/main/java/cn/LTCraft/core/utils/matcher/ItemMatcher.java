package cn.LTCraft.core.utils.matcher;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.utils.ItemUtils;
import cn.ltcraft.item.base.interfaces.LTItem;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * 物品匹配器
 * Created by Angel、 on 2022/6/22 23:55
 */
public class ItemMatcher {
    //------------------------------------- static -------------------------------------
    /**
     * 两个物品堆 如果第一次比较失败则清除隐藏标签重新比较
     * @param itemStack1 物品1
     * @param itemStack2 物品2
     * @return 如果成功
     */
    public static boolean isSimilar(ItemStack itemStack1, ItemStack itemStack2){
        if (itemStack1.isSimilar(itemStack2))return true;
        //比较失败 清除可变标签
        itemStack1 = ItemUtils.cleanVar(itemStack1.clone());
        itemStack2 = ItemUtils.cleanVar(itemStack2.clone());
        return itemStack1.isSimilar(itemStack2);
    }

    /**
     * 两个杂物的对比
     * @param clutterItems1 杂物
     * @param clutterItems2 杂物
     * @return 如果是一个物品
     */
    public static boolean isSimilar(ClutterItem clutterItems1, ClutterItem clutterItems2){
        if (Objects.equals(clutterItems2, clutterItems1))return true;
        if (clutterItems1.getItemSource()!= clutterItems2.getItemSource())return false;
        return clutterItems1.isSimilar(clutterItems2);
    }
    /**
     * 一个 杂物 和一个物品堆 比较
     * @param clutterItems 杂物
     * @param itemStack 物品堆
     * @return 如果是一个物品
     */
    public static boolean isSimilar(ClutterItem clutterItems, ItemStack itemStack){
        return clutterItems.isSimilar(itemStack);
    }
    /**
     * 一个 杂物 和一个NBT 比较
     * @param clutterItems 杂物
     * @param nbtTagCompound NBT
     * @return 如果是一个物品
     */
    public static boolean isSimilar(ClutterItem clutterItems, NBTTagCompound nbtTagCompound){
        return clutterItems.isSimilar(nbtTagCompound);
    }

    /**
     * 一个 LTItem 和一个物品堆 比较
     * @param ltItems1 LTItem
     * @param itemStack 物品堆
     * @return 如果是同一个
     */
    public static boolean isSimilar(LTItem ltItems1, ItemStack itemStack){
        return isSimilar(ltItems1, cn.ltcraft.item.utils.Utils.getLTItems(itemStack));
    }

    /**
     * 一个 LTItem 和一个NBT 比较
     * @param ltItems1 LTItem
     * @param nbtTagCompound NBT 物品的nbt
     * @return 如果是同一个
     */
    public static boolean isSimilar(LTItem ltItems1, NBTTagCompound nbtTagCompound){
        return isSimilar(ltItems1, cn.ltcraft.item.utils.Utils.getLTItems(nbtTagCompound));
    }

    /**
     * 两个 LTItem 比较
     * @param ltItems1 LTItem
     * @param ltItems2 LTItem
     * @return 如果是同一个
     */
    public static boolean isSimilar(LTItem ltItems1, LTItem ltItems2){
        if (Objects.equals(ltItems2, ltItems1))return true;
        if (ltItems2 == null || ltItems1 == null)return false;
        return ltItems1.getType().equals(ltItems2.getType()) && ltItems1.getName().equals(ltItems2.getName());
    }
}
