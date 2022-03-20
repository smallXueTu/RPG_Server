package cn.LTCraft.core.task;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.Utils;
import org.bukkit.entity.Player;

import java.util.*;

public class ClientCheckTask {
    private int id;
    private String result;
    private String tag;
    private Player player;
    private long time;
    private boolean complete = false;
    public ClientCheckTask(int id, String tag, Player player){
        this.id = id;
        this.tag = tag;
        this.player = player;
        time = Utils.getSecondTime();
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

    public static void checkTask(){
        LinkedHashMap<Integer, ClientCheckTask> tacks = Main.getInstance().getTacks();
        Iterator iterable = tacks.entrySet().iterator();
        if (iterable.hasNext()) {
            do {
                Map.Entry entry = (Map.Entry) iterable.next();
                ClientCheckTask clientCheckTask = (ClientCheckTask) entry.getValue();
                if (clientCheckTask.isComplete()) {
                    if (Utils.getSecondTime() - clientCheckTask.getTime() > 30) {
                        Main.getInstance().getLogger().warning("玩家" + clientCheckTask.getPlayer().getName() + "延迟较高。");
                    }
                    iterable.remove();
                    continue;
                }
                if (!clientCheckTask.getPlayer().isOnline()) {
                    iterable.remove();
                    continue;
                }
                if (Utils.getSecondTime() - clientCheckTask.getTime() > 60) {
                    Main.getInstance().getLogger().warning("玩家" + clientCheckTask.getPlayer().getName() + " " + clientCheckTask.getTag() + "效验失败 该玩家可能篡改客户端！");
                    iterable.remove();
                }
            } while (iterable.hasNext());
        }
    }
}
