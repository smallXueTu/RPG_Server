/**
 * Created with IntelliJ IDEA.
 * projectName: RPG
 * fileName: Request.java
 * packageName: cn.ltcraft.teleport
 * date: 2020-07-15 12:56
 *
 * @Auther: Angel、
 */
package cn.ltcraft.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * @Auther: Angel、
 * @Date: 2020/07/15/12:56
 * @Description:
 */

public class Request {
    private final Player player;
    private final Player transmitters;
    private final long time;
    public Request(Player player, Player transmitters){
        this.player = player;
        this.transmitters = transmitters;
        time = System.currentTimeMillis() / 1000;
    }

    /**
     * 获取创建时间
     * @return 请求创建的时候
     * 如果超过30秒视为过期
     */
    public long getTime() {
        return time;
    }

    /**
     * 获取传送者
     * @return 要被传送的玩家
     */
    public Player getTransmitters() {
        return transmitters;
    }

    /**
     * 获取玩家 也是这个请求要传送的坐标
     * @return 返回玩家对象
     */
    public Player getPlayer() {
        return player;
    }
}
