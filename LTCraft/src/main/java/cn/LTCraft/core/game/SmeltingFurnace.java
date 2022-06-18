package cn.LTCraft.core.game;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.game.more.FakeBlock;
import cn.LTCraft.core.game.more.SmeltingFurnaceDrawing;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.other.exceptions.SmeltingFurnaceErrorException;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.*;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import com.gmail.filoghost.holographicdisplays.object.line.CraftHologramLine;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import net.minecraft.server.v1_12_R1.DamageSource;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 熔炼炉
 * Created by Angel、 on 2022/4/25 20:39
 */
public class SmeltingFurnace implements TickEntity {
    //------------------------------------static------------------------------------
    private static int FID = 0;
    private static final Map<Integer, SmeltingFurnace> smeltingFurnaceMap = new HashMap<>();
    //------------------------------------static------------------------------------
    private Player player;
    /**
     * 玩家名字
     */
    private String playerName;
    /**
     * 中心 玻璃的坐标
     */
    private final Location location;
    /**
     * 箱子的坐标
     */
    private Block chest;
    /**
     * 物品展示框
     */
    private final Location itemFrame;
    /**
     * 展示框的实体
     */
    private final Entity itemFrameEntity;
    /**
     * 图纸
     */
    private final SmeltingFurnaceDrawing drawing;
    /**
     * 是否已关闭
     */
    private boolean closed = false;
    /**
     * 悬浮字
     */
    private Hologram hologram;
    private final int id;//唯一id
    private int process = 0;//阶段
    private int temperature;//温度
    private Block[] furnaces;//三个熔炉
    private Block[] anvils;//三个铁砧
    /**
     * 悬浮转动实体
     */
    private List<Entity> floatItemEntity = new ArrayList<>();
    /**
     * 可变行
     */
    private List<TextLine> lines = new ArrayList<>();
    /**
     * 错误行
     */
    private List<TextLine> errorLines = new ArrayList<>();
    /**
     * 内部物品
     */
    private List<ItemStack> inventory = new ArrayList<>();
    /**
     * 错误时间
     */
    private int errorTick = 0;
    /**
     * 致命错误
     */
    private boolean fatalError = false;
    /**
     * 旋转速度
     */
    private double speed = 1;
    /**
     * 旋转进度
     */
    private double rotationProgress = 0;
    /**
     * 旋转角度
     */
    private double angle = 0;
    /**
     * 存活时间
     */
    private int age = 0;
    /**
     * 锻造时间
     */
    private int meltingTick = 0;
    /**
     * 冷却
     */
    private boolean cooling = false;
    /**
     * 等待时间
     */
    private int waitingTime = 0;
    /**
     * 快速冷却
     */
    private boolean fastCooling = false;
    /**
     * 完成
     */
    private boolean done = false;
    private ItemStack[] furnacesItemStack = new ItemStack[2];
    /**
     * 最近的异常
     */
    private SmeltingFurnaceErrorException lastException = null;

    public SmeltingFurnace(Player player, Location location, Entity itemFrame, SmeltingFurnaceDrawing drawing){
        this.location = location;
        WorldUtils.SIDE entrance = WorldUtils.getForDirection(location, itemFrame.getLocation());
        this.itemFrame = WorldUtils.getSide(location, entrance).add(0.5, 0.5, 0.5);
        this.itemFrameEntity = itemFrame;
        this.player = player;
        if (player != null) {
            this.playerName = player.getName();
        }
        this.drawing = drawing;
        id = FID++;
        smeltingFurnaceMap.put(id, this);
        init();
        GlobalRefresh.addTickEntity(this);
    }

    /**
     * 初始化
     */
    public void init(){
        hologram = HologramsAPI.createHologram(Main.getInstance(), itemFrame.clone().add(0, 2, 0));
        HologramsAPI.registerPlaceholder(Main.getInstance(), "LTSF:" + id + ":process", 1, () -> Utils.getNumberCapitalize(process));
        HologramsAPI.registerPlaceholder(Main.getInstance(), "LTSF:" + id + ":temperature", 1, this::getTemperatureString);
        HologramsAPI.registerPlaceholder(Main.getInstance(), "LTSF:" + id + ":stable", 1, this::getStable);
        HologramsAPI.registerPlaceholder(Main.getInstance(), "LTSF:" + id + ":errorTick", 1, () -> String.valueOf((60 * 20 - errorTick) / 20));
        hologram.appendTextLine("§e§l熔炼祭坛");
        hologram.appendTextLine("§d当前第：LTSF:" + id + ":process阶段。");
        hologram.appendTextLine("§d当前温度：LTSF:" + id + ":temperature°§d。");
        hologram.appendTextLine("§d当前状态：LTSF:" + id + ":stable§d。");
        hologram.setAllowPlaceholders(true);
        furnaces = getFurnaces(location, itemFrame);
        anvils = getAnvils(location, itemFrame);
        chest = WorldUtils.getSideBlock(location, WorldUtils.SIDE.UP);
    }
    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * tick
     * @param tick 从服务器开启到现在经过的游戏时刻
     * @return 返回false代表熔炼已结束
     */
    public boolean doTick(long tick){
        age++;
        collectAround();
        int add = 1;
        try {
            if (fatalError)throw new SmeltingFurnaceErrorException();
            updateProcess();
            if (age % 20 == 0){
                add = 20;//每20s检查一次错误 所以要等于20
                FakeBlock[] check = check(location, itemFrame);
                if (check.length > 0){
                    throw new SmeltingFurnaceErrorException("熔炼坛结构被破坏！", SmeltingFurnaceErrorException.Type.STRUCTURE);
                }
                checkDrawing();
                checkHologram();
                //玩家修复了错误 还原状态
                if (errorTick > 0){
                    ArrayList<TextLine> textLines = new ArrayList<>(lines);
                    lines.clear();
                    for (TextLine textLine : textLines) {
                        lines.add(hologram.appendTextLine(textLine.getText()));
                    }
                    for (TextLine errorLine : errorLines) {
                        errorLine.removeLine();
                    }
                    errorLines.clear();
                    errorTick = 0;
                }
                if (lastException != null)lastException = null;
            }
        } catch (SmeltingFurnaceErrorException e) {//发生了错误 留给玩家60s的时间用于玩家修复错误！
            if (lastException == null) {
                lastException = e;
                if (errorTick == 0) {
                    errorTick = 60 * 20 - e.getTime() * 20;
                    for (TextLine line : lines) {
                        line.removeLine();
                    }
                    if (fatalError) {
                        errorLines.add(hologram.appendTextLine("§c致命错误：" + e.getMessage()));
                        errorLines.add(hologram.appendTextLine("§c熔炼坛将在LTSF:" + id + ":errorTick秒后坠毁！"));
                    } else {
                        errorLines.add(hologram.appendTextLine("§c错误：" + e.getMessage()));
                        errorLines.add(hologram.appendTextLine("§c请及时修正，否者将在LTSF:" + id + ":errorTick秒后坠毁！"));
                    }
                }
            }
            errorTick += add;
            if (errorTick > 60 * 20){
                close();
                if (e.isBlast()){
                    explosive();
                }
            }
        }
        ((CraftHologram) hologram).refreshSingleLines();
        return !closed;
    }

