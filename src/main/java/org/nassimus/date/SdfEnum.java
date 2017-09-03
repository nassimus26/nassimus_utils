package org.nassimus.date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public enum SdfEnum {

    DDMMYYYY_HHMM("ddMMyyyy:HHmm"),
    YYYYMMDD("yyyyMMdd"), YYYYMMDD_HHMMSS("yyyyMMdd_HHmmss"), YYYYMMDD__HHMMSS("yyyyMMdd HHmmss"), YYYYMMDDHHMMSS("yyyyMMddHHmmss"), YYYYMMDDHHMMSSSS("yyyyMMddHHmmssS"), YYYYMMDDHHMMSSSSS("yyyyMMddHHmmssSSS"), YYYY_MM_DD("yyyy\\MM\\dd"), YYYY__MM__DD("yyyy-MM-dd"), DD_MM_YYYY(
            "dd\\MM\\yyyy"), YYYYpMMpDD("yyyy.MM.dd"),

    HH_MM_SS("HH:mm:ss"), HH_MM_SS_MS("HH:mm:ss_SSS"), MM_SS("mm:ss"), MM_SS_MS("mm:ss_SSS"), SS("ss"), SS_MS("ss_SSS"), YYYY_MM_DD_HH_MM_SS_SSS("yyyy/MM/dd HH:mm:ss_SSS"), yyyy_MM_ddTHH_mm_ssZ("yyyy-MM-dd HH:mm:ss"), yyyy_MM_dd_HH_mm_ss("yyyy-MM-dd HH:mm:ss"), yyyy_MM_ddTHH_mm_ss(
            "yyyy-MM-dd'T'HH:mm:ss"), YYYY_MM_DDT("yyyy-MM-dd"), ISO_8601("yyyy-MM-dd'T'HH:mm:ssXXX"), FILE_LOG_RPLATFORM("yyyy.MM.dd.HH'h'mm'm'ss"), DDMMYYYY_HHMMSS("ddMMyyyy_HHmmss"), DD_MMM_YYYY_HH_MM_SS_Z("dd MMM yyyy HH:mm:ss Z"), YYYYMMDD_HHMMSS_Z("yyyyMMdd_HHmmss_z");

    private String template;
    private static final Logger logger = LoggerFactory.getLogger(SdfEnum.class);
    private SdfEnum(String template) {
        this.template = template;
    }

    public Date parseWOException(String dateStr) {
        Date date = null;
        try {
            date = new SimpleDateFormat(template).parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    public Date parseWOException(String dateStr, TimeZone timeZone) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(template);
            simpleDateFormat.setTimeZone(timeZone);
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }
    public static Date parse(String template, String dateStr, TimeZone timeZone) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(template);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.parse(dateStr);
    }
    public static Date parse(String template, String dateStr) throws ParseException {
        return new SimpleDateFormat(template).parse(dateStr);
    }
    public static Date parseWithNoE(String template, String dateStr) {
        try {
            return new SimpleDateFormat(template).parse(dateStr);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    public Date parse(String dateStr) throws ParseException {
        return new SimpleDateFormat(template).parse(dateStr);
    }

    public Date parse(String dateStr, TimeZone timeZone) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(template);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.parse(dateStr);
    }



    public String format(Date date) {
        return new SimpleDateFormat(template).format(date);
    }

    public String format(Date date, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(template);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(date);
    }

    public static String format(String template, Date date, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(template);
        if (timeZone!=null)
            simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(date);
    }

    public String getTemplate() {
        return template;
    }

    public static int retrieve(Date date, int calendarUnit, TimeZone timeZone) {
        GregorianCalendar gregorianCalendar = timeZone != null ? new GregorianCalendar(timeZone) : new GregorianCalendar();
        gregorianCalendar.setTime(date);
        return gregorianCalendar.get(calendarUnit);
    }

    public static Date removeTimePart(Date date, TimeZone timeZone) {
        GregorianCalendar gregorianCalendar = timeZone != null ? new GregorianCalendar(timeZone) : new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.HOUR_OF_DAY, 0);
        gregorianCalendar.set(Calendar.MINUTE, 0);
        gregorianCalendar.set(Calendar.SECOND, 0);
        gregorianCalendar.set(Calendar.MILLISECOND, 0);
        return gregorianCalendar.getTime();
    }

    public static Date removeTimePart(Date date) {
        return removeTimePart(date, null);
    }

    public static Date removeDatePart(Date date, TimeZone timeZone) {
        GregorianCalendar gregorianCalendar = timeZone != null ? new GregorianCalendar(timeZone) : new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.YEAR, 1970);
        gregorianCalendar.set(Calendar.MONTH, 0);
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return gregorianCalendar.getTime();
    }

    public static Date removeDatePart(Date date) {
        return removeDatePart(date, null);
    }

    public static Date add(Date date, int calendarUnit, int nb) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendarUnit, nb);
        return calendar.getTime();

    }

    public static long nbBetweenDates(Date dateStart, Date dateEnd, ChronoUnit chronoUnit) {
        LocalDateTime dateStart2 = LocalDateTime.ofInstant(dateStart.toInstant(), ZoneId.systemDefault());
        LocalDateTime dateEnd2 = LocalDateTime.ofInstant(dateEnd.toInstant(), ZoneId.systemDefault());
        return chronoUnit.between(dateStart2, dateEnd2);
    }
}
