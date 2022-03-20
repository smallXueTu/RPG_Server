/**
 * Created with IntelliJ IDEA.
 * projectName: RPG
 * fileName: SQLManage.java
 * packageName: cn.LTCraft.core.dataBase
 * date: 2020-07-12 15:50
 *
 * @Auther: Angel、
 */
package cn.LTCraft.core.dataBase;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * @Auther: Angel、
 * @Date: 2020/07/12/15:50
 * @Description: SQL管理类
 */

public class SQLManage extends Thread{
    /**
     * SQL队列
     */
    private final LinkedTransferQueue<SQLQueue> queue = new LinkedTransferQueue<SQLQueue>();
    private final ConcurrentLinkedQueue<SQLQueue> doneQueue = new ConcurrentLinkedQueue<SQLQueue>();

    private boolean shutdown = false;
    private SQLServer sqlServer = null;
    public SQLManage(SQLServer SQLServer){
        sqlServer = SQLServer;
        start();//Go Go!!
    }

    /**
     * @return 返回数据库服务器
     */
    public SQLServer getSqlServer() {
        return sqlServer;
    }

    /**
     * @return 队列
     */
    public LinkedTransferQueue<SQLQueue> getQueue() {
        return queue;
    }

    /**
     * @return 已经完成的队列 等待主线程取用
     */
    public ConcurrentLinkedQueue<SQLQueue> getDoneQueue() {
        return doneQueue;
    }

    /**
     * 关闭
     */
    public void setShutdown() {
        this.shutdown = true;
    }

    /**
     * Run！！
     */
    @Override
    public void run() {
        while (!shutdown || queue.size() > 0){
            //为了方便debug的编译和重新加载
            doRun();
        }
    }
    public void doRun(){
        SQLQueue sqlQueue = null;
        try {
            sqlQueue = queue.poll(50, TimeUnit.MILLISECONDS);
            if (sqlQueue == null)return;
            Object queue = sqlQueue.getQueueChunk().queue();
            sqlQueue.setResult(queue);
            sqlQueue.setStatus(SQLQueue.STATUS.DONE);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (sqlQueue != null && sqlQueue.getSqlSession() != null) {
                if (sqlQueue.isSubmit()) {
                    sqlQueue.getSqlSession().commit();
                }
                if (sqlQueue.isClose()) {
                    sqlQueue.getSqlSession().close();
                }
            }
        }
        doneQueue.add(sqlQueue);
    }
}
