package cn.LTCraft.core.game;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.entityClass.messy.ItemAction;
import cn.LTCraft.core.other.exceptions.BlockCeilingException;
import cn.LTCraft.core.hook.MM.mechanics.singletonSkill.AirDoor;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.task.PlayerClass;
import cn.LTCraft.core.utils.ItemUtils;
import cn.LTCraft.core.utils.PlayerUtils;
import cn.LTCraft.core.utils.Utils;
import cn.ltcraft.item.base.AICLA;
import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.items.Armor;
import cn.ltcraft.item.items.BaseWeapon;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.teleport.Teleport;
import cn.ltcraft.teleport.Warp;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import io.lumine.utils.tasks.Scheduler;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.EventID;
import pl.betoncraft.betonquest.ObjectNotFoundException;
import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.*;

public class Game {
    public static final List<String> resourcesWorlds = new ArrayList<String>() {
        {
            this.add("zy");
            this.add("world_nether");
            this.add("world_the_end");
        }
    };
    public static final List<Integer> burningBlocks = new ArrayList<Integer>() {
        {
            this.add(10);
            this.add(11);
            this.add(51);
        }
    };
    public static final List<String> rpgWorlds = new ArrayList<String>() {
        {
            this.add("t1");
            this.add("t2");
            this.add("t3");
            this.add("t4");
            this.add("f1");
            this.add("f2");
            this.add("rpg");
            this.add("RPG");
        }
    };
    public static final Map<String, String> worldNames = new HashMap<String, String>(){
        {
            put("zy", "zy");
            put("world_nether", "nether");
            put("world_the_end", "ender");
            put("world", "zc");
        }
    };
    public static final Map<String, String> remarks = new HashMap<String, String>(){
        {
            worldNames.forEach((s, s2) -> {
                put(s2, s);
            });
        }
    };
    public static final List<Integer> mines = new ArrayList<Integer>(){{
        add(14);//金矿
        add(15);//铁矿
        add(16);//煤矿
        add(56);//钻石
        add(21);//青金石
        add(73);//红石
        add(74);//发光红石
        add(129);//绿宝石
        add(153);//石英矿石
    }};
    public static final List<Integer> woods = new ArrayList<Integer>(){{
        add(17);//木头
        add(162);//木头
        add(18);//树叶
        add(161);//树叶
    }};
    public static final String[] playerClasses = new String[]{"刺客", "战士", "法师", "牧师"};
    /**
     * 判断是否为矿物
     * @param block 方块
     * @return boolean
     */
    public static boolean isMine(Block block){
        return mines.contains(block.getTypeId());
    }

    /**
     * 判断是否为木头
     * @param block 方块
     * @return boolean
     */
    public static boolean isWood(Block block){
        return woods.contains(block.getTypeId());
    }

