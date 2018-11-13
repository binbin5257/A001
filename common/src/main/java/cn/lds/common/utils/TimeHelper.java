package cn.lds.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leadingsoft on 2017/8/10.
 */

public class TimeHelper {
    public static final String FORMAT1 = "yyyyMMdd HH:mm:ss";
    public static final String FORMAT2 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT3 = "yyyy-MM-dd";
    public static final String FORMAT4 = "HH:mm";
    public static final String FORMAT5 = "yyyyMMdd";
    public static final String FORMAT6 = "HH:mm:ss";
    public static final String FORMAT7 = "yyyy年MM月dd日  HH:mm:ss";
    public static final String FORMAT8 = "yyyy-MM-dd HH:mm";
    public static final String FORMAT9 = "yyyy.MM.dd HH:mm";
    public static final String FORMAT10 = "yyyy/MM/dd HH:mm:ss";
    private static String DEF_FORMAT1 = FORMAT1;

    /**
     * 获取当前时间--指定格式
     *
     * @param type
     * @return
     */
    public static String getCurrentTime(String type) {
        SimpleDateFormat formatter = new SimpleDateFormat(type);
        Date curDate = new Date();
        return formatter.format(curDate);
    }

    /**
     * 获取当前时间--默认格式
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(DEF_FORMAT1);
        Date curDate = new Date();
        return formatter.format(curDate);
    }

    /**
     * 获取当前时间--默认格式
     *
     * @param time
     *         时间
     * @param type
     *         显示类型
     * @return
     */
    public static String getTimeByType(long time, String type) {
        SimpleDateFormat formatter = new SimpleDateFormat(type);
        Date curDate = new Date(time);
        return formatter.format(curDate);
    }

    /**
     * 字符串转换为日历对象
     * @param timeStr 时间字符串
     * @param type 转换类型
     * @return 返回日历对象
     */
    public static Calendar getCalendarByString(String timeStr,String type){

        try {
            SimpleDateFormat formatter = new SimpleDateFormat(type);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatter.parse(timeStr));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

}
