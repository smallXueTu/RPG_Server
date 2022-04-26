package cn.LTCraft.core.game;

import cn.LTCraft.core.game.more.FakeBlock;
import cn.LTCraft.core.utils.WorldUtils;
import com.google.common.primitives.Ints;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 熔炼炉
 * Created by Angel、 on 2022/4/25 20:39
 */
public class SmeltingFurnace {
    private final Player player;
    private final Location location;
    public SmeltingFurnace(Player player, Location location){
        this.location = location;
        this.player = player;
        init();
    }
    public void init(){

    }
    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }
    public void doTick(int tick){

    }

    private static final List<Integer> sides = Ints.asList(4, 5, 2, 3);
    private static final List<Integer> faces = Ints.asList(0, 0, 3, 2, 1, 0);
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
        World world = location.getWorld();
        Block block;
        WorldUtils.SIDE direction = WorldUtils.getForDirection(location, itemFrame);
        //先检查下面是否为岩浆
        if (world.getBlockAt(itemFrame.add(0, -1, 0)).getType() != Material.LAVA){
            blocks.add(new FakeBlock(itemFrame.add(0, -1, 0), Material.LAVA));
        }
        //检查上方是否为箱子 并且朝向入口
        WorldUtils.SIDE pSide = WorldUtils.getForDirection(itemFrame, location);//物品展示框贴的面
        WorldUtils.SIDE entrance = WorldUtils.SIDE.byId(pSide.getId() % 2 == 0 ? pSide.getId() + 1 : pSide.getId() -1);//如果是偶数 应对方向应该+1 如果是奇数 应该-1
        if ((block = WorldUtils.getSideBlock(location, WorldUtils.SIDE.UP)).getType() != Material.CHEST || block.getData() != entrance.getId()){
            blocks.add(new FakeBlock(itemFrame.add(0, -1, 0), Material.CHEST, (byte) entrance.getId()));
        }else{
            if (faces.get(block.getData()) != direction.getId()){//如果是箱子检查朝向是否为入口
                blocks.add(new FakeBlock(itemFrame.add(0, -1, 0), Material.CHEST, (byte) entrance.getId()));
            }
        }
        Block tmpBlock;
        Block furnace = null;
        Block anvil = null;
        Block bottom;
        //四个面 分别为 2 3 4 5(北 南 西 东)     1和2位上下
        for (int side = 2; side < 6; side++){
            if (side != entrance.getId()){
                furnace = WorldUtils.getSideBlock(location, WorldUtils.SIDE.byId(side), 4);//应该为熔炉
                anvil = WorldUtils.getSideBlock(furnace.getLocation(), WorldUtils.SIDE.UP);// 应该为铁砧
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
        }
        return blocks.toArray(new FakeBlock[]{});
    }
    public static void tickAll(){

    }
}
