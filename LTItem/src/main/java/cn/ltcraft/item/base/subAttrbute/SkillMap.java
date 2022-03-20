package cn.ltcraft.item.base.subAttrbute;

import cn.LTCraft.core.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SkillMap<K,V> extends HashMap<K,V> {
    public SkillMap(Map<? extends K, ? extends V> m) {
        super(m);
    }
    public SkillMap(){
        super();
    }
    @Override
    public String toString() {
        if (size() <= 0){
            return "无";
        }else if (size() == 1){
            Entry<K, V> next = entrySet().iterator().next();
            String[] split = next.getKey().toString().split("\\|");
            if (split.length <= 1)return "无";
            return split[1] + " " + Utils.formatNumber((Double) next.getValue()) + "%";
        }else {
            StringBuilder str = new StringBuilder("");
            for (Iterator<Entry<K, V>>  iterator = entrySet().iterator(); iterator.hasNext();){
                Entry<K, V> next = iterator.next();
                String[] split = next.getKey().toString().split("\\|");
                if (split.length <= 1)continue;
                str.append(split[1]).append(" ").append(Utils.formatNumber((Double) next.getValue())).append("%");
                if (iterator.hasNext())str.append("\n§b:");
            }
            return str.toString();
        }
    }
}
