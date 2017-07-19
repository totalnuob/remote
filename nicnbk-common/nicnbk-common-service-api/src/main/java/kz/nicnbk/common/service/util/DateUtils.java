package kz.nicnbk.common.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by magzumov on 05.01.2017.
 */
public class DateUtils {

    // calculate months between two dates
    public static int getMonthsDifference(Date fromDate, Date toDate){
        if(fromDate == null || toDate == null){
            throw new IllegalArgumentException("Date argument is null");
        }
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(fromDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(toDate);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        return diffMonth;
    }

    public static int getYear(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static Date getDate(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateFormatted(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(date);
    }

    public static int getCurrentYear(){
        return getYear(new Date());
    }

    public static Date getFirstDayCurrentYear(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return simpleDateFormat.parse("01.01." + getCurrentYear());
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }
}
