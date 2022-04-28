package cn.LTCraft.core.game.more.tickEntity;

/**
 * tick的实体 每getTickRate() * 0.05秒调用一次
 * 在doTick(int)返回false的时候停止调用
 * Created by Angel、 on 2022/4/26 14:51
 */
public interface TickEntity {
    /**
     *
     * @param tick 从服务器开启到现在经过的游戏时刻
     * @return 返回false停止调用
     */
    boolean doTick(long tick);

    /**
     * tick率
     * @return 1 = 1游戏时刻
     */
    default int getTickRate(){
        return 1;
    }

    /**
     * @return 如果是true 则在副线程中调用 {@link TickEntity#doTick(long)}
     */
    default boolean isAsync(){
        return false;
    }
}
