package com.firstapp.android.iaccounts;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created by Ibrahimkb on 17-12-2015.
 */
public class Viewaccount extends AppCompatActivity {


    private FirebaseAnalytics mFirebaseAnalytics;
    TextView totaldrcr,Account_head,tv_totaldr,tv_totalcr;
    float totaldr,totalcr,totalamt;
    @Override
    protected void onRestart() {
        super.onRestart();
        selectedmonth = "All";
        selectedcategory = "All";
        selectedView = "All";
        selectedyear = "2016";


    }
    String selectedyear = "2016";
    String selectedmonth = "All";
    String selectedcategory = "All";
    String selectedView = "All";
    String selectedAccount = "Default";

    String[] Months = new String[]{ "Jan" , "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec" };
    RadioGroup rg;


    @Override
    protected void onResume() {
        super.onResume();
         BudgetManager obj5 = new BudgetManager(this);

        try{
            obj5.open();}catch (SQLException s){s.printStackTrace();}

        motherofifelse(obj5);


    }
    SharedPreferences sp;

    String[] arrayyear =  new String[]{ "2016" , "2017"};

    String current_month,current_year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewaccount2);
         totaldrcr = (TextView) findViewById(R.id.total);
        Account_head = (TextView) findViewById(R.id.current_acc);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        tv_totalcr = (TextView) findViewById(R.id.tot_inc);
        tv_totaldr = (TextView) findViewById(R.id.tot_exp);
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);


        current_month = Months[cal.get(Calendar.MONTH)];
        current_year = String.valueOf(year);




        selectedmonth = current_month;
        selectedyear = current_year;
        selectedcategory = "All";
        selectedView = "All";
        selectedAccount = "Default";
        Account_head.setText(sp.getString("default_acc","General"));


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }


        final BudgetManager obj = new BudgetManager(this);

        try{
            obj.open();}catch (SQLException s){s.printStackTrace();}
        motherofifelse(obj);

    }

        public void emptylayout(){
            LinearLayout emptyL = (LinearLayout) findViewById(R.id.ll);
            emptyL.removeAllViews();


        }








    public void motherofifelse(final BudgetManager obj){

        if(!obj.isEmpty()) {



            rg = (RadioGroup) findViewById(R.id.rg);
            Spinner yearspin = (Spinner) findViewById(R.id.yearspin);
            Spinner monthspin = (Spinner) findViewById(R.id.monthspin);
            Spinner accountsspin = (Spinner) findViewById(R.id.accountsspin);
            final Spinner ecatspin = (Spinner) findViewById(R.id.ecatspin);
            final Spinner icatspin = (Spinner) findViewById(R.id.icatspin);

            ArrayAdapter<String> adapter;
            ArrayAdapter<String> adapter3;
            ArrayAdapter<String> adapter4;
            ArrayAdapter<String> adapter5;
            ArrayAdapter<String> adapter6;


            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    TextView totaldrcr = (TextView) findViewById(R.id.total);
                    float totaldr,totalcr,totalamt;

                    if(checkedId == R.id.all){
                        selectedView = "All";
                        removeAllElements(obj);
                        obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);

                        if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                            emptylayout();
                            totaldrcr.setText("0.0");
                            tv_totaldr.setText("0.0");
                            tv_totalcr.setText("0.0");
                        }else {

                            layout(obj);
                            int i;
                            totaldr =0;
                            totalcr = 0;
                            for(i=0;i<=obj.count-1;i++) {
                                totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                                totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                            }
                            totalamt = totalcr - totaldr;
                            tv_totaldr.setText("" + totaldr);
                            tv_totalcr.setText("" + totalcr);
                            totaldrcr.setText("" + totalamt);

                        }

                    }
                    else if(checkedId == R.id.inc){
                        selectedView = "Income";
                        removeAllElements(obj);
                        obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);
                        if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                            emptylayout();
                            totaldrcr.setText("0.0");
                            tv_totaldr.setText("0.0");
                            tv_totalcr.setText("0.0");

                        }else {
                            layout(obj);
                            int i;
                            totaldr =0;
                            totalcr = 0;
                            for(i=0;i<=obj.count - 1;i++) {
                                totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                                totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                            }
                            totalamt = totalcr - totaldr;
                            tv_totaldr.setText("" + totaldr);
                            tv_totalcr.setText("" + totalcr);
                            totaldrcr.setText("" + totalamt);
                        }

                    }else if(checkedId == R.id.exp){
                        selectedView = "Expense";
                        removeAllElements(obj);
                        obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);
                        if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                            emptylayout();
                            totaldrcr.setText("0.0");
                            tv_totaldr.setText("0.0");
                            tv_totalcr.setText("0.0");
                        }else {

                            layout(obj);
                            int i;
                            totaldr =0;
                            totalcr = 0;
                            for(i=0;i<=obj.count - 1;i++) {
                                totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                                totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                            }
                            totalamt = totalcr - totaldr;
                            tv_totaldr.setText("" + totaldr);
                            tv_totalcr.setText("" + totalcr);
                            totaldrcr.setText("" + totalamt);

                        }


                    }

                }
            });


            MainActivity obj2 = new MainActivity();
            obj2.iCategories.clear();
            obj2.eCategories.clear();
            obj2.accounts.clear();
            obj2.iCategories.add("All");
            obj2.eCategories.add("All");
            obj2.accounts.add("Default");
            CategoryManager catman = new CategoryManager(this);
            try{catman.open();}catch(SQLException d){d.printStackTrace();}
            catman.vIcategories.removeAllElements();
            catman.vEcategories.removeAllElements();
            catman.vacc.removeAllElements();
            catman.icatfilter();
            catman.ecatfilter();
            catman.accfilter();
            for (int i = 0; i <= catman.vIcategories.indexOf(catman.vIcategories.lastElement()); i++)
                obj2.iCategories.add(catman.vIcategories.elementAt(i).toString());
            for (int i = 0; i <= catman.vEcategories.indexOf(catman.vEcategories.lastElement()); i++)
                obj2.eCategories.add(catman.vEcategories.elementAt(i).toString());
            for (int i = 0; i <= catman.vacc.indexOf(catman.vacc.lastElement()); i++)
                obj2.accounts.add(catman.vacc.elementAt(i).toString());
            catman.close();
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,obj2.fMonths);
            adapter3 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,obj2.eCategories);
            adapter4 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,obj2.iCategories);
            adapter5 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,obj2.accounts);
            adapter6 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,arrayyear);

            DisplayMetrics vmetrics = getResources().getDisplayMetrics();
            int vdeviceWidth = vmetrics.widthPixels;
            float vwidthInPercentage =  ( (float) 400 / 1080 )  * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution
            int vdropWidth = (int) ( (vwidthInPercentage * vdeviceWidth) / 100 );
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
                ecatspin.setDropDownWidth(vdropWidth);
                icatspin.setDropDownWidth(vdropWidth);
                monthspin.setDropDownWidth(vdropWidth);
                accountsspin.setDropDownWidth(vdropWidth);
                yearspin.setDropDownWidth(vdropWidth);
            }


            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            monthspin.setAdapter(adapter);
            ecatspin.setAdapter(adapter3);
            icatspin.setAdapter(adapter4);
            accountsspin.setAdapter(adapter5);
            yearspin.setAdapter(adapter6);


            ArrayAdapter mymonthadapter = (ArrayAdapter) monthspin.getAdapter(); //cast to an ArrayAdapter
            ArrayAdapter myyearadapter = (ArrayAdapter) yearspin.getAdapter();

            int spinnerPosition = mymonthadapter.getPosition(current_month);
            int spinnerPositionyear = myyearadapter.getPosition(current_year);

