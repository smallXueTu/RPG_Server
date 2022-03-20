package cn.ltcraft.item.base.interfaces;

import org.bukkit.configuration.MemoryConfiguration;

import java.util.List;

public interface ConfigurableLTItem extends LTItem {
    /**
     * 获取说明
     * @return 说明
     */
    List<String> getLore();

    /**
     * 获取配置文件
     * @return 配文件
     */
    MemoryConfiguration getConfig();
}
