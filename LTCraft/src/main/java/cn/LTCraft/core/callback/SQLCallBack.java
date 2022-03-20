package cn.LTCraft.core.callback;

import cn.LTCraft.core.dataBase.SQLQueue;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Angel、
 * @Date: 2020/07/13/16:22
 * @Description: 所有回调必须要实现这个方法
 */
public interface SQLCallBack {
    /**
     *  查询完成后调用的方法 处理自己的逻辑
     */
    void onComplete(SQLQueue sqlQueue);
}
