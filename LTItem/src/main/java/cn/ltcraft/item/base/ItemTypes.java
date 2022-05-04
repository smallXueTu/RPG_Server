package cn.ltcraft.item.base;


import java.util.HashMap;
import java.util.Map;

public enum ItemTypes {
    Melee("近战"),
    Armor("盔甲"),
    Gemstone("宝石"),
    Ornament("饰品"),
    Material("材料");
    private final String name;
    private static final Map<String, ItemTypes> BY_NAME = new HashMap();
    static {
        ItemTypes[] classes = values();
        for (ItemTypes aClass : classes) {
            BY_NAME.put(aClass.getName(), aClass);
        }
    }
    ItemTypes(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ItemTypes byName(String name){
        return BY_NAME.get(name);
    }
}
