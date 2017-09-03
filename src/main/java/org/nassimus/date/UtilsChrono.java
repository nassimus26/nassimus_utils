package org.nassimus.date;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilsChrono {

    private static final Logger logger = LoggerFactory.getLogger(UtilsChrono.class);

    private Date dateStart;
    private Date dateCurr;

    static public UtilsChrono getInstance() {
        return new UtilsChrono();
    }

    private UtilsChrono() {
        dateStart = new Date();
    }

    public long getMilliSecondes() {
        dateCurr = new Date();
        return dateCurr.getTime() - dateStart.getTime();
    }

    public String getFormattedChrono(SdfEnum sdfEnum) {
        dateCurr = new Date();
        return sdfEnum.format(new Date(dateCurr.getTime() - dateStart.getTime()));
    }

    public String getFormattedSS_MS() {
        dateCurr = new Date();
        double l = dateCurr.getTime() - dateStart.getTime();
        return Double.toString(l / 1000);
    }

    public String getFormattedSS_MSFull() {
        dateCurr = new Date();
        double l = dateCurr.getTime() - dateStart.getTime();
        return new StringBuffer("Elapsed time : ").append(l / 1000).append("s").toString();
    }

    public Date getDateCurr() {
        return dateCurr;
    }

    public void reset() {
        dateStart = new Date();
    }

    public static void sleepNoException(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 0D_0H_3M_54S
     * 
     * @param millis
     * @return
     */

    public static String formatMillisDuration(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be up to 0!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append("D_");
        sb.append(hours);
        sb.append("H_");
        sb.append(minutes);
        sb.append("M_");
        sb.append(seconds);
        sb.append("S");

        return (sb.toString());
    }

}