//set the default according to value
            monthspin.setSelection(spinnerPosition);
            yearspin.setSelection(spinnerPositionyear);


            icatspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView totaldrcr = (TextView) findViewById(R.id.total);
                    float totaldr,totalcr,totalamt;
                    selectedcategory = parent.getSelectedItem().toString();
                    removeAllElements(obj);
                    obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);
                    if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                        emptylayout();
                        totaldrcr.setText("0.0");
                        tv_totaldr.setText("0.0");
                        tv_totalcr.setText("0.0");
                    }else {

                        layout(obj);
                        int i;
                        totaldr =0;
                        totalcr = 0;
                        for(i=0;i<=obj.count - 1;i++) {
                            totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                            totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                        }
                        totalamt = totalcr - totaldr;
                        tv_totaldr.setText("" + totaldr);
                        tv_totalcr.setText("" + totalcr);
                        totaldrcr.setText("" + totalamt);
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ecatspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView totaldrcr = (TextView) findViewById(R.id.total);
                    float totaldr,totalcr,totalamt;
                    selectedcategory = parent.getSelectedItem().toString();
                    removeAllElements(obj);
                    obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);
                    if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                        emptylayout();
                        totaldrcr.setText("0.0");
                        tv_totaldr.setText("0.0");
                        tv_totalcr.setText("0.0");
                    }else {

                        layout(obj);
                        int i;
                        totaldr =0;
                        totalcr = 0;
                        for(i=0;i<=obj.count-1;i++) {
                            totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                            totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                        }
                        totalamt = totalcr - totaldr;
                        tv_totaldr.setText("" + totaldr);
                        tv_totalcr.setText("" + totalcr);
                        totaldrcr.setText("" + totalamt);
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            monthspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView totaldrcr = (TextView) findViewById(R.id.total);
                    float totaldr,totalcr,totalamt;
                    selectedmonth = parent.getSelectedItem().toString();
                    removeAllElements(obj);
                    obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);
                    if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                        emptylayout();
                        totaldrcr.setText("0.0");
                        tv_totaldr.setText("0.0");
                        tv_totalcr.setText("0.0");
                    }else {

                        layout(obj);
                        int i;
                        totaldr =0;
                        totalcr = 0;
                        for(i=0;i<=obj.count-1;i++) {
                            totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                            totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                        }
                        totalamt = totalcr - totaldr;
                        tv_totaldr.setText("" + totaldr);
                        tv_totalcr.setText("" + totalcr);
                        totaldrcr.setText("" + totalamt);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            yearspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView totaldrcr = (TextView) findViewById(R.id.total);
                    float totaldr,totalcr,totalamt;
                    selectedyear = parent.getSelectedItem().toString();
                    removeAllElements(obj);
                    obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);
                    if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                        emptylayout();
                        totaldrcr.setText("0.0");
                        tv_totaldr.setText("0.0");
                        tv_totalcr.setText("0.0");
                    }else {

                        layout(obj);
                        int i;
                        totaldr =0;
                        totalcr = 0;
                        for(i=0;i<=obj.count-1;i++) {
                            totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                            totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                        }
                        totalamt = totalcr - totaldr;
                        tv_totaldr.setText("" + totaldr);
                        tv_totalcr.setText("" + totalcr);
                        totaldrcr.setText("" + totalamt);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            accountsspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView totaldrcr = (TextView) findViewById(R.id.total);
                    float totaldr,totalcr,totalamt;
                    if(parent.getSelectedItem().toString().equals("Default"))
                        Account_head.setText(sp.getString("default_acc","General"));
                    else
                    Account_head.setText(parent.getSelectedItem().toString());
                    removeAllElements(obj);
                    obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);
                    if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                        emptylayout();
                        totaldrcr.setText("0.0");
                        tv_totaldr.setText("0.0");
                        tv_totalcr.setText("0.0");
                    }else {

                        layout(obj);
                        int i;
                        totaldr =0;
                        totalcr = 0;
                        for(i=0;i<=obj.count-1;i++) {
                            totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                            totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                        }
                        totalamt = totalcr - totaldr;
                        tv_totaldr.setText("" + totaldr);
                        tv_totalcr.setText("" + totalcr);
                        totaldrcr.setText("" + totalamt);

                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            removeAllElements(obj);
            obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);
            if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                totaldrcr.setText("0.0");
                tv_totaldr.setText("0.0");
                tv_totalcr.setText("0.0");

            }else {
                layout(obj);
                int i;
                totaldr =0;
                totalcr = 0;
                for(i=0;i<=obj.count-1 ;i++) {
                    totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                    totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                }
                totalamt = totalcr - totaldr;
                tv_totaldr.setText("" + totaldr);
                tv_totalcr.setText("" + totalcr);
                totaldrcr.setText("" + totalamt);

            }
            //obj.close();
        }
        else
        {
            emptylayout();
            totaldrcr.setText("0.0");
            tv_totaldr.setText("0.0");
            tv_totalcr.setText("0.0");
            Toast.makeText(this,"No Transactions! Please make a new entry",Toast.LENGTH_LONG).show();
        }

    }
    public void layout(final BudgetManager obj){


        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int deviceWidth = metrics.widthPixels;

        int deviceHeight = metrics.heightPixels;

        float widthInPercentage =  ( (float) 1080 / 1080 )  * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution

        float heightInPercentage =  ( (float) 265 / 1920 ) * 100; // same procedure 300 is the height of the LinearLayout and i'm converting it into percentage

        int mLayoutWidth = (int) ( (widthInPercentage * deviceWidth) / 100 );

        int mLayoutHeight = (int) ( (heightInPercentage * deviceHeight) / 100 );

        float imgwidthInPercentage =  ( (float) 54 / 1080 )  * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution

        float imgheightInPercentage =  ( (float) 66 / 1920 ) * 100; // same procedure 300 is the height of the LinearLayout and i'm converting it into percentage

        int imgmLayoutWidth = (int) ( (imgwidthInPercentage * deviceWidth) / 100 );

        int imgmLayoutHeight = (int) ( (imgheightInPercentage * deviceHeight) / 100 );

        float paddingpercent =  ( (float) 12 / 1080 )  * 100;

        int padding = (int) ( (paddingpercent * deviceHeight) / 100 );



        RelativeLayout lin[];
        lin = new RelativeLayout[obj.count + 1];
        int i;
        LinearLayout lout = (LinearLayout) findViewById(R.id.ll);
        lout.removeAllViews();
        String dr, cr, date, cat, nar;
        for (i = 0; i <= obj.count-1; i++) {
            lin[i] = new RelativeLayout(Viewaccount.this);
            LinearLayout.LayoutParams rlplp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mLayoutHeight);
            rlplp.setMargins(4,7,4,0);
            lout.addView(lin[i],rlplp);

            if (Float.valueOf(obj.vdr.elementAt(i).toString()) > 0) {

                lin[i].setBackgroundResource(R.drawable.cardlayoutred);
            }
            else {
                lin[i].setBackgroundResource(R.drawable.cardlayoutgreen);
            }

            dr = "-" + obj.vdr.elementAt(i).toString();
            cr = "+" + obj.vcr.elementAt(i).toString();
            date = obj.vdate.elementAt(i).toString();
            cat = obj.vcat.elementAt(i).toString();
            nar = "" + obj.vnar.elementAt(i).toString();
            TextView tvdr = new TextView(Viewaccount.this);
            TextView tvcr = new TextView(Viewaccount.this);
            TextView tvdate = new TextView(Viewaccount.this);
            TextView tvnar = new TextView(Viewaccount.this);
            TextView tvcat = new TextView(Viewaccount.this);
            Button bedit = new Button(Viewaccount.this);
            Button bdelete = new Button(Viewaccount.this);



            tvdr.setText("" + Float.valueOf(dr));
            tvcat.setText(cat);
            tvdate.setText(date);
            tvcr.setText("+" + Float.valueOf(cr));
            tvnar.setText(nar);

            bdelete.setBackgroundResource(R.drawable.delete2);



            tvdate.setId(i + 1);
            tvdr.setId(i + 2);
            tvcr.setId(i + 3);
            tvcat.setId(i + 4);
            tvnar.setId(i + 5);
            bedit.setId(i + 7);
            bdelete.setId(i);


            bdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int l = v.getId();
                    final String todeletedate = obj.vdate.elementAt(l).toString();
                    final String todeletedr = obj.vdr.elementAt(l).toString();
                    final String todeletecr = obj.vcr.elementAt(l).toString();
                    final String todeletecat = obj.vcat.elementAt(l).toString();


                    final AlertDialog deletealert = new AlertDialog.Builder(Viewaccount.this)
                            .setTitle("Delete Transaction")
                            .setMessage("Do you want to delete this transaction?")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TextView totaldrcr = (TextView) findViewById(R.id.total);
                                    float totaldr,totalcr,totalamt;

                                    obj.deleteEntry(todeletedate,todeletecat,todeletecr,todeletedr,Account_head.getText().toString());

                                    mFirebaseAnalytics.logEvent("delete_transaction",null);

                                    removeAllElements(obj);
                                    obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear);
                                    if(obj.godOfFilter(selectedmonth,selectedcategory,selectedView,Account_head.getText().toString(),selectedyear) == 1){
                                        emptylayout();
                                        totaldrcr.setText("0.0");
                                        tv_totaldr.setText("0.0");
                                        tv_totalcr.setText("0.0");
                                    }else {

                                        layout(obj);
                                        int i;
                                        totaldr =0;
                                        totalcr = 0;
                                        for(i=0;i<=obj.count-1;i++) {
                                            totaldr = totaldr + Float.valueOf(obj.vdr.elementAt(i).toString());
                                            totalcr = totalcr + Float.valueOf(obj.vcr.elementAt(i).toString());
                                        }
                                        totalamt = totalcr - totaldr;
                                        tv_totaldr.setText("" + totaldr);
                                        tv_totalcr.setText("" + totalcr);
                                        totaldrcr.setText("" + totalamt);
                                    }

                                }
                            }).setCancelable(true)
                            .create();
                    deletealert.show();
                }
            });

            RelativeLayout.LayoutParams rlp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams rlp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams rlp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams rlp5 = new RelativeLayout.LayoutParams(imgmLayoutWidth,imgmLayoutHeight);



            lin[i].setPadding(9, padding, 9, padding);
            rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlp5.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            rlp2.addRule(RelativeLayout.BELOW, tvdate.getId());
            tvcat.setTypeface(null, Typeface.BOLD);
            tvcat.setTextColor(Color.BLACK);


            tvdate.setTextColor(Color.DKGRAY);


            rlp4.addRule(RelativeLayout.BELOW, tvcat.getId());



            if (Float.valueOf(obj.vdr.elementAt(i).toString()) > 0) {
                rlp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                tvdr.setGravity(Gravity.END);
                tvdr.setTypeface(null, Typeface.BOLD);
                rlp4.addRule(RelativeLayout.LEFT_OF,tvdr.getId());
                tvdr.setTextColor(Color.argb(255, 223, 24, 24));
                tvdr.setTextSize(22);
                lin[i].addView(tvdr, rlp1);
            } else {
                rlp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                tvcr.setGravity(Gravity.END);
                rlp4.addRule(RelativeLayout.LEFT_OF,tvcr.getId());
                tvcr.setTypeface(null, Typeface.BOLD);
                tvcr.setTextColor(Color.argb(255, 34, 193, 10));
                tvcr.setTextSize(22);
                lin[i].addView(tvcr, rlp3);
            }
            lin[i].addView(bdelete,rlp5);
            lin[i].setId(i);
            lin[i].addView(tvdate);
            lin[i].addView(tvcat, rlp2);
            lin[i].addView(tvnar, rlp4);


        }

    }

    public void removeAllElements(BudgetManager obj){
        obj.vdr.removeAllElements();
        obj.vcr.removeAllElements();
        obj.vdate.removeAllElements();
        obj.vnar.removeAllElements();
        obj.vcat.removeAllElements();
        obj.vmonth.removeAllElements();

    }


}

