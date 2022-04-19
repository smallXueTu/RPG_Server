package cn.ltcraft.love;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author 寻琴
 * @year 2022年04月19日20:34
 */
public class Love extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        getCommand("结婚").setExecutor(new Command());
    }
}
