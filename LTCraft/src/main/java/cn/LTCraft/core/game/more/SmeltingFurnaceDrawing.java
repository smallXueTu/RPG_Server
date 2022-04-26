package cn.LTCraft.core.game.more;

import cn.LTCraft.core.Config;
import io.lumine.utils.config.file.YamlConfiguration;
import io.lumine.xikage.mythicmobs.io.MythicConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 熔炼炉图纸
 * Created by Angel、 on 2022/4/25 20:41
 */
public class SmeltingFurnaceDrawing {
    private static final Map<String, SmeltingFurnaceDrawing> map = new HashMap<>();
    private final String name;
    private final MythicConfig config;
    private static SmeltingFurnaceDrawing getSmeltingFurnaceDrawing(String name){
        SmeltingFurnaceDrawing drawing = null;
        YamlConfiguration drawingYaml = Config.getInstance().getDrawingYaml();
        if (drawingYaml.contains("name")){
            MythicConfig mythicConfig = new MythicConfig(name, drawingYaml);
            if (mythicConfig.getString("parent")!=null){
                MythicConfig parent = new MythicConfig(mythicConfig.getString("parent"), drawingYaml);
                drawing = new SmeltingFurnaceDrawing(name, mythicConfig, parent);
            }else {
                drawing = new SmeltingFurnaceDrawing(name, mythicConfig);
            }
        }
        return drawing;
    }
    /**
     *
     * @param name 熔炼图纸名字
     * @param config 配置
     */
    private SmeltingFurnaceDrawing(String name, MythicConfig config){
        this.name = name;
        this.config = config;
    }
    /**
     *
     * @param name 熔炼图纸名字
     * @param config 配置
     */
    private SmeltingFurnaceDrawing(String name, MythicConfig config, MythicConfig parent){
        this.name = name;
        this.config = config;
    }

    public static Map<String, SmeltingFurnaceDrawing> getMap() {
        return map;
    }
}
