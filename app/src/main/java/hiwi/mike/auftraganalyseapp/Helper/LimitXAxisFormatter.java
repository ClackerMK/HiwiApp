package hiwi.mike.auftraganalyseapp.Helper;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by dave on 28.09.16.
 */
public class LimitXAxisFormatter implements AxisValueFormatter {
    float min;
    float max;

    DecimalFormat mFormat;

    public LimitXAxisFormatter(float min, float max)
    {
        this.min = min;
        this.max = max;

        mFormat = new DecimalFormat("###");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value < min)
        {
            return "<" + mFormat.format(min);
        } else if (value > max)
        {
            return ">" + mFormat.format(max);
        }else
        {
            return mFormat.format(value);
        }
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}
