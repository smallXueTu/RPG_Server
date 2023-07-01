package cn.LTCraft.core.enums;

/**
 * Created by Angel、 on 2023/7/1 10:44
 * 环境模式
 */
public enum EnvironmentMode {
    DEVELOP,//开发
    PRODUCTION;//生产
    public static EnvironmentMode valueIgnoreCaseOf(String mode){
        for (EnvironmentMode value : values()) {
            if (value.name().equalsIgnoreCase(mode))return value;
        }
        return PRODUCTION;
    }
}
