package cn.LTCraft.core.entityClass;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.ItemUtils;
import cn.LTCraft.core.utils.matcher.ItemMatcher;
import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.objs.ItemObjs;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitItemStack;
import io.lumine.xikage.mythicmobs.items.MythicItem;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.ItemID;
import pl.betoncraft.betonquest.ObjectNotFoundException;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.item.QuestItem;

import java.util.Arrays;
import java.util.Optional;

/**
 * 杂物
 * 包含
 * LTCraft物品
 * BetonQuest物品
 * MythicMobs物品
 * 构建方式只需要传递一个字符串，将自动判断属于那个类型的物品
 */
public class ClutterItem {
    public enum ItemSource{
        LTCraft,
        BetonQuest,
        MythicMobs,
        Minecraft,
    }

    /**
     * 生成杂物物品
     * @param itemString 字符串 用来解析
     * @return 杂物
     */
    public static ClutterItem spawnClutterItem(String itemString){
        ClutterItem clutterItem = new ClutterItem(itemString);
        if (clutterItem.isFail()){
            Main.getInstance().getLogger().warning("无法解析：" + itemString);
            return new ClutterItem("AIR");
        }
        return clutterItem;
    }
    public static ClutterItem spawnClutterItem(String itemString, ItemSource itemSource){
        ClutterItem clutterItem = new ClutterItem(itemString, itemSource);
        if (clutterItem.isFail()){
            Main.getInstance().getLogger().warning("无法解析：" + itemString);
            return new ClutterItem("AIR");
        }
        return clutterItem;
    }
    private QuestItem questItem = null;
    private MythicItem mythicItem = null;
    Material type;
    private ItemStack itemStack = new ItemStack(Material.AIR);
    private LTItem ltItem = null;
    private int number = 1;
    private ItemSource itemSource = null;
    private final String itemString;
    private ClutterItem(String itemStr){
        itemString = itemStr;
        //如果是LTCraft物品
        if (decodeLTCraft()) {
            itemSource = ItemSource.LTCraft;
        }
        //如果是BQ物品
        if(itemSource == null) {
            if (decodeBetonQuest()) {
                itemSource = ItemSource.BetonQuest;
            }
        }
        //如果是MM物品
        if(itemSource == null) {
            if (decodeMythicMobs()) {
                itemSource = ItemSource.MythicMobs;
            }
        }
        if (itemSource == null){
            decodeMinecraft();
            itemSource = ItemSource.Minecraft;
        }
    }
    private ClutterItem(String itemStr, ItemSource itemSource){
        itemString = itemStr;
        this.itemSource = itemSource;
        switch (itemSource){
            case LTCraft:
                decodeLTCraft();
            break;
            case BetonQuest:
                decodeBetonQuest();
            break;
            case MythicMobs:
                decodeMythicMobs();
            break;
            case Minecraft:
                decodeMinecraft();
            break;
        }
    }
    public ItemStack generate(int count){
        if (itemSource == null)return null;
        ItemStack result;
        switch (itemSource){
            case LTCraft:
                if (itemStack == null || itemStack.getType() == Material.AIR){
                    itemStack = ltItem.generate(count);
                    itemStack.setAmount(number);
                }
                result = itemStack.clone();
                result.setAmount(count);
                return result;
            case BetonQuest:
                if (itemStack == null || itemStack.getType() == Material.AIR){
                    itemStack = questItem.generate(count);
                    itemStack.setAmount(number);
                }
                result = itemStack.clone();
                result.setAmount(count);
                return result;
            case Minecraft:
                result = itemStack.clone();
                result.setAmount(count);
                return result;
            case MythicMobs:
                if (itemStack == null || itemStack.getType() == Material.AIR){
                    AbstractItemStack abstractItemStack = mythicItem.generateItemStack(count);
                    if (abstractItemStack == null) return null;
                    itemStack = ((BukkitItemStack) abstractItemStack).build();
                    itemStack.setAmount(number);
                }
                result = itemStack.clone();
                result.setAmount(count);
                return result;
            default:
                return null;
        }
    }
    public ItemStack generate(){
        return generate(number);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ItemSource getItemSource() {
        return itemSource;
    }

    public boolean compare(ItemStack itemStack){
        return isSimilar(itemStack);
    }

    /**
     * 是否相似
     * @param itemStack 物品堆
     * @return 是否相识
     */
    public boolean isSimilar(ItemStack itemStack){
        ItemStack generate = generate();
        if (generate == null && itemStack == null)return true;
        if (generate == null)return false;
        if (itemSource == ItemSource.LTCraft) {
            return ItemMatcher.isSimilar(getLtItems(), itemStack);
        }
        return ItemMatcher.isSimilar(generate, itemStack);
    }

    /**
     * 是否相似
     * @param nbtTagCompound nbt
     * @return 是否相识
     * 注意 NBT只能和 {@link ItemSource#LTCraft} 比较
     */
    public boolean isSimilar(NBTTagCompound nbtTagCompound){
        ItemStack generate = generate();
        if (generate == null && nbtTagCompound == null)return true;
        if (generate == null)return false;
        if (itemSource == ItemSource.LTCraft) {
            return ItemMatcher.isSimilar(getLtItems(), nbtTagCompound);
        }
        return false;
    }

    /**
     * 是否相识
     * @param clutterItem 杂物
     * @return 是否相识
     */
    public boolean isSimilar(ClutterItem clutterItem){
        if (getItemSource() != clutterItem.getItemSource())return false;
        if (clutterItem.getItemSource() == ItemSource.LTCraft && getItemSource() == ItemSource.LTCraft){
            return ItemMatcher.isSimilar(clutterItem.getLtItems(), getLtItems());
        }
        ItemStack generate = generate();
        return ItemMatcher.isSimilar(generate, clutterItem.generate());
    }
    @Override
    public String toString() {
        return itemString;
    }

    /**
     * 获取可读的String
     * @return 可被 玩家理解的
     */
    public String toStringReadable(){
        switch (itemSource){
            case LTCraft:
                return ltItem.getType().toString() + "类型的" + ltItem.getName() + "×" + number;
            case BetonQuest:
            case MythicMobs:
            case Minecraft:
            default:
                return itemString;
        }
    }
    /**
     * 解析LTCraft物品
     * @return 是否成功
     */
    public boolean decodeLTCraft(){
        String type = null;
        String name;
        if (itemString.contains(":")){
            String[] split = itemString.split(":");
            type = split[0];
            try {
                if (ItemTypes.byName(type) == null){
                    name = type;
                    type = null;
                    number = Integer.parseInt(split[1]);
                }else {
                    name = split[1];
                }
                if (split.length >= 3){
                    number = Integer.parseInt(split[2]);
                }
            }catch (NumberFormatException e){
                return false;
            }
        }else {
            name = itemString;
        }
        LTItem ltItems = ItemObjs.getLTItem(type, name);
        if (ltItems != null){
            this.ltItem = ltItems;
        }
        return ltItems != null;
    }

    /**
     * 解析MM物品
     * @return 是否成功
     */
    public boolean decodeMythicMobs(){
        String[] split = itemString.split(":");
        Optional<MythicItem> maybeItem = MythicMobs.inst().getItemManager().getItem(split[0]);
        try {
            if (split.length >= 2)number = Integer.parseInt(split[1]);
        }catch (NumberFormatException e){
            return false;
        }
        maybeItem.ifPresent(item -> mythicItem = item);
        return mythicItem != null;
    }

    /**
     * 解析原版物品
     */
    public void decodeMinecraft(){
        try {
        if (itemString.matches("\\d+(:\\d+)?(:\\d+)?")) {
            String[] mI = itemString.split(":");
            itemStack = new ItemStack(Integer.parseInt(mI[0]), mI.length > 2 ? Integer.parseInt(mI[2]) : 1, mI.length > 1 ? Short.parseShort(mI[1]) : 0);
            number = itemStack.getAmount();
        } else {
            number = 1;
            String itemString = this.itemString;
            if (itemString.contains(":")){
                itemString = itemString.split(":")[0];
                number = Integer.parseInt(this.itemString.split(":")[1]);
            }
            if (Material.getMaterial(itemString) != null) {
                itemStack = new ItemStack(Material.getMaterial(itemString), number);
            }
        }
        }catch (NumberFormatException ignored){
        }
    }

    /**
     * 解析BQ物品
     * @return 是否成功
     */
    public boolean decodeBetonQuest(){
        String itemString = this.itemString;
        try {
            ItemID item = null;
            ConfigPackage pack = null;
            if (itemString.contains(".")){
                pack = Config.getPackages().get(itemString.split("\\.")[0]);
                itemString = itemString.split("\\.")[1];
            }
            if (itemString.contains(":")){
                item = new ItemID(pack, itemString.split(":")[0]);
                number = Integer.parseInt(itemString.split(":")[1]);
            }else {
                item = new ItemID(pack, itemString);
            }
            questItem = new QuestItem(item);
        } catch (ObjectNotFoundException | InstructionParseException ignored) {
            return false;
        }
        return true;
    }

    public LTItem getLtItems() {
        return ltItem;
    }

    public MythicItem getMythicItem() {
        return mythicItem;
    }

    public QuestItem getQuestItem() {
        return questItem;
    }

    public String getItemString() {
        return itemString;
    }

    public ItemStack getItemStack() {
        if (itemStack == null || itemStack.getType() == Material.AIR)generate();
        return itemStack;
    }

    /**
     * 是否解析失败
     * @return 如果解析失败
     */
    public boolean isFail(){
        return itemSource == null;
    }
}
