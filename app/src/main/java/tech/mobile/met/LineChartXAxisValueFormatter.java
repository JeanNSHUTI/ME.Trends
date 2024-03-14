package tech.mobile.met;

import android.util.Log;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class LineChartXAxisValueFormatter extends IndexAxisValueFormatter {

    @Override
    public String getFormattedValue(float value) {

        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        //long emissionsMilliSince1970Time = ((long) value) ;  //* 1000
        long emissionsMilliSince1970Time = Long.parseUnsignedLong(String.valueOf(value));

        // Show time in local version
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time * 1000);
        DateFormat dateTimeFormat = DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.getDefault());
        Log.v("DATEEPOCH2", "Date:" + timeMilliseconds);
        Log.v("DATEEPOCH2", "Date:" + timeMilliseconds.getTime());

        String FORMAT = "yyyy MM dd";
        //return dateTimeFormat.format(timeMilliseconds);
        return android.text.format.DateFormat.format(FORMAT, timeMilliseconds).toString();
    }
}