    public static void breakMines(Block block, int count, List<Block> ignore, ItemStack itemStack, int depth) throws BlockCeilingException {
        if (count > 150){
            throw new BlockCeilingException();
        }
        World world = block.getWorld();
        List<Block> blocks = new ArrayList<>();
        blocks.add(world.getBlockAt(block.getLocation().add(0, 1, 0)));
        blocks.add(world.getBlockAt(block.getLocation().add(0, -1, 0)));
        blocks.add(world.getBlockAt(block.getLocation().add(0, 0, 1)));
        blocks.add(world.getBlockAt(block.getLocation().add(0, 0, -1)));
        blocks.add(world.getBlockAt(block.getLocation().add(1, 0, 0)));
        blocks.add(world.getBlockAt(block.getLocation().add(-1, 0, 0)));
        for (Block tBlock : blocks){
            if (ignore.contains(tBlock)){
                continue;
            }
            if (Game.isMine(tBlock)){
                count++;
                ignore.add(tBlock);
                if (count<150){
                    breakMines(tBlock, ++count, ignore, itemStack, depth + 1);
                }
                tBlock.breakNaturally(itemStack);
            }
        }
    }
    public static void breakWoods(Block block, int count, List<Block> ignore, ItemStack itemStack, int depth) throws BlockCeilingException{
        if (count > 150) {
            throw new BlockCeilingException();
        }
        World world = block.getWorld();
        List<Block> blocks = new ArrayList<>();
        blocks.add(world.getBlockAt(block.getLocation().add(0, 1, 0)));
        blocks.add(world.getBlockAt(block.getLocation().add(0, -1, 0)));
        blocks.add(world.getBlockAt(block.getLocation().add(0, 0, 1)));
        blocks.add(world.getBlockAt(block.getLocation().add(0, 0, -1)));
        blocks.add(world.getBlockAt(block.getLocation().add(1, 0, 0)));
        blocks.add(world.getBlockAt(block.getLocation().add(-1, 0, 0)));
        for (Block tBlock : blocks){
            if (ignore.contains(tBlock)){
                continue;
            }
            if (Game.isWood(tBlock)){
                count++;
                ignore.add(tBlock);
                if (count<150){
                    breakWoods(tBlock, ++count, ignore, itemStack, depth + 1);
                }
                tBlock.breakNaturally(itemStack);
            }
        }
    }
    /**
     * 重置世界
     * @param worldName 世界名字
     */
    public static void resetWorld(String worldName){
        MultiverseCore multiverseCore = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        World world = Bukkit.getWorld(worldName);
        if (world != null){
            multiverseCore.getMVWorldManager().deleteWorld(worldName, true, true);
        }
        World.Environment environment = null;
        WorldType worldType = null;
        if (worldName.equals("world_nether")){
            environment = World.Environment.NETHER;
            worldType = WorldType.NORMAL;
        }else if(worldName.equals("world_the_end")){
            environment = World.Environment.THE_END;
            worldType = WorldType.NORMAL;
        }else {
            environment = World.Environment.NORMAL;
            worldType = WorldType.NORMAL;
        }
        boolean success = multiverseCore.getMVWorldManager().addWorld(worldName, environment, null, worldType, true, null, true);
        if (!success)return;
        world = Bukkit.getWorld(worldName);
        multiverseCore.getMVWorldManager().getMVWorld(worldName).setKeepSpawnInMemory(true);
        multiverseCore.getMVWorldManager().getMVWorld(worldName).setDifficulty(Difficulty.HARD);
        world.setGameRuleValue("keepInventory", "true");
        world.setDifficulty(Difficulty.HARD);
        Location location = world.getSpawnLocation();
        if (location.getY() <= 1) {
            DestinationFactory df = multiverseCore.getDestFactory();
            MVDestination d = df.getDestination(worldName);
            location = d.getLocation(null);
        }
        if (location.getY() > 120 && worldName.equals("world_nether")) {
            location.add(0.0D, -20, 0.0D);
            world.setSpawnLocation(location);
        } else if (worldName.equals("world_the_end")) {
            location.add(5.0D, 0.0D, 5.0D);
            location = world.getHighestBlockAt(location).getLocation();
            world.setSpawnLocation(location);
        }
        Teleport.getInstance().getTmpWarps().put(worldNames.get(worldName), new Warp(world.getSpawnLocation()));
        if (!worldName.equals("world_the_end")) {
            Location startLocation = location.clone().add(16.0D, 0, 16);
            Location endLocation = location.clone().add(-16.0D, 0, -16);
            for (int x = (int) Math.floor(Math.min(startLocation.getX(), endLocation.getX())); (double) x <= Math.floor(Math.max(startLocation.getX(), endLocation.getX())); ++x) {
                for (int z = (int) Math.floor(Math.min(startLocation.getZ(), endLocation.getZ())); (double) z <= Math.floor(Math.max(startLocation.getZ(), endLocation.getZ())); ++z) {
                    world.getBlockAt(x, location.getBlockY(), z).setType(Material.AIR);
                    world.getBlockAt(x, location.getBlockY() + 1, z).setType(Material.AIR);
                    world.getBlockAt(x, location.getBlockY() + 2, z).setType(Material.AIR);
                    world.getBlockAt(x, location.getBlockY() + 3, z).setType(Material.AIR);
                    if (!world.getBlockAt(x, location.getBlockY() - 1, z).getType().isSolid()) {
                        world.getBlockAt(x, location.getBlockY() - 1, z).setType(Material.BEDROCK);
                    }
                }
            }
        }
    }

