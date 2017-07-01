package com.firstapp.android.iaccounts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVWriter;
import jxl.LabelCell;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Created by Ibrahimkb on 17-12-2015.
 */
public class BudgetManager extends Activity {
    public Vector vdr = new Vector();
    public Vector vcr = new Vector();
    public Vector vdate = new Vector();
    public Vector vcat = new Vector();
    public Vector vnar = new Vector();
    public Vector vmonth = new Vector();
    public static final String KEY_CREDIT = "credit";
    public static final String KEY_EXPENSE = "expense";
    public static final String KEY_DEBIT = "debit";
    public static final String KEY_ID = "_id";
    public static final String KEY_CATEGORY = "category";
    public int count;

    public static final String KEY_DATE = "date";
    public static final String KEY_MONTH = "month";
    public static final String KEY_MONTH_CODE = "monthcode";
    public static final String KEY_YEAR_CODE = "yearcode";

    public static final String KEY_DAY_CODE = "daycode";
    public static final String KEY_ACCOUNT = "account";


    public static final String DATABASE_NAME = "Expense";
    public static final String DATABASE_TABLE = "Expensetable";
    public static final int DATABASE_VERSION = 4;

    public static final String Sort = " ORDER BY "+ KEY_MONTH_CODE +" DESC, " + KEY_DAY_CODE+ " DESC;";



    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;
    private final Context ourContext;

