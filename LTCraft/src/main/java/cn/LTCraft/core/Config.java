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
    private final File MMSpawnFile;
    private final File gateFile;
    private final File drawingFile;

    private YamlConfiguration NPCClickYaml;
    private YamlConfiguration MySQLInfoYaml;
    private YamlConfiguration itemsYaml;
    private YamlConfiguration worldTitleYaml;
    private io.lumine.utils.config.file.YamlConfiguration gateYaml;
    private io.lumine.utils.config.file.YamlConfiguration MMSpawnYaml;
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

        MMSpawnFile = new File(plugin.getDataFolder(), "spawns/MMSpawn.yml");
        MMSpawnYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(MMSpawnFile);

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

    public io.lumine.utils.config.file.YamlConfiguration getMMSpawnYaml() {
        return MMSpawnYaml;
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
        MMSpawnYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(MMSpawnFile);
        gateYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(gateFile);
        drawingYaml = io.lumine.utils.config.file.YamlConfiguration.loadConfiguration(drawingFile);
    }

    /**
     * 保存
     */
    public void save(){
        try {
            MMSpawnYaml.save(MMSpawnFile);
            gateYaml.save(gateFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
