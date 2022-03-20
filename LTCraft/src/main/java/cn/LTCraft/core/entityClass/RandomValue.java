package cn.LTCraft.core.entityClass;

import cn.LTCraft.core.utils.Utils;

public class RandomValue {
    private final double min;
    private final double max;
    private final int diff;
    public RandomValue(double value){
        min = value;
        max = value;
        diff = (int) Math.round(max - min);
    }
    public RandomValue(String value){
        min = Double.parseDouble(value.split("-")[0]);
        if (value.split("-").length >= 2) {
            max = Double.parseDouble(value.split("-")[1]);
        }else {
            max = min;
        }
        diff = (int) Math.round(max - min);
    }
    public RandomValue(double max, double min){
        this.max = max;
        this.min = min;
        diff = (int) Math.round(max - min);
    }

    public double getValue() {
        if (diff == 0)return min;//随便一个
        double damage = Utils.getRandom().nextInt(diff);
        return damage + min;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public boolean isRandom(){
        return diff == 0;
    }
    @Override
    public String toString() {
        if (isRandom())return min + "";
        return min + "-" + max;
    }
}
