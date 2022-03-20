package cn.LTCraft.core.task;

import cn.LTCraft.core.Main;

public class LTCraftRestartRunnable implements Runnable{
    private int time = 0;
    private Main plugins = null;

    /**
     *
     * @param time
     */
    public LTCraftRestartRunnable(int time, Main plugins){
        this.time = time;
        this.plugins = plugins;
    }

    /**
     *
     */
    @Override
    public void run() {
        if (time == 60){
            plugins.getServer().getScheduler().scheduleSyncDelayedTask(plugins, new LTCraftRestartRunnable(30, plugins), 20*30);
            plugins.getServer().broadcastMessage("&l&cLTCraft将在60秒后进行重启!");
        }
        if (time == 30){
            plugins.getServer().getScheduler().scheduleSyncDelayedTask(plugins, new LTCraftRestartRunnable(10, plugins), 20*20);
            plugins.getServer().broadcastMessage("&l&cLTCraft将在30秒后进行重启!");
        }
        if (time == 10){
            plugins.getServer().getScheduler().scheduleSyncDelayedTask(plugins, new LTCraftRestartRunnable(0, plugins), 20*10);
            plugins.getServer().broadcastMessage("&l&cLTCraft将在10秒后进行重启!");
        }
        if (time == 0){
            plugins.getServer().shutdown();
        }
    }
}
