package cn.LTCraft.core.other.exceptions;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * 熔炼坛错误异常
 * Created by Angel、 on 2022/4/27 21:50
 */
public class SmeltingFurnaceErrorException extends Exception {
    private boolean blast = true;
    public SmeltingFurnaceErrorException(String message){
        super(message);
    }
    public SmeltingFurnaceErrorException(String message, boolean blast){
        super(message);
        this.blast = blast;
    }

    public boolean isBlast() {
        return blast;
    }

    public String serialize() {
        return getMessage() + (blast ? 1 : 0);
    }

    public static SmeltingFurnaceErrorException unSerialize(String serialize) {
        SmeltingFurnaceErrorException smeltingFurnaceErrorException = new SmeltingFurnaceErrorException(serialize.substring(0, serialize.length() - 2));
        smeltingFurnaceErrorException.blast = serialize.endsWith("1");
        return smeltingFurnaceErrorException;
    }
}
