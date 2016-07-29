package hiwi.mike.auftraganalyseapp.DialogFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.Locale;

import hiwi.mike.auftraganalyseapp.Database.WorkbookContract;
import hiwi.mike.auftraganalyseapp.Database.WorkbookDbHelper;
import hiwi.mike.auftraganalyseapp.R;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by dave on 21.07.16.
 */
public class ExportDialogFragment extends DialogFragment {
    private Integer workbook_id = null;
    private Integer project_id = null;

    private Integer checked_id = null;

    public void setWorkbook_id(Integer workbook_id) {
        this.workbook_id = workbook_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Exportieren");
        CharSequence[] listItems;

        WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String nameMappe = "";
        String nameProjekt = "";
        if (workbook_id != null) {
            Cursor crs = db.rawQuery(WorkbookContract.GET_WORKBOOKS_BY_ID(workbook_id), null);
            crs.moveToFirst();
            nameMappe = crs.getString(crs.getColumnIndexOrThrow(
                    WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));
            crs.close();
        }
        if (project_id != null) {
            Cursor crs = db.rawQuery(WorkbookContract.GET_PROJECTS_BY_WORKBOOK(project_id), null);
            crs.moveToFirst();
            nameProjekt = crs.getString(crs.getColumnIndexOrThrow(
                    WorkbookContract.ProjectEntry.COLUMN_NAME_ENTRY_NAME
            ));
            crs.close();
        }


        if (workbook_id == null && project_id == null) {
            listItems = new CharSequence[]{"Alle Arbeitsmappen"};
        } else if (workbook_id != null && project_id == null) {
            listItems = new CharSequence[]{"Alle Arbeitsmappen",
                    "Aktuelle Arbeitsmappe: " + nameMappe};
        } else if (workbook_id != null && project_id != null) {
            listItems = new CharSequence[]{"Alle Arbeitsmappe",
                    "Aktuelle Arbeitsmappe: " + nameMappe,
                    "Aktuelles Projekt: " + nameProjekt};
        } else {
            throw new IllegalStateException();
        }

        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Export onClick() item", Integer.toString(which));
                checked_id = which;
            }
        });

        builder.setPositiveButton("xsl", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checked_id == 2)
                {
                    exportToXSL(new int[] {workbook_id}, new int[] {project_id}, true);
                }
            }
        });

        return builder.create();
    }

    private void exportToXSL(int[] workbooks, int[] projects, boolean override) {
        if (!(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())))
        {
            Log.d("export XSL", "no external Staorage");
            return;
        }


        WorkbookDbHelper dbHelper = new WorkbookDbHelper(getActivity());
        SQLiteDatabase sqlDB = dbHelper.getReadableDatabase();
        Cursor crs;
        WorkbookSettings wbSettings = new WorkbookSettings();
        WritableFont arialFont = new WritableFont(WritableFont.ARIAL, 10);
        WritableCellFormat arialFormat = new WritableCellFormat(arialFont);
        try {
            arialFormat.setWrap(true);
        } catch (WriteException e) {
            e.printStackTrace();
        }
        WritableFont arialBoldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        WritableCellFormat arialBoldFormat = new WritableCellFormat(arialBoldFont);
        try {
            arialBoldFormat.setWrap(true);
        } catch (WriteException e) {
            e.printStackTrace();
        }

        final String fExtension = ".xls";

        wbSettings.setLocale(new Locale("de", "DE"));

        // Create a new Excel Workbook for each Workbook
        for (int i = 0; i < workbooks.length; ++i) {
            String workbookName;
            File file;

            crs = sqlDB.rawQuery(WorkbookContract.GET_WORKBOOKS_BY_ID(workbooks[i]), null);
            crs.moveToFirst();
            workbookName = crs.getString(crs.getColumnIndexOrThrow(WorkbookContract.WorkbookEntry.COLUMN_NAME_ENTRY_NAME));
            crs.close();
            file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                            "/" + getText(R.string.app_name).toString());

            if (!file.mkdirs())
            {
                Log.e("export xls", "Directory not created");
                //return;
            }

            file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                            "/" + getText(R.string.app_name).toString(), workbookName + fExtension);

            if (file.exists()) {
                if (!override) {
                    int a = 0;
                    while (!file.exists() || file.isDirectory()) {
                        file = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                                        "/" + getText(R.string.app_name).toString(), workbookName + ++a + fExtension);
                    }
                } else if (file.isDirectory()) {
                    int a = 0;
                    while (file.isDirectory()) {
                        file = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                                        "/" + getText(R.string.app_name).toString(), workbookName + ++a + fExtension);
                    }
                }
            }


            WritableWorkbook workbook = null;
            try {
                workbook = Workbook.createWorkbook(file, wbSettings);
            } catch (Throwable e) {
                Log.e("export", e.toString(), e);
            }

            // Insert all Projects as Worksheets into the Workbook
            for (int y = 0; y < projects.length; ++y) {
                String worksheetName;
                WritableSheet worksheet;

                crs = sqlDB.rawQuery(WorkbookContract.GET_PROJECTS_BY_ID(projects[y]), null);
                crs.moveToFirst();

                if (crs.getInt(crs.getColumnIndexOrThrow(
                        WorkbookContract.ProjectEntry.COLUMN_NAME_WORKBOOK_ID)) == workbooks[i]) {
                    worksheetName = crs.getString(crs.getColumnIndexOrThrow(
                            WorkbookContract.ProjectEntry.COLUMN_NAME_ENTRY_NAME));
                    worksheet = workbook.createSheet(worksheetName, Integer.MAX_VALUE);

                    // Add caption
                    try {
                        worksheet.addCell(new Label(0, 0, "#", arialBoldFormat));
                        worksheet.addCell(new Label(1, 0, "Zieldatum", arialBoldFormat));
                        worksheet.addCell(new Label(2, 0, "ZAU", arialBoldFormat));
                        worksheet.addCell(new Label(3, 0, "WIP", arialBoldFormat));
                        worksheet.addCell(new Label(4, 0, "Arbeitsstation", arialBoldFormat));
                    }catch (WriteException e)
                    {
                        e.printStackTrace();
                    }
                    crs = sqlDB.rawQuery(WorkbookContract.GET_ORDERS_BY_PROJECT(projects[y]),null);
                    crs.moveToFirst();
                    int a = 1;
                    while (!crs.isAfterLast()) {
                        try {
                            worksheet.addCell(new Label(0, a,
                                    crs.getString(crs.getColumnIndexOrThrow(
                                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_NR)),
                                    arialFormat));
                            worksheet.addCell(new Label(1, a,
                                    crs.getString(crs.getColumnIndexOrThrow(
                                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TARGET_DATE)),
                                    arialFormat));

                            worksheet.addCell(new Label(2, a,
                                    crs.getString(crs.getColumnIndexOrThrow(
                                            WorkbookContract.OrderEntry.COLUMN_NAME_ENTRY_TIME)),
                                    arialFormat));
                            worksheet.addCell(new Number(3, a,
                                    crs.getInt(crs.getColumnIndexOrThrow(
                                            WorkbookContract.OrderEntry.COLUMN_NAME_WIP)),
                                    arialFormat));
                            worksheet.addCell(new Label(4, a,
                                    crs.getString(crs.getColumnIndexOrThrow(
                                            WorkbookContract.WorkstationEntry.COLUMN_NAME_ENTRY_NAME)),
                                    arialFormat));
                        } catch (WriteException e) {
                            e.printStackTrace();
                        }
                        a++;
                        crs.moveToNext();
                    }
                    crs.close();
                }
            }

            try {
                assert workbook != null;
                workbook.write();
                workbook.close();
            } catch (Exception e) {
                Log.e("export", e.toString(), e);
            }
        }
    }
}
