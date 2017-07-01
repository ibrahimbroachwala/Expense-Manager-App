package com.firstapp.android.iaccounts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by Ibrahimkb on 30-12-2015.
 */
public class CategoryManager {
    public Vector vIcategories = new Vector();
    public Vector vEcategories = new Vector();
    public Vector vacc = new Vector();

    public  static  final  String KEY_ID = "id";
    public static final String KEY_ICATEGORIES = "icategories";
    public static final String KEY_ECATEGORIES = "ecategories";
    public static final String KEY_ACCOUNTS = "accounts";

    public static final String DATABASE_NAME = "Categories";
    public static final String DATABASE_TABLE = "Categorytable";
    public static final int DATABASE_VERSION = 2;

    private DbHelper ourHelper;
    private SQLiteDatabase ourDatabase;
    private final Context ourContext;


    public long createEntry(String ecat, String icat, String acc) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ECATEGORIES,ecat);
        cv.put(KEY_ICATEGORIES, icat);
        cv.put(KEY_ACCOUNTS,acc);

        return ourDatabase.insert(DATABASE_TABLE,null,cv);
    }

    public boolean isEmpty(){
        Cursor c = ourDatabase.rawQuery("SELECT COUNT(*) FROM "+DATABASE_TABLE,null);
        if (c.moveToFirst()){
            if(c.getInt(0)==0)
                return true;
        }
        return false;
    }

    public void ecatfilter() {

        Cursor h = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_ECATEGORIES + "<>'0';",null);
        int ecat = h.getColumnIndex(KEY_ECATEGORIES);
        for(h.moveToFirst();!h.isAfterLast();h.moveToNext())
            vEcategories.addElement(h.getString(ecat));


    }

    public void icatfilter() {
        Cursor h = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_ICATEGORIES + "<>'0';",null);
        int icat = h.getColumnIndex(KEY_ICATEGORIES);
        for(h.moveToFirst();!h.isAfterLast();h.moveToNext())
            vIcategories.addElement(h.getString(icat));
    }

    public void accfilter() {
        Cursor h = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+ KEY_ACCOUNTS + "<>'0';",null);
        int acc = h.getColumnIndex(KEY_ACCOUNTS);
        for(h.moveToFirst();!h.isAfterLast();h.moveToNext())
            vacc.addElement(h.getString(acc));
    }
    public void deleteDb(){
        ourContext.deleteDatabase(DATABASE_NAME);
        //ourHelper.onUpgrade(ourDatabase,DATABASE_VERSION,2);
    }

    public void deleteCategory(String todeleteecat,String todeleteicat,String todeleteacc) {
        if(todeleteecat.equals("0") && todeleteicat.equals("0")){
            ourDatabase.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE " + KEY_ACCOUNTS + "='" + todeleteacc + "';");
        }else if(todeleteicat.equals("0")){
            ourDatabase.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE " + KEY_ECATEGORIES + "='" + todeleteecat + "';");
        }else if(todeleteecat.equals("0")){
            ourDatabase.execSQL("DELETE FROM " + DATABASE_TABLE + " WHERE " + KEY_ICATEGORIES + "='" + todeleteicat + "';");
        }

    }

    public boolean acc_count() {
        Cursor h = ourDatabase.rawQuery("SELECT COUNT("+KEY_ACCOUNTS+") FROM "+DATABASE_TABLE,null);
        if (h.moveToFirst()){
            if(h.getInt(0)==0)
                return true;
        }
        return false;
    }

    public void editCategory(String selected, String selectednew, String ioe) {
        if(ioe.equals("e"))
        ourDatabase.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_ECATEGORIES + "='"+selectednew+"' WHERE "+
                KEY_ECATEGORIES+"='"+selected+"';");
        else if(ioe.equals("i"))
            ourDatabase.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_ICATEGORIES + "='"+selectednew+"' WHERE "+
                    KEY_ICATEGORIES+"='"+selected+"';");
    }

    public void editAcc(String selected, String selectednew) {
        ourDatabase.execSQL("UPDATE " + DATABASE_TABLE + " SET " + KEY_ACCOUNTS + "='"+selectednew+"' WHERE "+
                KEY_ACCOUNTS+"='"+selected+"';");
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }


        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE "+DATABASE_TABLE+ " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_ICATEGORIES + " TEXT NOT NULL, " + KEY_ECATEGORIES + " TEXT NOT NULL, "+KEY_ACCOUNTS
                    + " TEXT NOT NULL);"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


                if(newVersion==2)db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD COLUMN " + KEY_ACCOUNTS + " Varchar(30);");


        }
    }

    public CategoryManager(Context c){
        ourContext = c;
    }
    public CategoryManager open() throws SQLException {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        ourHelper.close();

    }

}
