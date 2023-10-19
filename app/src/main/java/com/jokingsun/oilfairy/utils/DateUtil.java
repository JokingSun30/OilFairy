package com.jokingsun.oilfairy.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;

import com.jokingsun.oilfairy.common.constant.AppConstant;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author cfd058
 */
public class DateUtil {

    public static String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 獲取今天日期 選擇樣式：TYPE_DATE_TRADITIONAL_FORMAT、TYPE_DATE_DASH_FORMAT、TYPE_DATE_SLASH_FORMAT
     *
     * @return String
     */
    @SuppressLint("DefaultLocale")
    public static String getToday(String formatStyle) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        return dateFormatWithStyle(String.format("%4d", cal.get(Calendar.YEAR)),
                String.format("%02d", cal.get(Calendar.MONTH) + 1), String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)), formatStyle);
    }

    /**
     * 獲取今天日期(選擇顯示樣式長度)
     *
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    public static String getToday(Date date, boolean userLong) {
        if (userLong) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     * 3月 : 3
     *
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String getThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return String.format("%2d", cal.get(Calendar.MONTH) + 1);
    }

    /**
     * 獲取日期分析 MODEL
     *
     * @return GeneralDateBean
     */
    @SuppressLint("DefaultLocale")
    public static GeneralDateBean analysisDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        GeneralDateBean analysisCalender = new GeneralDateBean();

        analysisCalender.setYear(String.format("%4d", cal.get(Calendar.YEAR)));
        analysisCalender.setMonth(String.format("%02d", cal.get(Calendar.MONTH) + 1));
        analysisCalender.setDay(String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));

        return analysisCalender;
    }

    /**
     * 獲取當周第一天
     *
     * @return String
     */
    @SuppressLint("SimpleDateFormat")

    public static String getFirstDayOfWeek(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(date);
            //歐美地區第一天為亞洲地區的星期日，故 value + 1 = 2 ，才是亞洲的星期一(第一天)
            calendar.set(Calendar.DAY_OF_WEEK, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return format.format(calendar.getTime());
    }

    /**
     * 獲取當周最後一天
     *
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    public static String getLastDayOfWeek(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_WEEK, 2);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 6);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return format.format(calendar.getTime());
    }

    /**
     * 獲取當月第一天
     *
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentMonthFirstDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar;
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return format.format(calendar.getTime());
    }

    /**
     * 獲取當月最後一天
     *
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentMonthLastDay() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar;
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return format.format(calendar.getTime());
    }

    /**
     * 萬用時間選擇器
     *
     * @param context     文本
     * @param targetView  指定要顯示時間的 view
     * @param title       選擇器標題
     * @param isShowYear  是否顯示年份
     * @param isShowMonth 是否顯示月份
     * @param isShowDay   是否顯示日
     * @param formatStyle 指定日期顯示風格
     */
    @SuppressLint("DefaultLocale")
    public static void datePicker(Context context, View targetView, String title,
                                  boolean isShowYear, boolean isShowMonth, boolean isShowDay,
                                  String formatStyle, long maxDateLong,long minDateLong) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int yy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH)+1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog pickerDialog = new DatePickerDialog(new ContextThemeWrapper(context,
                android.R.style.Theme_Holo_Light_Dialog), (view, year1, month1, day1) -> {
            month1 = month1 + 1;

            String year = "";
            String month = "";
            String day = "";

            if (isShowYear) {
                year = String.format("%04d", year1);
            }
            if (isShowMonth) {
                month = String.format("%02d", month1);
            }
            if (isShowDay) {
                day = String.format("%02d", day1);
            }

            String dateTime = dateFormatWithStyle(year, month, day, formatStyle);

            if (targetView instanceof TextView) {
                ((TextView) targetView).setText(dateTime);
            }


        }, yy, mm, dd) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                LinearLayout mSpinners = findViewById(getContext().getResources().getIdentifier("android:id/pickers", null, null));
                if (mSpinners != null) {
                    NumberPicker mDaySpinner = findViewById(getContext().getResources().getIdentifier("android:id/day", null, null));
                    NumberPicker mMonthSpinner = findViewById(getContext().getResources().getIdentifier("android:id/month", null, null));
                    NumberPicker mYearSpinner = findViewById(getContext().getResources().getIdentifier("android:id/year", null, null));

                    mSpinners.removeAllViews();

                    if (mYearSpinner != null && isShowYear) {
                        mSpinners.addView(mYearSpinner);
                    }

                    if (mMonthSpinner != null && isShowMonth) {
                        mSpinners.addView(mMonthSpinner);
                    }

                    if (mDaySpinner != null && isShowDay) {
                        mSpinners.addView(mDaySpinner);
                    }

                }

                View yearPickerView = findViewById(getContext().getResources().getIdentifier("android:id/year", null, null));
                if (yearPickerView != null && !isShowYear) {
                    yearPickerView.setVisibility(View.GONE);
                }

                View monthPickerView = findViewById(getContext().getResources().getIdentifier("android:id/month", null, null));
                if (monthPickerView != null && !isShowMonth) {
                    monthPickerView.setVisibility(View.GONE);
                }

                View dayPickerView = findViewById(getContext().getResources().getIdentifier("android:id/day", null, null));
                if (dayPickerView != null && !isShowDay) {
                    dayPickerView.setVisibility(View.GONE);
                }

            }
        };

        pickerDialog.setTitle(title);
        //指定最大值(小於今日之日期)
        if (maxDateLong != -1) {
            pickerDialog.getDatePicker().setMaxDate(maxDateLong);
        }

        if(minDateLong!=-1){
            pickerDialog.getDatePicker().setMinDate(minDateLong);
        }

        pickerDialog.show();

    }

    /**
     * 搭配 datePicker fun 操作 ，請依需求輸入是否顯示 年 月 日
     */
    public static String dateFormatWithStyle(String year, String month, String day, String formatStyle) {
        String dateTime = "";

        if (formatStyle.equals(AppConstant.TYPE_DATE_TRADITIONAL_FORMAT)) {
            if (!year.isEmpty()) {
                dateTime = year + "年";
            }
            if (!month.isEmpty()) {
                dateTime = dateTime + month + "月";
            }
            if (!day.isEmpty()) {
                dateTime = dateTime + day + "日";
            }
        }

        if (formatStyle.equals(AppConstant.TYPE_DATE_DASH_FORMAT)) {
            if (!year.isEmpty()) {
                dateTime = year + "-";
            }
            if (!month.isEmpty()) {
                dateTime = dateTime + month + "-";
            }
            if (!day.isEmpty()) {
                dateTime = dateTime + day;
            }
        }

        if (formatStyle.equals(AppConstant.TYPE_DATE_SLASH_FORMAT)) {
            if (!year.isEmpty()) {
                dateTime = year + "/";
            }
            if (!month.isEmpty()) {
                dateTime = dateTime + month + "/";
            }
            if (!day.isEmpty()) {
                dateTime = dateTime + day;
            }
        }

        return dateTime;

    }

    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String strTime, String formatType) {
        SimpleDateFormat formatter = null;
        switch (formatType) {
            case AppConstant.TYPE_DATE_DASH_FORMAT:
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case AppConstant.TYPE_DATE_SLASH_FORMAT:
                formatter = new SimpleDateFormat("yyyy/MM/dd");
                break;
            default:
                break;
        }
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 取得指定格式日期的系統秒時間
     */
    @SuppressLint("SimpleDateFormat")
    public static long getTimeMillisByTargetDate(String formatDate, boolean useLongSdf) {
        SimpleDateFormat dateFormat = useLongSdf ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") :
                new SimpleDateFormat("yyyy-MM-dd");
        long time = 0;
        try {
            time = Objects.requireNonNull(dateFormat.parse(formatDate)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static boolean checkAdult(Date userBirthday) {

        Calendar current = Calendar.getInstance();
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTime(userBirthday);

        int year = current.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        if (year > 18) {
            return true;
        } else if (year < 18) {
            return false;
        }
        // 如果年相等，就比较月份
        int month = current.get(Calendar.MONTH) - birthDay.get(Calendar.MONTH);
        if (month > 0) {
            return true;
        } else if (month < 0) {
            return false;
        }
        // 如果月也相等，就比较天
        int day = current.get(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH);


        return day >= 0;
    }

    /**
     * 解析秒數-->轉換為 時/分/秒
     */
    @SuppressLint("DefaultLocale")
    public static int[] parseSecondsToCompleteStyle(int seconds) {

        int[] timeComplete = new int[3];

        timeComplete[2] = seconds % AppConstant.TIME_RULE_SECONDS_OF_ONE_MINUTES;

        int tmpMinutes = seconds / AppConstant.TIME_RULE_SECONDS_OF_ONE_MINUTES;

        timeComplete[1] = tmpMinutes % AppConstant.TIME_RULE_MINUTES_OF_ONE_HOUR;

        timeComplete[0] = tmpMinutes / AppConstant.TIME_RULE_MINUTES_OF_ONE_HOUR;

        return timeComplete;
    }

    public static final int FIRST_DATE_AFTER_SECOND_DATE = 1;
    public static final int FIRST_DATE_BEFORE_SECOND_DATE = 2;
    public static final int FIRST_DATE_EQUAL_SECOND_DATE = 0;

    /**
     * 比較兩個日期的先後順序
     *
     * @param firstDate  第一個日期
     * @param secondDate 第二個日期
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static int compareBetweenDates(String firstDate, String secondDate, String formatType) {

        SimpleDateFormat formatter = null;

        switch (formatType) {

            case AppConstant.TYPE_DATE_SLASH_FORMAT:
                formatter = new SimpleDateFormat("yyyy/MM/dd");
                break;

            case AppConstant.TYPE_DATE_DASH_FORMAT:
                formatter = new SimpleDateFormat("yyyy-MM-dd");
            default:
                break;
        }


        try {
            Date date1 = formatter.parse(firstDate);
            Date date2 = formatter.parse(secondDate);

            if (date1.after(date2)) {
                Logger.d("Date1 時間在 Date2 之後");
                return 1;
            }

            if (date1.before(date2)) {
                Logger.d("Date1 時間在 Date2 之前");
                return 2;
            }

            if (date1.equals(date2)) {
                Logger.d("Date1 時間與 Date2 相等");
                return 0;
            }

        } catch (ParseException e) {
            Logger.d("日期比較異常");
            return -1;
        }

        return -1;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)

    public static long countDaysByTwoDateWithVsO(String startDate, String endDate) {
        long days = -1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate firstDate = LocalDate.parse(startDate, formatter);
        LocalDate secondDate = LocalDate.parse(endDate, formatter);
        days = ChronoUnit.DAYS.between(firstDate, secondDate);
        return days;
    }

    public static long countDaysByTwoDate(String startDate, String endDate) {
        long days = -1;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = formatter.parse(startDate);
            Date date2 = formatter.parse(endDate);
            long diff = date2.getTime() - date1.getTime();
            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            System.out.println("Days: " + days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    public static String timeStamp2Date(long seconds, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(seconds));
    }

    public static long date2TimeStamp(String date_str, String format) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static class GeneralDateBean {
        private String year;
        private String month;
        private String day;

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }
    }
}
