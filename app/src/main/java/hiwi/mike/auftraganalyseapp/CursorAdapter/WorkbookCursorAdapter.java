package hiwi.mike.auftraganalyseapp.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 08.06.16.
 */

public class WorkbookCursorAdapter extends CursorAdapter{
    public WorkbookCursorAdapter(Context context, Cursor cursor, int flags)
    {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));
        int project_count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

        tvHeader.setText(name);
        tvBody.setText(String.format("Projekte: %d",project_count));

        view.setTag(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
    }

    @Override
    public void changeCursor(Cursor cursor)
    {
        super.changeCursor(cursor);
    }
}
