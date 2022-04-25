package cn.LTCraft.core.utils;


import cn.ltcraft.item.base.ItemTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class WorldUtils {
    /**
     * 获取最低的可以站人的方块
     * @param block 方块
     * @return 方块
     */
    public static Block getDownBlock(Block block){
        Block b = null, bb = null, bbb = null;
        World world = block.getWorld();
        for (int y = block.getLocation().getBlockY(); y > 0; y--) {
            b = world.getBlockAt(block.getLocation().getBlockX(), y, block.getLocation().getBlockZ());
            if (b.getType() == Material.AIR){
                bb = world.getBlockAt(block.getLocation().getBlockX(), y - 1, block.getLocation().getBlockZ());
                bbb = world.getBlockAt(block.getLocation().getBlockX(), y + 1, block.getLocation().getBlockZ());
                if (bb.getType().isSolid() && bbb.getType() == Material.AIR){
                    break;
                }
            }
        }
        if (b == null)return null;
        return b.getType() == Material.AIR?b:null;
    }

    /**
     * 范围随机
     * @param location 要随机的中心
     * @param range 随机的半径
     * @return 随机后的坐标
     * 2022年4月10日01:27:34 睡不着耶
     */
    public static Location rangeLocation(Location location, double range){
        double x = (Utils.getRandom().nextInt((int) (range * 200)) - range * 100) / 100;
        double z = (Utils.getRandom().nextInt((int) (range * 200)) - range * 100) / 100;
        return location.add(x, 0, z);
    }

    /**
     * 获取制定面的坐标
     * @param location 坐标
     * @param side 面
     * @return 坐标
     */
    public static Location getSide(Location location, SIDE side){
        return getSide(location, side, 1);
    }

    /**
     * @param side 步长
     */
    public static Location getSide(Location location, SIDE side, int step){
        switch(side){
            case DOWN://下
                return location.add(0, - step, 0);
            case UP://上
                return location.add(0, step, 0);
            case NORTH:
                return location.add(0, 0, - step);
            case SOUTH:
                return location.add(0, 0, step);
            case WEST:
                return location.add(- step, 0, 0);
            case EAST:
                return location.add(step, 0, 0);
            default:
                return location;
        }
    }

    /**
     * 方块面
     */
    enum SIDE{
        DOWN(0),//下 y - 1
        UP(1),//上 y + 1
        NORTH(2),//北 z - 1
        SOUTH(3),//男 z + 1
        WEST(4),//西 x - 1
        EAST(5);//东 x + 1
        private static final Map<Integer, SIDE> BY_NAME = new HashMap<>();
        static {
            SIDE[] classes = values();
            for (SIDE aClass : classes) {
                BY_NAME.put(aClass.getId(), aClass);
            }
        }
        private final int id;
        SIDE(int id){
            this.id = id;
        }

        public int getId() {
            return id;
        }
        public static SIDE byName(Integer id){
            return BY_NAME.get(id);
        }
    }
}
