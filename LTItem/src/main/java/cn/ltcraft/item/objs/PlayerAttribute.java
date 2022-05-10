package cn.ltcraft.item.objs;

import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.DragonCoreUtil;
import cn.LTCraft.core.utils.ItemUtils;
import cn.LTCraft.core.utils.MathUtils;
import cn.ltcraft.item.base.AICLA;
import cn.ltcraft.item.base.AbstractAttribute;
import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.base.interfaces.InsertableConfigurableLTItem;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.items.Armor;
import cn.ltcraft.item.items.BaseWeapon;
import cn.ltcraft.item.items.MeleeWeapon;
import cn.ltcraft.item.items.Ornament;
import cn.ltcraft.item.utils.Utils;
import com.google.common.collect.ObjectArrays;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerAttribute extends AbstractAttribute implements TickEntity {
    private static Map<String, PlayerAttribute> playerAttributeMap = new HashMap<>();
    private ItemStack[] equipment = new ItemStack[4];
    private Armor[] equipmentAtt = new Armor[4];
    private ItemStack[] ornaments = new ItemStack[7];//饰品
    private Ornament[] ornamentsAtt = new Ornament[7];
    private ItemStack hand;
    private BaseWeapon handAtt;
    private ItemStack offHand;
    private final Player owner;
    private int tick = 0;
    public PlayerAttribute(Player player){
        owner = player;
        GlobalRefresh.addTickEntity(this);
        init();
    }

    /**
     * tick 每1tick
     */
    public boolean doTick(long tick){
        if (!owner.isOnline()){
            playerAttributeMap.remove(owner.getName());
            return false;
        }
        if (tick % 200 == 0){
            getPotion().forEach((type, potionAttribute) -> {
                if (MathUtils.ifAdopt(potionAttribute.getProbability())) {
                    owner.addPotionEffect(potionAttribute);
                }
            });
        }
        if (tick % 20 == 0){
            onChangeHand();
        }
        checkChangeArmor();
        return true;
    }

    @Override
    public int getTickRate() {
        return 1;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    /**
     * 检查玩家盔甲更新
     */
    public void checkChangeArmor(){
        PlayerInventory playerInventory = owner.getInventory();
        if (!Arrays.equals(equipment, playerInventory.getArmorContents())){
            //更新盔甲
            ItemStack[] armorContents = playerInventory.getArmorContents();
            int healthValue = getHealthValue();
            for (int i = 0; i < armorContents.length; i++) {
                ItemStack itemStack = armorContents[i];
                if (!Objects.equals(equipment[i], itemStack)){
                    if (equipmentAtt[i] != null)removeAttribute(equipmentAtt[i]);
                    equipment[i] = itemStack;
                    equipmentAtt[i] = null;
                    if (itemStack != null)itemStack = itemStack.clone();
                    if (itemStack == null)continue;
                    NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
                    boolean canUse = true;//是否有权限使用
                    LTItem ltItem = Utils.getLTItems(nbt);
                    if (ltItem == null)continue;
                    String binding = ItemUtils.getBinding(nbt);
                    if (ltItem.binding()) {
                        if (binding != null && !binding.equalsIgnoreCase(owner.getName())) {
                            if (!binding.equals("*")) {
                                if (binding.equals("?")) {
                                    itemStack = ItemUtils.setBinding(itemStack, owner.getName());
                                } else {
                                    canUse = false;
                                }
                            }
                        }
                    }
                    if (canUse && ltItem instanceof Armor) {
                        Armor armor = (Armor) ltItem;
                        armor = (Armor) Utils.calculationAttr(armor.clone(), nbt);
                        equipmentAtt[i] = armor;
                        itemStack = Utils.updateNameAndLore(itemStack, armor, armor.getLore());
                        addAttribute(equipmentAtt[i]);
                    }else if (ltItem instanceof ConfigurableLTItem){
                        AbstractAttribute attribute = null;
                        if (ltItem instanceof AbstractAttribute) {
                            attribute = (AbstractAttribute) ltItem;
                            if (ltItem instanceof AICLA){
                                attribute = Utils.calculationAttr((AICLA) attribute.clone(), nbt);
                            }
                        }
                        itemStack = Utils.updateNameAndLore(itemStack, ltItem instanceof AbstractAttribute?attribute:null, ((ConfigurableLTItem)ltItem).getLore());
                    }
                    if (!itemStack.equals(armorContents[i])) {
                        equipment[i] = itemStack;
                        armorContents[i] = itemStack;
                    }
                }
            }
            if (!Arrays.equals(armorContents, playerInventory.getArmorContents())){
                playerInventory.setArmorContents(armorContents);
            }
            if (healthValue != getHealthValue()){
                owner.setMaxHealth(owner.getMaxHealth() + (getHealthValue() - healthValue));
            }
        }
    }

    /**
     * 玩家手持
     */
    public void onChangeHand(){
        onChangeHand(owner.getInventory().getHeldItemSlot());
    }
    public void onChangeHand(int slot){
        ItemStack itemStack = owner.getInventory().getItem(slot);
        if (Objects.equals(hand, itemStack))return;
        if (itemStack != null)itemStack = itemStack.clone();
        //移除原本手持属性
        if (handAtt != null)removeAttribute(handAtt);
        //设置现在手持
        hand = itemStack;
        handAtt = null;
        //手持空气
        if (itemStack == null)return;
        NBTTagCompound nbt = ItemUtils.getNBT(itemStack);//获取NBT
        LTItem ltItem = Utils.getLTItems(nbt);
        if (ltItem == null)return;
        boolean canUse = true;//是否有权限使用
        if (ltItem.binding()) {
            String binding = ItemUtils.getBinding(nbt);//获取绑定
            if (binding != null && !binding.equalsIgnoreCase(owner.getName())) {//如果绑定与玩家id不符合
                if (!binding.equals("*")) {//不是不绑定的
                    if (binding.equals("?")) {//还未绑定
                        itemStack = ItemUtils.setBinding(itemStack, owner.getName());//绑定
                    } else {
                        canUse = false;
                    }
                }
            }
        }
        if (canUse && ltItem instanceof BaseWeapon) {
            BaseWeapon baseWeapon = (BaseWeapon) ltItem;
            baseWeapon = (BaseWeapon) Utils.calculationAttr(baseWeapon.clone(), nbt);
            handAtt = baseWeapon;
            itemStack = Utils.updateNameAndLore(itemStack, baseWeapon, baseWeapon.getLore());
            addAttribute(handAtt);
        }else if (ltItem instanceof ConfigurableLTItem){
            AbstractAttribute attribute = null;
            if (ltItem instanceof AbstractAttribute) {
                attribute = (AbstractAttribute) ltItem;
                if (ltItem instanceof AICLA){
                    attribute = Utils.calculationAttr((AICLA) attribute.clone(), nbt);
                }
            }
            itemStack = Utils.updateNameAndLore(itemStack, ltItem instanceof AbstractAttribute?attribute:null, ((ConfigurableLTItem)ltItem).getLore());
        }
        if (!owner.getInventory().getItem(slot).equals(itemStack)) {
            hand = itemStack;
            owner.getInventory().setItem(slot, itemStack);
        }
    }
    public void onChangeOrnament(int slot){
        DragonCoreUtil.getItemStack(owner, "饰品槽位" + slot, (itemStack) -> {
            if (Objects.equals(ornaments[slot - 1], itemStack))return;
            if (itemStack != null)itemStack = itemStack.clone();
            //移除原本手持属性
            if (ornamentsAtt[slot - 1] != null)removeAttribute(ornamentsAtt[slot - 1]);
            //设置现在手持
            ornaments[slot - 1] = itemStack;
            ornamentsAtt[slot - 1] = null;
            //手持空气
            if (itemStack == null)return;
            NBTTagCompound nbt = ItemUtils.getNBT(itemStack);//获取NBT
            LTItem ltItem = Utils.getLTItems(nbt);
            if (ltItem == null)return;
            boolean canUse = true;//是否有权限使用
            if (ltItem.binding()) {
                String binding = ItemUtils.getBinding(nbt);//获取绑定
                if (binding != null && !binding.equalsIgnoreCase(owner.getName())) {//如果绑定与玩家id不符合
                    if (!binding.equals("*")) {//不是不绑定的
                        canUse = false;
                    }
                }
            }
            if (canUse && ltItem instanceof Ornament) {
                Ornament ornament = (Ornament) ltItem;
                ornamentsAtt[slot - 1] = ornament;
                addAttribute(ornament);
            }
        });
    }

    public Player getOwner() {
        return owner;
    }


    /**
     * 获取主手物品
     * @return 主手物品
     */
    public BaseWeapon getHandAtt() {
        return handAtt;
    }


    /**
     * 获取盔甲
     * @return 盔甲
     */
    public Armor[] getEquipmentAtt() {
        return equipmentAtt;
    }

    /**
     * 获取全部佩戴的饰品
     * @return 佩戴的饰品
     */
    public Ornament[] getOrnamentsAtt() {
        return ornamentsAtt;
    }

    /**
     * 获取饰品的物品
     * @return 饰品物品
     */
    public ItemStack[] getOrnaments() {
        return ornaments;
    }

    /**
     * 获取装备
     * @return 装备物品
     */
    public ItemStack[] getEquipment() {
        return equipment;
    }

    /**
     * @return 副手
     */
    public ItemStack getOffHand() {
        return offHand;
    }


    /**
     * @return 主手
     */
    public ItemStack getHand() {
        return hand;
    }

    /**
     * 获取全部LTItem物品
     * @return LTItem物品
     */
    public LTItem[] getLTItems(){
        return ObjectArrays.concat(new LTItem[]{handAtt, null/* 副手 */}, ObjectArrays.concat(ornamentsAtt, equipmentAtt, LTItem.class), LTItem.class);
    }

    @Override
    public void reset() {
        super.reset();
        hand = null;
        handAtt = null;
        offHand = null;
        equipment = new ItemStack[4];
        equipmentAtt = new Armor[4];
        ornaments = new ItemStack[7];
        ornamentsAtt = new Ornament[7];
        onChangeHand();
        checkChangeArmor();
        for (int i = 0; i < 7; i++) {
            onChangeOrnament(i + 1);
        }
    }
    public void init(){
        onChangeHand();
        checkChangeArmor();
        for (int i = 0; i < 7; i++) {
            onChangeOrnament(i + 1);
        }
    }
    public static PlayerAttribute getPlayerAttribute(Player player){
        if (!playerAttributeMap.containsKey(player.getName())){
            playerAttributeMap.put(player.getName(), new PlayerAttribute(player));
        }
        return playerAttributeMap.get(player.getName());
    }

    public static Map<String, PlayerAttribute> getPlayerAttributeMap() {
        return playerAttributeMap;
    }
}
