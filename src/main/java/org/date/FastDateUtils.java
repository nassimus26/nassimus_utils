package org.date;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by VF5416 on 10/09/2016.
 */
public class FastDateUtils {
    private final Calendar c = Calendar.getInstance();
    public Date add(Date date,int calendarField,int amount){
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }
    public Date set(Date date,int calendarField,int amount){
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }
    public int get(Date date,int calendarField){
        c.setTime(date);
        return c.get(calendarField);
    }
    public Date addSeconds(Date date,int amount){
        return add( date, Calendar.SECOND, amount );
    }

    public Date cleanDateMargin(Date date, int calendarField){
        if (calendarField == Calendar.MONTH) {
            date = set(date, Calendar.DAY_OF_MONTH, 1);
            date = set(date, Calendar.HOUR_OF_DAY, 0);
            date = set(date, Calendar.MINUTE, 0);
            date = set(date, Calendar.SECOND, 0);
            date = set(date, Calendar.MILLISECOND, 0);
        }
        if (calendarField == Calendar.DAY_OF_MONTH) {
            date = set(date, Calendar.HOUR_OF_DAY, 0);
            date = set(date, Calendar.MINUTE, 0);
            date = set(date, Calendar.SECOND, 0);
            date = set(date, Calendar.MILLISECOND, 0);
        }
        if (calendarField == Calendar.MINUTE) {
            date = set(date, Calendar.SECOND, 0);
            date = set(date, Calendar.MILLISECOND, 0);
        }
        return date;
    }

}