    /**
     * 更新进度
     * @throws SmeltingFurnaceErrorException 出错
     */
    public void updateProcess() throws SmeltingFurnaceErrorException {
        List<String> needMaterial;
        switch (process){
            case 0:
                checkPlayer();
                lines.add(hologram.appendTextLine("§e注意事项，熔炼时间大概需要" + DateUtils.getDateFormat(getLevel().getTime()) + "(不算强制冷却时间)。"));
                lines.add(hologram.appendTextLine("§e请不要在箱子内放其他无用物品。"));
                lines.add(hologram.appendTextLine("§e请尽量保持在线。熔炼完成请玩家在6小时内查收！"));
                lines.add(hologram.appendTextLine("§e请将以下需要的材料放置到上方箱子中："));
                needMaterial = drawing.getStringList("needMaterial");
                for (String s : needMaterial) {
                    ClutterItem clutterItem = ClutterItem.spawnClutterItem(s);
                    lines.add(hologram.appendTextLine("§e" + clutterItem.toStringReadable()));
                }
                process++;
                break;
            case 1:
                checkPlayer();
                needMaterial = drawing.getStringList("needMaterial");
                ItemStack[] yItemStacks = ItemUtils.clone(getChest());
                ItemStack[] itemStacks = ItemUtils.clone(yItemStacks);
                int numberOfSuccesses = 0;
                for (int i = 0; i < needMaterial.size(); i++) {
                    String material = needMaterial.get(i);
                    ClutterItem clutterItem = ClutterItem.spawnClutterItem(material);
                    if (ItemUtils.removeItem(itemStacks, clutterItem, player) <= 0) {
                        numberOfSuccesses++;
                        if (lines.get(i + 4).getText().startsWith("§e")) {
                            clutterItem = ClutterItem.spawnClutterItem(material);
                            lines.get(i + 4).setText("§a" + clutterItem.toStringReadable());
                        }
                    }else {
                        if (lines.get(i + 4).getText().startsWith("§a")) {
                            clutterItem = ClutterItem.spawnClutterItem(material);
                            lines.get(i + 4).setText("§e" + clutterItem.toStringReadable());
                        }
                    }
                }
                if (numberOfSuccesses >= needMaterial.size()){

                    for (String material : needMaterial) {
                        ClutterItem clutterItem = ClutterItem.spawnClutterItem(material);
                        ItemUtils.removeItem(yItemStacks, clutterItem, player);
                    }
                    setChest(yItemStacks);
                    Collections.addAll(inventory, yItemStacks);
                    lines.forEach(HologramLine::removeLine);
                    lines.clear();
                    lines.add(hologram.appendTextLine("§e请将以下需要的材料丢弃到下方岩浆中："));
                    String smeltingStone = getSmeltingStone();
                    String[] split = smeltingStone.split(":");
                    lines.add(hologram.appendTextLine("§e" + split[0] + "×" + split[1]));
                    process++;
                    waitingTime = 0;
                }else {
                    waitingTime++;
                    if(waitingTime > 60 * 60 * 20){//超时
                        throw new SmeltingFurnaceErrorException("超时！", SmeltingFurnaceErrorException.Type.TIMEOUT, 0);
                    }
                }
                break;
            case 2:
                checkPlayer();
                String smeltingStone = getSmeltingStone();
                ItemStack[] stacks = ItemUtils.clone(inventory.toArray(new ItemStack[0]));
                ItemStack generate = ClutterItem.spawnClutterItem(smeltingStone).generate();
                if (ItemUtils.removeItem(stacks, generate) <= 0) {//进入第三步
                    lines.forEach(HologramLine::removeLine);
                    lines.clear();
                    lines.add(hologram.appendTextLine("§e请在三个熔炉分别放置："));
                    String fuel = getFuel();
                    String[] split = fuel.split(":");
                    lines.add(hologram.appendTextLine("§e" + split[0] + "×" + split[1]));
                    process++;
                    waitingTime = 0;
                }else {
                    waitingTime++;
                    if(waitingTime > 60 * 60 * 20){//超时
                        throw new SmeltingFurnaceErrorException("超时！", SmeltingFurnaceErrorException.Type.TIMEOUT, 0);
                    }
                }
                break;
            case 3:
                checkPlayer();
                String fuel = getFuel();
                int successes = 0;
                ItemStack generate1 = ClutterItem.spawnClutterItem(fuel).generate();
                ItemStack[][] furnaces = getFurnaces();
                for (ItemStack[] furnace : furnaces) {
                    if (ItemUtils.removeItem(ItemUtils.clone(furnace), generate1) <= 0){
                        successes++;
                    }
                }
                if (successes >= furnaces.length){//开始锻造
                    furnaces = getFurnaces();
                    for (ItemStack[] furnace : furnaces) {
                        ItemUtils.removeItem(furnace, generate1);
                    }
                    setFurnaces(furnaces);
                    furnacesItemStack[0] = ClutterItem.spawnClutterItem(getSmeltingStone()).generate(1);
                    furnacesItemStack[1] = ClutterItem.spawnClutterItem(getFuel()).generate(1);
                    process++;
                    lines.forEach(HologramLine::removeLine);
                    lines.clear();
                    lines.add(hologram.appendTextLine("§e等待熔炼完成..."));
                    lines.add(hologram.appendTextLine("§e请不要干扰熔炉工作！"));
                    waitingTime = 0;
                    //锻造开始 清理库存的燃料和锻造石
                    inventory.removeIf(next ->
                            next != null && next.hasItemMeta() &&
                                    (Utils.clearColor(next.getItemMeta().getDisplayName()).equals(getFuel()) || Utils.clearColor(next.getItemMeta().getDisplayName()).equals(getSmeltingStone())));
                }else {
                    waitingTime++;
                    if(waitingTime > 60 * 60 * 20){//超时
                        throw new SmeltingFurnaceErrorException("超时！", SmeltingFurnaceErrorException.Type.TIMEOUT, 0);
                    }
                }
            break;
            case 4:
                if (age % 20 == 0) {
                    boolean b = location.getWorld().hasStorm();
                    if (cooling){//在冷却
                        if (!fastCooling){
                            ItemStack[] chests = getChest();
                            if (ItemUtils.removeItem(chests, ClutterItem.spawnClutterItem("急速冷却液").generate()) == 0){
                                setChest(chests);
                                getChestInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
                                fastCooling = true;
                                lines.add(hologram.appendTextLine("§e冷却液加速冷却中..."));
                            }
                        }
                        if (fastCooling) {
                            temperature -= Utils.getRandom().nextInt(b?3000:2000);//减少温度
                        }else {
                            temperature -= Utils.getRandom().nextInt(b?600:400);//减少温度
                        }
                        temperature = Math.max(temperature, 2000);
                        if (temperature <= 5000){
                            fastCooling = false;
                            cooling = false;
                            if (meltingTick >= getLevel().getTime()){
                                process++;
                                waitingTime = 0;
                                lines.forEach(HologramLine::removeLine);
                                lines.clear();
                                lines.add(hologram.appendTextLine("§a等待玩家靠近..."));
                                break;
                            }
                            lines.forEach(HologramLine::removeLine);
                            lines.clear();
                            lines.add(hologram.appendTextLine("§e等待熔炼完成..."));
                            lines.add(hologram.appendTextLine("§e请不要干扰熔炉工作！"));
                        }
                    }else {
                        meltingTick++;
                        temperature += Utils.getRandom().nextInt(b ? 1000 : 2000);//增加温度
                        if (temperature > 100000) {
                            if (meltingTick >= getLevel().getTime()) {//熔炼结束
                                lines.forEach(HologramLine::removeLine);
                                lines.clear();
                                lines.add(hologram.appendTextLine("§a锻造结束，等待冷却完成。"));
                                meltingTick = getLevel().getTime();
                                cooling = true;
                            }else {
                                lines.forEach(HologramLine::removeLine);
                                lines.clear();
                                lines.add(hologram.appendTextLine("§e温度过高！强制冷却中..."));
                                cooling = true;
                                ItemStack[] chests = getChest();
                                if (ItemUtils.removeItem(chests, ClutterItem.spawnClutterItem("急速冷却液").generate()) == 0) {
                                    setChest(chests);
                                    getChestInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
                                    fastCooling = true;
                                    lines.add(hologram.appendTextLine("§e冷却液加速冷却中..."));
                                }
                            }
                        }
                        if (meltingTick % 10 == 0) {
                            for (Block anvil : anvils) {
                                anvil.getWorld().playSound(anvil.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);//铁砧声音
                            }
                        }
                    }
                }
                checkFurnaces();
                if (age % (Utils.getRandom().nextInt(20) + 10) == 0) {
                    boolean b = location.getWorld().hasStorm();
                    if (b || fastCooling) {
                        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);//灭火
                    }
                }
                if (meltingTick > 1 && !cooling && (meltingTick - 1 % 10 == 0 || meltingTick % 10 == 0 || meltingTick + 1 % 10 == 0)){
                    for (Block furnace : this.furnaces) {
                        spawnParticle(furnace.getLocation().add(0.5, 0.8, 0.5), 10);
                    }
                }
                checkItemEntity();
                break;
            case 5:
                if (age % 20 == 0){
                    if (meltingTick >= getLevel().getTime() || playerIsOnline()){
                        checkPlayer();
                        if (player.getWorld() == location.getWorld() && player.getLocation().distance(location) < 5) {
                            if (!lines.get(0).getText().equals("§a正在融合...")){
                                lines.forEach(HologramLine::removeLine);
                                lines.clear();
                                lines.add(hologram.appendTextLine("§a正在融合..."));
                            }
                            meltingTick--;
                            if (meltingTick % 5 == 0 || speed == 1) {
                                speed += 5;
                            }
                            if (meltingTick == getLevel().getTime() - 30){
                                final Location location = chest.getLocation().add(0.5, 2, 0.5);
                                for (int i = 0; i < 10; i++) {
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(cn.LTCraft.core.Main.getInstance(), () -> {
                                        location.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, location, 300, 0, 0, 0, 5);
                                    }, i * 10 + Utils.getRandom().nextInt(6) + 7);
                                }
                                Bukkit.getScheduler().scheduleSyncDelayedTask(cn.LTCraft.core.Main.getInstance(), () -> {
                                    process++;
                                    lines.forEach(HologramLine::removeLine);
                                    lines.clear();
                                }, 150);
                            }
                        }else {
                            if (!lines.get(0).getText().equals("§e等待玩家靠近...")){
                                lines.forEach(HologramLine::removeLine);
                                lines.clear();
                                lines.add(hologram.appendTextLine("§e等待玩家靠近..."));
                            }
                        }
                    }else {
                        waitingTime++;
                        if(waitingTime > 60 * 60 * 6){//超时
                            close();
                        }
                    }
                }
                checkItemEntity();
                break;
            case 6:
                if (age % 20 == 0){
                    checkPlayer();
                    if (!done) {
                        if (player.getLocation().distance(location) < 5) {
                            lines.add(hologram.appendTextLine("§a熔炼已完成！"));
                            PlayerUtils.dropItemFloat(player, chest.getLocation().add(0.5, 1, 0.5), getResult());
                            done = true;
                            waitingTime = 0;
                            //不再删除图纸
//                            ((ItemFrame) itemFrameEntity).setItem(new ItemStack(Material.AIR));
                            floatItemEntity.forEach(Entity::remove);
                            cleanFurnaces();
                            inventory.clear();
                        } else {
                            lines.add(hologram.appendTextLine("§a熔炼已完成，等待玩家靠近！"));
                        }
                    }else {
                        waitingTime++;
                        if (waitingTime > 10){
                            close();
                        }
                    }
                }
                if (!done)checkItemEntity();
                break;
        }
    }
    /**
     * 收集周围燃烧的物品
     */
    public void collectAround(){
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 5, 3, 5);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof Item){
                Block down = WorldUtils.getSideBlock(nearbyEntity.getLocation(), WorldUtils.SIDE.DOWN);
                Block block = nearbyEntity.getLocation().getWorld().getBlockAt(nearbyEntity.getLocation());
                if (
                        block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA ||
                        down.getType() == Material.LAVA || down.getType() == Material.STATIONARY_LAVA ||
                        nearbyEntity.getFireTicks() > 0
                ) {
                    inventory.add(((Item) nearbyEntity).getItemStack());
                    nearbyEntity.remove();
                }
            }else if (age % 20 == 0 && nearbyEntity instanceof LivingEntity){
                LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                float damage = 0;
                if (temperature > 8000 && temperature < 15000)damage = 1;
                if (temperature >= 15000 && temperature < 30000)damage = 3;
                if (temperature >= 30000 && temperature < 60000)damage = 8;
                if (temperature >= 60000)damage = 15;
                if (damage > 0) {
                    ((CraftLivingEntity) livingEntity).getHandle().damageEntity(DamageSource.BURN, damage);
                }
            }
        }
    }

    /**
     * 检查悬浮字 防止行太多陷入地下
     */
    public void checkHologram(){
        List<CraftHologramLine> linesUnsafe = ((CraftHologram) hologram).getLinesUnsafe();
        if (Math.abs(hologram.getY() - (linesUnsafe.size() * 0.3)) > 0.3){
            hologram.teleport(itemFrame.clone().add(0, linesUnsafe.size() * 0.3, 0));
        }
    }

    /**
     * 检查玩家
     */
    public void checkPlayer() throws SmeltingFurnaceErrorException{
        if (!playerIsOnline()) {
            throw new SmeltingFurnaceErrorException("超时！", SmeltingFurnaceErrorException.Type.PLAYER_OFFLINE);
        }
    }

    /**
     * 玩家是否在线
     * @return 玩家是否在线
     */
    public boolean playerIsOnline(){
        if (player == null || !player.isOnline()){
            Player playerExact = Bukkit.getPlayerExact(playerName);
            if (playerExact == null) {
                return false;
            }
            player = playerExact;
        }
        return true;
    }
    /**
     * 检查旋转掉落物
     */
    public void checkItemEntity(){
        if (floatItemEntity.size() <=0 || floatItemEntity.stream().anyMatch(Entity::isDead)){
            for (Entity entity : floatItemEntity) {
                entity.remove();
            }
            floatItemEntity.clear();
            List<String> needMaterial = drawing.getStringList("needMaterial");
            needMaterial.add(getSmeltingStone());
            Location side = WorldUtils.getSide(location, WorldUtils.SIDE.UP);
            for (String need : needMaterial) {
                ClutterItem clutterItem = ClutterItem.spawnClutterItem(need);
                ItemStack generate = clutterItem.generate();
                Item item = location.getWorld().dropItem(side, generate);
                item.setPickupDelay(Integer.MAX_VALUE);
                item.setGravity(false);
                floatItemEntity.add(item);
                angle = Math.max(360 / floatItemEntity.size(), 1);
            }
        }else {//旋转他们
            Location location = this.location.clone();
            double x = location.add(0.5, 0, 0.5).getX();
            double z = location.getZ();
            for (int i = 1; i <= floatItemEntity.size(); i ++) {
                CraftItem craftItem = (CraftItem)floatItemEntity.get(i - 1);
                double targetX = Math.cos(((angle * i + rotationProgress) % 360) * Math.PI / 180) * 1.5 + x;
                double targetZ = Math.sin(((angle * i + rotationProgress) % 360) * Math.PI / 180) * 1.5 + z;
                craftItem.getHandle().motX = (targetX - craftItem.getLocation().getX());
                craftItem.getHandle().motY = 0;
                craftItem.getHandle().motZ = (targetZ - craftItem.getLocation().getZ());
                craftItem.getHandle().impulse = true;
            }
            rotationProgress += speed;
            rotationProgress %= 360;
        }
    }

    /**
     * 是否为随机
     * @return 是否为随机
     */
    public boolean isRandom() throws SmeltingFurnaceErrorException {
        ItemStack itemStack = ClutterItem.spawnClutterItem(getLevel().getName() + "不稳定要素").generate();
        return ItemUtils.removeItem(ItemUtils.clone(getChest()), itemStack) <= 0;
    }

    /**
     * 获取熔炼结果
     * @return 获取熔炼结果
     */
    private ItemStack[] getResult() throws SmeltingFurnaceErrorException {
        ClutterItem clutterItem;
        List<ItemStack> list = new ArrayList<>();
        if (isRandom()){
            ItemStack[] clone = ItemUtils.clone(getChest());
            ItemStack itemStack = ClutterItem.spawnClutterItem(getLevel().getName() + "不稳定要素").generate();
            ItemUtils.removeItem(clone, itemStack);
            setChest(clone);
            List<String> results = drawing.getStringList("randomResult");
            Map<Double, String> randomTable = MathUtils.getRandomTable(results);
            String result = MathUtils.calculationRandom(randomTable);
            clutterItem = ClutterItem.spawnClutterItem(result);
            list.add(clutterItem.generate());
        }else {
            List<String> results = drawing.getStringList("stableResult");
            for (String result : results) {
                clutterItem = ClutterItem.spawnClutterItem(result);
                list.add(clutterItem.generate());
            }
        }
        ItemStack[] itemStacks = list.toArray(new ItemStack[0]);
        for (int i = 0; i < itemStacks.length; i++) {
            itemStacks[i] = ItemUtils.setBinding(itemStacks[i], playerName);
        }
        return itemStacks;
    }
    /**
     * 检查熔炉
     * @throws SmeltingFurnaceErrorException 如果被玩家干扰
     */
    public void checkFurnaces() throws SmeltingFurnaceErrorException {
        for (Block furnace : furnaces) {
            if (furnace.getState() instanceof Furnace){
                Furnace state = (Furnace) furnace.getState();
                FurnaceInventory inventory = state.getInventory();
                if (state.getBurnTime() <= 0 && (inventory.getSmelting() == null || inventory.getSmelting().getType() == Material.AIR) && (inventory.getResult() == null || inventory.getResult().getType() == Material.AIR)) {
                    inventory.setSmelting(furnacesItemStack[0].clone());
                    inventory.setFuel(furnacesItemStack[1].clone());
                }
                if (inventory.getResult() != null && inventory.getResult().getType() != Material.AIR) {
                    inventory.setResult(new ItemStack(Material.AIR));
                    inventory.setSmelting(furnacesItemStack[0].clone());
                }
                if (state.getBurnTime() <= 1){
                    inventory.setFuel(furnacesItemStack[1].clone());
                }
                if (!Objects.equals(inventory.getSmelting(), furnacesItemStack[0])){
                    fatalError = true;
                    throw new SmeltingFurnaceErrorException("熔炼过程熔炉被干扰！", SmeltingFurnaceErrorException.Type.FURNACES_DISTURBED, 60, true);
                }
            }
        }
    }
    /**
     * 检查图纸
     * @throws SmeltingFurnaceErrorException 错误
     */
    public void checkDrawing() throws SmeltingFurnaceErrorException {
        if (itemFrameEntity == null || itemFrameEntity.isDead() || !(itemFrameEntity instanceof ItemFrame)){
            throw new SmeltingFurnaceErrorException("找不到物品展示框！", SmeltingFurnaceErrorException.Type.FRAME_ABNORMAL, 60, true);
        }
        if (done)return;
        ItemStack itemStack = ((ItemFrame) itemFrameEntity).getItem();
        String name;
        if (itemStack == null || !itemStack.hasItemMeta() || (name = itemStack.getItemMeta().getDisplayName()) == null){
            throw new SmeltingFurnaceErrorException("找不到图纸！", SmeltingFurnaceErrorException.Type.DRAWING_ABNORMAL, 60, true);
        }
        if (!Utils.clearColor(name).equals(this.drawing.getName())) {
            throw new SmeltingFurnaceErrorException("现在图纸与开始图纸不符合！", SmeltingFurnaceErrorException.Type.DRAWING_ABNORMAL, 60, true);
        }
    }


    /**
     * 粒子效果
     * @param location 粒子的中心
     * @param count 循环次数
     */
    public void spawnParticle(Location location, int count){
        double x,z;
        for (int ii = 0; ii < count; ii++){
            x = location.getX() + 1 * Math.cos((age % 360) * 3.14 / 9) ;
            z = location.getZ() + 1 * Math.sin((age % 360) * 3.14 / 9) ;
            location.getWorld().spawnParticle(Particle.LAVA, new Location(location.getWorld(), x, location.getY(), z), 1);
        }
    }
    /**
     *
     * @return 三个熔炉 的物品
     * @throws SmeltingFurnaceErrorException 如果 {@link SmeltingFurnace#anvils} 不属于 {@link Container}
     */
    public ItemStack[][] getFurnaces() throws SmeltingFurnaceErrorException{
        ItemStack[][] itemStacks = new ItemStack[3][];
        for (int i = 0; i < furnaces.length; i++) {
            Block furnace = furnaces[i];
            if (furnace != null && furnace.getState() instanceof Container) {
                Inventory inventory = ((Container) furnace.getState()).getInventory();
                itemStacks[i] = inventory.getContents();
            } else throw new SmeltingFurnaceErrorException("找不到熔炼坛熔炉！", SmeltingFurnaceErrorException.Type.FURNACES_ABNORMAL, 60, true);
        }
        return itemStacks;
    }

    /**
     * 设置三个熔炉的物品
     * @param itemStacks 要设置的物品
     * @throws SmeltingFurnaceErrorException  如果 {@link SmeltingFurnace#anvils} 不属于 {@link Container}
     */
    public void setFurnaces(ItemStack[][] itemStacks) throws SmeltingFurnaceErrorException{
        for (int i = 0; i < furnaces.length; i++) {
            Block furnace = furnaces[i];
            if (furnace != null && furnace.getState() instanceof Container) {
                Inventory inventory = ((Container) furnace.getState()).getInventory();
                inventory.setContents(itemStacks[i]);
            } else throw new SmeltingFurnaceErrorException("找不到熔炼坛熔炉！", SmeltingFurnaceErrorException.Type.FURNACES_ABNORMAL, 60, true);
        }
    }

    /**
     *
     * @return 箱子的物品
     * @throws SmeltingFurnaceErrorException 如果 {@link SmeltingFurnace#chest} 不属于 {@link Container}
     */
    public ItemStack[] getChest() throws SmeltingFurnaceErrorException{
        if (chest.getState() instanceof Container){
            Inventory inventory = ((Container) chest.getState()).getInventory();
            return inventory.getContents();
        }else throw new SmeltingFurnaceErrorException("找不到熔炼坛箱子！", SmeltingFurnaceErrorException.Type.CHEST_ABNORMAL, 60, true);
    }

    /**
     *
     * @return 箱子的库存
     * @throws SmeltingFurnaceErrorException 如果 {@link SmeltingFurnace#chest} 不属于 {@link Container}
     */
    public Inventory getChestInventory() throws SmeltingFurnaceErrorException{
        if (chest.getState() instanceof Container){
            return ((Container) chest.getState()).getInventory();
        }else throw new SmeltingFurnaceErrorException("找不到熔炼坛箱子！", SmeltingFurnaceErrorException.Type.CHEST_ABNORMAL, 60, true);
    }

    /**
     * 设置箱子的物品
     * @param itemStacks 要设置的物品
     * @throws SmeltingFurnaceErrorException  如果 {@link SmeltingFurnace#chest} 不属于 {@link Container}
     */
    public void setChest(ItemStack[] itemStacks) throws SmeltingFurnaceErrorException{
        if (chest.getState() instanceof Container){
            Inventory inventory = ((Container) chest.getState()).getInventory();
            inventory.setContents(itemStacks);
        }else throw new SmeltingFurnaceErrorException("找不到熔炼坛箱子！", SmeltingFurnaceErrorException.Type.CHEST_ABNORMAL, 60, true);
    }
    /**
     * @return string
     */
    public String getTemperatureString() {
        String p;
        if(temperature < 5000){
            p = "§a";
        }else if (temperature < 15000){
            p = "§e";
        }else if (temperature < 50000){
            p = "§6";
        }else {
            p = "§c";
        }
        return p + Utils.formatNumber(temperature / 100d);
    }


    /**
     * 稳定性
     * @return 稳定性
     */
    public String getStable()
    {
        if (errorTick > 0){
            if (lastException != null && lastException.isBlast())
                return "§c！！！濒临爆炸！！！";
            else
                return "§c！！！锻造错误！！！";
        }
        if (temperature > 50000)return "§e温度过高";
        return "非常稳定";
    }
    /**
     * 获取熔炼坛等级
     * @return 等级
     */
    public Level getLevel(){
        return Level.CURRENCY;//目前只实现通用
    }

    /**
     * 获取燃料等级
     * @return 燃料
     */
    public String getFuel(){
        return getLevel().getFuel();
    }

    /**
     * 获取熔炼石
     * @return 熔炼石
     */
    public String getSmeltingStone(){
        return getLevel().getSmeltingStone();
    }

    /**
     * 获取这个熔炼坛的唯一id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * 关闭这个熔炼坛
     */
    public void close(){
        if (!closed){
            hologram.delete();
            if (!done){
                try {
                    for (ItemStack itemStack : inventory) {
                        if (itemStack != null)getChestInventory().addItem(itemStack);
                    }
                } catch (SmeltingFurnaceErrorException ignore) {
                    //箱子都没了 退什么？
                }
            }
            inventory.clear();
            lines.clear();
            errorLines.clear();
            smeltingFurnaceMap.remove(id);
            closed = true;
            furnacesItemStack = new ItemStack[0];
            floatItemEntity.forEach(Entity::remove);
            floatItemEntity.clear();
            cleanFurnaces();
        }
    }

    /**
     * 清空熔炉
     */
    public void cleanFurnaces(){
        ItemStack[][] itemStacks = new ItemStack[3][];
        Arrays.fill(itemStacks, new ItemStack[0]);
        try {
            setFurnaces(itemStacks);
        } catch (SmeltingFurnaceErrorException ignore) {

        }
    }
    public YamlConfiguration save(){
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("playerName", playerName);
        yamlConfiguration.set("location", GameUtils.spawnLocationString(location));
        yamlConfiguration.set("itemFrameEntity", itemFrameEntity.getUniqueId().toString());
        yamlConfiguration.set("drawing", drawing.getName());
        yamlConfiguration.set("process", process);
        yamlConfiguration.set("temperature", temperature);
        yamlConfiguration.set("floatItemEntity", floatItemEntity.stream().map(entity -> entity.getUniqueId().toString()).collect(Collectors.toList()));
        yamlConfiguration.set("lines", lines.stream().map(TextLine::getText).collect(Collectors.toList()));
        yamlConfiguration.set("errorLines", errorLines.stream().map(TextLine::getText).collect(Collectors.toList()));
        yamlConfiguration.set("inventory", inventory.stream().filter(Objects::nonNull).map(ItemStack::serialize).collect(Collectors.toList()));
        yamlConfiguration.set("errorTick", errorTick);
        yamlConfiguration.set("fatalError", fatalError);
        yamlConfiguration.set("speed", speed);
        yamlConfiguration.set("rotationProgress", rotationProgress);
        yamlConfiguration.set("angle", angle);
        yamlConfiguration.set("age", age);
        yamlConfiguration.set("meltingTick", meltingTick);
        yamlConfiguration.set("cooling", cooling);
        yamlConfiguration.set("waitingTime", waitingTime);
        yamlConfiguration.set("fastCooling", fastCooling);
        yamlConfiguration.set("done", done);
        if (lastException != null) {
            yamlConfiguration.set("lastException", lastException.serialize());
        }
        return yamlConfiguration;
    }
    public static SmeltingFurnace load(YamlConfiguration yamlConfiguration){
        Location location = GameUtils.spawnLocation(yamlConfiguration.getString("location"));
        String itemFrameUUID = yamlConfiguration.getString("itemFrameEntity");
        Optional<Entity> first = location.getWorld().getNearbyEntities(location, 5, 3, 5).stream().filter(entity -> entity.getUniqueId().toString().equals(itemFrameUUID)).findFirst();
        if (!first.isPresent())return null;
        Entity itemFrame = first.get();
        SmeltingFurnace smeltingFurnace = new SmeltingFurnace(null, location, itemFrame, SmeltingFurnaceDrawing.getSmeltingFurnaceDrawing(yamlConfiguration.getString("drawing")));
        smeltingFurnace.playerName = yamlConfiguration.getString("playerName");
        smeltingFurnace.process = yamlConfiguration.getInt("process");
        smeltingFurnace.temperature = yamlConfiguration.getInt("temperature");
        List<String> floatItemEntityUUIDs = yamlConfiguration.getStringList("floatItemEntity");
        smeltingFurnace.floatItemEntity = location.getWorld().getNearbyEntities(location, 5, 3, 5).stream().filter(entity -> floatItemEntityUUIDs.contains(entity.getUniqueId().toString())).collect(Collectors.toList());
        smeltingFurnace.errorTick = yamlConfiguration.getInt("errorTick");
        smeltingFurnace.fatalError = yamlConfiguration.getBoolean("fatalError");
        smeltingFurnace.lines = new ArrayList<>();
        smeltingFurnace.errorLines = new ArrayList<>();
        List<String> lines = yamlConfiguration.getStringList("lines");
        for (String line : lines) {
            smeltingFurnace.lines.add(smeltingFurnace.hologram.appendTextLine(line));
        }
        if (smeltingFurnace.errorTick > 0){
            for (TextLine line : smeltingFurnace.lines) {
                line.removeLine();
            }
            lines = yamlConfiguration.getStringList("errorLines");
            for (String line : lines) {
                smeltingFurnace.errorLines.add(smeltingFurnace.hologram.appendTextLine(line));
            }
        }
        List<ItemStack> inventory = (List<ItemStack>)yamlConfiguration.get("inventory");
        for (Object itemStack : inventory) {
            ItemStack deserialize = ItemStack.deserialize((Map)itemStack);
            smeltingFurnace.inventory.add(deserialize);
        }
        smeltingFurnace.speed = yamlConfiguration.getInt("speed");
        smeltingFurnace.rotationProgress = yamlConfiguration.getInt("rotationProgress");
        smeltingFurnace.angle = yamlConfiguration.getInt("angle");
        smeltingFurnace.age = yamlConfiguration.getInt("age");
        smeltingFurnace.meltingTick = yamlConfiguration.getInt("meltingTick");
        smeltingFurnace.cooling = yamlConfiguration.getBoolean("cooling");
        smeltingFurnace.waitingTime = yamlConfiguration.getInt("waitingTime");
        smeltingFurnace.fastCooling = yamlConfiguration.getBoolean("fastCooling");
        smeltingFurnace.done = yamlConfiguration.getBoolean("done");
        if (smeltingFurnace.process > 3){
            smeltingFurnace.furnacesItemStack[0] = ClutterItem.spawnClutterItem(smeltingFurnace.getSmeltingStone()).generate(1);
            smeltingFurnace.furnacesItemStack[1] = ClutterItem.spawnClutterItem(smeltingFurnace.getFuel()).generate(1);
        }
        if (yamlConfiguration.contains("lastException"))smeltingFurnace.lastException = SmeltingFurnaceErrorException.unSerialize(yamlConfiguration.getString("lastException"));
        return smeltingFurnace;
    }
    /**
     * 产出一个爆炸
     * //fixme 增加爆炸破坏方块
     */
    public void explosive(){
        location.getWorld().createExplosion(location, 5);
    }
    private static final List<Integer> reverses = Ints.asList(0, 0, 3, 2, 5, 4);//反向
    private static final List<Integer> tReverses = Ints.asList(0, 0, 1, 3, 0, 2);//反向
    /**
     * 检查多方块结构
     * 这个玩法最难实现的功能实现了还有啥呢。
     * @param location 玻璃坐标
     * @param itemFrame 物品展示框坐标
     * TODO: 优化 它！
     * @return 错误的方块
     */
    public static FakeBlock[] check(Location location, Location itemFrame){
        List<FakeBlock> blocks = new ArrayList<>();
        Block block;
        //背向
        WorldUtils.SIDE pSide = WorldUtils.getForDirection(itemFrame, location);
        //入口
        WorldUtils.SIDE entrance = WorldUtils.getForDirection(location, itemFrame);
        //检查上方是否为箱子 并且朝向入口
        if ((block = WorldUtils.getSideBlock(location, WorldUtils.SIDE.UP)).getType() != Material.CHEST || block.getData() != entrance.getId()){
            blocks.add(new FakeBlock(WorldUtils.getSide(location, WorldUtils.SIDE.UP), Material.CHEST, (byte) entrance.getId()));
        }else{
            if (block.getData() != entrance.getId()){//如果是箱子检查朝向是否为入口
                blocks.add(new FakeBlock(WorldUtils.getSide(location, WorldUtils.SIDE.UP), Material.CHEST, (byte) entrance.getId()));
            }
        }
        Block tmpBlock;
        Block furnace = null;
        Block anvil = null;
        Block bottom;
        //四个面 分别为 2 3 4 5(北 南 西 东)     1和2位上下
        for (int side = 2; side < 6; side++){
            List<Block> lavas = new ArrayList<>();//应该是石头的所有坐标
            if (side != entrance.getId()){
                furnace = WorldUtils.getSideBlock(location, WorldUtils.SIDE.byId(side), 4);//应该为熔炉
                anvil = WorldUtils.getSideBlock(furnace.getLocation(), WorldUtils.SIDE.UP);// 应该为铁砧
                lavas.add(WorldUtils.getSideBlock(WorldUtils.getSide(location, WorldUtils.SIDE.DOWN), WorldUtils.SIDE.byId(side)));//应该为岩浆
            }
            List<Block> stones = new ArrayList<>();//应该是石头的所有坐标
            WorldUtils.SIDE[] orSo = new WorldUtils.SIDE[2]; //左右
            switch(WorldUtils.SIDE.byId(side)){//计算祭坛一个面的两边
                case NORTH://北
                case SOUTH://南
                    orSo = new WorldUtils.SIDE[]{WorldUtils.SIDE.WEST, WorldUtils.SIDE.EAST};
                    break;
                case WEST://西
                case EAST://东
                    orSo = new WorldUtils.SIDE[]{WorldUtils.SIDE.NORTH, WorldUtils.SIDE.SOUTH};
                    break;
            }
            for (WorldUtils.SIDE s : orSo){//查找熔炉的左右
                if (side != entrance.getId()) {
                    assert furnace != null;
                    stones.add(WorldUtils.getSideBlock(furnace.getLocation(), s));//不是入口 应该有石头
                    if (side == pSide.getId()){
                        lavas.add(WorldUtils.getSideBlock(lavas.get(0).getLocation(), s));//应该为岩浆
                    }
                }
                tmpBlock = WorldUtils.getSideBlock(location, WorldUtils.SIDE.byId(side), 3);//熔炉前面的方块
                stones.add((bottom = WorldUtils.getSideBlock(tmpBlock.getLocation(), s, 2)));//底部 往上两个应该为石头
                stones.add(WorldUtils.getSideBlock(bottom.getLocation(), WorldUtils.SIDE.UP));
                if (side != entrance.getId())stones.add(WorldUtils.getSideBlock(bottom.getLocation(), WorldUtils.SIDE.UP, 2));
            }
            if (side != entrance.getId() && ((furnace.getType() != Material.FURNACE && furnace.getType() != Material.BURNING_FURNACE) || furnace.getData() != reverses.get(side))){
                blocks.add(new FakeBlock(furnace.getLocation(), Material.FURNACE, (byte) (int) reverses.get(side)));
            }
            if(side != entrance.getId()){
                assert anvil != null;
                if (anvil.getType() != Material.ANVIL){
                    blocks.add(new FakeBlock(anvil.getLocation(), Material.ANVIL, (byte) (int) tReverses.get(side)));
                }else if (tReverses.get(side) == 0 || tReverses.get(side) == 2){
                    if(anvil.getData() != 0 && anvil.getData() != 2){
                        blocks.add(new FakeBlock(anvil.getLocation(), Material.ANVIL, (byte) (int) tReverses.get(side)));
                    }
                }else{
                    if(anvil.getData() != 1 && anvil.getData() != 3){
                        blocks.add(new FakeBlock(anvil.getLocation(), Material.ANVIL, (byte) (int) tReverses.get(side)));
                    }
                }
            }
            for (Block stone : stones) {
                if (stone.getType() != Material.SMOOTH_BRICK || stone.getData() != 3){
                    blocks.add(new FakeBlock(stone.getLocation(), Material.SMOOTH_BRICK, (byte) 3));
                }
            }
            for (Block lava : lavas) {
                if (lava.getType() != Material.LAVA && lava.getType() != Material.STATIONARY_LAVA){
                    blocks.add(new FakeBlock(lava.getLocation(), Material.LAVA));
                }
            }
        }
        return blocks.toArray(new FakeBlock[]{});
    }

    /**
     * 获取三个铁砧
     * @param location location中心
     * @param itemFrame 物品展示框
     * @return 三个铁砧
     */
    public static Block[] getAnvils(Location location, Location itemFrame) {
        List<Block> blocks = new ArrayList<>();
        //入口
        WorldUtils.SIDE entrance = WorldUtils.getForDirection(location, itemFrame);
        //四个面 分别为 2 3 4 5(北 南 西 东)     1和2位上下
        for (int side = 2; side < 6; side++){
            if (side != entrance.getId()){
                blocks.add(WorldUtils.getSideBlock(WorldUtils.getSide(location, WorldUtils.SIDE.byId(side), 4), WorldUtils.SIDE.UP));//找到它了
            }
        }
        return blocks.toArray(blocks.toArray(new Block[0]));
    }

    /**
     * 获取三个熔炉
     * @param location location中心
     * @param itemFrame 物品展示框
     * @return 三个熔炉
     */
    public static Block[] getFurnaces(Location location, Location itemFrame) {
        List<Block> blocks = new ArrayList<>();
        //入口
        WorldUtils.SIDE entrance = WorldUtils.getForDirection(location, itemFrame);
        //四个面 分别为 2 3 4 5(北 南 西 东)     1和2位上下
        for (int side = 2; side < 6; side++){
            if (side != entrance.getId()){
                blocks.add(WorldUtils.getSideBlock(location, WorldUtils.SIDE.byId(side), 4));//找到它了
            }
        }
        return blocks.toArray(blocks.toArray(new Block[0]));
    }

    public static Map<Integer, SmeltingFurnace> getSmeltingFurnaceMap() {
        return smeltingFurnaceMap;
    }

    public static enum Level{
        CURRENCY("通用", "初级燃料:10", "通用熔炼石:30", 1000),
        ADVANCED("进阶", "高级燃料:10", "进阶熔炼石:30", 2000),
        LEGEND("传说", "传说燃料:10", "传说熔炼石:30", 3000),
        CHAOS("混沌", "混沌燃料:10", "混沌熔炼石:30", 4000);
        private static Map<String, Level> map = new HashMap<>();
        static {
            for (Level value : values()) {
                map.put(value.getName(), value);
            }
        }
        private final String name;
        private final String fuel;
        private final String smeltingStone;
        private final int time;
        Level(String name, String fuel, String smeltingStone, int time){
            this.name = name;
            this.fuel = fuel;
            this.smeltingStone = smeltingStone;
            this.time = time;
        }

        public String getName() {
            return name;
        }

        public String getFuel() {
            return fuel;
        }

        public int getTime() {
            return time;
        }

        public String getSmeltingStone() {
            return smeltingStone;
        }

        public static Level getByName(String name){
            return map.get(name);
        }
    }
}
