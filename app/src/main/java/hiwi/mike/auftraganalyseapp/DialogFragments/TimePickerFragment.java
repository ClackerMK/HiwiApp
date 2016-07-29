package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dave on 21.07.16.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{

    TextView targetText;

    public void setTargetText(TextView targetText) {
        this.targetText = targetText;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int s = 0, m = 0;
        Pattern p1 = Pattern.compile("([0-9]+?):([0-9]{1,2})");
        Pattern p2 = Pattern.compile("[0-9]{1,2}");

        Matcher m1 = p1.matcher(targetText.getText());
        Matcher m2 = p2.matcher(targetText.getText());

        if (m1.matches())
        {
            m = Integer.parseInt(m1.group(1));
            s = Integer.parseInt(m1.group(2));
        } else if (m2.matches())
        {
            s = Integer.parseInt(m1.group(1));
        }

        return new TimePickerDialog(getActivity(), this, m, s, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        targetText.setText(String.format("%d:%d",hourOfDay, minute));
    }
}
