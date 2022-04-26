package cn.LTCraft.core.task;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.game.more.tickEntity.TickEntity;
import cn.LTCraft.core.utils.Utils;
import org.bukkit.entity.Player;

import java.util.*;

public class ClientCheckTask implements TickEntity {
    private final int id;
    private String result;
    private final String tag;
    private final Player player;
    private final long time;
    private boolean complete = false;
    public ClientCheckTask(int id, String tag, Player player){
        this.id = id;
        this.tag = tag;
        this.player = player;
        time = Utils.getSecondTime();
        GlobalRefresh.addTickEntity(this);
    }

    public String getTag() {
        return tag;
    }

    public Player getPlayer() {
        return player;
    }

    public long getTime() {
        return time;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public int getId() {
        return id;
    }

    public boolean doTick(long tick){
        LinkedHashMap<Integer, ClientCheckTask> tacks = Main.getInstance().getTacks();
        synchronized (tacks) {
            Iterator<Map.Entry<Integer, ClientCheckTask>> iterator = tacks.entrySet().iterator();
            boolean remove;
            while (iterator.hasNext()) {
                remove = false;
                Map.Entry<Integer, ClientCheckTask> entry = iterator.next();
                ClientCheckTask clientCheckTask = entry.getValue();
                if (clientCheckTask.isComplete()) {
                    if (Utils.getSecondTime() - clientCheckTask.getTime() > 30) {
                        Main.getInstance().getLogger().warning("玩家" + clientCheckTask.getPlayer().getName() + "延迟较高。");
                    }
                    remove = true;
                } else if (!clientCheckTask.getPlayer().isOnline()) {
                    remove = true;
                } else if (Utils.getSecondTime() - clientCheckTask.getTime() > 60) {
                    Main.getInstance().getLogger().warning("玩家" + clientCheckTask.getPlayer().getName() + " " + clientCheckTask.getTag() + "效验失败 该玩家可能篡改客户端！");
                    remove = true;
                }
                if (remove) iterator.remove();
            }
        }
        return true;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public int getTickRate() {
        return 20;
    }
}
