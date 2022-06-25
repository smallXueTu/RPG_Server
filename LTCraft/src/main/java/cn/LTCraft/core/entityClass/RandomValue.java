package cn.LTCraft.core.entityClass;

import cn.LTCraft.core.utils.Utils;

import java.util.Objects;

/**
 * 可变随机值
 * 提供一个最大值和最小值
 * 或固定值 在{@link this#getValue()}会返回一个可变或固定值
 */
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
    public RandomValue(double min, double max){
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RandomValue that = (RandomValue) o;
        return Double.compare(that.min, min) == 0 && Double.compare(that.max, max) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }
}
