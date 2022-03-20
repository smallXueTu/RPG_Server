package cn.LTCraft.core.utils;

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
}