    /**
     * 判断条件
     * @param player .
     * @param demand .
     * @return 如果通过 true
     */
    public static boolean demand(Player player, String demand){
        String type = demand.split(":")[0];
        demand = demand.length() == type.length()?demand:demand.substring(type.length() + 1);
        double[][] poses;
        World world = player.getWorld();
        boolean sign = true;
        switch (type){
            case "BQTag":
            PlayerData playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                return playerData.hasTag(demand);
            case "custom1":
                poses = new double[][]{{262.5, 9.5, 159.5}, {258.5, 9.5, 159.5}, {258.5, 9.5, 137.5}, {260.5, 9.5, 137.5}};
                for (double[] pos : poses) {
                    if (world.getNearbyEntities(new Location(world, pos[0], pos[1], pos[2]), 0.5, 1, 0.5).stream().noneMatch(entity -> Temp.playerDropItem.getOrDefault(entity, "").equals(player.getName()))){
                        sign = false;
                        break;
                    }
                }
                return sign;
            case "custom2":
                poses =  new double[][]{
                        {137.5, 5.5, 480.5}, {135.5, 5.5, 480.5}, {132.5, 5.5, 477.5},
                        {132.5, 5.5, 473.5}, {132.5, 5.5, 469.5}, {135.5, 5.5, 466.5}, {137, 5.5, 466.5},
                };
                for (double[] pos : poses) {
                    if (world.getNearbyEntities(new Location(world, pos[0], pos[1], pos[2]), 0.5, 1, 0.5).stream().noneMatch(entity -> Temp.playerDropItem.getOrDefault(entity, "").equals(player.getName()))){
                        sign = false;
                        break;
                    }
                }
                return sign;
        }
        return false;
    }

    /**
     * 处决
     * @param player 玩家
     * @param type 类型
     * @param args 参数
     */
    public static void execute(Player player, String type, Object ...args){
        if (type == null || type.equals(""))return;
        String[] info = type.split(":");
        switch (info[0]){
            case "弹开":
                PlayerMoveEvent event = (PlayerMoveEvent)args[0];
                double x = event.getFrom().getX() - event.getTo().getX();
                double z = event.getFrom().getZ() - event.getTo().getZ();
                double f = Math.sqrt(x * x + z * z);
                if(f > 0) {
                    f = 1 / f;
                    CraftPlayer craftPlayer = (CraftPlayer)player;
                    craftPlayer.setMomentum(new Vector(x * f * 2, 0.1, z * f * 2));
                }
                player.sendMessage(((AirDoor)args[1]).getMessage());
                event.setCancelled(true);
            break;
            case "烧死":
                ((CraftPlayer)player).getHandle().setHealth(0);
                ((CraftPlayer)player).getHandle().getCombatTracker().trackDamage(DamageSource.BURN, 0f, 0f);
                ((CraftPlayer)player).getHandle().die(DamageSource.BURN);
                player.sendMessage(((AirDoor)args[0]).getMessage());
            break;
            case "skill":
                PlayerUtils.castMMSkill(player, info[1]);
            break;
            case "BQEvent":
                try {
                    EventID eventID = new EventID(null, info[1]);
                    BetonQuest.event(PlayerConverter.getID(player), eventID);
                } catch (ObjectNotFoundException e) {
                    Main.getInstance().getLogger().warning("找不到BQEvent：" + info[1]);
                }
            break;
        }
    }

