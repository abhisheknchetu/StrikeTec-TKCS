package com.striketec.mobile.util;

import android.util.Log;

import com.calendarlibarary.caldroid.CalendarHelper;
import com.striketec.mobile.StriketecApp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

/**
 * Created by Qiang on 8/7/2017.
 */

public class DatesUtil {

    public static Date getDateFromMilliseconds(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return calendar.getTime();
    }

    public static ArrayList<Date> getPreviousWeek(Date inputstartDate){

        ArrayList<Date> result = new ArrayList<>();
        Date startDate, endDate;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputstartDate);

        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK)-1));
        endDate = calendar.getTime();

        calendar.add(Calendar.DAY_OF_WEEK, - 6);
        startDate = calendar.getTime();

        result.add(startDate);
        result.add(endDate);

        return result;
    }

    public static  ArrayList<Date> getNextWeek(Date inputstartDate){

        ArrayList<Date> result = new ArrayList<>();
        Date startDate, endDate;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputstartDate);

        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK)-1));
        calendar.add(Calendar.DAY_OF_WEEK, 8);
        startDate = calendar.getTime();

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        endDate = calendar.getTime();

        result.add(startDate);
        result.add(endDate);

        return result;
    }

    public static Date getDatefromString (String inputDate){
        Date date = null;
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            date = inputFormat.parse(inputDate);
        }catch (Exception e){

        }

        return date;
    }

    public static String changeDateFormat(String inputString){
        SimpleDateFormat inputformat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        String result = "";

        try {
            result = outputFormat.format(inputformat.parse(inputString));
        }catch (Exception e){

        }

        return result;
    }

    public static String changeDateFormat1(String inputString){
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

        String result = "";

        try {
            result = outputFormat.format(inputFormat.parse(inputString));
        }catch (Exception e){

        }

        return result;
    }

    public static String getUTCfromMilliseconds(long milli){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return  dateFormat.format(new Date(milli));
    }

    public static String getTimestamp(long milli){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  format.format(new Date(milli));
    }


    public static String getDefaultTimefromMilliseconds(long milli){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return  dateFormat.format(new Date(milli));
    }

    public static String changeDatetoString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        return dateFormat.format(date);
    }

    public static Date getCurrentDay(){
        Calendar c = Calendar.getInstance();

        return c.getTime();
    }

    public static int getAge(String birthday){
        int age = 0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formatter.parse(birthday);
            int birthyear = Integer.parseInt((String) android.text.format.DateFormat.format("yyyy", date));

            int currentyear = Calendar.getInstance().get(Calendar.YEAR);

            age = currentyear - birthyear;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return age;
    }

    public static long getFirstDayofWeek(){
        Calendar c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if (c.getTime().after(DatesUtil.getCurrentDay()))
            c.add(Calendar.DATE, -6);

        return c.getTime().getTime();
    }

    public static long getEndDayofWeek(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.add(Calendar.DATE, 6);
        return c.getTime().getTime();
    }

    public static Date addDate(Date startDate, int days){
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DATE, days);

        return c.getTime();
    }

    public static String changeMilestoDate(String miles){
        DateFormat dateFormat = new SimpleDateFormat("yyyy, MMM dd : KK:mm a");

        return dateFormat.format(Long.parseLong(miles));
    }

    public static String getDuration(Date startDate, Date endDate){
        DateTime startDateTime = CalendarHelper.convertDateToDateTime(startDate);
        DateTime endDateTime = CalendarHelper.convertDateToDateTime(endDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM d");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("d, yyyy");

        if (startDateTime.getYear().equals(endDateTime.getYear())){
            if (startDateTime.getMonth().equals(endDateTime.getMonth())){
                return dateFormat1.format(startDate) + " TO " + dateFormat2.format(endDate);
            }else {
                return dateFormat1.format(startDate) + " TO " + dateFormat.format(endDate);
            }
        }else {
            return dateFormat.format(startDate) + " TO " + dateFormat.format(endDate);
        }

    }

    public static String getDateFromMilliseconds(String miles){
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(Long.parseLong(miles));
    }

    public static String getYearMonth(String miles){
        DateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
        return dateFormat.format(Long.parseLong(miles));
    }

    public static String getDate(String miles){
        DateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(Long.parseLong(miles));
    }

    public static String secondsToTime(long seconds) {

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + ":" + twoDigitString(minutes) + ":" + twoDigitString(seconds);
    }

    /**
     * Prepends 0 if number < 10
     */
    public static String twoDigitString(long number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

}
