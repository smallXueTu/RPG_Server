package cn.ltcraft.item.utils;

import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.other.UseItemEffect;
import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.utils.*;
import cn.ltcraft.item.base.*;
import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.item.objs.ItemObjs;
import com.google.gson.JsonSyntaxException;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.*;

public class Utils {
    private static List<String> list = Arrays.asList("近战", "远程", "通用");

    /**
     * 获取一个NBT内的json标签
     * @param nbt 此json可以用来存储武器属性
     * @return json
     */
    public static Map<String, Object> getStringMap(NBTTagCompound nbt){
        String json;
        if (nbt.hasKey("json")){
            json = nbt.getString("json");
        }else if(nbt.hasKey("ltAttribute")){
            NBTTagCompound nbtTagCompound = nbt.getCompound("ltAttribute");
            if (nbtTagCompound.hasKey("json"))
                json = nbtTagCompound.getString("json");
            else return new HashMap<>();
        }else return new HashMap<>();

        try {
            Map<String, Object> map = cn.LTCraft.core.utils.Utils.getGson().fromJson(json, Map.class);
            return map;
        }catch (JsonSyntaxException ignore){

        }
        return new HashMap<>();
    }

    /**
     * 设置物品的json属性
     * @param itemStack 物品
     * @param map json
     * @return 设置后的物品堆
     */
    public static ItemStack setStringMap(ItemStack itemStack, Map<String, Object> map){
        NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
        if (nbt!=null && nbt.hasKey("ltAttribute")){
            String json = cn.LTCraft.core.utils.Utils.getGson().toJson(map);
            NBTTagCompound ltAttribute = nbt.getCompound("ltAttribute");
            ltAttribute.setString("json", json);
            nbt.set("ltAttribute", ltAttribute);
            itemStack = ItemUtils.setNBT(itemStack, nbt);
        }
        return itemStack;
    }

    /**
     * 指定物品的nbt是否为武器
     * @param nbt nbt 也可以是物品堆
     * @see #isWeapon(ItemStack)
     * @return 如果是武器
     */
    public static boolean isWeapon(NBTTagCompound nbt){
        if (nbt.hasKey("ltAttribute")){
            NBTTagCompound nbtTagCompound = nbt.getCompound("ltAttribute");
            String type = nbtTagCompound.getString("type");
            return isWeapon(type);
        }
        return false;
    }
    public static boolean isWeapon(ItemStack itemStack){
        return isWeapon(ItemUtils.getNBT(itemStack));
    }

    /**
     * @see Utils#list
     */
    public static boolean isWeapon(String type){
        return list.contains(type);
    }

    /**
     * 获取一个堆栈对应的LTItem
     * @param itemStack 堆栈
     * @return 可能为null
     */
    public static LTItem getLTItems(ItemStack itemStack) {
        return getLTItems(ItemUtils.getNBT(itemStack));
    }

    /**
     * 如果已经取到NBT，更推荐使用该方法，以节省微小的性能...
     * @param nbtTagCompound NBT
     * @return 同上
     */
    public static LTItem getLTItems(NBTTagCompound nbtTagCompound){
        if (nbtTagCompound == null)return null;
        if (nbtTagCompound.hasKey("ltAttribute")){
            NBTTagCompound ltAttribute = nbtTagCompound.getCompound("ltAttribute");
            String type = ltAttribute.getString("type");
            String name = ltAttribute.getString("name");
            return ItemObjs.getLTItem(type, name);
        }
        return null;
    }

