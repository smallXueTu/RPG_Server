/**
 * Created with IntelliJ IDEA.
 * projectName: RPG
 * fileName: Home.java
 * packageName: cn.ltcraft.teleport
 * date: 2020-07-17 10:02
 *
 * @Auther: Angel、
 */
package cn.ltcraft.teleport;

import org.bukkit.Location;
import org.bukkit.World;

import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * @Auther: Angel、
 * @Date: 2020/07/17/10:02
 * @Description:
 */

public class Home {
    private final double x;
    private final double y;
    private final double z;
    private final String worldName;
    private final World world;
    private final Location location;

    /**
     * 构造函数
     * @param x X
     * @param y Y
     * @param z Z
     * @param world 世界对象
     */
    public Home(double x, double y, double z, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.worldName = world.getName();
        location = new Location(world, x, y, z);
    }

    /**
     * 构造函数
     * @param location 坐标
     */
    public Home(Location location){
        this(location.getX(), location.getY(), location.getZ(), location.getWorld());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * 返回 Map 用于保存
     * @return Map
     */
    public HashMap<String, String> tpMap(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("x", getX() + "");
        map.put("y", getY() + "");
        map.put("z", getZ() + "");
        map.put("world", getWorldName());
        return map;
    }
}
