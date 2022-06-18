package cn.LTCraft.core.other.exceptions;

/**
 * 熔炼坛错误异常
 * Created by Angel、 on 2022/4/27 21:50
 */
public class SmeltingFurnaceErrorException extends Exception {
    private boolean blast = false;
    private Type type = Type.UNKNOWN;
    private int time = 60;
    public SmeltingFurnaceErrorException(){
    }
    public SmeltingFurnaceErrorException(String message){
        super(message);
    }
    public SmeltingFurnaceErrorException(String message, boolean blast){
        super(message);
        this.blast = blast;
    }
    public SmeltingFurnaceErrorException(String message, Type type){
        super(message);
        this.type = type;
    }
    public SmeltingFurnaceErrorException(String message, Type type, int time, boolean blast){
        super(message);
        this.type = type;
        this.time = time;
        this.blast = blast;
    }
    public SmeltingFurnaceErrorException(String message, Type type, int time){
        super(message);
        this.type = type;
        this.time = time;
    }

    public boolean isBlast() {
        return blast;
    }

    public int getTime() {
        return time;
    }

    public Type getType() {
        return type;
    }

    public String serialize() {
        return type + "|" + time + "|" + blast + "|" + getMessage();
    }

    public static SmeltingFurnaceErrorException unSerialize(String serialize) {
        String[] split = serialize.split("\\|");
        String mess = split[3];
        SmeltingFurnaceErrorException smeltingFurnaceErrorException = new SmeltingFurnaceErrorException(mess);
        smeltingFurnaceErrorException.type = Type.valueOf(split[0]);
        smeltingFurnaceErrorException.time = Integer.parseInt(split[1]);
        smeltingFurnaceErrorException.blast = Boolean.parseBoolean(split[2]);
        return smeltingFurnaceErrorException;
    }
    public enum Type{
        UNKNOWN,//未知
        TIMEOUT,//超时
        PLAYER_OFFLINE,//玩家离线
        FURNACES_DISTURBED,//熔炉被干扰
        DRAWING_ABNORMAL,//图纸异常
        CHEST_ABNORMAL,//箱子异常
        FRAME_ABNORMAL,//展示框异常
        FURNACES_ABNORMAL,//熔炉异常
        STRUCTURE//结构
    }
}