    /**
     * 通过一个可配置的LTItem构建它的物品堆
     * @param configurableLTItems 可配置LT物品
     * @return 物品堆
     */
    public static ItemStack getItem(ConfigurableLTItem configurableLTItems){
        String model = configurableLTItems.getConfig().getString("模型");
        CraftItemStack itemStack;
        if (model.matches("[0-9]+|[0-9]+:[0-9]+")) {
            String[] mI = model.split(":");
            itemStack = CraftItemStack.asCraftCopy(new ItemStack(Integer.parseInt(mI[0]), 1, mI.length > 1 ? Short.parseShort(mI[1]) : 0));
        } else {
            itemStack = CraftItemStack.asCraftCopy(new ItemStack(org.bukkit.Material.getMaterial(model)));
        }
        NBTTagCompound all = new NBTTagCompound();
        NBTTagCompound ltAttribute = new NBTTagCompound();
        ltAttribute.setString("type", configurableLTItems.getType().getName());
        ltAttribute.setString("name", configurableLTItems.getName());
        if (configurableLTItems.binding()){
            ltAttribute.setString("binding", "?");
        }else {
            ltAttribute.setString("binding", "*");
        }
        if (configurableLTItems.getConfig().getBoolean("不可叠加")){
            ltAttribute.setString("sign", String.valueOf(UUID.randomUUID()));
        }
        all.set("ltAttribute", ltAttribute);
        net.minecraft.server.v1_12_R1.ItemStack handle = new net.minecraft.server.v1_12_R1.ItemStack(CraftMagicNumbers.getItem(itemStack.getType()), 1, 0);
        handle.setTag(all);
        itemStack = (CraftItemStack) ItemUtils.setHandle(itemStack, handle);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(configurableLTItems instanceof AbstractAttribute)) {
            itemMeta.setLore(configurableLTItems.getConfig().getStringList("说明"));
        }
        itemMeta.setDisplayName(configurableLTItems.getConfig().getString("显示名字"));
        if (configurableLTItems.getConfig().getBoolean("无限耐久"))itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        if (configurableLTItems instanceof AbstractAttribute){
            return updateNameAndLore(itemStack, (AbstractAttribute) configurableLTItems, configurableLTItems.getConfig().getStringList("说明"));
        }else {
            return itemStack;
        }
    }

    /**
     * 获取已镶嵌的宝石
     * @param itemStack 喔唷地堆
     * @return 可能为null
     */
    public static List<String> getSet(ItemStack itemStack){
        return getSet(ItemUtils.getNBT(itemStack));
    }
    public static List<String> getSet(NBTTagCompound nbtTagCompound){
        Map<String, Object> stringMap = cn.ltcraft.item.utils.Utils.getStringMap(nbtTagCompound);
        return stringMap.get("gemstones") == null?new ArrayList<>():(List<String>)stringMap.get("gemstones");
    }

    /**
     * 更新一个 物品堆的lore
     * @param itemStack 物品堆
     * @param abstractAttribute 物品的属性
     * @param lore 原本的lore
     * @return 已更新的
     */
    public static ItemStack updateNameAndLore(ItemStack itemStack, AbstractAttribute abstractAttribute, List<String> lore){
        ItemMeta itemMeta = itemStack.getItemMeta();
        StringBuilder loreStrB = new StringBuilder();
        for (int i = 0; i < lore.size(); i++) {
            loreStrB.append(lore.get(i));
            if (i != lore.size() - 1)loreStrB.append("\n");
        }
        String loreStr = loreStrB.toString().replace("&", "§");
        Class<AbstractAttribute> abstractAttributeClass = AbstractAttribute.class;
        Field[] declaredFields = abstractAttributeClass.getDeclaredFields();
        if (abstractAttribute != null) {
            for (Field declaredField : declaredFields) {
                try {
                    declaredField.setAccessible(true);
                    Object value = declaredField.get(abstractAttribute);
                    loreStr = loreStr.replace("%" + declaredField.getName() + "%", value instanceof Double ? cn.LTCraft.core.utils.Utils.formatNumber((double) value) : value.toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (abstractAttribute instanceof AICLA && (loreStr.contains("%maxSet%") || loreStr.contains("%set%"))){
            AICLA aicla = (AICLA) abstractAttribute;
            loreStr = loreStr.replace("%maxSet%", aicla.getMaxSet() + "");
            loreStr = loreStr.replace("%set%", getSet(itemStack).size() + "");
        }
        if (abstractAttribute instanceof ConfigurableLTItem && loreStr.contains("%quality%")){
            ConfigurableLTItem configurableLTItems = (ConfigurableLTItem) abstractAttribute;
            String level = configurableLTItems.getConfig().getString("品质", "无品质");
            loreStr = loreStr.replace("%quality%", GameUtils.getLevelPrefix(level) + level + "");
        }
        if (abstractAttribute instanceof ConfigurableLTItem && loreStr.contains("%binding%")){
            ConfigurableLTItem configurableLTItems = (ConfigurableLTItem) abstractAttribute;
            if ( configurableLTItems.binding()) {
                loreStr = loreStr.replace("%binding%", ItemUtils.getBinding(itemStack));
            }
        }
        List<String> list = Arrays.asList(loreStr.split("\n"));
        itemMeta.setLore(list);
        itemStack.setItemMeta(itemMeta);
        return itemStack.clone();
    }

    /**
     * 计算LTItem的属性 注意 请传递可能已镶嵌宝石的物品堆
     * @param aicla 可配置可镶嵌的LTItem
     * @param itemStack 物品堆
     * @return LTItem
     */
    public static AICLA calculationAttr(AICLA aicla, ItemStack itemStack) {
        return calculationAttr(aicla, ItemUtils.getNBT(itemStack));
    }
    public static AICLA calculationAttr(AICLA aicla, NBTTagCompound nbt){
        Map<String, Object> json = Utils.getStringMap(nbt);
        if (json.containsKey("gemstones")){
            List<String> gemstones = (List<String>) json.get("gemstones");
            for (String gemstone : gemstones) {
                GemsStone gemsStone = ItemObjs.getGemstone(gemstone);
                aicla.addAttribute(gemsStone);
            }
        }
        aicla.addAttribute(new JsonAttribute(json));
        return aicla;
    }
    public static void action(Player player, ConfigurableLTItem ltItem, String key, Object ...objects){
        String action = ltItem.getConfig().getString(key, "无");
        for (String s : action.split("&")) {
            if (!s.equals("无")){
                int cooling = 0;
                String coolingGroup = ltItem.getType().getName() + ":" + ltItem.getName();
                String[] split = s.split(":");
                if (split.length >= 3){
                    if (split[2].contains("|")){
                        cooling = Integer.parseInt(split[2].split("\\|")[0]);
                        coolingGroup = split[2].split("\\|")[1];
                    }else {
                        cooling = Integer.parseInt(split[2]);
                    }
                }
                if (Cooling.isCooling(player, coolingGroup))return;
                String base = ltItem.getType().getName() + ":" + ltItem.getName() + ":base";
                if (Cooling.isCooling(player, base))return;
                Cooling.cooling(player, base, 1);
                String cmd;
                int count = 1;
                int number = 1;
                String[] split1;
                EntityDamageByEntityEvent event;
                switch (split[0]){
                    case "MMSkill":
                        PlayerUtils.castMMSkill(player, split[1]);
                        break;
                    case "ClassSkill":
                        PlayerUtils.castClassSkill(player, split[1], null);
                        break;
                    case "cmd":
                        cmd = split[1];
                        cmd = cmd.replace("%player%", player.getName());
                        Bukkit.getServer().dispatchCommand(player, cmd);
                        break;
                    case "use":
                        count = UseItemEffect.onUse(player, split[1]);
                        break;
                    case "增加金币":
                        number = Integer.parseInt(split[1]);
                        cn.LTCraft.core.dataBase.bean.PlayerInfo playerInfo = cn.LTCraft.core.entityClass.PlayerConfig.getPlayerConfig(player).getPlayerInfo();
                        playerInfo.addGold(number);
                        player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1, 1);
                    break;
                    case "破甲":
                        split1 = split[1].split("\\|");
                        double aDouble = Double.parseDouble(split1[0]);
                        int tick = Integer.parseInt(split1[1]);
                        event = (EntityDamageByEntityEvent) objects[0];
                        Temp.addArmorBreaking(event.getEntity(), new Temp.ArmorBreaking(aDouble, tick));
                        break;
                    case "增加橙币":
                        number = Integer.parseInt(split[1]);
                        cn.LTCraft.core.Main.getInstance().getEconomy().withdrawPlayer(player, number);
                        player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1, 1);
                    break;
                    case "刺杀":
                        cooling = 0;
                        if (!player.isSneaking()) {
                            return;
                        }
                        event = (EntityDamageByEntityEvent) objects[0];
                        LivingEntity entity = ((LivingEntity) event.getEntity());
                        Location location = entity.getLocation();
                        ActiveMob mythicMob = EntityUtils.getMythicMob(entity);
                        if (mythicMob != null && (mythicMob.getType().getInternalName().endsWith("护卫") || mythicMob.getType().getInternalName().endsWith("士兵")) && location.distance(player.getLocation()) < 1.5 && mythicMob.getEntity().getTarget() == null){
                            if (MathUtils.getMinAngle(location.getYaw(), player.getLocation().getYaw()) < 90) {
                                //满足再背后要求
                                int max = Integer.parseInt(split[1]);
                                if (entity.getHealth() <= max){
                                    event.setDamage(Integer.MAX_VALUE);
                                    EntityUtils.castMMSkill(entity, "刺杀特效");
                                    cooling = Integer.parseInt(split[2].split("\\|")[0]);
                                }
                            }
                        }
                        break;
                    case "sudo":
                        boolean isOp = player.isOp();
                        player.setOp(true);
                        cmd = split[1];
                        cmd = cmd.replace("%player%", player.getName());
                        Bukkit.getServer().dispatchCommand(player, cmd);
                        player.setOp(isOp);
                        break;
                }
                if (split.length >= 4){
                    switch (split[3]){
                        case "消耗":
                            ItemStack inHand = player.getItemInHand();
                            inHand.setAmount(inHand.getAmount() - count);
                            player.getInventory().setItemInMainHand(inHand);
                        break;
                    }
                }
                if (cooling > 0){
                    Cooling.cooling(player, coolingGroup, cooling, ltItem.getType().getName() + ":" + ltItem.getName() + "冷却：%s%S");
                }
            }
        }
    }
    public static int getMaxSet(String quality){
        switch (cn.LTCraft.core.utils.Utils.clearColor(quality)){
            case "中级":
                return 4;
            case "高级":
            case "稀有":
            case "传说":
                return 5;
            case "终极":
                return 6;
            case "史诗":
                return 8;
            case "神话":
            case "至臻":
                return 10;
            default:
                return 3;
        }
    }
}
