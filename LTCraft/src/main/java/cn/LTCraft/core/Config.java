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

    private final File NPCClickFile;
    private final File MySQLInfoFile;
    private final File worldTitleFile;
    private final File itemsFile;
    private final File spawnFile;
    private final File gateFile;
    private final File drawingFile;

    private YamlConfiguration NPCClickYaml;
    private YamlConfiguration MySQLInfoYaml;
    private YamlConfiguration itemsYaml;
    private YamlConfiguration worldTitleYaml;
    private io.lumine.utils.config.file.YamlConfiguration gateYaml;
    private io.lumine.utils.config.file.YamlConfiguration spawnYaml;
    private io.lumine.utils.config.file.YamlConfiguration drawingYaml;
    private Config(Main plugin){
        this.plugin = plugin;
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

        spawnFile = new File(plugin.getDataFolder(), "spawn.yml");
        spawnYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(spawnFile);

        drawingFile = new File(plugin.getDataFolder(), "drawingFile.yml");
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

    public io.lumine.utils.config.file.YamlConfiguration getSpawnYaml() {
        return spawnYaml;
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

    /**
     * 重新加载
     */
    public void reload(){
        MySQLInfoYaml = YamlConfiguration.loadConfiguration(MySQLInfoFile);
        NPCClickYaml = YamlConfiguration.loadConfiguration(NPCClickFile);
        itemsYaml = YamlConfiguration.loadConfiguration(itemsFile);
        worldTitleYaml = YamlConfiguration.loadConfiguration(worldTitleFile);
        spawnYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(spawnFile);
        gateYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(gateFile);
        drawingYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(drawingFile);
    }

    /**
     * 保存
     */
    public void save(){
        try {
            spawnYaml.save(spawnFile);
            gateYaml.save(gateFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
