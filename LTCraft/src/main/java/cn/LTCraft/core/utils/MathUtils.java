package cn.LTCraft.core.utils;

import org.bukkit.Location;

public class MathUtils {
    /**
     * 几率
     * @return 如果通过
     */
    public static boolean ifAdopt(double p){
        return Utils.getRandom().nextInt(101) < p * 100;
    }
    /**
     * 获得免伤百分比
     * @param armorValue 护甲值
     * @return 免伤百分比 0.00 - 1.00
     * 500护甲 = 71.14%免伤
     * 200护甲 = 50%免伤
     * 100护甲 = 33.33%免伤
     * 50护甲 = 20%免伤
     * 10护甲 = 4%免伤
     */
    public static double getInjuryFreePercentage(int armorValue){
        return armorValue / (armorValue + 200d);
    }

    /**
     * 坐标Hash
     * @return Hash
     */
    public static int blockHash(int x, int y, int z)
    {
        return ((x & 0xFFFFFFF) << 4) | ((y & 0xFF) << 28) | (z & 0xFFFFFFF);
    }
    public static int blockHash(Location location)
    {
        return (((int)location.getX() & 0xFFFFFFF) << 4) | (((int)location.getY() & 0xFF) << 28) | ((int)location.getZ() & 0xFFFFFFF);
    }

    /**
     * 获取最小的yaw
     * @param a1
     * @param a2
     * @return
     */
    public static double getMinAngle(double a1, double a2){
        double max = Math.max(a1, a2);
        double min = Math.min(a1, a2);
        return max - min > 180 ? min + 360 - max:max - min;
    }

    /**
     * 获取两个坐标的yaw 即点1对于点2而的
     * @param location1
     * @param location2
     * @return
     */
    public static double getYaw(Location location1, Location location2){
        double x = location1.getX() - location2.getX();
        double z = location1.getZ() - location2.getZ();
        double diff = Math.abs(x) + Math.abs(z);
        return (-Math.atan2(x / diff, z / diff) * 180 / Math.PI + 360) % 360;
    }
}