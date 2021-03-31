package kz.nicnbk.common.service.util;

import org.mockito.cglib.core.Local;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

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

    public static int getDaysDifference(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
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

    public static int getDay(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeek(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static Date getDate(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Date getDate(String date, String format){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Date getDate_MMddyyyy(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Date getMMMYYYYYFormatLastDayMonthDate(String date){
        if(date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            try {
                return getLastDayOfCurrentMonth(simpleDateFormat.parse(date));
            } catch (ParseException e) {
                //e.printStackTrace();
            }
        }
        return null;
    }

    public static Date getMM_YYYYYFormatLastDayMonthDate(String date){
        if(date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.yyyy", Locale.ENGLISH);
            try {
                return getLastDayOfCurrentMonth(simpleDateFormat.parse(date));
            } catch (ParseException e) {
                //e.printStackTrace();
            }
        }
        return null;
    }

    public static String getMM_YYYYYFormatDate(Date date){
        if(date != null){
            int month = getMonth(date) + 1;
            return (month < 10 ? "0" + month : month)+ "." + getYear(date);
        }
        return null;
    }

    public static String getDateFormatted(Date date){
        if(date == null){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(date);
    }

    public static String getDateFormattedWithTime(Date date){
        if(date == null){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return simpleDateFormat.format(date);
    }

    // For DB operations, date format for psql is 'yyyy-MM-dd'
    public static String getDateFormatted_YYYY_MM_DD(Date date){
        if(date == null){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    public static Date getFirstDayYear(int year){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return simpleDateFormat.parse("01.01." + year);
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

    public static Date getLastDayOfCurrentMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, getMonth(date));
        cal.set(Calendar.YEAR, getYear(date));
        cal.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper results
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //System.out.println(cal.getTime());
        return cal.getTime();
    }

    public static boolean isSameDate(Date date1, Date date2){
        if(date1 == null || date2 == null){
            return false;
        }
        return getDateFormatted(date1).equalsIgnoreCase(getDateFormatted(date2));
    }

    public static boolean isSameMonth(Date date1, Date date2){
        if(date1 == null || date2 == null){
            return false;
        }
        return getMonth(date1) == getMonth(date2);
    }

    public static Date getLastDayOfCurrentYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, getYear(date));
        cal.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper results
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //System.out.println(cal.getTime());
        return cal.getTime();
    }

    public static Date getLastDayOfNextMonth(Date date){
        // TODO: december-january
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, getMonth(date) + 1);
        cal.set(Calendar.YEAR, getYear(date));
        cal.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper results
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //System.out.println(cal.getTime());
        return cal.getTime();
    }

    public static Date getFirstDayOfCurrentMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, getMonth(date));
        cal.set(Calendar.YEAR, getYear(date));
        cal.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper results
        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //System.out.println(cal.getTime());
        return cal.getTime();
    }

    public static Date getFirstDayOfNextMonth(Date date){
        // TODO: december-january
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, getMonth(date) + 1);
        cal.set(Calendar.YEAR, getYear(date));
        cal.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper results
        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //System.out.println(cal.getTime());
        return cal.getTime();
    }

    public static Date getLastDayOfPreviousMonth(Date date){
        // TODO: december-january
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, getMonth(date) -1);
        cal.set(Calendar.YEAR, getYear(date));
        cal.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper results
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //System.out.println(cal.getTime());
        return cal.getTime();
    }

    public static Date getFirstDayOfDateYear(Date date){
        String firstDay = "01.01." + getYear(date);
        return getDate(firstDay);
    }

    public static Date getNextDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, getMonth(date));
        cal.set(Calendar.YEAR, getYear(date));
        cal.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper results
        cal.set(Calendar.DATE, getDay(date) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //System.out.println(cal.getTime());
        return cal.getTime();
    }

    public static Date getDateOnly(Date date){
        return getDate(getDateFormatted(date));
    }


    /**
     * Returns date in full format in Russian, e.g.
     * 01 сентября 2017 года
     * @param date - date
     * @return - textual date
     */
    public static String getDateRussianTextualDateOnFirstDayNextMonth(Date date){
        //int day = getDay(date);
        int month = getMonth(date);
        int year = getYear(date);

        if(month < 11){
            month = month + 2;
        }else{
            // за декабрь - 1 января
            month = 1;
            year = year + 1;
        }

        String dateRu = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(new Locale("ru")).format(LocalDate.of(year, month, 1));
        if(dateRu != null && dateRu.startsWith("1 ")){
            dateRu = "0" + dateRu;
        }
        return  dateRu.replace("г.", "года");
    }

    public static String getDateRussianTextualDate(Date date){
        //int day = getDay(date);
        int month = getMonth(date);
        int year = getYear(date);
        int day = getDay(date);

        month = month + 1;

        String dateRu = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(new Locale("ru")).format(LocalDate.of(year, month, day));
        if(dateRu != null && dateRu.startsWith("1 ")){
            dateRu = "0" + dateRu;
        }
        return  dateRu.replace("г.", "года");
    }

    public static String getDateEnglishTextualDate(Date date){
        int day = getDay(date);
        int month = getMonth(date);
        int year = getYear(date);

        String dateEn = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(new Locale("en")).format(LocalDate.of(year, (month + 1), day));
        if(dateEn != null && dateEn.startsWith("1 ")){
            dateEn = "0" + dateEn;
        }
        return dateEn;
    }

    public static String getDateShortMonthYearEnglishTextualDate(Date date){
        String format = "MMM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdf.format(date);
    }

    public static Date getDateWithTime(Date date,String time){
        String fullDate = getDateFormatted(date) + " " + time;
        String format = "dd.MM.yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        try{
            return sdf.parse(fullDate);
        }catch (ParseException ex){
            //
        }
        return null;
    }

    public static boolean isJanuary(Date date){
        return date != null && getMonth(date) == 0;
    }

    public static boolean isDecember(Date date){
        return date != null && getMonth(date) == 11;
    }

    public static Date moveDateByMonths(Date referenceDate, int months){
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }

    public static Date moveDateByDays(Date referenceDate, int days, boolean skipWeekend){
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        if(!skipWeekend) {
            c.add(Calendar.DAY_OF_MONTH, days);
        }else {
            if (days > 0) {
                for (int i = 1; i <= days; ) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    if (getDayOfWeek(c.getTime()) != Calendar.SATURDAY && getDayOfWeek(c.getTime()) != Calendar.SUNDAY) {
                        i++;
                    }
                }
            } else if (days < 0) {
                for (int i = -1; i >= days; ) {
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    if (getDayOfWeek(c.getTime()) != Calendar.SATURDAY && getDayOfWeek(c.getTime()) != Calendar.SUNDAY) {
                        i--;
                    }
                }
            }
        }
        return c.getTime();
    }

    public static Date moveDateByHours(Date referenceDate, int days, boolean skipWeekend){
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        if(days > 0) {
            for (int i = 1; i <= days;) {
                c.add(Calendar.DAY_OF_MONTH, 1);
                if(getDayOfWeek(c.getTime()) != 1 && getDayOfWeek(c.getTime()) !=7 ){
                    i ++;
                }
            }
        }else if(days < 0){
            for (int i = -1; i >= days;) {
                c.add(Calendar.DAY_OF_MONTH, -1);
                if(getDayOfWeek(c.getTime()) != 1 && getDayOfWeek(c.getTime()) !=7 ){
                    i --;
                }
            }
        }
        return c.getTime();
    }

    public static int getMonthsChanged(Date fromDate, Date toDate){
        if(fromDate == null || toDate == null){
            throw new IllegalArgumentException("Date argument is null");
        }
//        if(getYear(fromDate) == getYear(toDate) && getMonth(fromDate) == getMonth(toDate)){
//            return 0;
//        }
        Date start = getFirstDayOfCurrentMonth(fromDate);
        Date end = getFirstDayOfCurrentMonth(toDate);
        return getMonthsDifference(start, end) + 1;
    }

    public static String getMonthYearDate(Date date){
        int month = DateUtils.getMonth(date);
        return ((month + 1) < 10 ? "0" + (month + 1) : month + 1) + "." + DateUtils.getYear(date);
    }
}
