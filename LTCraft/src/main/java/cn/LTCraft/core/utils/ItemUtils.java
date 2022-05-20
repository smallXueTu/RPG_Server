package cn.LTCraft.core.utils;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class ItemUtils {
    /**
     * 替换lore
     * @param itemStack 物品
     * @param oldStr 老字符串
     * @param newStr 新字符串
     * @return 替换后的
     */
    public static ItemStack replaceLore(ItemStack itemStack, String oldStr, String newStr){
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        lore.replaceAll(s1 -> s1.replace(oldStr, newStr));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * 两个物品堆 如果第一次比较失败则清除隐藏标签重新比较
     * @param itemStack1 物品1
     * @param itemStack2 物品2
     * @return 如果成功
     */
    public static boolean isSimilar(ItemStack itemStack1, ItemStack itemStack2){
        if (itemStack1.isSimilar(itemStack2))return true;
        //比较失败 清除可变标签
        itemStack1 = cleanVar(itemStack1.clone());
        itemStack2 = cleanVar(itemStack2.clone());
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

    /**
     * 清除可变标签
     * @param itemStack 物品 注意先 {@link ItemStack#clone()}
     * @return 隐藏后的
     */
    public static ItemStack cleanVar(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)return itemStack;
        List<String> lore = itemMeta.getLore();
        if (lore != null && lore.size() > 0 && lore.get(lore.size() - 1).startsWith("sign")){
            lore.remove(lore.size() - 1);
            itemMeta.setLore(lore);
        }
        itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * 获取物品Handle
     * @param itemStack 物品堆
     * @return Handle 句柄
     */
    public static net.minecraft.server.v1_12_R1.ItemStack getHandle(ItemStack itemStack){
        Field handle = ReflectionHelper.findField(CraftItemStack.class, "handle");
        if (handle == null)return null;
        ItemStack item = itemStack;
        if (!(itemStack instanceof CraftItemStack)){
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            CraftItemStack.asNMSCopy(itemStack).save(nbtTagCompound);
            item = CraftItemStack.asCraftMirror(new net.minecraft.server.v1_12_R1.ItemStack(nbtTagCompound));
        }
        try {
            return (net.minecraft.server.v1_12_R1.ItemStack) handle.get(item);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置物品Handle
     * @param itemStack .
     * @param handleObj .
     */
    public static ItemStack setHandle(ItemStack itemStack, net.minecraft.server.v1_12_R1.ItemStack handleObj){
        Field handle = ReflectionHelper.findField(CraftItemStack.class, "handle");
        if (handle == null)return itemStack;
        if (!(itemStack instanceof CraftItemStack)){
            itemStack = CraftItemStack.asCraftCopy(itemStack);
        }
        try {
            handle.set(itemStack, handleObj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return itemStack;
    }

    /**
     * 获取物品NBT
     * @param itemStack 物品
     * @return NBT
     */
    public static NBTTagCompound getNBT(ItemStack itemStack){
        net.minecraft.server.v1_12_R1.ItemStack handle = getHandle(itemStack);
        if (handle == null)return null;
        return handle.getTag();
    }

    /**
     * 设置物品NBT
     * @param itemStack 物品
     * @param nbt NBT
     */
    public static ItemStack setNBT(ItemStack itemStack, NBTTagCompound nbt){
        if (!(itemStack instanceof CraftItemStack)){
            itemStack = CraftItemStack.asCraftCopy(itemStack);
        }
        net.minecraft.server.v1_12_R1.ItemStack handle = getHandle(itemStack);
        if (handle == null)return itemStack;
        handle.setTag(nbt);
        return itemStack;
    }

    /**
     * 绑定相关
     * @param itemStack 堆栈
     * @return 绑定的id 比较请忽略大小写
     */
    public static String getBinding(ItemStack itemStack) {
        return getBinding(getNBT(itemStack));
    }
    public static String getBinding(NBTTagCompound nbt){
        if (nbt == null)return null;
        if (nbt.hasKey("ltAttribute")){
            NBTTagCompound ltAttribute = nbt.getCompound("ltAttribute");
            return ltAttribute.getString("binding");
        }
        return null;
    }
    public static ItemStack setBinding(ItemStack itemStack, String binding) {
        NBTTagCompound nbt = getNBT(itemStack);
        if (nbt == null)return itemStack;
        nbt = setBinding(nbt, binding);
        return setNBT(itemStack, nbt);
    }
    public static NBTTagCompound setBinding(NBTTagCompound nbt, String binding){
        if (nbt == null)return null;
        if (nbt.hasKey("ltAttribute")){
            NBTTagCompound ltAttribute = nbt.getCompound("ltAttribute");
            if (ltAttribute.getString("binding").equals("?")) {
                ltAttribute.setString("binding", binding.toLowerCase());
            }
        }
        return nbt;
    }

    /**
     * 是否可以使用
     * @param binding 绑定
     * @param playerName 玩家名字
     * @return 是否可以使用
     */
    public static boolean canUse(String binding, String playerName){
        if (binding.equals("*"))return true;
        return binding.equalsIgnoreCase(playerName);
    }
    public static boolean canUse(String binding, Player player){
        return canUse(binding, player.getName());
    }
    public static boolean canUse(NBTTagCompound nbt, Player player){
        return canUse(getBinding(nbt), player.getName());
    }
    public static boolean canUse(ItemStack itemStack, Player player){
        return canUse(getBinding(itemStack), player.getName());
    }

    /**
     * 判断目标背包是否拥有足够的项目
     * @param itemStacks 物品
     * @param player 玩家
     * @return 是否通过
     */
    public static boolean sufficientItem(ItemStack[] itemStacks, Player player) {
        return sufficientItem(itemStacks, clone(player.getInventory().getContents()));
    }
    public static boolean sufficientItem(ItemStack[] itemStacks, ItemStack[] contents){
        for (ItemStack itemStack : itemStacks) {
            if (removeItem(contents, itemStack) > 0) {
                return false;
            }
        }
        return true;
    }
    public static boolean sufficientItem(ItemStack itemStack, Player player) {
        return sufficientItem(itemStack, clone(player.getInventory().getContents()));
    }
    public static boolean sufficientItem(ItemStack itemStack, ItemStack[] contents){
        return removeItem(contents, itemStack) <= 0;
    }

    /**
     * 扣除物品
     * @param items 要被扣除的
     * @param itemStack 扣除的物品
     * @return 未扣除成功的
     */
    public static int removeItem(ItemStack[] items, ItemStack itemStack) {
        int counter = itemStack.getAmount();
        for(int i = 0; i < items.length; ++i) {
            if (items[i] == null)continue;
            if (itemStack.isSimilar(items[i])) {
                if (items[i].getAmount() - counter <= 0) {
                    counter -= items[i].getAmount();
                    items[i] = null;
                } else {
                    items[i].setAmount(items[i].getAmount() - counter);
                    counter = 0;
                }
                if (counter <= 0) {
                    break;
                }
            }
        }
        return counter;
    }

    /**
     *
     * @param items 扣除容器
     * @param itemStacks 需要扣除的物品
     */
    public static void removeItems(ItemStack[] items, ItemStack[] itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null) removeItem(items, itemStack);
        }
    }
    public static void removeItems(ItemStack[] items, ClutterItem[] clutterItems, Player player) {
        for (ClutterItem clutterItem : clutterItems) {
            removeItem(items, clutterItem, player);
        }
    }

    /**
     *
     * @param player 为了验证绑定 请提供玩家对象用于验证
     */
    public static boolean sufficientItem(ClutterItem[] items, Player player){
        ItemStack[] contents = clone(player.getInventory().getContents());
        for (ClutterItem item : items) {
            if (removeItem(contents, item, player) > 0){
                return false;
            }
        }
        return true;
    }
    public static int removeItem(ItemStack[] items, ClutterItem clutterItem, Player player) {
        int counter = clutterItem.getNumber();
        NBTTagCompound nbt;
        for(int i = 0; i < items.length; ++i) {
            if (items[i] == null)continue;
            if (clutterItem.getItemSource() == ClutterItem.ItemSource.LTCraft){
                nbt = ItemUtils.getNBT(items[i]);
                if (!ItemUtils.isSimilar(clutterItem, nbt) || !ItemUtils.canUse(nbt, player))continue;
            }else if (!ItemUtils.isSimilar(clutterItem, items[i]))continue;
            if (items[i].getAmount() - counter <= 0) {
                counter -= items[i].getAmount();
                items[i] = null;
            } else {
                items[i].setAmount(items[i].getAmount() - counter);
                counter = 0;
            }
            if (counter <= 0) {
                break;
            }
        }
        return counter;
    }

    /**
     * 克隆一组 ItemStack
     * @param itemStacks ItemStack[]
     * @return 克隆后的 防止引用的问题
     */
    public static ItemStack[] clone(ItemStack[] itemStacks){
        ItemStack[] itemStacks1 = new ItemStack[itemStacks.length];
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] == null)continue;
            itemStacks1[i] = itemStacks[i].clone();
        }
        return itemStacks1;
    }
    public static ItemStack getItem(String itemName){
        return ClutterItem.spawnClutterItem(itemName).generate();
    }
}
