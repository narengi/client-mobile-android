package xyz.narengi.android.util;

import android.content.Context;

import com.byagowi.persiancalendar.Entity.Day;
import com.byagowi.persiancalendar.Utils;

import java.util.Calendar;
import java.util.Date;

import calendar.CivilDate;
import calendar.DateConverter;

/**
 * @author Siavash Mahmoudpour
 */
public class DateUtils {

    private static DateUtils instance;
    private Context context;

    private DateUtils(Context context) {
        this.context = context;
    }

    public static DateUtils getInstance(Context context) {
        if (instance == null)
            instance = new DateUtils(context);
        return instance;
    }

    public String getSelectedDateString(Day day) {

        String dateString = "";
        if (day != null) {
            char[] digits = Utils.getInstance().preferredDigits(context);
            dateString = Utils.getInstance().dateToString(day.getPersianDate(), digits);
        }

        return dateString;
    }

    public Date getDateOfDay(Day day) {

        Date date = null;

        if (day != null && day.getPersianDate() != null) {
            CivilDate civilDate = DateConverter.persianToCivil(day.getPersianDate());
            Calendar calendar = Calendar.getInstance();
            calendar.set(civilDate.getYear(), civilDate.getMonth() - 1, civilDate.getDayOfMonth());
            date = calendar.getTime();
        }

        return date;
    }
}
