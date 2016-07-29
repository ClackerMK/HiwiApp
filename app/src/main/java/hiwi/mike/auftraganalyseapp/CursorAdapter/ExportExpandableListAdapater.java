package hiwi.mike.auftraganalyseapp.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.R;

/**
 * Created by dave on 28.07.16.
 */
public class ExportExpandableListAdapater extends BaseExpandableListAdapter {

    private Context _context;
    private SQLiteDatabase _db;
    private List<Pair<Integer, String>> _listDataHeader;
    private HashMap<Integer, List<Pair<Integer, String>>> _listDataChild;

    public ExportExpandableListAdapater(Context context, SQLiteDatabase db)
    {
        _context = context;
        _db = db;

        Cursor wbCrs = db.rawQuery(WorkbookContract.GET_ALL_WORKBOOKS(),null);

        _listDataHeader = new Vector<Pair<Integer,String>>();
        _listDataChild = new HashMap<Integer, List<Pair<Integer,String>>>();
        while (wbCrs.moveToNext())
        {
            final Integer wbId = wbCrs.getInt(wbCrs.getColumnIndexOrThrow("_id"));
            final String wbName = wbCrs.getString(wbCrs.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));

            _listDataHeader.add(new Pair<Integer, String>(wbId, wbName));

            Cursor prjCrs = db.rawQuery(WorkbookContract.GET_WORKSTATIONS_BY_WORKBOOK(wbId),null);
            List<Pair<Integer, String>> prjList = new Vector<Pair<Integer, String>>();
            while (prjCrs.moveToNext())
            {
                final Integer prjId = prjCrs.getInt(wbCrs.getColumnIndexOrThrow("_id"));
                final String prjName = prjCrs.getString(wbCrs.getColumnIndexOrThrow(WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME));

                prjList.add(new Pair<Integer, String>(prjId, prjName));
            }
            _listDataChild.put(wbId, prjList);
            prjCrs.close();
        }
        wbCrs.close();
    }

    @Override
    public int getGroupCount() {
        return _listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return _listDataChild.get(_listDataHeader.get(groupPosition).first).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _listDataChild.get(_listDataHeader.get(groupPosition).first).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String name = ((Pair<Integer, String>) getGroup(groupPosition)).second;


        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exportlist_group, null);
        }

        TextView header = (TextView) convertView.findViewById(R.id.elHeader);
        header.setTypeface(null, Typeface.BOLD);
        header.setText(name);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String name = ((Pair<Integer, String>) getChild(groupPosition, childPosition)).second;


        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exportlist_item, null);
        }

        TextView header = (TextView) convertView.findViewById(R.id.elItem);
        header.setTypeface(null, Typeface.NORMAL);
        header.setText(name);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
