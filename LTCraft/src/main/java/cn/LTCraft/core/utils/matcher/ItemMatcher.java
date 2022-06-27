package cn.LTCraft.core.utils.matcher;

import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.entityClass.RandomValue;
import cn.LTCraft.core.utils.ItemUtils;
import cn.ltcraft.item.base.AICLA;
import cn.ltcraft.item.base.AbstractAttribute;
import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.base.interfaces.Attribute;
import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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
    public static ItemMatcher parse(String syntax){
        String[] split = syntax.split("---");
        ItemMatcher itemMatcher = new ItemMatcher();
        for (String s : split) {
            String start = s.substring(0, 2);
            String rule = s.substring(2);
            switch (start){
                case "NM"://nameMatcher
                    itemMatcher.setNameMatcher(rule);
                break;
                case "IT"://itemType
                    itemMatcher.setItemType(rule);
                break;
                case "QM"://qualityMatcher
                    itemMatcher.setQualityMatcher(rule);
                break;
                case "PP"://PVPDamage
                    itemMatcher.setPVPDamage(rule);
                break;
                case "PE"://PVEDamage
                    itemMatcher.setPVEDamage(rule);
                break;
                case "LR"://lore
                    itemMatcher.addLore(rule);
                break;
                case "EM"://enchantments
                    itemMatcher.addEnchantments(rule);
                break;
            }
        }
        return itemMatcher;
    }
    /**
     * 名字匹配
     */
    private Pattern nameMatcher = null;
    /**
     * 类型匹配
     */
    private ItemTypes itemType = null;
    /**
     * 品质匹配
     */
    private String qualityMatcher = null;
    /**
     * PVP伤害匹配
     */
    private RandomValue PVPDamage = null;
    /**
     * PVE伤害匹配
     */
    private RandomValue PVEDamage = null;
    /**
     * lore匹配
     */
    private List<Pattern> lore = null;
    /**
     * 附魔匹配
     */
    private List<Enchantment> enchantments = null;

    public ItemMatcher setNameMatcher(Pattern nameMatcher) {
        this.nameMatcher = nameMatcher;
        return this;
    }

    public ItemMatcher setNameMatcher(String nameMatcher) {
        this.nameMatcher = Pattern.compile(nameMatcher);
        return this;
    }

    public ItemMatcher setItemType(ItemTypes itemType) {
        this.itemType = itemType;
        return this;
    }

    public ItemMatcher setItemType(String itemType) {
        this.itemType = ItemTypes.byName(itemType);
        return this;
    }

    public ItemMatcher setQualityMatcher(String qualityMatcher) {
        this.qualityMatcher = qualityMatcher;
        return this;
    }

    public ItemMatcher setPVPDamage(String PVPDamage) {
        this.PVPDamage = new RandomValue(PVPDamage);
        return this;
    }

    public ItemMatcher setPVPDamage(RandomValue PVPDamage) {
        this.PVPDamage = PVPDamage;
        return this;
    }

    public ItemMatcher setPVEDamage(String PVEDamage) {
        this.PVPDamage = new RandomValue(PVEDamage);
        return this;
    }

    public ItemMatcher setPVEDamage(RandomValue PVEDamage) {
        this.PVEDamage = PVEDamage;
        return this;
    }

    public ItemMatcher addEnchantments(String enchantment) {
        if (enchantments == null)enchantments = new ArrayList<>();
        this.enchantments.add(Enchantment.getByName(enchantment));
        return this;
    }

    public ItemMatcher addEnchantments(Enchantment enchantment) {
        if (enchantments == null)enchantments = new ArrayList<>();
        this.enchantments.add(enchantment);
        return this;
    }

    public ItemMatcher addLore(String lore) {
        if (this.lore == null)this.lore = new ArrayList<>();
        this.lore.add(Pattern.compile(lore));
        return this;
    }

    public ItemMatcher addLore(Pattern lore) {
        if (this.lore == null)this.lore = new ArrayList<>();
        this.lore.add(lore);
        return this;
    }

    /**
     * 测试匹配
     * @param itemStack 要测试的物品
     * @param player 绑定的玩家
     * @return 如果通过
     */
    public boolean matches(ItemStack itemStack, Player player){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (nameMatcher != null && !nameMatcher.matcher(cn.LTCraft.core.utils.Utils.clearColor(itemMeta.getDisplayName())).find())return false;
        if (lore != null && lore.stream().noneMatch(lore -> itemMeta.getLore().stream().noneMatch(l -> lore.matcher(l).find())))return false;
        if (enchantments != null && enchantments.stream().noneMatch(enchantment -> itemMeta.getEnchantLevel(enchantment) > 0))return false;//TODO 附魔等级
        LTItem ltItems = Utils.getLTItems(itemStack);
        if (player == null)return true;
        if (ltItems != null && (ltItems.binding() && !ItemUtils.getBinding(itemStack).equals(player.getName().toLowerCase()))) {
            return false;
        }
        return matches(ltItems);
    }
    public boolean matches(ClutterItem clutterItem, Player player){
        ItemStack generate = clutterItem.generate();
        if (!matches(generate, null)) {
            return false;
        }
        if (clutterItem.getItemSource() == ClutterItem.ItemSource.LTCraft){
            return matches(clutterItem.getLtItems());
        }
        return true;
    }
    public boolean matches(LTItem ltItems){
        if (itemType != null && (ltItems == null || ltItems.getType() != itemType))return false;
        if (qualityMatcher != null && (!(ltItems instanceof ConfigurableLTItem) || !Objects.equals(qualityMatcher, ((ConfigurableLTItem) ltItems).getConfig().getString("品质", "未知"))))return false;
        if (PVPDamage != null && (!(ltItems instanceof AbstractAttribute) || !((AbstractAttribute) ltItems).getDamage(Attribute.Type.PVP).equals(PVPDamage)))return false;
        if (PVEDamage != null && (!(ltItems instanceof AbstractAttribute) || !((AbstractAttribute) ltItems).getDamage(Attribute.Type.PVE).equals(PVEDamage)))return false;
        return true;
    }
}
