package cn.ltcraft.item.base.interfaces;

import cn.ltcraft.item.base.ItemTypes;
import org.bukkit.inventory.ItemStack;

/**
 * LTItem
 * 实现这个接口的物品一定是LTItem(LTCraft)插件的物品
 * 该物品目前实现的类型有
 * @see ItemTypes
 * 想要通过物品堆获取 LTItem 请通过 {@link cn.ltcraft.item.utils.Utils#getLTItems(ItemStack)}
 *
 */
public interface LTItem {
    /**
     * 获取物品类型
     * @return 类型
     */
    ItemTypes getType();

    /**
     * 获取物品名字
     * @return 物品名
     */
    String getName();

    /**
     * 获取物品
     * @return 物品
     */
    ItemStack getItemStack();

    /**
     * 构建指定数量的物品
     * @param count 数量
     * @return 物品
     */
    ItemStack generate(int count);

    /**
     * 判断指定玩家是否可以使用此武器
     * @return 是否绑定
     */
    boolean binding();
}
