package cn.LTCraft.core.utils;

import com.comphenix.net.sf.cglib.proxy.Enhancer;
import com.comphenix.net.sf.cglib.proxy.MethodInterceptor;
import com.comphenix.net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 由于MythicMobs中一个类的掉落物只允许有一个，他Map的Key为class
 * 所以这用于代理实现{@link io.lumine.xikage.mythicmobs.drops.Drop}
 * @see io.lumine.xikage.mythicmobs.drops.LootBag#lootTableIntangible
 * Created by Angel、 on 2022/6/22 15:00
 */
public class ProxyUtils {
    /**
     * 获取一个继承的代理类
     * @param superclass 返回的子类
     * @param argumentTypes 方法的参数类型
     * @param arguments 构造方法的参数
     * @return 必定是 superclass 的子类
     * @param <T> 任何类型
     */
    public static <T> T getInheritProxy(Class<T> superclass, Class<?>[] argumentTypes, Object[] arguments){
        Enhancer enhancer = new Enhancer();
        enhancer.setUseCache(false);
        enhancer.setSuperclass(superclass);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> proxy.invokeSuper(obj, args));
        return (T) enhancer.create(argumentTypes, arguments);
    }
}
