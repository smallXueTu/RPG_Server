package cn.ltcraft.item.base.subAttrbute;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PotionMap<K,V> extends HashMap<K,V> {

    public PotionMap(Map<? extends K, ? extends V> m) {
        super(m);
    }
    public PotionMap(){
        super();
    }
    @Override
    public String toString() {
        if (size() <= 0){
            return "无";
        }else if (size() == 1){
            return values().iterator().next().toString();
        }else {
            StringBuilder str = new StringBuilder("");
            for (Iterator<V> iterator = values().iterator();iterator.hasNext();){
                V next = iterator.next();
                str.append(next.toString());
                if (iterator.hasNext())str.append("\n§b触发:");
            }
            return str.toString();
        }
    }
    public String toMapString(){
        return super.toString();
    }
}
