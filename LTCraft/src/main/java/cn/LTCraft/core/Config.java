package cn.LTCraft.core;


import cn.LTCraft.core.entityClass.Exchange;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * 配置文件
 */
public class Config {
    private static Config instance = null;
    private final Main plugin;

    private final File configFile;
    private final File NPCClickFile;
    private final File MySQLInfoFile;
    private final File worldTitleFile;
    private final File itemsFile;
    private final File timerSpawnFile;
    private final File chestSpawnFile;
    private final File gateFile;
    private final File drawingFile;

    private YamlConfiguration configYaml;
    private YamlConfiguration NPCClickYaml;
    private YamlConfiguration MySQLInfoYaml;
    private YamlConfiguration itemsYaml;
    private YamlConfiguration worldTitleYaml;
    private io.lumine.utils.config.file.YamlConfiguration gateYaml;
    private io.lumine.utils.config.file.YamlConfiguration timerSpawnYaml;
    private io.lumine.utils.config.file.YamlConfiguration chestSpawnYaml;
    private io.lumine.utils.config.file.YamlConfiguration drawingYaml;
    private Config(Main plugin){
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "Config.yml");
        configYaml = YamlConfiguration.loadConfiguration(configFile);

        MySQLInfoFile = new File(plugin.getDataFolder(), "MySQLInfo.yml");
        MySQLInfoYaml = YamlConfiguration.loadConfiguration(MySQLInfoFile);

        NPCClickFile = new File(plugin.getDataFolder(), "NPCClick.yml");
        NPCClickYaml = YamlConfiguration.loadConfiguration(NPCClickFile);


        worldTitleFile = new File(plugin.getDataFolder(), "worldTitle.yml");
        worldTitleYaml = YamlConfiguration.loadConfiguration(worldTitleFile);

        gateFile = new File(plugin.getDataFolder(), "gates.yml");
        gateYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(gateFile);

        itemsFile = new File(plugin.getDataFolder(), "items.yml");
        itemsYaml = YamlConfiguration.loadConfiguration(itemsFile);

        timerSpawnFile = new File(plugin.getDataFolder(), "spawns/TimerSpawn.yml");
        timerSpawnYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(timerSpawnFile);

        chestSpawnFile = new File(plugin.getDataFolder(), "spawns/ChestSpawn.yml");
        chestSpawnYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(chestSpawnFile);

        drawingFile = new File(plugin.getDataFolder(), "drawing.yml");
        drawingYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(drawingFile);
    }

    public synchronized static Config getInstance(){
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
        return itemsYaml;
    }

    public io.lumine.utils.config.file.YamlConfiguration getTimerSpawnYaml() {
        return timerSpawnYaml;
    }

    public io.lumine.utils.config.file.YamlConfiguration getGateYaml() {
        return gateYaml;
    }

    public YamlConfiguration getWorldTitleYaml() {
        return worldTitleYaml;
    }

    public io.lumine.utils.config.file.YamlConfiguration getDrawingYaml() {
        return drawingYaml;
    }

    public io.lumine.utils.config.file.YamlConfiguration getChestSpawnYaml() {
        return chestSpawnYaml;
    }

    public YamlConfiguration getConfigYaml() {
        return configYaml;
    }

    /**
     * 重新加载
     */
    public void reload(){
        configYaml = YamlConfiguration.loadConfiguration(configFile);
        MySQLInfoYaml = YamlConfiguration.loadConfiguration(MySQLInfoFile);
        NPCClickYaml = YamlConfiguration.loadConfiguration(NPCClickFile);
        itemsYaml = YamlConfiguration.loadConfiguration(itemsFile);
        worldTitleYaml = YamlConfiguration.loadConfiguration(worldTitleFile);
        timerSpawnYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(timerSpawnFile);
        chestSpawnYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(chestSpawnFile);
        gateYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(gateFile);
        drawingYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(drawingFile);
    }

    /**
     * 保存
     */
    public void save(){
        try {
            timerSpawnYaml.save(timerSpawnFile);
            gateYaml.save(gateFile);
            chestSpawnYaml.save(chestSpawnFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static class GlobalConfig{
        /**
         * 发包范围
         */
        public static final int sendPacketRange = 32;
    }
}
