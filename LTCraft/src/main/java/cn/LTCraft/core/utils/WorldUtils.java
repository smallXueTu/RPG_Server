package cn.LTCraft.core.utils;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

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

    public static void main(String[] args) {
        int xx = 0;
        int zz = 0;
        for (int i = 0; i < 100; i++) {
            double x = (Utils.getRandom().nextInt((int) (5 * 200)) - 5d * 100) / 100;
            double z = (Utils.getRandom().nextInt((int) (5 * 200)) - 5d * 100) / 100;
            System.out.println("x:" + (xx + x) + ", z:" + (zz + z));
        }
    }
}
