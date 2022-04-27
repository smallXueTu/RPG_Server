package cn.LTCraft.core.game;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.entityClass.ClutterItem;
import cn.LTCraft.core.game.more.FakeBlock;
import cn.LTCraft.core.game.more.SmeltingFurnaceDrawing;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.other.exceptions.SmeltingFurnaceErrorException;
import cn.LTCraft.core.task.GlobalRefresh;
import cn.LTCraft.core.utils.ItemUtils;
import cn.LTCraft.core.utils.Utils;
import cn.LTCraft.core.utils.WorldUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import com.google.common.primitives.Ints;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

/**
 * 熔炼炉
 * Created by Angel、 on 2022/4/25 20:39
 */
public class SmeltingFurnace implements TickEntity {
    //------------------------------------static------------------------------------
    private static int FID = 0;
    private static final Map<Integer, SmeltingFurnace> smeltingFurnaceMap = new HashMap<>();
    //------------------------------------static------------------------------------
    private final Player player;
    private final Location location;
    private Block chest;
    private final Location itemFrame;
    private final SmeltingFurnaceDrawing drawing;
    private boolean closed = false;
    private Hologram hologram;
    private final int id;//唯一id
    private int process = 0;//阶段
    private double temperature;//温度
    private Block[] furnaces;//三个熔炉
    private Block[] anvils;//三个铁砧
    private List<TextLine> lines = new ArrayList<>();
    private List<TextLine> errorLines = new ArrayList<>();
    private List<ItemStack> inventory = new ArrayList<>();
    private int errorTick = 0;

    public SmeltingFurnace(Player player, Location location, Location itemFrame, SmeltingFurnaceDrawing drawing){
        this.location = location;
        WorldUtils.SIDE entrance = WorldUtils.getForDirection(location, itemFrame);
        this.itemFrame = WorldUtils.getSide(location, entrance).add(0.5, 0.5, 0.5);
        this.player = player;
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
     * @return 返回false带边吃熔炼已结束
     */
    public boolean doTick(long tick){
        collectAround();
        try {
            updateProcess();
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
        } catch (SmeltingFurnaceErrorException e) {//发生了错误 留给玩家60s的时间用于玩家修复错误！
            if (errorTick == 0){
                for (TextLine line : lines) {
                    line.removeLine();
                }
                errorLines.add(hologram.appendTextLine("§c错误：" + e.getMessage()));
                errorLines.add(hologram.appendTextLine("§c请及时修正，否者将在LTSF:" + id + ":errorTick秒后坠毁！"));
            }
            errorTick++;
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
                lines.add(hologram.appendTextLine("§e请将以下需要的材料放置到上方箱子中："));
                needMaterial = drawing.getStringList("needMaterial");
                for (String s : needMaterial) {
                    String[] split = s.split(":");
                    lines.add(hologram.appendTextLine("§e" + split[0] + "类型" + split[1] + "×" + split[2]));
                }
                process++;
                break;
            case 1:
                needMaterial = drawing.getStringList("needMaterial");
                ItemStack[] itemStacks = getChest();
                int numberOfSuccesses = 0;
                for (int i = 0; i < needMaterial.size(); i++) {
                    String material = needMaterial.get(i);
                    ClutterItem clutterItem = new ClutterItem(material, ClutterItem.ItemSource.LTCraft);
                    if (ItemUtils.removeItem(itemStacks, clutterItem, player) <= 0) {
                        numberOfSuccesses++;
                        String[] split = material.split(":");
                        lines.get(i + 1).setText("§a" + split[0] + "类型" + split[1] + "×" + split[2]);
                    }
                }
                if (numberOfSuccesses >= needMaterial.size()){
                    lines.forEach(HologramLine::removeLine);
                    lines.clear();
                    lines.add(hologram.appendTextLine("请将以下需要的材料丢弃到下方岩浆中："));
                    String smeltingStone = getSmeltingStone();
                    String[] split = smeltingStone.split(":");
                    lines.add(hologram.appendTextLine("§e" + split[0] + "×" + split[1]));
                    process++;
                }
                break;
            case 2:

                break;
        }
    }

    /**
     * 收集周围燃烧的物品
     */
    public void collectAround(){
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 3, 3, 3);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof Item && nearbyEntity.getFireTicks() > 0){
                inventory.add(((Item) nearbyEntity).getItemStack());
                nearbyEntity.remove();
            }
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public int getTickRate() {
        return 1;
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
        }else throw new SmeltingFurnaceErrorException("找不到熔炼坛箱子！");
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
        }else throw new SmeltingFurnaceErrorException("找不到熔炼坛箱子！");
    }
    /**
     * @return string
     */
    public String getTemperatureString() {
        String p;
        if(temperature < 50){
            p = "§a";
        }else if (temperature < 150){
            p = "§e";
        }else if (temperature < 500){
            p = "§6";
        }else if (temperature >= 500){
            p = "§c";
        }else{
            p = "";
        }
        return p + temperature;
    }


    /**
     * 稳定性
     * @return 稳定性
     */
    public String getStable()
    {
        if (errorTick > 0)return "§c！！！濒临爆炸！！！";
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
            inventory.clear();
            lines.clear();
            errorLines.clear();
            smeltingFurnaceMap.remove(id);
            closed = true;
        }
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
        CURRENCY("通用", "初级燃料", "通用熔炼石:30"),
        ADVANCED("进阶", "高级燃料", "进阶熔炼石:30"),
        LEGEND("传说", "传说燃料", "传说熔炼石:30"),
        CHAOS("混沌", "混沌燃料", "混沌熔炼石:30");
        private static Map<String, Level> map = new HashMap<>();
        static {
            for (Level value : values()) {
                map.put(value.getName(), value);
            }
        }
        private final String name;
        private final String fuel;
        private final String smeltingStone;
        Level(String name, String fuel, String smeltingStone){
            this.name = name;
            this.fuel = fuel;
            this.smeltingStone = smeltingStone;
        }

        public String getName() {
            return name;
        }

        public String getFuel() {
            return fuel;
        }

        public String getSmeltingStone() {
            return smeltingStone;
        }

        public static Level getByName(String name){
            return map.get(name);
        }
    }
}
