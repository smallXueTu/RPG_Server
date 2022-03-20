package cn.LTCraft.core;


import cn.LTCraft.core.entityClass.Exchange;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * 配置文件
 */
public class Config {
    private static Config instance = null;
    private Main plugin;
    private File NPCClickFile;
    private File MySQLInfoFile;
    private File ItemsFile;
    private YamlConfiguration NPCClickYaml;
    private YamlConfiguration MySQLInfoYaml;
    private YamlConfiguration ItemsYaml;
    private Config(Main plugin){
        this.plugin = plugin;
        MySQLInfoFile = new File(plugin.getDataFolder(), "MySQLInfo.yml");
        MySQLInfoYaml = YamlConfiguration.loadConfiguration(MySQLInfoFile);
        NPCClickFile = new File(plugin.getDataFolder(), "NPCClick.yml");
        NPCClickYaml = YamlConfiguration.loadConfiguration(NPCClickFile);
        ItemsFile = new File(plugin.getDataFolder(), "items.yml");
        ItemsYaml = YamlConfiguration.loadConfiguration(ItemsFile);
    }
    public static Config getInstance(){
        if (instance == null){
            instance = new Config(Main.getInstance());
            Exchange.config = instance;
        }
        return instance;
    }
    public YamlConfiguration getNPCClickYaml() {
        return NPCClickYaml;
    }

    public YamlConfiguration getMySQLInfoYaml() {
        return MySQLInfoYaml;
    }

    public YamlConfiguration getItemsYaml() {
        return ItemsYaml;
    }

    /**
     * 重新加载
     */
    public void reload(){
        MySQLInfoYaml = YamlConfiguration.loadConfiguration(MySQLInfoFile);
        NPCClickYaml = YamlConfiguration.loadConfiguration(NPCClickFile);
        ItemsYaml = YamlConfiguration.loadConfiguration(ItemsFile);
    }
}
