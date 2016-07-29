package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dave on 21.07.16.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView targetText;

    public void setTargetText(TextView targetText) {
        this.targetText = targetText;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int y, m, d;

        Pattern pattern = Pattern.compile("([0-3]?[0-9]).([0-1]?[0-9]?).([0-9]*)");
        Matcher matcher = pattern.matcher(targetText.getText());

        if (matcher.matches()) {
            d = Integer.parseInt(matcher.group(1));
            m = Integer.parseInt(matcher.group(2));
            y = Integer.parseInt(matcher.group(3));
        } else {

            final Calendar c = Calendar.getInstance();

            y = c.get(Calendar.YEAR);
            m = c.get(Calendar.MONTH);
            d = c.get(Calendar.DAY_OF_MONTH);
        }
        return new DatePickerDialog(getActivity(), this, y, m, d);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        targetText.setText(String.format("%d.%d.%d", dayOfMonth, monthOfYear+1, year));
    }
}
