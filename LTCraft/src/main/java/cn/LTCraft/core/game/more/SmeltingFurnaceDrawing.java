package cn.LTCraft.core.game.more;

import cn.LTCraft.core.Config;
import io.lumine.utils.config.MemoryConfiguration;
import io.lumine.utils.config.file.YamlConfiguration;
import io.lumine.xikage.mythicmobs.io.MythicConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 熔炼炉图纸
 * Created by Angel、 on 2022/4/25 20:41
 */
public class SmeltingFurnaceDrawing extends MemoryConfiguration {
    private static final Map<String, SmeltingFurnaceDrawing> map = new HashMap<>();
    private final String name;
    public static SmeltingFurnaceDrawing getSmeltingFurnaceDrawing(String name){
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
        set("level", config.getString("level", "通用"));
        set("fuelCount", config.getInteger("fuelCount", 10));
        set("needMaterial", config.getStringList("needMaterial"));
        set("result", config.getStringList("result"));
    }
    /**
     *
     * @param name 熔炼图纸名字
     * @param config 配置
     * @param parent 父类配置
     */
    private SmeltingFurnaceDrawing(String name, MythicConfig config, MythicConfig parent){
        this.name = name;
        set("level", config.getString("level", parent.getString("level", "通用")));//等级
        set("fuelCount", config.getInteger("fuelCount", parent.getInteger("fuelCount", 10)));//燃料数量
        set("needMaterial", config.getStringList("needMaterial").addAll(parent.getStringList("needMaterial")));//需要材料
        set("result", config.getStringList("result").addAll(parent.getStringList("result")));//结果
    }

    @Override
    public String getName() {
        return name;
    }

    public static Map<String, SmeltingFurnaceDrawing> getMap() {
        return map;
    }
}
