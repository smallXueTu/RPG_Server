package cn.LTCraft.core.entityClass;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by Angel、 on 2022/4/16 16:23
 * 附加值 可百分比和固定值
 */
public class Additional implements Cloneable{
    private List<Value> additional = new ArrayList<>();

    /**
     * 获取 附加值的Map
     * @return map
     */
    public List<Value> getAdditional() {
        Collections.sort(additional);
        return additional;
    }
    public double getValue(double finalValue){
        for (Value value : additional) {
            if (value.isPercentage()){
                finalValue += finalValue * value.getValue();
            }else {
                finalValue += value.getValue();
            }
        }
        return finalValue;
    }

    /**
     * 增加一个附加值
     * @param value 附加值
     */
    public void addAdditional(Value value){
        additional.add(value);
    }
    public void addAdditional(Additional additional){
        additional.getAdditional().forEach(this::addAdditional);
    }

    /**
     * 移除一个附加值
     * @param value 附加值
     */
    public void removeAdditional(Value value){
        additional.remove(value);
    }
    public void removeAdditional(Additional additional){
        additional.getAdditional().forEach(this::removeAdditional);
    }

    @Override
    public Additional clone() {
        Additional a = null;
        try {
            a = (Additional) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        a.additional = new ArrayList<>(a.additional);
        return a;
    }

    @Override
    public String toString() {
        return Joiner.on("+").join(additional);
    }

    static class Value implements Comparable {
        /**
         * 附加值 可能是百分比
         */
        private double value;
        /**
         * 如果是百分比那一定是true
         */
        private boolean percentage = false;
        public Value(String value){
            if (value.endsWith("%")){
                percentage = true;
                this.value = Double.parseDouble(value.substring(0, value.length() -1));
            }else {
                this.value = Double.parseDouble(value);
            }
        }

        public Value(double value){
            this.value = value;
        }

        public Value(double value, boolean percentage){
            this.value = value;
            this.percentage = percentage;
        }

        public double getValue(double value){
            if (percentage) {
                return value + value * value;
            }else {
                return value;
            }
        }

        public double getValue() {
            return value;
        }

        public boolean isPercentage() {
            return percentage;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public void setPercentage(boolean percentage) {
            this.percentage = percentage;
        }

        @Override
        public String toString() {
            return percentage?value + "%" : value + "";
        }
        @Override
        public int compareTo(@NotNull Object o) {
            return this.hashCode() - o.hashCode();
        }

        @Override
        public int hashCode() {
            return (int) ((percentage?0-value:value) * 100);
        }
    }
}
