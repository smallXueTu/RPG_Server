/**
 * Created with IntelliJ IDEA.
 * projectName: RPG
 * fileName: SQLQueue.java
 * packageName: cn.LTCraft.core.dataBase
 * date: 2020-07-12 17:38
 *
 * @Auther: Angel、
 */
package cn.LTCraft.core.dataBase;

import cn.LTCraft.core.callback.SQLCallBack;
import org.apache.ibatis.session.SqlSession;

/**
 * Created with IntelliJ IDEA.
 * @Auther: Angel、
 * @Date: 2020/07/12/17:38
 * @Description:
 */

public class SQLQueue {

    public enum STATUS{//状态
        DONE,//完成
        FAIL,//失败
        WAITING,//等待
    }
    private STATUS status = STATUS.WAITING;
    private Object result = null;
    private final SQLQueueChunk queueChunk;
    private SqlSession sqlSession = null;
    private SQLCallBack callBack = null;
    private boolean close = true;
    private boolean submit = true;

    /**
     * 新建查询，此查询无返回值，不推荐使用
     * @param sqlSession SqlSession
     * @param queueChunk 查询块
     */
    public SQLQueue(SqlSession sqlSession, SQLQueueChunk queueChunk){
        this.sqlSession = sqlSession;
        this.queueChunk = queueChunk;
    }

    /**
     * 新建查询，此查询无返回值，不推荐使用
     * @param sqlSession SqlSession
     * @param queueChunk 查询块
     * @param close 是否在查询完成后关闭SqlSession
     * @param submit 查询完成后提交
     */
    public SQLQueue(SqlSession sqlSession, SQLQueueChunk queueChunk, boolean close, boolean submit){
        this.sqlSession = sqlSession;
        this.queueChunk = queueChunk;
        this.close = close;
        this.submit = submit;
    }

    /**
     * 新建查询，此查询无返回值，不推荐使用
     * @param queueChunk 查询块
     */
    public SQLQueue(SQLQueueChunk queueChunk){
        this.queueChunk = queueChunk;
    }

    /**
     * 新建查询
     * @param queueChunk 查询块
     * @param sqlCallBack 查询完成的回调函数
     * @param close 是否在查询完成后关闭SqlSession
     * @param submit 查询完成后提交
     */
    public SQLQueue(SQLQueueChunk queueChunk, SQLCallBack sqlCallBack, boolean close, boolean submit){
        this.queueChunk = queueChunk;
        callBack = sqlCallBack;
        this.close = close;
        this.submit = submit;
    }

    /**
     * 新建查询
     * @param queueChunk 查询块
     * @param sqlCallBack 查询完成的回调函数
     */
    public SQLQueue(SQLQueueChunk queueChunk, SQLCallBack sqlCallBack){
        this.queueChunk = queueChunk;
        callBack = sqlCallBack;
    }

    /**
     * 新建查询 如果sqlSession != null 会在查询完成后关闭SqlSession
     * @param sqlSession SqlSession
     * @param queueChunk 查询块
     * @param sqlCallBack 查询完成的回调函数
     */
    public SQLQueue(SqlSession sqlSession, SQLQueueChunk queueChunk, SQLCallBack sqlCallBack){
        this.sqlSession = sqlSession;
        this.queueChunk = queueChunk;
        callBack = sqlCallBack;
    }
    /**
     * 获取回调
     * @return 如果没设置 返回null
     */
    public SQLCallBack getCallBack() {
        return callBack;
    }

    /**
     * 获取查询块
     * @return 查询块
     */
    public SQLQueueChunk getQueueChunk() {
        return queueChunk;
    }

    public void setSubmit(boolean submit) {
        this.submit = submit;
    }

    public boolean isSubmit() {
        return submit;
    }

    /**
     * @return getSqlSession
     */
    public SqlSession getSqlSession() {
        return sqlSession;
    }

    /**
     * 获取返回结果
     * @param <T> 返回结果
     * @return 返回结果
     */
    public <T> T getResult() {
        if (result == null)return null;
        return (T) result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * @return 当前队列的状态
     */
    public STATUS getStatus() {
        return status;
    }

    /**
     * @param status 新状态
     */
    public void setStatus(STATUS status) {
        this.status = status;
    }

    public boolean isClose() {
        return close;
    }

    /**
     * 设置回调方法
     * @param callBack 需要回调的方法
     */
    public void setCallBack(SQLCallBack callBack) {
        this.callBack = callBack;
    }

    public interface SQLQueueChunk{
        Object queue();
    }
}
