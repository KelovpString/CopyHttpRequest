/**
 * Copyrights  2015  HuangBaoChe
 * <p>
 * All rights reserved.
 * <p>
 * Created on 2015年9月30日 下午3:02:55
 *
 * @author HanZhaozhan
 */
package com.kelovp.compare.dashboard.util;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
public class DateUtil {

    /*
     * 只到日期
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    /*
     * 时分秒
     */
    public static final String TIME_PATTERN_M = "yyyy-MM-dd HH:mm";
    public static final String TIMESTRPARTDAY = "yyyyMMdd";
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final int TIMEZONE_OFFSET_BJ = 8;
    public static final long MILLISECOND_PER_HOUR = 3600 * 1000;
    public static final long MILLISECOND_PER_DAY = 24 * MILLISECOND_PER_HOUR;
    public static final int MINUTES_PER_HOUR = 60;

    public static final String ZERO_TIME_SUFFIX = " 00:00:00";
    public static final String LAST_TIME_SUFFIX = " 23:59:59";

    public static final String COMMON_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String hour_noms_data = "HH:mm";
    public static final String BLANK_STR = " ";

    public static final Date getAddDate(Date beginDate, int nextDate) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + nextDate);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return endDate;
    }

    /**
     * 获取只保留年月日的日期
     *
     * @param date
     * @return
     */
    public static Date getYMDDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        cal.clear();
        cal.set(year, month, day);
        return cal.getTime();
    }

    public static int getDaysBetween(Date startDate, Date endDate) {
        Date start = getYMDDate(startDate);
        Date end = getYMDDate(endDate);
        return (int) ((end.getTime() / MILLISECOND_PER_DAY) - (start.getTime() / MILLISECOND_PER_DAY));
    }

    public static Date initAndGetTime(Date targetDate, int hours, int minutes, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(targetDate);
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, seconds);
        return cal.getTime();
    }

    /**
     * 日期格式化
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        try {
            return new SimpleDateFormat(pattern).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * yyyy-MM-dd HH:mm:ss 日期格式化
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        try {
            return new SimpleDateFormat(COMMON_TIME_FORMAT).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * yyyy-MM-dd日期格式化
     *
     * @param date
     * @return
     */
    public static String formatDateyMd(Date date) {
        try {
            return new SimpleDateFormat(DATE_PATTERN).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期格式补充时间后缀
     * @param date
     * @param suffix
     * @param pattern
     * @return
     */
    public static Date formatDateHms(Date date, String suffix, String pattern) {
        try {
            String dateStr = formatDate(date, DATE_PATTERN);
            return parse(dateStr + BLANK_STR + suffix, pattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期格式补充时间后缀
     * @param date
     * @param suffix
     * @return
     */
    public static Date formatDateHms(Date date, String suffix) {
        return formatDateHms(date, suffix, TIME_PATTERN);
    }

    /**
     * 日期格式补充时间后缀
     * @param date
     * @return
     */
    public static Date formatDateHms(Date date) {
        return formatDateHms(date, ZERO_TIME_SUFFIX);
    }

    /**
     * 字符串日期解析
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date parse(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 获取北京时间
     *
     * @param localDate  当地时间
     * @param zoneOffset 当地时区偏移量(相对于UTC)
     * @return
     */
    public static Date getBjTime(Date localDate, float zoneOffset) {
        return getTime(localDate, zoneOffset, TIMEZONE_OFFSET_BJ);
    }

    public static Date addDays(Date time, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    /**
     * @param time
     * @param years
     * @return
     */
    public static Date addYears(Date time, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }

    /**
     * 获取当地时间
     *
     * @param bjTime     北京时间
     * @param zoneOffset 当地时区偏移量(相对于UTC)
     * @return
     */
    public static Date getLocalTime(Date bjTime, float zoneOffset) {
        return getTime(bjTime, TIMEZONE_OFFSET_BJ, zoneOffset);
    }

    /**
     * 根据本地时间和本地时区转换成目标时区时间
     *
     * @param localTime          本地时间
     * @param localTimezone      本地时区偏移量(相对于UTC)
     * @param targetCityTimezone 目标城市时区偏移量(相对于UTC)
     * @return
     */
    public static Date getTime(Date localTime, float localTimezone, float targetCityTimezone) {
        float tzOffset = targetCityTimezone - localTimezone;
        return new Date(localTime.getTime() + (long) (tzOffset * MILLISECOND_PER_HOUR));
    }

    /**
     * 获取当地时间
     *
     * @param zoneOffset 当地时区偏移量(相对于UTC)
     * @return
     */
    public static Date getLocalCurrentTime(float zoneOffset) {
        return getLocalTime(getBjNow(), zoneOffset);
    }

    public static Date getLocalCurrentYMDDate(float zoneOffset) {
        return getYMDDate(getLocalTime(getBjNow(), zoneOffset));
    }

    /**
     * 获取当前北京时间
     *
     * @return
     */
    public static Date getBjNow() {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of("UTC+08:00"));
        return new Date(dateTime.toInstant().toEpochMilli());
    }

    /**
     * 增加分钟数
     *
     * @param date
     * @return
     */
    public static Date addMinutes(Date date, int minutes) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 两个日期之间的分钟数
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int getMinutesBetweenDates(Date beginDate, Date endDate) {
        return Long.valueOf(((endDate.getTime() - beginDate.getTime()) / 1000) / 60).intValue();
    }

    /**
     * 时差：小时数
     */
    public static long hourDiff(Date startTime, Date endTime) {

        long endTimeValue = endTime.getTime();
        long startTimeValue = startTime.getTime();

        long diff = endTimeValue - startTimeValue;

        long min = diff / (60 * 60 * 1000);
        return min;
    }

    /**
     * 时间间隔(分钟)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long minuteDiff(Date startTime, Date endTime) {
        return (endTime.getTime() - startTime.getTime()) / (60 * 1000);
    }

    /**
     * 时差：秒数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long secondDiff(Date startTime, Date endTime) {

        long endTimeValue = endTime.getTime();
        long startTimeValue = startTime.getTime();

        long diff = endTimeValue - startTimeValue;

        long min = diff / (1000);
        return min;
    }

    /**
     * <Method Simple Description>
     *
     * @see
     */
    public static String dateToString(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        try {
            SimpleDateFormat sfDate = new SimpleDateFormat(pattern);
            sfDate.setLenient(false);
            return sfDate.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <Method Simple Description>
     *
     * @see
     */
    public static Date stringToDate(String dateText, String format) {
        if (dateText == null) {
            return null;
        }
        DateFormat df;
        try {
            if (format == null) {
                df = new SimpleDateFormat();
            } else {
                df = new SimpleDateFormat(format);
            }
            df.setLenient(false);
            return df.parse(dateText);
        } catch (ParseException e) {
            return null;
        }
    }


    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            return isSameDay(cal1, cal2);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    /**
     * @description 给现在的时间加numActivityFourAnniversarySessionSqlProvider.java
     * ActivityFourAnniversarySessionMapper.java天数
     * @author zhangjin
     * @date 2018/8/2
     */
    public static Date plusDay(int num) {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currdate = format.format(d);
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, num);
        d = ca.getTime();
        String enddate = format.format(d);
        Date newDay = stringToDate(enddate, DateUtil.TIME_PATTERN);
        return newDay;
    }


    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    /**
     * @description 获取指点时间的的前几天或者后几天
     * @author zhangjin
     * @date 2018/8/13
     */
    public static Date getNDay(Date inputDay, int num) {
        Calendar c = Calendar.getInstance();
        c.setTime(inputDay);
        c.add(Calendar.DAY_OF_MONTH, num);
        return c.getTime();
    }


    /**
     * @description 获取当天的最大时间和最小时间
     * @author zhangjin
     * @date 2018/8/15
     */
    public static List<Date> getNowMinMaxTime() {
        Calendar todayStart = Calendar.getInstance();
        Calendar todayEnd = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        Date start = todayStart.getTime();
        Date end = todayEnd.getTime();
        List<Date> dateList = new ArrayList<>(2);
        dateList.add(start);
        dateList.add(end);
        return dateList;
    }


    public static int daysBetween(Date early, Date late) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(early);
        c2.setTime(late);
        return daysBetween(c1, c2);
    }

    public static int daysBetween(Calendar early, Calendar late) {
        return (int) (toJulian(late) - toJulian(early));
    }

    public static float toJulian(Calendar c) {
        int Y = c.get(Calendar.YEAR);
        int M = c.get(Calendar.MONTH);
        int D = c.get(Calendar.DATE);
        int A = Y / 100;
        int B = A / 4;
        int C = 2 - A + B;
        float E = (int) (365.25f * (Y + 4716));
        float F = (int) (30.6001f * (M + 1));
        return C + D + E + F - 1524.5f;
    }

    public static Date getCurrentDateTime() {
        return Calendar.getInstance().getTime();
    }


    public static String getDateByWeek(Date date) {
        String dayInfo = formatDate(date, DATE_PATTERN);
        String hourInfo = formatDate(date, hour_noms_data);
        String weekInfo = getChinaWeek(date);
        return dayInfo + " " + weekInfo + " " + hourInfo;
    }

    public static String getDateByWeekNoHour(Date date) {
        String dayInfo = formatDate(date, DATE_PATTERN);
        String weekInfo = getChinaWeek(date);
        return dayInfo + " " + weekInfo;
    }

    public static String getChinaWeek(Date date) {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

}
