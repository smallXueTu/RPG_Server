package cn.LTCraft.core.utils;

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
    public static <T, E> T getPrivateValue(Class<? extends E> classToAccess, Object instance, String fieldName)
    {
        try {
            return (T) findField(classToAccess, fieldName).get(instance);
        } catch (Exception e) {
            return null;
        }
    }
    public static <T, E> boolean setPrivateValue(Class<? extends E> classToAccess, Object instance, String fieldName, Object value)
    {
        try {
            findField(classToAccess, fieldName).set(instance, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
