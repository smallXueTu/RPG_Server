package cn.LTCraft.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Angel、 on 2022/4/6 11:02
 */
public class DateUtils {
    public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
    public static String getDateFormat(int time){
        int second = time % 60;
        time /= 60;
        int branch = time % 60;
        time /= 60;
        int hour = time % 24;
        time /= 24;
        if (time > 0){
            return time + "天" + hour + "小时" + branch + "分" + second + "秒";
        }
        if (hour > 0){
            return hour + "小时" + branch + "分" + second + "秒";
        }
        if (branch > 0){
            return branch + "分" + second + "秒";
        }
        return second + "秒";
    }
}
