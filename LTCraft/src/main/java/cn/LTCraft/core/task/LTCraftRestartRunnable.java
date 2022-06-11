package cn.LTCraft.core.task;

import cn.LTCraft.core.Main;
import cn.LTCraft.core.utils.PlayerUtils;
import org.bukkit.Bukkit;

public class LTCraftRestartRunnable implements Runnable{
    private int time = 0;
    private Main plugin = null;
    private final int id;

    /**
     *
     */
    public LTCraftRestartRunnable(int time){
        this.time = time;
        plugin = Main.getInstance();
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
    }

    /**
     *
     */
    @Override
    public void run() {
        if (time == 60){
            plugin.getServer().broadcastMessage("§c§lLTCraft将在60秒后进行重启!");
        }else if (time == 30){
            plugin.getServer().broadcastMessage("§c§lLTCraft将在30秒后进行重启!");
        }else if (time <= 10 && time > 0){
            PlayerUtils.sendActionMessage("§c§lLTCraft将在" + time + "秒后进行重启!");
        }else if (time == 0){
            plugin.getServer().shutdown();
        }
        time--;
    }
    public void cancel(){
        Bukkit.getScheduler().cancelTask(id);
    }
}
