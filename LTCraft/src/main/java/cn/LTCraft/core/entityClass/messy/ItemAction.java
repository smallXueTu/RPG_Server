package cn.LTCraft.core.entityClass.messy;

/**
 * Created by Angel、 on 2022/4/19 13:37
 * 物品动作，主要包含手持动作，主动攻击动作等
 * 这个类的目的是为了解决运行一些异步任务，方便执行一些不可异步执行的代码块
 * 推荐 {@link Runnable}
 */
@Deprecated()
public interface ItemAction {
    void run();
}