        public void ExportToExcel(String selected_account_for_export){
            Cursor cur = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE + " WHERE "+KEY_ACCOUNT+" ='"+selected_account_for_export+
                    "'"+ Sort,null);
            cur.moveToFirst();
            String cr = gettotalcr(selected_account_for_export);
            String dr = gettotaldr(selected_account_for_export);
            exportToExcel(cur,selected_account_for_export,dr,cr);

        }





    private void exportToExcel(Cursor cursor,String selected_account_for_export,String debit,String credit) {
        //
        int col;
        final String fileName = selected_account_for_export+".xls";

        //Saving file in external storage
        //File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(Environment.getExternalStorageDirectory() + "/Expense Manager");
        if(!directory.isDirectory())
        directory.mkdirs();


        //file path
        File file = new File(directory, fileName);
        /*File ifile= new File(Environment.getExternalStorageDirectory().toString() + "/MasterofCoin");
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(ifile));
        sendBroadcast(intent);*/


        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;

        try {

            workbook = Workbook.createWorkbook(file,wbSettings);



            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet(selected_account_for_export,0);

            WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            WritableCellFormat bold = new WritableCellFormat(boldFont);


            try {
                sheet.addCell(new Label(0, 0, "Date",bold));// column and row
                sheet.addCell(new Label(1, 0, "Category",bold));
                sheet.addCell(new Label(2, 0, "Description",bold));
                sheet.addCell(new Label(3, 0, "Debit",bold));
                sheet.addCell(new Label(4, 0, "Credit",bold));
                if (cursor.moveToFirst()) {
                    do {
                        String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                        String category = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY));
                        String desc = cursor.getString(cursor.getColumnIndex(KEY_EXPENSE));
                        String dr = cursor.getString(cursor.getColumnIndex(KEY_DEBIT));
                        String cr = cursor.getString(cursor.getColumnIndex(KEY_CREDIT));
                        col = cursor.getPosition() + 1;
                        sheet.addCell(new Label(0, col, date));
                        sheet.addCell(new Label(1, col, category));
                        sheet.addCell(new Label(2, col, desc));
                        sheet.addCell(new Label(3, col, dr));
                        sheet.addCell(new Label(4, col, cr));
                    } while (cursor.moveToNext());
                    sheet.addCell(new Label(0,col+1,"TOTAL",bold));
                    sheet.addCell(new Label(3,col+1,debit,bold));
                    sheet.addCell(new Label(4,col+1,credit,bold));
                    String bal = String.valueOf(Float.valueOf(Float.valueOf(credit) - Float.valueOf(debit)));
                    sheet.addCell(new Label(5,col+1,"Balance = " + bal,bold));
                }
                //closing cursor
                cursor.close();
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();

            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



        public Integer godOfFilter(String selectedMonth, String selectedCategory, String selectedView, String selected_account, String selected_year){
            int rere = 0;

            if(selectedMonth.equals("All") && selectedCategory.equals("All") && selectedView.equals("All")){
                Cursor a = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE +" WHERE "+KEY_ACCOUNT+"='"+
                        selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = a.getCount();
                FilterHelper(count,a);
                if(FilterHelper(count,a)==1){
                    rere =1;
                }
            }
            else if(selectedMonth.equals("All") && selectedCategory.equals("All") && selectedView.equals("Income")){
                Cursor b = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_DEBIT + "='0' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = b.getCount();
                FilterHelper(count,b);
                if(FilterHelper(count,b)==1){
                    rere =1;
                }
            }
            else if(selectedMonth.equals("All") && selectedCategory.equals("All") && selectedView.equals("Expense")){
                Cursor c = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_CREDIT + "='0' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = c.getCount();
                FilterHelper(count,c);
                if(FilterHelper(count,c)==1){
                    rere =1;
                }

            }
            else if(selectedMonth.equals("All") && selectedView.equals("All") ){
                Cursor d = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_CATEGORY +"='"+selectedCategory+"' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = d.getCount();
                FilterHelper(count,d);
                if(FilterHelper(count,d)==1){
                    rere =1;
                }

            }else if(selectedMonth.equals("All") && selectedView.equals("Income")){
                Cursor g = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_CATEGORY +"='"+selectedCategory+"' AND "+ KEY_DEBIT+ "='0' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = g.getCount();
                FilterHelper(count,g);
                if(FilterHelper(count,g)==1) {
                    rere = 1;
                }


            }else if(selectedMonth.equals("All") && selectedView.equals("Expense")){
                Cursor g = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_CATEGORY +"='"+selectedCategory+"' AND "+ KEY_CREDIT+ "='0' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = g.getCount();
                FilterHelper(count,g);
                if(FilterHelper(count,g)==1) {
                    rere = 1;
                }

            }
            else if( selectedCategory.equals("All") && selectedView.equals("All")) {

                Cursor e = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_MONTH +"='"+selectedMonth+"' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = e.getCount();
                FilterHelper(count,e);
                if(FilterHelper(count,e)==1){
                    rere =1;
                }
                e.close();
            }
            else if(selectedCategory.equals("All") && selectedView.equals("Income")){
                Cursor f = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_MONTH +
                        "='"+selectedMonth+"' AND " + KEY_DEBIT + "='0' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = f.getCount();
                FilterHelper(count,f);
                if(FilterHelper(count,f)==1){
                    rere =1;
                }

            }else if(selectedCategory.equals("All") && selectedView.equals("Expense")){
                Cursor g = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_MONTH +
                        "='"+selectedMonth+"' AND " + KEY_CREDIT + "='0' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = g.getCount();
                FilterHelper(count,g);
                if(FilterHelper(count,g)==1){
                    rere =1;
                }

            }
            else if(selectedView.equals("All")){
                Cursor h = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_CATEGORY +"='"+selectedCategory+"' AND "+ KEY_MONTH +
                        "='"+selectedMonth+"'AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = h.getCount();
                FilterHelper(count,h);
                if(FilterHelper(count,h)==1){
                    rere =1;
                }


            }
            else if(selectedView.equals("Income")){
                Cursor h = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_CATEGORY +"='"+selectedCategory+"' AND "+ KEY_MONTH +
                        "='"+selectedMonth+"' AND " + KEY_DEBIT + "='0' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = h.getCount();
                FilterHelper(count,h);
                if(FilterHelper(count,h)==1){
                    rere =1;
                }

            }
            else if(selectedView.equals("Expense")){
                Cursor g = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_CATEGORY +"='"+selectedCategory+"' AND "+ KEY_MONTH +
                        "='"+selectedMonth+"' AND " + KEY_CREDIT+ "='0' AND " +
                        KEY_ACCOUNT+ "='" +selected_account+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"'"+ Sort,null);
                count = g.getCount();
                FilterHelper(count,g);
                if(FilterHelper(count,g)==1){
                    rere =1;
                }

            }



            return rere;
        }

    public Integer FilterHelper(int count, Cursor c){
        int re = 0;
        if (count == 0) {
            re = 1;
        } else {
            int fmnth = c.getColumnIndex(KEY_MONTH);
            int fdate = c.getColumnIndex(KEY_DATE);
            int fcat = c.getColumnIndex(KEY_CATEGORY);
            int fdr = c.getColumnIndex(KEY_DEBIT);
            int fcr = c.getColumnIndex(KEY_CREDIT);
            int fnar = c.getColumnIndex(KEY_EXPENSE);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {


                vmonth.addElement(c.getString(fmnth));
                vdr.addElement(c.getString(fdr));
                vcr.addElement(c.getString(fcr));
                vdate.addElement(c.getString(fdate));
                vcat.addElement(c.getString(fcat));
                vnar.addElement(c.getString(fnar));
            }


        }
        return re;
    }

    public Integer datefilter(String selecteddate, String selected_acc){
        int rere=0;
        Cursor g = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_DATE +"='"+selecteddate +"' AND "+
                KEY_ACCOUNT + "='"+selected_acc+"';",null);
        count = g.getCount();
        FilterHelper(count,g);
        if(FilterHelper(count,g)==1){
            rere =1;
        }
        return rere;
    }

    public long createEntry(String date, String category,String expense, String credit,String debit,String month,String mcode,String dcode,String account, String ycode) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_EXPENSE,expense);
        cv.put(KEY_CREDIT, credit);
        cv.put(KEY_DATE,date);
        cv.put(KEY_DEBIT,debit);
        cv.put(KEY_CATEGORY,category);
        cv.put(KEY_MONTH,month);
        cv.put(KEY_MONTH_CODE,mcode);
        cv.put(KEY_DAY_CODE,dcode);
        cv.put(KEY_ACCOUNT,account);
        cv.put(KEY_YEAR_CODE,ycode);

        return ourDatabase.insert(DATABASE_TABLE,null,cv);

    }


    public String gettotaldr(String s){

            Cursor c = ourDatabase.rawQuery("SELECT SUM (" + KEY_DEBIT + ") FROM " + DATABASE_TABLE + " WHERE "+KEY_ACCOUNT+"='"+s+"';" , null);
            if (c.moveToFirst())
                return c.getString(0);
        return null;
    }
    public String gettotalcr(String s){

        Cursor c = ourDatabase.rawQuery("SELECT SUM (" + KEY_CREDIT + ") FROM " + DATABASE_TABLE+ " WHERE "+KEY_ACCOUNT+"='"+s+"';" , null);
            if (c.moveToFirst())
                return c.getString(0);

        return null;

    }
    public void deleteDb(){
        ourContext.deleteDatabase(DATABASE_NAME);

        //ourHelper.onUpgrade(ourDatabase,DATABASE_VERSION,2);
    }

    public boolean isEmpty(){
        Cursor c = ourDatabase.rawQuery("SELECT COUNT(*) FROM "+DATABASE_TABLE,null);
        if (c.moveToFirst()){
            if(c.getInt(0)==0)
                return true;
        }
        return false;
    }




    public void deleteEntry(String todeletedate,String todeletecat,String todeletecr,String todeletedr,String selected_acc) {
        if(todeletecr.equals("0")) {
             ourDatabase.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE " + KEY_DATE + "='" + todeletedate + "' AND " + KEY_DEBIT + "='"
                    + todeletedr + "' AND " + KEY_CATEGORY + "='" + todeletecat + "' AND "+
                     KEY_ACCOUNT + "='"+selected_acc+"';");
        }else if(todeletedr.equals("0")){
             ourDatabase.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE " + KEY_DATE + "='" + todeletedate + "' AND " + KEY_CREDIT + "='"
                    + todeletecr + "' AND " + KEY_CATEGORY + "='" + todeletecat + "' AND "+
                     KEY_ACCOUNT + "='"+selected_acc+"';");

        }

    }

    public String gettotaldrmonth(String selected_month, String selected_acc, String selected_year) {
        Cursor c;
        if(selected_month.equals("All") && selected_year.equals("All")) {
             c = ourDatabase.rawQuery("SELECT SUM (" + KEY_DEBIT + ") FROM " + DATABASE_TABLE+ " WHERE "+ KEY_ACCOUNT +
                     "='"+selected_acc+"';", null);
        }
            else if(selected_month.equals("All")) {
             c = ourDatabase.rawQuery("SELECT SUM (" + KEY_DEBIT + ") FROM " + DATABASE_TABLE + " WHERE "+
                     KEY_ACCOUNT + "='"+selected_acc+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"';", null);
        }else if(selected_year.equals("All")){
            c = ourDatabase.rawQuery("SELECT SUM (" + KEY_DEBIT + ") FROM " + DATABASE_TABLE + " WHERE " + KEY_MONTH + "='" + selected_month + "' AND "+
                    KEY_ACCOUNT + "='"+selected_acc+"';", null);
        }
        else {
            c = ourDatabase.rawQuery("SELECT SUM (" + KEY_DEBIT + ") FROM " + DATABASE_TABLE + " WHERE " + KEY_MONTH + "='" + selected_month + "' AND "+
                    KEY_ACCOUNT + "='"+selected_acc+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"';", null);

        }
        if (c.moveToFirst())
            if(c.isNull(0))
                return  "0";
            else
                return c.getString(0);

        return "0";


    }

    public String gettotalcrmonth(String selected_month, String selected_acc, String selected_year) {
        Cursor c;
        if(selected_month.equals("All") && selected_year.equals("All")) {
            c = ourDatabase.rawQuery("SELECT SUM (" + KEY_CREDIT + ") FROM " + DATABASE_TABLE+ " WHERE "+ KEY_ACCOUNT +
                    "='"+selected_acc+"';", null);
        }
        else if(selected_month.equals("All")) {
            c = ourDatabase.rawQuery("SELECT SUM (" + KEY_CREDIT + ") FROM " + DATABASE_TABLE + " WHERE "+
                    KEY_ACCOUNT + "='"+selected_acc+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"';", null);
        }else if(selected_year.equals("All")){
            c = ourDatabase.rawQuery("SELECT SUM (" + KEY_CREDIT + ") FROM " + DATABASE_TABLE + " WHERE " + KEY_MONTH + "='" + selected_month + "' AND "+
                    KEY_ACCOUNT + "='"+selected_acc+"';", null);
        }
        else {
            c = ourDatabase.rawQuery("SELECT SUM (" + KEY_CREDIT + ") FROM " + DATABASE_TABLE + " WHERE " + KEY_MONTH + "='" + selected_month + "' AND "+
                    KEY_ACCOUNT + "='"+selected_acc+"' AND "+KEY_YEAR_CODE+"='"+selected_year+"';", null);

        }
        if (c.moveToFirst())
            if(c.isNull(0))
            return  "0";
            else
            return c.getString(0);

        return "0";

    }


    public void editCategory(String selected, String selectednew) {

        ourDatabase.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_CATEGORY + "='"+selectednew+"' WHERE "+
                KEY_CATEGORY+"='"+selected+"';");
    }

    public void editAcc(String selected, String selectednew) {

        ourDatabase.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_ACCOUNT + "='"+selectednew+"' WHERE "+
                KEY_ACCOUNT+"='"+selected+"';");
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE "+DATABASE_TABLE+ " (" +
                            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_DATE + " TEXT NOT NULL, " + KEY_CATEGORY + " TEXT NOT NULL, " +
                            KEY_EXPENSE + " TEXT NOT NULL, "+ KEY_CREDIT + " TEXT NOT NULL, "+
            KEY_DEBIT +" TEXT NOT NULL, " + KEY_MONTH + " TEXT NOT NULL, " + KEY_MONTH_CODE + " INTEGER, " + KEY_DAY_CODE +
            " INTEGER, "+KEY_ACCOUNT+" TEXT NOT NULL, "+KEY_YEAR_CODE+" TEXT NOT NULL);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


                /*if(oldVersion<2) {
                    db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_MONTH_CODE + " INTEGER;");
                    db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_DAY_CODE + " INTEGER;");
                }
                else */if(oldVersion<3) {
                db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_ACCOUNT + " Varchar(30);");
                db.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_ACCOUNT + "='General';");

            } else if(oldVersion<4){

                db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_YEAR_CODE + " Varchar(30);");
                db.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_YEAR_CODE + "='2016';");
            }



        }
    }

    public BudgetManager(Context c){
        ourContext = c;
    }
    public BudgetManager open() throws SQLException {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();

        return this;
    }
    public void close(){
        ourHelper.close();

    }


}