    /**
     * 玩家右键点击方块
     * @param event .
     */
    public static void onRightClickBlock(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            String xyz = block.getWorld().getName() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
            Player player = event.getPlayer();
            switch (xyz){
                case "f1:990:65:-1189"://精髓激活台
                    ItemStack inHand = player.getItemInHand();
                    if (inHand.getTypeId() == 0)return;
                    PlayerClass playerClass = PlayerUtils.getClass(player);
                    if (playerClass == PlayerClass.NONE){
                        player.sendMessage("§c你还没有职业！");
                        return;
                    }
                    cn.LTCraft.core.entityClass.ClutterItem clutterItem = cn.LTCraft.core.entityClass.ClutterItem.spawnClutterItem("知识之瓶");
                    if (clutterItem.isSimilar(inHand)) {
                        int count = 32;
                        if (inHand.getAmount() >= count){
                            inHand.setAmount(inHand.getAmount() - count);
                            Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 1, 0.5), clutterItem.generate());
                            item.setGravity(false);
                            ((CraftEntity) item).setMomentum(new Vector(0, 0, 0));
                            Temp.playerDropItem.put(item, "Angel_XX");
                            Temp.discardOnly.add(item);
                            for (int i = 0; i < 10; i++) {
                                Bukkit.getScheduler().scheduleSyncDelayedTask(cn.LTCraft.core.Main.getInstance(), () -> {
                                    block.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, block.getLocation().add(0.5, 2, 0.5), 300, 0, 0, 0, 5);
                                }, i * 10 + Utils.getRandom().nextInt(6) + 7);
                            }
                            player.getInventory().setItemInMainHand(inHand);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(cn.LTCraft.core.Main.getInstance(), () -> {
                                item.remove();
                                for (int i = 0; i < 2; i++) {
                                    String pClass = Utils.arrayRandom(Game.playerClasses);
                                    cn.LTCraft.core.entityClass.ClutterItem quintessence = cn.LTCraft.core.entityClass.ClutterItem.spawnClutterItem(pClass + "知识精髓");
                                    PlayerUtils.dropItemFloat(player, block.getLocation().add(0.5, 1, 0.5), quintessence.generate());
                                }
                                cn.LTCraft.core.entityClass.ClutterItem bottle = ClutterItem.spawnClutterItem("未知知识之瓶");
                                bottle.setNumber(2);
                                PlayerUtils.dropItemFloat(player, block.getLocation().add(0.5, 1, 0.5), bottle.generate());
                            }, 13 * 10);
                        }else {
                            player.sendMessage("§c你需要至少32个才能激活！");
                        }
                    }else  {
                        player.sendMessage("§c请手持知识之瓶！");
                    }
                    event.setCancelled(true);
                break;
            }
        }
    }

    /**
     * 玩家点击右键
     * @param player 玩家
     */
    public static void onRightClick(Player player) {
        ItemStack itemStack = player.getItemInHand();
        if (canUse(player, itemStack)) {
            List<String> lore = itemStack.getItemMeta().getLore();
            for (String s : lore) {
                s = Utils.clearColor(s);
                if (s.contains("右键触发技能")){
                    String value = s.substring("右键触发技能:".length()).trim();
                    PlayerUtils.castClassSkill(player, value, null);
                }
            }
        }
    }

    /**
     * 物品是否可以使用
     * @param player 玩家
     * @param itemStack 物品
     * @return 如果可以
     */
    public static boolean canUse(Player player, ItemStack itemStack){
        boolean sign = true;
        if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null)return false;
        List<String> lore = itemStack.getItemMeta().getLore();
        for (String s : lore) {
            s = Utils.clearColor(s);
            if (s.contains("灵魂绑定")){
                String value = s.substring("灵魂绑定:".length()).trim();
                if (!value.equals(player.getName()))sign = false;
            }
            if (sign && s.contains("职业要求")){
                String value = s.substring("职业要求:".length()).trim();
                if (!PlayerClass.byName(value).equals(PlayerUtils.getClass(player)))sign = false;
            }
            if (sign && s.contains("职业等级要求")){
                int value = Integer.parseInt(s.substring("职业等级要求:".length()).trim());
                if (PlayerUtils.getClassLevel(player) < value)sign = false;
            }
        }
        return sign;
    }

    /**
     * 获取对应职业的法杖名字
     * @param playerClass 职业
     * @return 法杖名字
     */
    public static String getStaffName(PlayerClass playerClass){
        switch (playerClass){
            case MASTER:
            return "魔法法杖";
            case ASSASSIN:
            return "刺客法杖";
            case WARRIOR:
            return "护盾法杖";
            case MINISTER:
            return "生命法杖";
            default:
                return "";
        }
    }

    /**
     * 获取对应职业的基础技能名字
     * @param playerClass 职业
     * @return 基础技能名字
     */
    public static String getClassSkill(PlayerClass playerClass){
        switch (playerClass){
            case MASTER:
                return "缴械";
            case ASSASSIN:
                return "行刺之道";
            case WARRIOR:
                return "能量护盾";
            case MINISTER:
                return "救死扶伤";
            default:
                return "无";
        }
    }
    public static ItemStack getForgingResult(ItemStack equipment, ItemStack material, Player player){
        LTItem ltItem = cn.ltcraft.item.utils.Utils.getLTItems(equipment);
        LTItem LTm = cn.ltcraft.item.utils.Utils.getLTItems(material);
        if (LTm instanceof GemsStone){
            if (ltItem instanceof AICLA){
                AICLA aicla = (AICLA)ltItem;
                ItemStack result = equipment.clone();
                NBTTagCompound nbt = ItemUtils.getNBT(result);
                if (nbt == null)return null;
                Map<String, Object> stringMap = cn.ltcraft.item.utils.Utils.getStringMap(nbt);
                String name = LTm.getName();
                List<String> list = stringMap.get("gemstones") == null?new ArrayList<>():(List<String>)stringMap.get("gemstones");
                if (list.size() >= aicla.getMaxSet())return null;
                list.add(name);
                stringMap.put("gemstones", list);
                result = cn.ltcraft.item.utils.Utils.setStringMap(result, stringMap);
                aicla = cn.ltcraft.item.utils.Utils.calculationAttr(aicla.clone(), result);
                result = cn.ltcraft.item.utils.Utils.updateNameAndLore(result, aicla, aicla.getConfig().getStringList("说明"));
                return result;
            }
        }else if (material != null && material.getType()  == Material.ENCHANTED_BOOK){
            ItemStack result = equipment.clone();
            ItemMeta meta = material.getItemMeta();
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
            result.addUnsafeEnchantments(storageMeta.getStoredEnchants());
            return result;
        }
        return null;
    }
    public static boolean startForging(ItemStack equipment, ItemStack material, Player player){
        LTItem ltItem = cn.ltcraft.item.utils.Utils.getLTItems(equipment);
        LTItem LTm = cn.ltcraft.item.utils.Utils.getLTItems(material);
        if (LTm instanceof GemsStone){
            if (ltItem instanceof BaseWeapon || ltItem instanceof Armor){
                if (!PlayerUtils.hasBQTag(player, "default.宝石镶嵌")){
                    PlayerUtils.addBQTag(player, "default.宝石镶嵌");
                }
                player.playSound(player.getLocation(), "block.anvil.use", SoundCategory.BLOCKS, 2, 1);
                return true;
            }
        }else if (material != null && material.getType()  == Material.ENCHANTED_BOOK){
            ItemMeta meta = material.getItemMeta();
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
            EnchantItemEvent enchantItemEvent = new EnchantItemEvent(player, player.getOpenInventory(), null, equipment, 1, storageMeta.getStoredEnchants(), 0);
            Bukkit.getPluginManager().callEvent(enchantItemEvent);
            if (enchantItemEvent.isCancelled())return false;
            player.playSound(player.getLocation(), "block.anvil.use", SoundCategory.BLOCKS, 2, 1);
            return true;
        }
        return false;
    }

    private static final List<ItemAction> ItemActions = new ArrayList<>();
    /**
     * tick装备
     */
    public static void tickEquipment(long tick){
        synchronized (ItemActions) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerInventory inventory = player.getInventory();
                ItemStack[] itemStacks = inventory.getContents();
                for (int i = 0; i < itemStacks.length; i++) {
                    ItemStack itemStack = itemStacks[i];
                    NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
                    LTItem ltItem = cn.ltcraft.item.utils.Utils.getLTItems(nbt);
                    if (ltItem != null && ltItem.binding()) {
                        String binding = ItemUtils.getBinding(nbt);
                        if (binding.equals("?")){
                            binding = player.getName().toLowerCase();
                            nbt = ItemUtils.setBinding(nbt, binding);
                            inventory.setItem(i, ItemUtils.setNBT(itemStack, nbt));
                        }
                        if (!binding.equals("*") && !binding.equalsIgnoreCase(player.getName())) {
                            continue;
                        }
                    }
                    if (ltItem instanceof ConfigurableLTItem) {
                        ConfigurableLTItem configurable = (ConfigurableLTItem) ltItem;
                        int tickInterval = configurable.getConfig().getInt("TICK间隔", 10);
                        String tickAction = configurable.getConfig().getString("TICK动作", "无");
                        if (!tickAction.equals("无") && tick % tickInterval == 0) {
                            ItemActions.add(() -> cn.ltcraft.item.utils.Utils.action(player, configurable, "TICK动作"));
                        }
                    }
                }
            }
            if (ItemActions.size() > 0) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    synchronized (ItemActions) {
                        ItemActions.forEach(ItemAction::run);
                        ItemActions.clear();
                    }
                }, 0);
            }
        }
    }
}
