package hiwi.mike.auftraganalyseapp.Helper;

import android.widget.Spinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dave on 16.08.16.
 */
public class Helper {
    public static final DateFormat DMYFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateFormat ISOFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static int daysBetween(Calendar day1, Calendar day2){
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR))
        {
            return (dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR))
            {
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYeaDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            int result = extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYeaDays;

            if (day1.get(Calendar.YEAR) < day2.get(Calendar.YEAR)) {
                return result;
            } else {
                return -(result);
            }

        }
    }

    public static Calendar DateToCalendar(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static boolean isValidDate(String inDate)
    {
        try {
            DMYFormat.parse(inDate.trim());
        } catch (ParseException pe){
            return false;
        }
        return true;
    }

    public static String ISOtoDMY(String str) {
        try {
            return DMYFormat.format(ISOFormat.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String DMYtoISO(String str) {
        try {
            return ISOFormat.format(DMYFormat.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Integer searchSpinnerForValue(Spinner spinner, String value)
    {
        for (int i = 0; i < spinner.getCount(); i++)
        {
            if (spinner.getItemAtPosition(i).equals(value))
                return i;
        }
        return -1;
    }
}
