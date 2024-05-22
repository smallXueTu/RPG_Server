package cn.LTCraft.core.game;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.entityClass.Cooling;
import cn.LTCraft.core.entityClass.PlayerConfig;
import cn.LTCraft.core.entityClass.messy.ItemAction;
import cn.LTCraft.core.entityClass.spawns.ChestMobSpawn;
import cn.LTCraft.core.hook.MM.mechanics.singletonSkill.AirDoor;
import cn.LTCraft.core.other.Temp;
import cn.LTCraft.core.other.exceptions.BlockCeilingException;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.task.PlayerClass;
import cn.LTCraft.core.utils.*;
import cn.hutool.core.util.ObjectUtil;
import cn.ltcraft.item.base.AICLA;
import cn.ltcraft.item.base.AbstractAttribute;
import cn.ltcraft.item.base.interfaces.ConfigurableLTItem;
import cn.ltcraft.item.base.interfaces.LTItem;
import cn.ltcraft.item.base.interfaces.actions.TickItem;
import cn.ltcraft.item.items.Armor;
import cn.ltcraft.item.items.BaseWeapon;
import cn.ltcraft.item.items.GemsStone;
import cn.ltcraft.item.items.gemsstones.ConsumeGemstones;
import cn.ltcraft.item.objs.PlayerAttribute;
import cn.ltcraft.teleport.Teleport;
import cn.ltcraft.teleport.Warp;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.google.common.collect.ObjectArrays;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import io.lumine.utils.config.file.YamlConfiguration;
import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IItemDrop;
import io.lumine.xikage.mythicmobs.drops.LootBag;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
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
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

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
            this.add("t5");
            this.add("f1");
            this.add("f2");
            this.add("f3");
            this.add("rpg");
            this.add("RPG");
        }
    };
    public static final List<String> mainLineWorlds = new ArrayList<String>() {
        {
            this.add("t1");
            this.add("t2");
            this.add("t3");
            this.add("t4");
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
    public static final List<String> breakIng = new ArrayList<>();
    private static final Field playerInteractManager = ReflectionHelper.findField(EntityPlayer.class, "playerInteractManager");
    public static void breakMines(Player player, Block block, int count, List<Block> ignore, ItemStack itemStack, int depth) throws BlockCeilingException {
        if (count > 50){
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
                if (count<50){
                    breakMines(player, tBlock, ++count, ignore, itemStack, depth + 1);
                }
                PlayerInteractManager playerInteractManager = null;
                try {
                    playerInteractManager = (PlayerInteractManager) Game.playerInteractManager.get(((CraftPlayer) player).getHandle());
                } catch (Exception e) {
                    continue;
                }
                playerInteractManager.breakBlock(new BlockPosition(tBlock.getLocation().getBlockX(), tBlock.getLocation().getBlockY(), tBlock.getLocation().getBlockZ()));
//                tBlock.breakNaturally(itemStack);
            }
        }
    }
    public static void breakWoods(Player player, Block block, int count, List<Block> ignore, ItemStack itemStack, int depth) throws BlockCeilingException{
        if (count > 50) {
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
                if (count<50){
                    breakWoods(player, tBlock, ++count, ignore, itemStack, depth + 1);
                }
                PlayerInteractManager playerInteractManager = null;
                try {
                    playerInteractManager = (PlayerInteractManager) Game.playerInteractManager.get(((CraftPlayer) player).getHandle());
                } catch (Exception e) {
                    continue;
                }
                playerInteractManager.breakBlock(new BlockPosition(tBlock.getLocation().getBlockX(), tBlock.getLocation().getBlockY(), tBlock.getLocation().getBlockZ()));
//                tBlock.breakNaturally(itemStack);
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
        PlayerData playerData;
        World world = player.getWorld();
        boolean sign = true;
        switch (type){
            case "BQTag":
                playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                return playerData.hasTag(demand);
            case "NonBQTag":
                playerData = BetonQuest.getInstance().getPlayerData(PlayerConverter.getID(player));
                return !playerData.hasTag(demand);
            case "BQCondition":
                return PlayerUtils.satisfyBQCondition(player, demand);
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
                Cancellable event = (Cancellable)args[0];
                double x = ((Location) args[1]).getX() - ((Location) args[2]).getX();
                double z = ((Location) args[1]).getZ() - ((Location) args[2]).getZ();
                double f = Math.sqrt(x * x + z * z);
                if(f > 0) {
                    f = 1 / f;
                    CraftPlayer craftPlayer = (CraftPlayer)player;
                    craftPlayer.setMomentum(new Vector(x * f * 2, 0.1, z * f * 2));
                }
                player.sendMessage(((AirDoor)args[3]).getMessage());
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
                PlayerUtils.execBQEvent(player, info[1]);
            break;
            case "none"://无
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
            Location blockLocation = block.getLocation();
            String xyz = GameUtils.spawnLocationString(blockLocation);
            Player player = event.getPlayer();
            switch (xyz){
                case "990:65:-1189:f1"://精髓激活台
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
                            Item item = block.getWorld().dropItem(blockLocation.add(0.5, 1, 0.5), clutterItem.generate());
                            item.setGravity(false);
                            ((CraftEntity) item).setMomentum(new Vector(0, 0, 0));
                            Temp.playerDropItem.put(item, "Angel_XX");
                            Temp.discardOnly.add(item);
                            for (int i = 0; i < 10; i++) {
                                Bukkit.getScheduler().scheduleSyncDelayedTask(cn.LTCraft.core.Main.getInstance(), () -> {
                                    block.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, blockLocation.add(0.5, 2, 0.5), 300, 0, 0, 0, 5);
                                }, i * 10 + Utils.getRandom().nextInt(6) + 7);
                            }
                            player.getInventory().setItemInMainHand(inHand);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(cn.LTCraft.core.Main.getInstance(), () -> {
                                item.remove();
                                for (int i = 0; i < 2; i++) {
                                    String pClass = Utils.arrayRandom(Game.playerClasses);
                                    cn.LTCraft.core.entityClass.ClutterItem quintessence = cn.LTCraft.core.entityClass.ClutterItem.spawnClutterItem(pClass + "知识精髓");
                                    PlayerUtils.dropItemFloat(player, blockLocation.add(0.5, 1, 0.5), quintessence.generate());
                                }
                                cn.LTCraft.core.entityClass.ClutterItem bottle = ClutterItem.spawnClutterItem("未知知识之瓶");
                                bottle.setNumber(2);
                                PlayerUtils.dropItemFloat(player, blockLocation.add(0.5, 1, 0.5), bottle.generate());
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
            if (block.getType() == Material.SIGN_POST){
                BlockState state = block.getState();
                if (state instanceof Sign){
                    Sign sign = (Sign) state;
                    if(sign.getLine(1).equals("plcmd")){
                        if (Cooling.isCooling(player, "plcmd")) return;
                        Cooling.cooling(player, "plcmd", 1);
                        String command = sign.getLine(2) + sign.getLine(3);
                        if (Game.rpgWorlds.contains(block.getWorld().getName())){
                            PlayerUtils.sudoExec(player, command);
                        }else {
                            Bukkit.dispatchCommand(player, command);
                        }
                    }
                }
            }else if (block.getType() == Material.CHEST){

                String key = GameUtils.spawnLocationString(blockLocation);
                ChestMobSpawn chestMobSpawn = ChestSpawnManager.getInstance().getMobSpawn(key);
                if (chestMobSpawn == null){//如果这个坐标不存在箱子守卫者 则获取这个地图的
                    chestMobSpawn = ChestSpawnManager.getInstance().getMobSpawn(player.getWorld().getName());
                }
                if (chestMobSpawn == null){
                    return;//这个箱子不不存在守卫者
                }
                event.setCancelled(true);
                chestMobSpawn.attemptToOpen(player, block);
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

    /**
     * 获取锻造结果
     * @param equipment 装备
     * @param material 材料
     * @param player 玩家
     * @return 锻造结果
     */
    public static ItemStack getForgingResult(ItemStack equipment, ItemStack material, Player player){
        LTItem equipmentLTItem = cn.ltcraft.item.utils.Utils.getLTItems(equipment);
        LTItem materialLTItem = cn.ltcraft.item.utils.Utils.getLTItems(material);
        if (materialLTItem instanceof GemsStone){
            // 是否属于可镶嵌物品
            if (equipmentLTItem instanceof AICLA){
                AICLA aicla = (AICLA)equipmentLTItem;
                ItemStack result = equipment.clone();
                if (materialLTItem instanceof ConsumeGemstones){// 消耗物品 不占用宝石格子
                    ItemStack installResult = ((GemsStone) materialLTItem).installOn(result);
                    if (ObjectUtil.isNotEmpty(installResult)){
                        aicla = cn.ltcraft.item.utils.Utils.calculationAttr(aicla.clone(), installResult);
                        ItemStack updateItem = cn.ltcraft.item.utils.Utils.updateItem(installResult, aicla, aicla.getConfig().getStringList("说明"));
                        if (((ConsumeGemstones) materialLTItem).installSuccess()){
                            // 成功 返回安装结果
                            return updateItem;
                        }else {
                            // 失败，返回旧的物品 但lore是成功，防止玩家发现失败了
                            ItemMeta itemMeta = equipment.getItemMeta();
                            itemMeta.setLore(updateItem.getItemMeta().getLore());
                            equipment.setItemMeta(itemMeta);
                            return equipment;
                        }
                    }else {
                        return null;
                    }
                }else {
                    NBTTagCompound nbt = ItemUtils.getNBT(result);
                    if (nbt == null) return null;
                    Map<String, Object> stringMap = cn.ltcraft.item.utils.Utils.getStringMap(nbt);
                    String name = materialLTItem.getName();
                    List<String> list = stringMap.get("gemstones") == null ? new ArrayList<>() : (List<String>) stringMap.get("gemstones");
                    if (list.size() >= aicla.getMaxSet()) return null;
                    list.add(name);
                    stringMap.put("gemstones", list);
                    result = cn.ltcraft.item.utils.Utils.setStringMap(result, stringMap);
                    aicla = cn.ltcraft.item.utils.Utils.calculationAttr(aicla.clone(), result);
                    result = cn.ltcraft.item.utils.Utils.updateItem(result, aicla, aicla.getConfig().getStringList("说明"));
                    return result;
                }
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

    /**
     * 开始锻造
     * @param equipment  装备
     * @param material 材料
     * @param player 锻造玩家
     * @return 是否成功
     */
    public static boolean startForging(ItemStack equipment, ItemStack material, Player player){
        LTItem ltItem = cn.ltcraft.item.utils.Utils.getLTItems(equipment);
        LTItem LTm = cn.ltcraft.item.utils.Utils.getLTItems(material);
        if (LTm instanceof GemsStone){
            if (ltItem instanceof BaseWeapon || ltItem instanceof Armor){
                if (!PlayerUtils.hasBQTag(player, "default.宝石镶嵌")){
                    PlayerUtils.addBQTag(player, "default.宝石镶嵌");
                }
                player.playSound(player.getLocation(), "block.anvil.use", SoundCategory.BLOCKS, 2, 1);
            }

            if (LTm instanceof ConsumeGemstones){
                if (!((ConsumeGemstones) LTm).installSuccess()) {
                    return false;
                }
            }
            return true;
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

    /**
     * 尝试通过空气门
     * @param player 玩家
     * @param from 从哪里
     * @param to 到哪里
     * @param event 可取消的事件
     * @return 是否通过了
     */
    public static boolean tryAdoptAirDoor(Player player, Location from, Location to, Cancellable event){
        boolean sign = false;
        AirDoor airDoor = null;
        synchronized (AirDoor.getAirDoors()) {
            for (Iterator<AirDoor> iterator = AirDoor.getAirDoors().iterator(); iterator.hasNext(); ) {
                airDoor = iterator.next();
                if (airDoor.getBukkitEntity().isDead()) {
                    iterator.remove();
                    continue;
                }
                Location location = airDoor.getBukkitEntity().getLocation().clone();
                Location playerLocation = player.getLocation().clone();
                if (Math.abs(location.getY() - playerLocation.getY()) > airDoor.getYDistance())continue;
                location.setY(location.getY());
                if (airDoor.getBukkitEntity().getWorld() != player.getWorld() || location.distance(playerLocation) > airDoor
                        .getDistance())
                    continue;
                double fLocation = airDoor.getCheckDirection() == WorldUtils.COORDINATE.X ? from.getX() : airDoor.getCheckDirection() == WorldUtils.COORDINATE.Z ? from.getZ() : from.getY();
                double tLocation = airDoor.getCheckDirection() == WorldUtils.COORDINATE.X ? to.getX() : airDoor.getCheckDirection() == WorldUtils.COORDINATE.Z ? to.getZ() : to.getY();
                if (airDoor.isForward()) {
                    if (fLocation <= airDoor.getLocation() && tLocation > airDoor.getLocation()) {
                        sign = true;
                    }
                } else {
                    if (fLocation >= airDoor.getLocation() && tLocation < airDoor.getLocation()) {
                        sign = true;
                    }
                }
                if (sign && !Cooling.isCooling(player, airDoor.getConfigLine())) {
                    Cooling.cooling(player, airDoor.getConfigLine(), 1);
                    if (Game.demand(player, airDoor.getDemand())) {
                        Game.execute(player, airDoor.getSuccess());
                    } else {
                        Game.execute(player, airDoor.getFail(), event, from, to, airDoor);
                        break;
                    }
                }
            }
        }
        return !sign;
    }
    private static final List<ItemAction> ItemActions = new ArrayList<>();
    /**
     * tick装备
     */
    public static void tickEquipment(long tick){
        synchronized (ItemActions) {
            List<Player> onlinePlayers;
            do {
                try {
                    onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    break;
                }catch (ConcurrentModificationException ignored){//乐观锁
                }
            }while (true);
            for (Player player : onlinePlayers) {
                final PlayerInventory inventory = player.getInventory();
                ItemStack[] itemStacks;
                do {
                    try {
                        itemStacks = inventory.getContents();
                        break;
                    } catch (ConcurrentModificationException ignored) {
                    }
                }while (true);
                itemStacks = ObjectArrays.concat(itemStacks, PlayerAttribute.getPlayerAttribute(player).getOrnaments(), ItemStack.class);
                for (int i = 0; i < itemStacks.length; i++) {
                    ItemStack itemStack = itemStacks[i];
                    if (itemStack == null)continue;
                    NBTTagCompound nbt = ItemUtils.getNBT(itemStack);
                    LTItem ltItem = cn.ltcraft.item.utils.Utils.getLTItems(nbt);
                    if (ltItem != null && ltItem.binding()) {
                        String binding = ItemUtils.getBinding(nbt);
                        if (binding.equals("?")) {
                            binding = player.getName().toLowerCase();
                            nbt = ItemUtils.setBinding(nbt, binding);
                            inventory.setItem(i, ItemUtils.setNBT(itemStack, nbt));
                        }
                        if (!binding.equals("*") && !binding.equalsIgnoreCase(player.getName())) {
                            continue;
                        }
                    }
                    if (tick % 20 == 0 && ltItem != null){
                        if (ltItem instanceof AICLA){
                            AICLA aicla = cn.ltcraft.item.utils.Utils.calculationAttr(((AICLA) ltItem).clone(), nbt);
                            itemStack = cn.ltcraft.item.utils.Utils.updateItem(itemStack.clone(), aicla, aicla.getLore());
                        }else if (ltItem instanceof AbstractAttribute){
                            AbstractAttribute abstractAttribute = (AbstractAttribute) ltItem;
                            ConfigurableLTItem configurableLTItem = (ConfigurableLTItem) ltItem;
                            itemStack = cn.ltcraft.item.utils.Utils.updateItem(itemStack.clone(), abstractAttribute, configurableLTItem.getLore());
                        }else {
                            itemStack = ltItem.generate(itemStack.getAmount());
                        }
                        if (i < 41 && inventory.getItem(i) != null && !inventory.getItem(i).isSimilar(itemStack)) {
                            int finalI1 = i;
                            ItemStack finalItemStack = itemStack;
                            ItemActions.add(() -> {
                                if (inventory.getItem(finalI1) != null) {
                                    LTItem current = cn.ltcraft.item.utils.Utils.getLTItems(inventory.getItem(finalI1));
                                    if (current.getType().equals(ltItem.getType()) && current.getName().equals(ltItem.getName())) {
                                        inventory.setItem(finalI1, finalItemStack);
                                    }
                                }
                            });
                        }else if (i >= 41){
                            ItemStack ornament = PlayerAttribute.getPlayerAttribute(player).getOrnaments()[i - 41];
                            if (!ornament.isSimilar(itemStack)) {
                                int finalI1 = i;
                                ItemStack finalItemStack = itemStack;
                                ItemActions.add(() -> {
                                    ItemStack or = PlayerAttribute.getPlayerAttribute(player).getOrnaments()[finalI1 - 41];
                                    if (or != null && !or.isSimilar(finalItemStack)) {
                                        DragonCoreUtil.setItemStack(player, "饰品槽位" + (finalI1 - 40), finalItemStack, (is) -> {
                                            PlayerAttribute.getPlayerAttribute(player).onChangeOrnament(finalI1 - 40);
                                        }, true);
                                    }
                                });
                            }
                        }
                    }
                    if (ltItem instanceof TickItem) {
                        int finalI = i;
                        ItemActions.add(() -> {
                            TickItem tickItem = (TickItem) ltItem;
                            tickItem.doTick(tick, player, finalI);
                        });
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
