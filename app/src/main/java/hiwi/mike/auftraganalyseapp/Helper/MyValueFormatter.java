package hiwi.mike.auftraganalyseapp.Helper;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by dave on 28.09.16.
 */
public class MyValueFormatter implements ValueFormatter {
    private DecimalFormat mFormat;

    public MyValueFormatter (){
        mFormat = new DecimalFormat("###");
    }


    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value);
    }
}
