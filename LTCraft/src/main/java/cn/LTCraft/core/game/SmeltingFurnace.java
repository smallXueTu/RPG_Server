package cn.LTCraft.core.game;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.more.FakeBlock;
import cn.LTCraft.core.game.more.SmeltingFurnaceDrawing;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.other.exceptions.SmeltingFurnaceErrorException;
import cn.LTCraft.core.utils.Utils;
import cn.LTCraft.core.utils.WorldUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.primitives.Ints;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * 熔炼炉
 * Created by Angel、 on 2022/4/25 20:39
 */
public class SmeltingFurnace implements TickEntity {
    private static int FID = 0;
    private final Player player;
    private final Location location;
    private Block chest;
    private final Location itemFrame;
    private final SmeltingFurnaceDrawing drawing;
    private boolean closed = false;
    private Hologram hologram;
    private final int id;//唯一id
    private int process = 1;//阶段
    private double temperature;//温度
    private Block[] furnaces;//三个熔炉
    private Block[] anvils;//三个铁砧
    private List<TextLine> lines = new ArrayList<>();

    public SmeltingFurnace(Player player, Location location, Location itemFrame, SmeltingFurnaceDrawing drawing){
        this.location = location;
        this.itemFrame = new Location(itemFrame.getWorld(), itemFrame.getBlockX() + 0.5, itemFrame.getBlockY() + 0.5, itemFrame.getZ() + 0.5);
        this.player = player;
        this.drawing = drawing;
        id = FID++;
        init();
    }
    public void init(){
        hologram = HologramsAPI.createHologram(Main.getInstance(), itemFrame.clone().add(0, 2, 0));
        HologramsAPI.registerPlaceholder(Main.getInstance(), "LTSF:" + id + ":process", 1, () -> Utils.getNumberCapitalize(process));
        HologramsAPI.registerPlaceholder(Main.getInstance(), "LTSF:" + id + ":temperature", 1, this::getTemperatureString);
        hologram.appendTextLine("§e§l熔炼祭坛");
        hologram.appendTextLine("§d当前第：LTSF:" + id + ":process阶段。");
        hologram.appendTextLine("§d当前温度：LTSF:" + id + ":temperature°§d。");
        hologram.appendTextLine("§a阶段待完成事件：");
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
    public boolean doTick(long tick){

        return true;
    }

    public void updateProcess(){
        switch (process){
            case 0:
                lines.add(hologram.appendTextLine("请将以下需要的材料放置到上方箱子中："));
                List<String> needMaterial = drawing.getStringList("needMaterial");
                for (String s : needMaterial) {
                    String[] split = s.split(":");
                    hologram.appendTextLine("§e" + split[0] + "类型" + split[1] + "×" + split[2]);
                }
                process++;
                break;
            case 1:

                break;
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

    @Override
    public boolean isAsync() {
        return true;
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
        if (chest instanceof Container){
            Inventory inventory = ((Container) chest).getInventory();
            return inventory.getContents();
        }else throw new SmeltingFurnaceErrorException("找不到熔炼坛箱子！");
    }

    /**
     * 设置箱子的物品
     * @param itemStacks 要设置的物品
     * @throws SmeltingFurnaceErrorException  如果 {@link SmeltingFurnace#chest} 不属于 {@link Container}
     */
    public void setChest(ItemStack[] itemStacks) throws SmeltingFurnaceErrorException{
        if (chest instanceof Container){
            Inventory inventory = ((Container) chest).getInventory();
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
    public static enum Level{
        CURRENCY("通用", "初级燃料"),
        ADVANCED("进阶", "高级燃料"),
        LEGEND("传说", "传说燃料"),
        CHAOS("混沌", "混沌燃料");
        private static Map<String, Level> map = new HashMap<>();
        static {
            for (Level value : values()) {
                map.put(value.getName(), value);
            }
        }
        private final String name;
        private final String fuel;
        Level(String name, String fuel){
            this.name = name;
            this.fuel = fuel;
        }

        public String getName() {
            return name;
        }

        public String getFuel() {
            return fuel;
        }
        public static Level getByName(String name){
            return map.get(name);
        }
    }
}
