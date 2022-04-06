package cn.LTCraft.core.utils;

/**
 * Created by Angel、 on 2022/4/6 11:02
 */
public class DateUtils {
    /**
     * 获取时间戳的天数
     * @param timeStamp 时间戳 秒 不是微妙！
     * @return 包含多少天
     */
    public static int getTimeStampDays(long timeStamp){
        return (int) (timeStamp / 60 / 60 / 24);
    }

    /**
     * 获取指定天数的时间戳是多少
     * @param days 天数
     * @return 时间戳 秒
     */
    public static long getDaysTimeStamp(int days){
        return (long) days * 24 * 60 * 60;
    }
}
