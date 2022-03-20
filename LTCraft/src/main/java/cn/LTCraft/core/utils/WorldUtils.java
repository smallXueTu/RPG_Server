package cn.LTCraft.core.utils;


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
}
