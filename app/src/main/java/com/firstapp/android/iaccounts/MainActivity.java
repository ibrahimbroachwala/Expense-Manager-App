package com.firstapp.android.iaccounts;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,DialogInterface.OnClickListener{



    @Override
    protected void onResume() {
        super.onResume();
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        selected_acc_home = sp.getString("default_acc","General");
        current_acc_home.setText(selected_acc_home);
        mAdView.resume();
        CategoryManager catman6 = new CategoryManager(this);
        try{catman6.open();}catch(SQLException d){d.printStackTrace();}

        //if(catman2.acc_count())
        //catman2.createEntry("0","0","General");

        catman6.vacc.removeAllElements();

        catman6.accfilter();

        accounts.clear();
        accounts.add("Default");

        for (int i = 0; i <= catman6.vacc.indexOf(catman6.vacc.lastElement()); i++)
            accounts.add(catman6.vacc.elementAt(i).toString());

        //NO add new account.
        catman6.close();
        adapter_accounthome = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,accounts);
        adapter_accounthome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_accounthome.setAdapter(adapter_accounthome);
        BudgetManager obj1 = new BudgetManager(this);


        try{
            obj1.open();}catch (SQLException s){s.printStackTrace();}
        if(obj1.isEmpty()){
            tvexp.setText("0.0");
            tvinc.setText("0.0");
            tvbal.setText("0.0");
        }else {

            Float dr = Float.valueOf(obj1.gettotaldrmonth(selected_month,selected_acc_home,selected_year));
            Float cr = Float.valueOf(obj1.gettotalcrmonth(selected_month,selected_acc_home,selected_year));
            tvexp.setText("" + dr);
            tvinc.setText("" + cr);
            tvbal.setText("" +(cr-dr));
        }
        obj1.close();

        selecteddate = etdatehome.getText().toString();
        Viewaccount blah = new Viewaccount();
        BudgetManager objectB = new BudgetManager(MainActivity.this);
        try{objectB.open();}catch (SQLException f){f.printStackTrace();}
        blah.removeAllElements(objectB);
        objectB.datefilter(selecteddate,selected_acc_home);
        if(objectB.datefilter(selecteddate,selected_acc_home)==1) {
            hometotal.setText("0.0");
            Hemptylayout();
        }else{
            layout(objectB);
            int i;
            float totaldr =0;
            float totalcr = 0;
            float totalamt;
            for(i=0;i<=objectB.count-1;i++) {
                totaldr = totaldr + Float.valueOf(objectB.vdr.elementAt(i).toString());
                totalcr = totalcr + Float.valueOf(objectB.vcr.elementAt(i).toString());
            }
            totalamt = totalcr - totaldr;
            hometotal.setText("" + totalamt);
        }
        objectB.close();


    }

    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    List<String> eCategories = new ArrayList<String>();
    List<String> iCategories = new ArrayList<String>();
    List<String> accounts = new ArrayList<String>();
    int Hour;

     SharedPreferences sp6;


    String[] Months = new String[]{ "Jan" , "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec" };
    String[] fMonths = new String[]{ "All","Jan" , "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec" };
    //String[] Accounts = new String[]{ "General"};


    String selected_acc_for_exp;
    String selected_month = "All",selected_acc_home = "General";
    String selected_year = "2017";
    String tempmonth,selecteddate,tempmonthcode,tempdaycode,tempycode;
    Button badd,bdate,binc,homedatebut;
    Spinner categories,spinner_monthhome,spinner_accounthome,spinner_account,spinner_yearhome;
    TextView tvbal,tvexp,tvinc,hometotal,etdatehome,current_acc_home;
    ArrayAdapter<String> adapter,adapter_account_entry;
    ArrayAdapter<String> adapter_monthhome,adapter_accounthome,adapter_yearhome;
    EditText date,etamt,etexp,etcategory,etaccount;
    static final int Dialog_id = 0;
    static final int Dialog_id2 = 1;
    int myear,mmonth,mday;

    AdView mAdView;
    AdRequest adRequest;
    String[] arrayyear =  new String[]{ "All" , "2016" , "2017"};


    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAnalytics.Param Param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        BudgetManager Object = new BudgetManager(this);
        try{
            Object.open();}catch (SQLException s){s.printStackTrace();
            }

        Object.close();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        CategoryManager Objectcat = new CategoryManager(this);
        try{Objectcat.open();}catch (SQLException s){s.printStackTrace();}


         sp6 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        current_acc_home = (TextView) findViewById(R.id.current_acc_home);
        current_acc_home.setText(sp6.getString("default_acc","General"));

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9084920470877631~7718798301");
        mAdView = (AdView) findViewById(R.id.adView);
         adRequest = new AdRequest.Builder()
                .addTestDevice(adRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6EBA7182CFCE89CAC7DCBDC243045A97")
                .build();
        mAdView.loadAd(adRequest);

        selected_acc_home = sp6.getString("default_acc","General");




        setAlarm();
        setSupportActionBar(toolbar);



        DisplayMetrics mhmetrics = getResources().getDisplayMetrics();

        int mhdeviceWidth = mhmetrics.widthPixels;

        float mhwidthInPercentage =  ( (float) 400 / 1080 )  * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution

        int mhdropWidth = (int) ( (mhwidthInPercentage * mhdeviceWidth) / 100 );


        homedatebut = (Button) findViewById(R.id.home_date_but);
        etdatehome = (TextView) findViewById(R.id.etdatehome);
        hometotal = (TextView) findViewById(R.id.home_total);
        transbydate();


        CategoryManager catman = new CategoryManager(this);
        try{catman.open();}catch(SQLException d){d.printStackTrace();}

        if(catman.isEmpty()) {
            addecats();
            addicats();
            addaccounts();
            for (int i = 0; i < eCategories.size(); i++) {
                catman.createEntry(eCategories.get(i), "0","0");
            }
            for (int i = 0; i < iCategories.size(); i++) {
                catman.createEntry("0", iCategories.get(i),"0");
            }
            for (int i = 0; i < accounts.size(); i++) {
                catman.createEntry("0","0", accounts.get(i));
            }
        }else if(catman.acc_count()) {

            addaccounts();
            for (int i = 0; i < accounts.size(); i++)
                catman.createEntry("0", "0", accounts.get(i));
        }

        catman.close();

        CategoryManager catman2 = new CategoryManager(this);
        try{catman2.open();}catch(SQLException d){d.printStackTrace();}

        //if(catman2.acc_count())
        //catman2.createEntry("0","0","General");

        catman2.vacc.removeAllElements();

        catman2.accfilter();

        accounts.clear();
        accounts.add("Default");

        for (int i = 0; i <= catman2.vacc.indexOf(catman2.vacc.lastElement()); i++)
            accounts.add(catman2.vacc.elementAt(i).toString());

        //NO add new account.
        catman2.close();



        spinner_yearhome = (Spinner) findViewById(R.id.spinner_yearhome);
        adapter_yearhome = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,arrayyear);
        adapter_yearhome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_yearhome.setAdapter(adapter_yearhome);
        spinner_accounthome = (Spinner) findViewById(R.id.spinner_accounthome);
        adapter_accounthome = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,accounts);
        adapter_accounthome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_accounthome.setAdapter(adapter_accounthome);
        spinner_monthhome = (Spinner) findViewById(R.id.spinner_monthhome);
        adapter_monthhome = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,fMonths);
        adapter_monthhome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_monthhome.setAdapter(adapter_monthhome);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            spinner_monthhome.setDropDownWidth(mhdropWidth);
            spinner_accounthome.setDropDownWidth(mhdropWidth);
        }
        spinner_yearhome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BudgetManager obj6 = new BudgetManager(MainActivity.this);

                selected_year = parent.getSelectedItem().toString();


                try{
                    obj6.open();}catch (SQLException s){s.printStackTrace();}
                if(obj6.isEmpty()){
                    tvexp.setText("0.0");
                    tvinc.setText("0.0");
                    tvbal.setText("0.0");
                }else {

                    Float dr = Float.valueOf(obj6.gettotaldrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                    Float cr = Float.valueOf(obj6.gettotalcrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                    tvexp.setText("" + dr);
                    tvinc.setText("" + cr);
                    tvbal.setText("" +(cr-dr));


                }
                obj6.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_monthhome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BudgetManager obj6 = new BudgetManager(MainActivity.this);

                 selected_month = parent.getSelectedItem().toString();


                try{
                    obj6.open();}catch (SQLException s){s.printStackTrace();}
                if(obj6.isEmpty()){
                    tvexp.setText("0.0");
                    tvinc.setText("0.0");
                    tvbal.setText("0.0");
                }else {

                    Float dr = Float.valueOf(obj6.gettotaldrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                    Float cr = Float.valueOf(obj6.gettotalcrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                    tvexp.setText("" + dr);
                    tvinc.setText("" + cr);
                    tvbal.setText("" +(cr-dr));


                }
                obj6.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_accounthome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BudgetManager obj6 = new BudgetManager(MainActivity.this);

                selected_acc_home = parent.getSelectedItem().toString();
                if(selected_acc_home.equals("Default"))
                current_acc_home.setText(sp6.getString("default_acc","General"));
                else
                current_acc_home.setText(selected_acc_home);

                try{
                    obj6.open();}catch (SQLException s){s.printStackTrace();}
                if(obj6.isEmpty()){
                    tvexp.setText("0.0");
                    tvinc.setText("0.0");
                    tvbal.setText("0.0");
                }else {

                    Float dr = Float.valueOf(obj6.gettotaldrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                    Float cr = Float.valueOf(obj6.gettotalcrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                    tvexp.setText("" + dr);
                    tvinc.setText("" + cr);
                    tvbal.setText("" +(cr-dr));


                }
                obj6.close();


                selecteddate = etdatehome.getText().toString();
                Viewaccount blah = new Viewaccount();
                BudgetManager objectB = new BudgetManager(MainActivity.this);
                try{objectB.open();}catch (SQLException f){f.printStackTrace();}
                blah.removeAllElements(objectB);
                objectB.datefilter(selecteddate,current_acc_home.getText().toString());
                if(objectB.datefilter(selecteddate,current_acc_home.getText().toString())==1) {
                    hometotal.setText("0.0");
                    Hemptylayout();
                }else{
                    layout(objectB);
                    int i;
                    float totaldr =0;
                    float totalcr = 0;
                    float totalamt;
                    for(i=0;i<=objectB.count-1;i++) {
                        totaldr = totaldr + Float.valueOf(objectB.vdr.elementAt(i).toString());
                        totalcr = totalcr + Float.valueOf(objectB.vcr.elementAt(i).toString());
                    }
                    totalamt = totalcr - totaldr;
                    hometotal.setText("" + totalamt);
                }
                objectB.close();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        selecteddate = etdatehome.getText().toString();
        Viewaccount blah = new Viewaccount();
        BudgetManager objectB = new BudgetManager(MainActivity.this);
        try{objectB.open();}catch (SQLException f){f.printStackTrace();}
        blah.removeAllElements(objectB);
        objectB.datefilter(selecteddate,selected_acc_home);
        if(objectB.datefilter(selecteddate,selected_acc_home)==1) {
            hometotal.setText("0.0");
            Hemptylayout();
        }else{
            layout(objectB);
            int i;
            float totaldr =0;
            float totalcr = 0;
            float totalamt;
            for(i=0;i<=objectB.count-1;i++) {
                totaldr = totaldr + Float.valueOf(objectB.vdr.elementAt(i).toString());
                totalcr = totalcr + Float.valueOf(objectB.vcr.elementAt(i).toString());
            }
            totalamt = totalcr - totaldr;
            hometotal.setText("" + totalamt);
        }
        objectB.close();


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }



        final Calendar cal = Calendar.getInstance();
        myear=cal.get(Calendar.YEAR);
        mmonth=cal.get(Calendar.MONTH);
        mday = cal.get(Calendar.DAY_OF_MONTH);
        binc = (Button) findViewById(R.id.binc);

        badd = (Button) findViewById(R.id.badd);

        tvbal = (TextView) findViewById(R.id.tvbal);
        tvexp = (TextView) findViewById(R.id.tvexp);

        tvinc = (TextView) findViewById(R.id.tvinc);
        badd.setOnClickListener(this);
        binc.setOnClickListener(this);


       calculate();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            SharedPreferences spback = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            selected_acc_home = spback.getString("default_acc","General");
            current_acc_home.setText(selected_acc_home);

            BudgetManager entry = new BudgetManager(this);
            try {
                entry.open();
            } catch (SQLException r) {
                r.printStackTrace();
            }
            if(entry.isEmpty()) {
                tvexp.setText("0.0");
                tvinc.setText("0.0");
                tvbal.setText("0.0");
            }else {

                Float dr = Float.valueOf(entry.gettotaldrmonth(selected_month,selected_acc_home,selected_year));
                Float cr = Float.valueOf(entry.gettotalcrmonth(selected_month,selected_acc_home,selected_year));
                tvexp.setText("" + dr);
                tvinc.setText("" + cr);
                tvbal.setText("" + (cr - dr));


            }
            entry.close();
            CategoryManager catman6 = new CategoryManager(this);
            try{catman6.open();}catch(SQLException d){d.printStackTrace();}

            //if(catman2.acc_count())
            //catman2.createEntry("0","0","General");

            catman6.vacc.removeAllElements();

            catman6.accfilter();

            accounts.clear();
            accounts.add("Default");

            for (int i = 0; i <= catman6.vacc.indexOf(catman6.vacc.lastElement()); i++)
                accounts.add(catman6.vacc.elementAt(i).toString());

            //NO add new account.
            catman6.close();
            adapter_accounthome = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,accounts);
            adapter_accounthome.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_accounthome.setAdapter(adapter_accounthome);
            drawer.closeDrawer(GravityCompat.START);
        } else {


            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_viewdb){
            mFirebaseAnalytics.logEvent("seen_trans_history",null);
            Intent i = new Intent("com.firstapp.android.iaccounts.VIEWACCOUNT");
            startActivity(i);
        }else if(id == R.id.nav_pref){
            mFirebaseAnalytics.logEvent("seen_pref",null);
            Intent j = new Intent("com.firstapp.android.iaccounts.PREFERENCES");
            startActivity(j);
        }else if(id == R.id.nav_about){

            mFirebaseAnalytics.logEvent("seen_about",null);
            LayoutInflater l = getLayoutInflater();
            final View alayout = l.inflate(R.layout.about, null);
            LayoutInflater layoutInflater = getLayoutInflater();
            View titleview = layoutInflater.inflate(R.layout.titleview,null);
            TextView tv_titleview = (TextView) titleview.findViewById(R.id.tv_titleview);
            tv_titleview.setText("About");


            AlertDialog about = new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setView(alayout)
                    .setPositiveButton("Ok",this)
                    .setCustomTitle(titleview)
                    .create();
            about.show();
        }else if(id == R.id.nav_export) {

            CategoryManager catman3 = new CategoryManager(this);
            try{catman3.open();}catch(SQLException d){d.printStackTrace();}
            accounts.clear();
            catman3.vacc.removeAllElements();
            catman3.accfilter();
            LayoutInflater layoutInflater = getLayoutInflater();
            View titleview = layoutInflater.inflate(R.layout.titleview,null);
            TextView tv_titleview = (TextView) titleview.findViewById(R.id.tv_titleview);
            tv_titleview.setText("Select A/c to Export");

            for (int i = 0; i <= catman3.vacc.indexOf(catman3.vacc.lastElement()); i++)
                accounts.add(catman3.vacc.elementAt(i).toString());
            ArrayAdapter<String> adapter3;
            adapter3 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,accounts);
            final AlertDialog accalert = new AlertDialog.Builder(this)
                    .setTitle("Select A/c to Export")
                    .setAdapter(adapter3,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCustomTitle(titleview)
                    .create();

            accalert.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selected_acc_for_exp = parent.getItemAtPosition(position).toString();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                        } else {

                            BudgetManager blah = new BudgetManager(MainActivity.this);
                            try {
                                blah.open();
                            } catch (SQLException d) {
                                d.printStackTrace();
                            }
                            blah.ExportToExcel(selected_acc_for_exp);
                            blah.close();
                            Toast.makeText(MainActivity.this, ".xls file created at /sdcard/Expense Manager", Toast.LENGTH_SHORT).show();
                            mFirebaseAnalytics.logEvent("export_to_excel",null);

                        }

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.

                    } else {


                        BudgetManager blah = new BudgetManager(MainActivity.this);
                        try {
                            blah.open();
                        } catch (SQLException d) {
                            d.printStackTrace();
                        }
                        blah.ExportToExcel(selected_acc_for_exp);
                        blah.close();
                        Toast.makeText(MainActivity.this, ".xls file created at /sdcard/Expense Manager", Toast.LENGTH_SHORT).show();
                        mFirebaseAnalytics.logEvent("export_to_excel",null);
                    }


                    accalert.dismiss();

                }
            });
            accalert.setCancelable(true);
            accalert.show();


        }
        else if(id == R.id.rate_app){
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id="
                                + getPackageName())));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case  MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BudgetManager blah = new BudgetManager(MainActivity.this);
                    try{blah.open();}catch (SQLException d){d.printStackTrace();}
                    blah.ExportToExcel(selected_acc_for_exp);
                    blah.close();
                    Toast.makeText(this,".xls file created at /sdcard/Expense Manager",Toast.LENGTH_SHORT).show();
                    mFirebaseAnalytics.logEvent("export_to_excel",null);



                } else {


                }
                return;
            }
        }
    }



    @Override
    public void onClick(View v)  {

        switch(v.getId()){
            case R.id.badd:

                final SharedPreferences sp1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                LayoutInflater l = getLayoutInflater();
                final View layout = l.inflate(R.layout.adddialog, null);
                bdate = (Button) layout.findViewById(R.id.bdate);
                date = (EditText) layout.findViewById(R.id.editText);
                final Calendar cal2 = Calendar.getInstance();
                myear=cal2.get(Calendar.YEAR);
                mmonth=cal2.get(Calendar.MONTH);
                mday = cal2.get(Calendar.DAY_OF_MONTH);
                date.setText(mday + "-" + Months[mmonth]+ "-" + myear);
                tempmonth = Months[mmonth];
                tempmonthcode = String.valueOf(mmonth);
                tempdaycode = String.valueOf(mday);
                tempycode = String.valueOf(myear);
                etamt = (EditText) layout.findViewById(R.id.etamt);
                etcategory = (EditText) layout.findViewById(R.id.etcategory);
                etexp = (EditText) layout.findViewById(R.id.etexp);
                //
                etaccount = (EditText) layout.findViewById(R.id.etaccount);
                etaccount.setText(sp1.getString("default_acc","General"));


                categories = (Spinner) layout.findViewById(R.id.spinner);
                //
                spinner_account = (Spinner) layout.findViewById(R.id.spinner_acc);


                CategoryManager catman2 = new CategoryManager(this);
                try{catman2.open();}catch(SQLException d){d.printStackTrace();}
                catman2.vEcategories.removeAllElements();
                //
                catman2.vacc.removeAllElements();
                catman2.ecatfilter();
                //
                catman2.accfilter();
                eCategories.clear();
                //
                accounts.clear();
                accounts.add("Default");
                eCategories.add("Categories");
                //

                for (int i = 0; i <= catman2.vEcategories.indexOf(catman2.vEcategories.lastElement()); i++)
                    eCategories.add(catman2.vEcategories.elementAt(i).toString());
                //
                for (int i = 0; i <= catman2.vacc.indexOf(catman2.vacc.lastElement()); i++)
                    accounts.add(catman2.vacc.elementAt(i).toString());


                eCategories.add("Add new");
                //NO add new account.
                catman2.close();


                DisplayMetrics emetrics = getResources().getDisplayMetrics();
                int edeviceWidth = emetrics.widthPixels;
                float ewidthInPercentage =  ( (float) 400 / 1080 )  * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution
                int edropWidth = (int) ( (ewidthInPercentage * edeviceWidth) / 100 );
                //
                DisplayMetrics accmetrics = getResources().getDisplayMetrics();
                int accdeviceWidth = accmetrics.widthPixels;
                float accwidthInPercentage =  ( (float) 400 / 1080 )  * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution
                int accdropWidth = (int) ( (accwidthInPercentage * accdeviceWidth) / 100 );

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
                    categories.setDropDownWidth(edropWidth);
                    //
                    spinner_account.setDropDownWidth(accdropWidth);
                }

                adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,eCategories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categories.setAdapter(adapter);
                //
                adapter_account_entry = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,accounts);
                adapter_account_entry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_account.setAdapter(adapter_account_entry);

                categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                        LayoutInflater l = getLayoutInflater();
                        final View addlayout = l.inflate(R.layout.edittext, null);
                        String selected = parent.getSelectedItem().toString();
                        if(selected.equals("Categories")){
                            etcategory.setHint("");

                        }else if(selected.equals("Add new")) {
                            AlertDialog addnew = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("New Expense Category")
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                            CategoryManager cato = new CategoryManager(MainActivity.this);
                                            try{cato.open();}catch(SQLException d){d.printStackTrace();}
                                            TextView tvnew = (TextView) addlayout.findViewById(R.id.etedit);
                                            String newcat = tvnew.getText().toString();
                                            if(newcat.equals("")){
                                                Toast.makeText(MainActivity.this, "Empty Field!", Toast.LENGTH_SHORT).show();
                                            }else {
                                                cato.createEntry(newcat, "0","0");
                                                cato.close();
                                                Toast.makeText(MainActivity.this, "Category " + newcat + " Added successfully", Toast.LENGTH_SHORT).show();
                                                etcategory.setText(newcat);
                                            }

                                        }
                                    })
                                    .create();
                            addnew.setView(addlayout);
                            addnew.show();

                        }else
                        {
                                etcategory.setText(selected);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {


                    }
                });
                //
                spinner_account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected = parent.getSelectedItem().toString();
                        if(selected.equals("Default")){
                            etaccount.setText(sp1.getString("default_acc","General"));
                        }else
                        {
                            etaccount.setText(selected);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {


                    }
                });
                LayoutInflater layoutInflater = getLayoutInflater();
                View titleview = layoutInflater.inflate(R.layout.titleview,null);
                TextView tv_titleview = (TextView) titleview.findViewById(R.id.tv_titleview);
                tv_titleview.setText("New Expense Entry");

                final AlertDialog ad = new AlertDialog.Builder(this)
                        .setTitle("New Expense Entry")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .setNegativeButton("Cancel", this)
                        .setCustomTitle(titleview)
                        .create();

                bdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(Dialog_id);


                    }
                });
                ad.setView(layout);
                ad.show();
                ad.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(etamt.getText().toString().equals("")  || etcategory.getText().toString().equals("")
                               || date.getText().toString().equals("")){
                            final AlertDialog missinginfo = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Empty Field(s)")
                                    .setMessage("Please mention Date, Amount and Category.")
                                    .setPositiveButton("OK",null)
                                    .create();
                            missinginfo.show();


                        }else {
                            Boolean diditwork = true;

                            String Exp = etexp.getText().toString();
                            String Amt = etamt.getText().toString();
                            String cat = etcategory.getText().toString();
                            String Dt = date.getText().toString();
                            String m = tempmonth;
                            String mcode = tempmonthcode;
                            String dcode = tempdaycode;
                            String ycode = tempycode;
                            String acc = etaccount.getText().toString();

                           BudgetManager entry = new BudgetManager(MainActivity.this);

                            try {
                                entry.open();
                            } catch (SQLException s) {
                                diditwork = false;
                                Toast.makeText(MainActivity.this, "Entry Failed", Toast.LENGTH_SHORT).show();
                            } finally {
                                if (diditwork)

                                    Toast.makeText(MainActivity.this, "Entry Successful", Toast.LENGTH_SHORT).show();
                            }
                            entry.createEntry(Dt, cat, Exp, "0", Amt, m, mcode, dcode, acc ,ycode);


                            mFirebaseAnalytics.logEvent("new_expense_entry", null);

                            selected_acc_home = sp1.getString("default_acc","General");
                            current_acc_home.setText(selected_acc_home);
                            Float dr = Float.valueOf(entry.gettotaldrmonth(selected_month,selected_acc_home,selected_year));
                            Float cr = Float.valueOf(entry.gettotalcrmonth(selected_month,selected_acc_home,selected_year));
                            tvexp.setText("" + dr);
                            tvinc.setText("" + cr);
                            tvbal.setText("" + (cr - dr));
                            entry.close();


                            selecteddate = etdatehome.getText().toString();
                            Viewaccount blah = new Viewaccount();
                            BudgetManager objectB = new BudgetManager(MainActivity.this);
                            try{objectB.open();}catch (SQLException f){f.printStackTrace();}
                            blah.removeAllElements(objectB);
                            objectB.datefilter(selecteddate,selected_acc_home);
                            if(objectB.datefilter(selecteddate,selected_acc_home)==1) {
                                hometotal.setText("0.0");
                                Hemptylayout();
                            }else{
                                layout(objectB);
                                int i;
                                float totaldr =0;
                                float totalcr = 0;
                                float totalamt;
                                for(i=0;i<=objectB.count-1;i++) {
                                    totaldr = totaldr + Float.valueOf(objectB.vdr.elementAt(i).toString());
                                    totalcr = totalcr + Float.valueOf(objectB.vcr.elementAt(i).toString());
                                }
                                totalamt = totalcr - totaldr;
                                hometotal.setText("" + totalamt);
                            }
                            objectB.close();

                            etamt.setText("");
                            etexp.setText("");
                            etcategory.setHint("");
                            //ad.dismiss();
                        }

                    }
                });

                break;



            case R.id.binc:





                final SharedPreferences sp2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                LayoutInflater m = getLayoutInflater();
                View ilayout = m.inflate(R.layout.addincomedial, null);
                bdate = (Button) ilayout.findViewById(R.id.ibdate);
                date = (EditText) ilayout.findViewById(R.id.ieditText);
                final Calendar cal3 = Calendar.getInstance();
                myear=cal3.get(Calendar.YEAR);
                mmonth=cal3.get(Calendar.MONTH);
                mday = cal3.get(Calendar.DAY_OF_MONTH);
                date.setText(mday + "-" + Months[mmonth]+ "-" + myear);
                tempmonth = Months[mmonth];
                tempmonthcode = String.valueOf(mmonth);
                tempdaycode = String.valueOf(mday);
                tempycode = String.valueOf(myear);
                etamt = (EditText) ilayout.findViewById(R.id.ietamt);
                etcategory = (EditText) ilayout.findViewById(R.id.ietcategory);
                etexp = (EditText) ilayout.findViewById(R.id.ietexp);
                etaccount =(EditText) ilayout.findViewById(R.id.ietaccount);
                etaccount.setText(sp2.getString("default_acc","General"));

                categories = (Spinner) ilayout.findViewById(R.id.ispinner);
                //
                spinner_account =(Spinner) ilayout.findViewById(R.id.ispinner_acc);


                CategoryManager catman3 = new CategoryManager(this);
                try{catman3.open();}catch(SQLException d){d.printStackTrace();}
                catman3.vIcategories.removeAllElements();
                //
                catman3.vacc.removeAllElements();
                catman3.icatfilter();
                //
                catman3.accfilter();
                iCategories.clear();
                //
                accounts.clear();
                iCategories.add("Categories");
                //
                accounts.add("Default");

                for (int i = 0; i <= catman3.vIcategories.indexOf(catman3.vIcategories.lastElement()); i++)
                    iCategories.add(catman3.vIcategories.elementAt(i).toString());
                //
                for (int i = 0; i <= catman3.vacc.indexOf(catman3.vacc.lastElement()); i++)
                    accounts.add(catman3.vacc.elementAt(i).toString());

                iCategories.add("Add new");
                catman3.close();

                DisplayMetrics imetrics = getResources().getDisplayMetrics();
                int ideviceWidth = imetrics.widthPixels;
                float iwidthInPercentage =  ( (float) 400/ 1080 )  * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution
                int idropWidth = (int) ( (iwidthInPercentage * ideviceWidth) / 100 );
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
                    categories.setDropDownWidth(idropWidth);
                    spinner_account.setDropDownWidth(idropWidth);
                }
                adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,iCategories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categories.setAdapter(adapter);
                //
                adapter_account_entry = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,accounts);
                adapter_account_entry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_account.setAdapter(adapter_account_entry);


                categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        LayoutInflater l = getLayoutInflater();
                        final View addlayout = l.inflate(R.layout.edittext, null);
                        String selected = parent.getSelectedItem().toString();
                        if(selected.equals("Categories")){
                            etcategory.setHint("");

                        }else if(selected.equals("Add new")) {
                            AlertDialog addnew = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("New Income Category")
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {




                                            CategoryManager cato = new CategoryManager(MainActivity.this);
                                            try {
                                                cato.open();
                                            } catch (SQLException d) {
                                                d.printStackTrace();
                                            }
                                            TextView tvnew = (TextView) addlayout.findViewById(R.id.etedit);
                                            String newcat = tvnew.getText().toString();
                                            if(newcat.equals("")){
                                                Toast.makeText(MainActivity.this, "Empty Field!", Toast.LENGTH_SHORT).show();
                                            }else {
                                                cato.createEntry("0", newcat,"0");
                                                cato.close();
                                                Toast.makeText(MainActivity.this, "Category " + newcat + " Added successfully", Toast.LENGTH_SHORT).show();
                                                etcategory.setText(newcat);
                                            }

                                        }
                                    })
                                    .create();
                            addnew.setView(addlayout);
                            addnew.show();
                        }else
                        {
                            etcategory.setText(selected);
                        }

                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {


                    }
                });
                //
                spinner_account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selected = parent.getSelectedItem().toString();
                        if(selected.equals("Default")){
                            etaccount.setText(sp2.getString("default_acc","General"));
                        }else
                        {
                            etaccount.setText(selected);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {


                    }
                });
                LayoutInflater ilayoutInflater = getLayoutInflater();
                View ititleview = ilayoutInflater.inflate(R.layout.titleview,null);
                TextView itv_titleview = (TextView) ititleview.findViewById(R.id.tv_titleview);
                itv_titleview.setText("New Income Entry");


                final AlertDialog iad = new AlertDialog.Builder(this)
                        .setTitle("New Income Entry")
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {



                            }
                        })
                        .setNegativeButton("Cancel", this)
                        .setCustomTitle(ititleview)
                        .create();

                bdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(Dialog_id);


                    }
                });
                iad.setView(ilayout);

                iad.show();
                iad.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        if(etamt.getText().toString().equals("") || etcategory.getText().toString().equals("")
                                || date.getText().toString().equals("")){
                            final AlertDialog missinginfo = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Empty Field(s)")
                                    .setMessage("Please mention Date, Amount and Category.")
                                    .setPositiveButton("OK",null)
                                    .create();
                            missinginfo.show();


                        }else {
                            Boolean diditwork = true;

                            String Exp = etexp.getText().toString();
                            String Amt = etamt.getText().toString();
                            String cat = etcategory.getText().toString();
                            String Dt = date.getText().toString();
                            String m = tempmonth;
                            String mcode = tempmonthcode;
                            String ycode = tempycode;
                            String dcode = tempdaycode;
                            String acc = etaccount.getText().toString();
                            BudgetManager entry = new BudgetManager(MainActivity.this);

                            try {
                                entry.open();
                            } catch (SQLException s) {
                                diditwork = false;
                                Toast.makeText(MainActivity.this, "Entry Failed", Toast.LENGTH_SHORT).show();
                            } finally {
                                if (diditwork)
                                    Toast.makeText(MainActivity.this, "Entry Successful", Toast.LENGTH_SHORT).show();
                            }
                            entry.createEntry(Dt, cat, Exp, Amt, "0", m, mcode,dcode,acc,ycode);



                            mFirebaseAnalytics.logEvent("new_income_entry", null);


                            selected_acc_home = sp2.getString("default_acc","General");
                            current_acc_home.setText(selected_acc_home);
                            Float dr = Float.valueOf(entry.gettotaldrmonth(selected_month,selected_acc_home,selected_year));
                            Float cr = Float.valueOf(entry.gettotalcrmonth(selected_month,selected_acc_home,selected_year));
                            tvexp.setText("" + dr);
                            tvinc.setText("" + cr);
                            tvbal.setText("" + (cr - dr));
                            entry.close();



                            selecteddate = etdatehome.getText().toString();
                            Viewaccount blah = new Viewaccount();
                            BudgetManager objectB = new BudgetManager(MainActivity.this);
                            try{objectB.open();}catch (SQLException f){f.printStackTrace();}
                            blah.removeAllElements(objectB);
                            objectB.datefilter(selecteddate,selected_acc_home);
                            if(objectB.datefilter(selecteddate,selected_acc_home)==1) {
                                hometotal.setText("0.0");
                                Hemptylayout();
                            }else{
                                layout(objectB);
                                int i;
                                float totaldr =0;
                                float totalcr = 0;
                                float totalamt;
                                for(i=0;i<=objectB.count-1;i++) {
                                    totaldr = totaldr + Float.valueOf(objectB.vdr.elementAt(i).toString());
                                    totalcr = totalcr + Float.valueOf(objectB.vcr.elementAt(i).toString());
                                }
                                totalamt = totalcr - totaldr;
                                hometotal.setText("" + totalamt);
                            }
                            objectB.close();

                            etamt.setText("");
                            etexp.setText("");
                            etcategory.setHint("");


                            //iad.dismiss();
                        }

                    }
                });
                break;
        }
    }
    protected Dialog onCreateDialog(int id){
        if(id==Dialog_id)
            return new DatePickerDialog(this,Dsetlistener,myear,mmonth,mday);
        else if(id==Dialog_id2)
            return new DatePickerDialog(this,Dsetlistener2,myear,mmonth,mday);
        return null;
    }

    private DatePickerDialog.OnDateSetListener Dsetlistener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myear = year;
            mmonth = monthOfYear;
            mday = dayOfMonth;
            date.setText(mday + "-" + Months[mmonth]+ "-" + myear);
            tempmonthcode = String.valueOf(mmonth);
            tempdaycode = String.valueOf(mday);
            tempycode = String.valueOf(myear);
            tempmonth = Months[mmonth];

        }
    };


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which){
            case DialogInterface.BUTTON_POSITIVE :

                break;
            case DialogInterface.BUTTON_NEGATIVE :
                break;
        }
    }


    void calculate(){
        BudgetManager obj1 = new BudgetManager(this);

        try{
            obj1.open();}catch (SQLException s){s.printStackTrace();}
        if(obj1.isEmpty()){
            tvexp.setText("0.0");
            tvinc.setText("0.0");
            tvbal.setText("0.0");
        }else {

            Float dr = Float.valueOf(obj1.gettotaldrmonth(selected_month,selected_acc_home,selected_year));
            Float cr = Float.valueOf(obj1.gettotalcrmonth(selected_month,selected_acc_home,selected_year));
            tvexp.setText("" + dr);
            tvinc.setText("" + cr);
            tvbal.setText("" +(cr-dr));


        }
        obj1.close();

    }
    public void setAlarm(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean remindertog = sp.getBoolean("remindertoggle",true);
        String value = sp.getString("hour_of_day","8 pm");
        switch(value){
            case "6 am": Hour = 6;
                break;
            case "7 am": Hour = 7;
                break;
            case "8 am": Hour = 8;
                break;
            case "9 am": Hour = 9;
                break;
            case "10 am": Hour = 10;
                break;
            case "11 am": Hour = 11;
                break;
            case "12 pm": Hour = 12;
                break;
            case "1 pm": Hour = 13;
                break;
            case "2 pm": Hour = 14;
                break;
            case "3 pm": Hour = 15;
                break;
            case "4 pm": Hour = 16;
                break;
            case "5 pm": Hour = 17;
                break;
            case "6 pm": Hour = 18;
                break;
            case "7 pm": Hour = 19;
                break;
            case "8 pm": Hour = 20;
                break;
            case "9 pm": Hour = 21;
                break;
            case "10 pm": Hour = 22;
                break;
            case "11 pm": Hour = 23;
                break;
            case "12 am": Hour = 24;
                break;


        }
       Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY,Hour);
        time.set(Calendar.MINUTE,0);
        time.set(Calendar.SECOND,0);
        Intent alertintent = new Intent(this,AlarmReceiver.class);
        AlarmManager am =(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(remindertog) {
            if(PendingIntent.getBroadcast(this,1,alertintent,PendingIntent.FLAG_NO_CREATE)== null)
            am.setRepeating(am.RTC_WAKEUP, time.getTimeInMillis(),am.INTERVAL_DAY, PendingIntent.getBroadcast(this, 1, alertintent,
                    PendingIntent.FLAG_UPDATE_CURRENT));
        }
        else{
            am.cancel(PendingIntent.getBroadcast(this,1,alertintent,PendingIntent.FLAG_UPDATE_CURRENT));
        }



    }
    public void addecats(){

        eCategories.add("Food");
        eCategories.add("Conveyance");
        eCategories.add("Education");
        eCategories.add("Personal");
        eCategories.add("Fuel");
        eCategories.add("Entertainment");

    }
    public void addicats(){

        iCategories.add("Salary");
        iCategories.add("Pocket Money");
        iCategories.add("Bonus");
        iCategories.add("Sales");


    }
    public void addaccounts(){
        accounts.add("General");
    }


    public void transbydate(){

        final Calendar cal2 = Calendar.getInstance();
        myear=cal2.get(Calendar.YEAR);
        mmonth=cal2.get(Calendar.MONTH);
        mday = cal2.get(Calendar.DAY_OF_MONTH);
        etdatehome.setText(mday + "-" + Months[mmonth]+ "-" + myear);



        homedatebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Dialog_id2);


            }
        });
    }

    private DatePickerDialog.OnDateSetListener Dsetlistener2 = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myear = year;
            mmonth = monthOfYear;
            mday = dayOfMonth;
            etdatehome.setText(mday + "-" + Months[mmonth]+ "-" + myear);
            selecteddate = etdatehome.getText().toString();
            Viewaccount blah = new Viewaccount();
            BudgetManager objectB = new BudgetManager(MainActivity.this);
            try{objectB.open();}catch (SQLException f){f.printStackTrace();}
            blah.removeAllElements(objectB);
            objectB.datefilter(selecteddate,current_acc_home.getText().toString());
            if(objectB.datefilter(selecteddate,current_acc_home.getText().toString())==1) {
                hometotal.setText("0.0");
                Hemptylayout();
            }else{
                layout(objectB);
                int i;
                float totaldr =0;
                float totalcr = 0;
                float totalamt;
                for(i=0;i<=objectB.count-1;i++) {
                    totaldr = totaldr + Float.valueOf(objectB.vdr.elementAt(i).toString());
                    totalcr = totalcr + Float.valueOf(objectB.vcr.elementAt(i).toString());
                }
                totalamt = totalcr - totaldr;
                hometotal.setText("" + totalamt);
            }
            objectB.close();



        }
    };

    public void layout(final BudgetManager obj){


        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int deviceWidth = metrics.widthPixels;

        int deviceHeight = metrics.heightPixels;

        float widthInPercentage =  ( (float) 1012 / 1080 )  * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution

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
        LinearLayout lout = (LinearLayout) findViewById(R.id.home_ll);
        lout.removeAllViews();
        String dr, cr, date, cat, nar;
        for (i = 0; i <= obj.count-1; i++) {
            lin[i] = new RelativeLayout(this);
            LinearLayout.LayoutParams rlplp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mLayoutHeight);
            rlplp.setMargins(2,5,2,0);
            lout.addView(lin[i],rlplp);

            //MarginLayoutParams blp = new MarginLayoutParams(mLayoutWidth, mLayoutHeight);
            //blp.setMargins(0, 15, 0, 15);
            //lin[i].setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mLayoutHeight));
            //lin[i].setLayoutParams(rlplp);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                //lin[i].setElevation(7);
            }




            lin[i].setPadding(10, padding, 10, padding);

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
            TextView tvdr = new TextView(this);
            TextView tvcr = new TextView(this);
            TextView tvdate = new TextView(this);
            TextView tvnar = new TextView(this);
            TextView tvcat = new TextView(this);
            Button bedit = new Button(this);
            Button bdelete = new Button(this);




            tvdr.setText("" + Float.valueOf(dr));
            tvcat.setText(cat);
            tvdate.setText(date);
            tvcr.setText("+" + Float.valueOf(cr));
            tvnar.setText(nar);

            bdelete.setBackgroundResource(R.drawable.delete2);
           // bedit.setBackgroundResource();



            tvdate.setId(i + 1);
            tvdr.setId(i + 2);
            tvcr.setId(i + 3);
            tvcat.setId(i + 4);
            tvnar.setId(i + 5);
            bedit.setId(i+7);
            bdelete.setId(i);


            bdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int l = v.getId();
                    final String todeletedate = obj.vdate.elementAt(l).toString();
                    final String todeletedr = obj.vdr.elementAt(l).toString();
                    final String todeletecr = obj.vcr.elementAt(l).toString();
                    final String todeletecat = obj.vcat.elementAt(l).toString();



                    final AlertDialog deletealert = new AlertDialog.Builder(MainActivity.this)
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
                                    Viewaccount object = new Viewaccount();
                                    try {
                                        obj.open();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    obj.deleteEntry(todeletedate,todeletecat,todeletecr,todeletedr,current_acc_home.getText().toString());

                                    mFirebaseAnalytics.logEvent("delete_transaction",null);
                                    object.removeAllElements(obj);
                                    obj.datefilter(selecteddate,current_acc_home.getText().toString());
                                    if(obj.datefilter(selecteddate,current_acc_home.getText().toString()) == 1){
                                        Hemptylayout();
                                       hometotal.setText("0.0");

                                        if(obj.isEmpty()) {
                                            tvexp.setText("0.0");
                                            tvinc.setText("0.0");
                                            tvbal.setText("0.0");


                                        }else {
                                            Float dr = Float.valueOf(obj.gettotaldrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                                            Float cr = Float.valueOf(obj.gettotalcrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                                            tvexp.setText("" + dr);
                                            tvinc.setText("" + cr);
                                            tvbal.setText("" + (cr - dr));
                                        }

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
                                        hometotal.setText("" + totalamt);
                                        Float dr = Float.valueOf(obj.gettotaldrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                                        Float cr = Float.valueOf(obj.gettotalcrmonth(selected_month,current_acc_home.getText().toString(),selected_year));
                                        tvexp.setText("" + dr);
                                        tvinc.setText("" + cr);
                                        tvbal.setText("" + (cr - dr));
                                    }
                                   // obj.close();

                                }
                            }).setCancelable(true)
                            .create();
                    deletealert.show();
                }
            });




            /*bedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int l = v.getId();
                    final String toeditdate = obj.vdate.elementAt(l).toString();
                    final String toeditdr = obj.vdr.elementAt(l).toString();
                    final String toeditcr = obj.vcr.elementAt(l).toString();
                    final String toeditcat = obj.vcat.elementAt(l).toString();
                    final String toeditnar = obj.vnar.elementAt(l).toString();
                    View editlayout;
                    LayoutInflater m = getLayoutInflater();
                    if(obj.vcr.elementAt(l).toString().equals(0))
                        editlayout = m.inflate(R.layout.adddialog, null);
                        else
                        editlayout = m.inflate(R.layout.addincomedial, null);
                    EditText etamt = (EditText) editlayout.findViewById(R.id.etamt);
                    EditText etexp =
                    Button bdate = (Button) editlayout.findViewById(R.id.bdate);
                    bdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog(Dialog_id2);
                        }
                    });



                }
            });*/

            RelativeLayout.LayoutParams rlp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams rlp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams rlp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams rlp5 = new RelativeLayout.LayoutParams(imgmLayoutWidth,imgmLayoutHeight);


            rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlp5.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            rlp2.addRule(RelativeLayout.BELOW, tvdate.getId());
            tvcat.setTypeface(null, Typeface.BOLD);
            tvcat.setTextColor(Color.BLACK);

            tvdate.setTextColor(Color.DKGRAY);

            rlp4.addRule(RelativeLayout.BELOW, tvcat.getId());


            if (Float.valueOf(obj.vdr.elementAt(i).toString()) > 0) {
                rlp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);;
                tvdr.setGravity(Gravity.END);
                rlp4.addRule(RelativeLayout.LEFT_OF,tvdr.getId());
                tvdr.setTypeface(null, Typeface.BOLD);
                tvdr.setTextColor(Color.argb(255, 223, 24, 24));
                tvdr.setTextSize(23);
                lin[i].addView(tvdr, rlp1);
            } else {
                rlp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                tvcr.setGravity(Gravity.END);
                tvcr.setTypeface(null, Typeface.BOLD);
                rlp4.addRule(RelativeLayout.LEFT_OF,tvcr.getId());
                tvcr.setTextColor(Color.argb(255, 34, 193, 10));
                tvcr.setTextSize(23);
                lin[i].addView(tvcr, rlp3);
            }
            lin[i].addView(bdelete,rlp5);
            lin[i].setId(i);
            lin[i].addView(tvdate);
            lin[i].addView(tvcat, rlp2);
            lin[i].addView(tvnar, rlp4);



        }

    }
    public void Hemptylayout(){
        LinearLayout emptyL = (LinearLayout) findViewById(R.id.home_ll);
        emptyL.removeAllViews();


    }

    public void removeAllElements(CategoryManager obj){
        obj.vIcategories.removeAllElements();
        obj.vEcategories.removeAllElements();
        obj.vacc.removeAllElements();

    }




}
