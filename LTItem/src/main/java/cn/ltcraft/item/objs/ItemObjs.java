package cn.ltcraft.item.objs;

import cn.LTCraft.core.utils.ItemUtils;
import cn.ltcraft.item.base.ItemTypes;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.items.*;
import cn.ltcraft.item.utils.Utils;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemObjs {
    public static final Map<String, MeleeWeapon> meleeWeaponMap = new HashMap<>();
    public static final Map<String, Armor> armorMap = new HashMap<>();
    public static final Map<String, GemsStone> gemstoneMap = new HashMap<>();
    public static final Map<String, Ornament> ornamentMap = new HashMap<>();
    public static final Map<String, Material> materialMap = new HashMap<>();
    public static LTItem getHandLTItem(Player player){
        ItemStack itemStack = player.getItemInHand();
        NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
        return Utils.getLTItems(nbt);
    }
    public static LTItem getLTItem(String type, String name){
        if (type == null)return getLTItem(name);
        return getLTItem(ItemTypes.byName(type), name);
    }
    public static LTItem getLTItem(ItemTypes type, String name){
        if (type == null)return getLTItem(name);
        switch (type){
            case Material:
                return materialMap.get(name);
            case Armor:
                return armorMap.get(name);
            case Melee:
                return meleeWeaponMap.get(name);
            case Gemstone:
                return gemstoneMap.get(name);
            case Ornament:
                return ornamentMap.get(name);
            default:
                return null;
        }
    }
    public static LTItem getLTItem(String name){
        if (materialMap.containsKey(name))
            return materialMap.get(name);
        else if (armorMap.containsKey(name))
            return armorMap.get(name);
        else if (meleeWeaponMap.containsKey(name))
            return meleeWeaponMap.get(name);
        else if (gemstoneMap.containsKey(name))
            return gemstoneMap.get(name);
        else if (ornamentMap.containsKey(name))
            return ornamentMap.get(name);
        else
            return null;
    }
    public static GemsStone getGemstone(String name){
        return gemstoneMap.get(name);
    }
    public static void putLTItem(String name, LTItem ltItem){
        if (Boolean.TRUE == ltItem instanceof Material) {
            materialMap.put(name, (Material) ltItem);
        } else if (Boolean.TRUE == ltItem instanceof Armor) {
            armorMap.put(name, (Armor) ltItem);
        } else if (Boolean.TRUE == ltItem instanceof MeleeWeapon) {
            meleeWeaponMap.put(name, (MeleeWeapon) ltItem);
        } else if (Boolean.TRUE == ltItem instanceof GemsStone) {
            gemstoneMap.put(name, (GemsStone) ltItem);
        }
    }
}
