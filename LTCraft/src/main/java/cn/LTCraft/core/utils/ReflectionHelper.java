package cn.LTCraft.core.utils;

import net.minecraft.server.v1_12_R1.EntityItem;

import java.lang.reflect.Field;

public class ReflectionHelper {

    public static Field findField(Class<?> clazz, String fieldName)
    {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f;
        } catch (Exception ignored) {

        }
        return null;
    }
    public static <T, E> T getPrivateValue(Class<? extends E> classToAccess, EntityItem instance, String fieldName)
    {
        try {
            return (T) findField(classToAccess, fieldName).get(instance);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
