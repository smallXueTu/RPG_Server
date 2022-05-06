package cn.ltcraft.item.base;

import cn.LTCraft.core.entityClass.RandomValue;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * json属性 可将此属性转为json存入物品nbt中
 */
public class JsonAttribute extends AbstractAttribute{
    public JsonAttribute(Map<String, Object> json){
        Class<AbstractAttribute> abstractAttributeClass = AbstractAttribute.class;
        Field[] declaredFields = abstractAttributeClass.getDeclaredFields();
        try {
            for (Field declaredField : declaredFields) {
                if (json.containsKey(declaredField.getName())) {
                    Object value = json.get(declaredField.getName());
                    if (declaredField.getName().equals("PVEDamage") || declaredField.getName().equals("PVPDamage")){
                        value = new RandomValue(Double.parseDouble(value.toString()));
                    }
                    declaredField.set(this, value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
