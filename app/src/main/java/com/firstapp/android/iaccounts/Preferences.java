package com.firstapp.android.iaccounts;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;


/**
 * Created by Ibrahimkb on 28-12-2015.
 */
public class Preferences extends PreferenceActivity implements View.OnClickListener {


    Button bexpcat, binccat, bdel, bacc;
    TextView default_acc;
    AdView mAdView;
    AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitleTextColor(Color.WHITE);
        addPreferencesFromResource(R.xml.prefs);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        MainActivity obj = new MainActivity();


        MobileAds.initialize(getApplicationContext(), "ca-app-pub-9084920470877631~7718798301");
        mAdView = (AdView) findViewById(R.id.prefadView);
        adRequest = new AdRequest.Builder()
                .addTestDevice(adRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6EBA7182CFCE89CAC7DCBDC243045A97")
                .build();
        mAdView.loadAd(adRequest);

        toolbar.setTitle("Preferences");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        final ListPreference lp = (ListPreference) findPreference("hour_of_day");
        final SwitchPreference swp = (SwitchPreference) findPreference("remindertoggle");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        bexpcat = (Button) findViewById(R.id.exp_cats_but);
        binccat = (Button) findViewById(R.id.inc_cats_but);
        bdel = (Button) findViewById(R.id.del_wallet_but);
        bacc = (Button) findViewById(R.id.acccounts_but);
        default_acc = (TextView) findViewById(R.id.default_acc_pref);
        default_acc.setText("Default Account: " + sp.getString("default_acc", "General").toString());


        boolean remindertog = sp.getBoolean("remindertoggle", true);
        if (remindertog) {
            lp.setEnabled(true);
        } else if (!remindertog) {
            lp.setEnabled(false);
        }


        String value = sp.getString("hour_of_day", "7 pm");
        lp.setSummary(value);

        switch (value) {
            case "6 am":
                obj.Hour = 6;
                break;
            case "7 am":
                obj.Hour = 7;
                break;
            case "8 am":
                obj.Hour = 8;
                break;
            case "9 am":
                obj.Hour = 9;
                break;
            case "10 am":
                obj.Hour = 10;
                break;
            case "11 am":
                obj.Hour = 11;
                break;
            case "12 pm":
                obj.Hour = 12;
                break;
            case "1 pm":
                obj.Hour = 13;
                break;
            case "2 pm":
                obj.Hour = 14;
                break;
            case "3 pm":
                obj.Hour = 15;
                break;
            case "4 pm":
                obj.Hour = 16;
                break;
            case "5 pm":
                obj.Hour = 17;
                break;
            case "6 pm":
                obj.Hour = 18;
                break;
            case "7 pm":
                obj.Hour = 19;
                break;
            case "8 pm":
                obj.Hour = 20;
                break;
            case "9 pm":
                obj.Hour = 21;
                break;
            case "10 pm":
                obj.Hour = 22;
                break;
            case "11 pm":
                obj.Hour = 23;
                break;
            case "12 am":
                obj.Hour = 24;
                break;


        }

        bexpcat.setOnClickListener(this);
        binccat.setOnClickListener(this);
        bdel.setOnClickListener(this);
        bacc.setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        swp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                //swp.setChecked(Boolean.parseBoolean(newValue.toString()));
                MainActivity obj = new MainActivity();
                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, obj.Hour);
                time.set(Calendar.MINUTE, 0);
                time.set(Calendar.SECOND, 0);
                Intent alertintent = new Intent(Preferences.this, AlarmReceiver.class);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                boolean state = Boolean.parseBoolean(newValue.toString());
                if (state) {
                    lp.setEnabled(true);
                    am.setRepeating(am.RTC_WAKEUP, time.getTimeInMillis(), am.INTERVAL_DAY, PendingIntent.getBroadcast(Preferences.this, 1, alertintent,
                            PendingIntent.FLAG_UPDATE_CURRENT));
                } else if (!state) {
                    lp.setEnabled(false);
                    am.cancel(PendingIntent.getBroadcast(Preferences.this, 1, alertintent, PendingIntent.FLAG_UPDATE_CURRENT));
                }
                return true;
            }
        });


        lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MainActivity obj = new MainActivity();
                lp.setSummary(newValue.toString());
                String value = newValue.toString();
                if (value.equals("12 am"))
                    obj.Hour = 24;
                switch (value) {
                    case "6 am":
                        obj.Hour = 6;
                        break;
                    case "7 am":
                        obj.Hour = 7;
                        break;
                    case "8 am":
                        obj.Hour = 8;
                        break;
                    case "9 am":
                        obj.Hour = 9;
                        break;
                    case "10 am":
                        obj.Hour = 10;
                        break;
                    case "11 am":
                        obj.Hour = 11;
                        break;
                    case "12 pm":
                        obj.Hour = 12;
                        break;
                    case "1 pm":
                        obj.Hour = 13;
                        break;
                    case "2 pm":
                        obj.Hour = 14;
                        break;
                    case "3 pm":
                        obj.Hour = 15;
                        break;
                    case "4 pm":
                        obj.Hour = 16;
                        break;
                    case "5 pm":
                        obj.Hour = 17;
                        break;
                    case "6 pm":
                        obj.Hour = 18;
                        break;
                    case "7 pm":
                        obj.Hour = 19;
                        break;
                    case "8 pm":
                        obj.Hour = 20;
                        break;
                    case "9 pm":
                        obj.Hour = 21;
                        break;
                    case "10 pm":
                        obj.Hour = 22;
                        break;
                    case "11 pm":
                        obj.Hour = 23;
                        break;
                    case "12 am":
                        obj.Hour = 24;
                        break;


                }
                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, obj.Hour);
                time.set(Calendar.MINUTE, 0);
                time.set(Calendar.SECOND, 0);
                Intent alertintent = new Intent(Preferences.this, AlarmReceiver.class);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                am.setRepeating(am.RTC_WAKEUP, time.getTimeInMillis(), am.INTERVAL_DAY, PendingIntent.getBroadcast(Preferences.this, 1, alertintent,
                        PendingIntent.FLAG_UPDATE_CURRENT));

                Toast.makeText(Preferences.this, "Reminder Set: " + newValue + " Daily", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }

    @Override
    public void onClick(View v) {
        MainActivity obj = new MainActivity();
        LayoutInflater layoutInflater = getLayoutInflater();
        final View titleview = layoutInflater.inflate(R.layout.titleview,null);
        final TextView tv_titleview = (TextView) titleview.findViewById(R.id.tv_titleview);

        switch (v.getId()) {
            case R.id.exp_cats_but:
                LayoutInflater l = getLayoutInflater();
                final View layout = l.inflate(R.layout.edittext, null);
                CategoryManager catman = new CategoryManager(this);
                try {
                    catman.open();
                } catch (SQLException d) {
                    d.printStackTrace();
                }
                obj.eCategories.clear();
                catman.vEcategories.removeAllElements();
                catman.ecatfilter();
                for (int i = 0; i <= catman.vEcategories.indexOf(catman.vEcategories.lastElement()); i++)
                    obj.eCategories.add(catman.vEcategories.elementAt(i).toString());
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, obj.eCategories);


                tv_titleview.setText("Expense Categories");

                final AlertDialog expalert = new AlertDialog.Builder(this)
                        .setTitle("Expense Categories")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Add new", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                LayoutInflater layoutInflater1 = getLayoutInflater();
                                 View titleview1 = layoutInflater1.inflate(R.layout.titleview,null);
                                 TextView tv_titleview1 = (TextView) titleview1.findViewById(R.id.tv_titleview);
                                tv_titleview1.setText("New Expense Category");
                                AlertDialog addnew = new AlertDialog.Builder(Preferences.this)
                                        .setTitle("New Expense Category")
                                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                                CategoryManager cato = new CategoryManager(Preferences.this);
                                                try {
                                                    cato.open();
                                                } catch (SQLException d) {
                                                    d.printStackTrace();
                                                }
                                                TextView tvnew = (TextView) layout.findViewById(R.id.etedit);
                                                String newcat = tvnew.getText().toString();
                                                if (newcat.equals("")) {
                                                    Toast.makeText(Preferences.this, "Empty field!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    cato.createEntry(newcat, "0", "0");
                                                    cato.close();
                                                    Toast.makeText(Preferences.this, "Category " + newcat + " Added successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setCustomTitle(titleview1)
                                        .create();
                                addnew.setView(layout);
                                addnew.show();

                            }
                        })
                        .setCustomTitle(titleview)
                        .create();
                expalert.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        final TextView tvnew = (TextView) layout.findViewById(R.id.etedit);
                        final String selected = parent.getItemAtPosition(position).toString();
                        tvnew.setText("" + selected);
                        LayoutInflater layoutInflater1 = getLayoutInflater();
                        View titleview1 = layoutInflater1.inflate(R.layout.titleview,null);
                        TextView tv_titleview1 = (TextView) titleview1.findViewById(R.id.tv_titleview);
                        tv_titleview1.setText("Edit / Delete");
                        AlertDialog editdel = new AlertDialog.Builder(Preferences.this)
                                .setTitle("Edit / Delete")

                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        CategoryManager cato = new CategoryManager(Preferences.this);
                                        try {
                                            cato.open();
                                        } catch (SQLException d) {
                                            d.printStackTrace();
                                        }

                                        cato.deleteCategory(selected, "0", "0");

                                        cato.close();
                                        Toast.makeText(Preferences.this, "Category " + selected + " Deleted successfully", Toast.LENGTH_SHORT).show();


                                    }
                                })
                                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String selectednew = tvnew.getText().toString();
                                        if (tvnew.getText().toString().equals(""))
                                            Toast.makeText(Preferences.this, "Empty field!", Toast.LENGTH_SHORT).show();
                                        else {

                                            CategoryManager cato = new CategoryManager(Preferences.this);
                                            try {
                                                cato.open();
                                            } catch (SQLException d) {
                                                d.printStackTrace();
                                            }
                                            BudgetManager bato = new BudgetManager(Preferences.this);
                                            try {
                                                bato.open();
                                            } catch (SQLException d) {
                                                d.printStackTrace();
                                            }
                                            cato.editCategory(selected, selectednew, "e");
                                            bato.editCategory(selected, selectednew);
                                            bato.close();
                                            cato.close();
                                            Toast.makeText(Preferences.this, "Category " + selected + " changed to " + selectednew + " successfully", Toast.LENGTH_SHORT).show();


                                        }

                                    }
                                })
                                .setCustomTitle(titleview1)
                                .create();
                        editdel.setCancelable(true);
                        editdel.setView(layout);
                        editdel.show();
                        expalert.dismiss();


                    }
                });
                expalert.setCancelable(true);
                expalert.show();
                break;
            case R.id.inc_cats_but:

                LayoutInflater m = getLayoutInflater();
                final View mlayout = m.inflate(R.layout.edittext, null);
                CategoryManager catman2 = new CategoryManager(this);
                try {
                    catman2.open();
                } catch (SQLException d) {
                    d.printStackTrace();
                }
                obj.iCategories.clear();
                catman2.vIcategories.removeAllElements();
                catman2.icatfilter();
                for (int i = 0; i <= catman2.vIcategories.indexOf(catman2.vIcategories.lastElement()); i++)
                    obj.iCategories.add(catman2.vIcategories.elementAt(i).toString());
                ArrayAdapter<String> adapter2;
                adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, obj.iCategories);



                tv_titleview.setText("Income Categories");


                final AlertDialog incalert = new AlertDialog.Builder(this)
                        .setTitle("Income Categories")
                        .setAdapter(adapter2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Add new", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LayoutInflater layoutInflater1 = getLayoutInflater();
                                View titleview1 = layoutInflater1.inflate(R.layout.titleview,null);
                                TextView tv_titleview1 = (TextView) titleview1.findViewById(R.id.tv_titleview);
                                tv_titleview1.setText("New Income Category");
                                AlertDialog addnew = new AlertDialog.Builder(Preferences.this)
                                        .setTitle("New Income Category")
                                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                                CategoryManager cato = new CategoryManager(Preferences.this);
                                                try {
                                                    cato.open();
                                                } catch (SQLException d) {
                                                    d.printStackTrace();
                                                }
                                                TextView tvnew = (TextView) mlayout.findViewById(R.id.etedit);
                                                String newcat = tvnew.getText().toString();
                                                if (newcat.equals("")) {
                                                    Toast.makeText(Preferences.this, "Empty field!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    cato.createEntry("0", newcat, "0");
                                                    cato.close();
                                                    Toast.makeText(Preferences.this, "Category " + newcat + " Added successfully", Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        })
                                        .setCustomTitle(titleview1)
                                        .create();

                                addnew.setView(mlayout);
                                addnew.show();

                            }
                        })
                        .setCustomTitle(titleview)

                        .create();
                incalert.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        final TextView tvnew = (TextView) mlayout.findViewById(R.id.etedit);
                        final String selected = parent.getItemAtPosition(position).toString();
                        tvnew.setText("" + selected);
                        LayoutInflater layoutInflater1 = getLayoutInflater();
                        View titleview1 = layoutInflater1.inflate(R.layout.titleview,null);
                        TextView tv_titleview1 = (TextView) titleview1.findViewById(R.id.tv_titleview);
                        tv_titleview1.setText("Edit / Delete");
                        final AlertDialog editdel = new AlertDialog.Builder(Preferences.this)
                                .setTitle("Edit / Delete")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        CategoryManager cato = new CategoryManager(Preferences.this);
                                        try {
                                            cato.open();
                                        } catch (SQLException d) {
                                            d.printStackTrace();
                                        }

                                        cato.deleteCategory("0", selected, "0");

                                        cato.close();
                                        Toast.makeText(Preferences.this, "Category " + selected + " Deleted successfully", Toast.LENGTH_SHORT).show();

                                    }
                                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String selectednew = tvnew.getText().toString();
                                        if (tvnew.getText().toString().equals(""))
                                            Toast.makeText(Preferences.this, "Empty field!", Toast.LENGTH_SHORT).show();
                                        else {
                                            CategoryManager cato = new CategoryManager(Preferences.this);
                                            try {
                                                cato.open();
                                            } catch (SQLException d) {
                                                d.printStackTrace();
                                            }
                                            BudgetManager bato = new BudgetManager(Preferences.this);
                                            try {
                                                bato.open();
                                            } catch (SQLException d) {
                                                d.printStackTrace();
                                            }

                                            cato.editCategory(selected, selectednew, "i");
                                            bato.editCategory(selected, selectednew);
                                            bato.close();
                                            cato.close();
                                            Toast.makeText(Preferences.this, "Category " + selected + " changed to " + selectednew + " successfully", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                .setCustomTitle(titleview1)
                                .create();
                        editdel.setCancelable(true);
                        editdel.setView(mlayout);
                        editdel.show();
                        incalert.dismiss();


                    }
                });
                incalert.setCancelable(true);
                incalert.show();

                break;
            case R.id.del_wallet_but:
                final MainActivity obj5 = new MainActivity();
                tv_titleview.setText("Alert!");
                final AlertDialog ad = new AlertDialog.Builder(this)
                        .setTitle("Alert!")
                        .setMessage("This will remove Transaction records from all of your accounts.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BudgetManager blah = new BudgetManager(Preferences.this);
                                try {
                                    blah.open();
                                } catch (SQLException d) {
                                    d.printStackTrace();
                                }
                                blah.deleteDb();
                                blah.close();

                                SharedPreferences spdel = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor spdeledit = spdel.edit();
                                spdeledit.putString("default_acc", "General");

                                CategoryManager CM = new CategoryManager(Preferences.this);
                                try {
                                    CM.open();
                                } catch (SQLException d) {
                                    d.printStackTrace();
                                }
                                CM.deleteDb();


                                if (CM.isEmpty()) {
                                    obj5.eCategories.clear();
                                    obj5.iCategories.clear();
                                    obj5.accounts.clear();
                                    obj5.removeAllElements(CM);
                                    obj5.addecats();
                                    obj5.addicats();
                                    obj5.addaccounts();
                                    for (int i = 0; i < obj5.eCategories.size(); i++) {
                                        CM.createEntry(obj5.eCategories.get(i), "0", "0");
                                    }
                                    for (int i = 0; i < obj5.iCategories.size(); i++) {
                                        CM.createEntry("0", obj5.iCategories.get(i), "0");
                                    }
                                    for (int i = 0; i < obj5.accounts.size(); i++) {
                                        CM.createEntry("0", "0", obj5.accounts.get(i));
                                    }
                                }
                                CM.close();

                                Toast.makeText(Preferences.this, "All Transactions Deleted", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCustomTitle(titleview)
                        .create();
                ad.show();


                break;
            case R.id.acccounts_but:
                LayoutInflater a = getLayoutInflater();
                final View alayout = a.inflate(R.layout.edittext, null);
                CategoryManager catman3 = new CategoryManager(this);
                try {
                    catman3.open();
                } catch (SQLException d) {
                    d.printStackTrace();
                }
                obj.accounts.clear();
                catman3.vacc.removeAllElements();
                catman3.accfilter();
                for (int i = 0; i <= catman3.vacc.indexOf(catman3.vacc.lastElement()); i++)
                    obj.accounts.add(catman3.vacc.elementAt(i).toString());
                ArrayAdapter<String> adapter3;
                adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, obj.accounts);

                tv_titleview.setText("Accounts");

                final AlertDialog accalert = new AlertDialog.Builder(this)
                        .setTitle("Accounts")
                        .setAdapter(adapter3, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Add new", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LayoutInflater layoutInflater1 = getLayoutInflater();
                                View titleview1 = layoutInflater1.inflate(R.layout.titleview,null);
                                TextView tv_titleview1 = (TextView) titleview1.findViewById(R.id.tv_titleview);
                                tv_titleview1.setText("New Account");
                                AlertDialog addnew = new AlertDialog.Builder(Preferences.this)
                                        .setTitle("New Account")
                                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                                CategoryManager cato = new CategoryManager(Preferences.this);
                                                try {
                                                    cato.open();
                                                } catch (SQLException d) {
                                                    d.printStackTrace();
                                                }
                                                TextView tvnew = (TextView) alayout.findViewById(R.id.etedit);
                                                String newacc = tvnew.getText().toString();
                                                if (newacc.equals("")) {
                                                    Toast.makeText(Preferences.this, "Empty field!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    cato.createEntry("0", "0", newacc);
                                                    cato.close();
                                                    Toast.makeText(Preferences.this, "Account " + newacc + " created successfully", Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        })
                                        .setCustomTitle(titleview1)
                                        .create();

                                addnew.setView(alayout);
                                addnew.show();

                            }
                        })
                        .setCustomTitle(titleview)
                        .create();
                accalert.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {


                        final TextView tvnew = (TextView) alayout.findViewById(R.id.etedit);
                        final String selected = parent.getItemAtPosition(position).toString();
                        tvnew.setText("" + selected);
                        if (position == 0) {
                            LayoutInflater layoutInflater1 = getLayoutInflater();
                            View titleview1 = layoutInflater1.inflate(R.layout.titleview,null);
                            TextView tv_titleview1 = (TextView) titleview1.findViewById(R.id.tv_titleview);
                            tv_titleview1.setText("Edit");
                            final AlertDialog editdel = new AlertDialog.Builder(Preferences.this)
                                    .setTitle("Edit")
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            SharedPreferences sP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                            SharedPreferences.Editor sPedit = sP.edit();

                                            String selectednew = tvnew.getText().toString();
                                            if (tvnew.getText().toString().equals(""))
                                                Toast.makeText(Preferences.this, "Empty field!", Toast.LENGTH_SHORT).show();
                                            else {
                                                CategoryManager cato = new CategoryManager(Preferences.this);
                                                try {
                                                    cato.open();
                                                } catch (SQLException d) {
                                                    d.printStackTrace();
                                                }
                                                BudgetManager bato = new BudgetManager(Preferences.this);
                                                try {
                                                    bato.open();
                                                } catch (SQLException d) {
                                                    d.printStackTrace();
                                                }

                                                cato.editAcc(selected, selectednew);
                                                bato.editAcc(selected, selectednew);
                                                bato.close();
                                                cato.close();
                                                if (selected.equals(sP.getString("default_acc", "General")))
                                                    sPedit.putString("default_acc", selectednew);
                                                sPedit.apply();
                                                Toast.makeText(Preferences.this, "Account " + selected + " changed to " + selectednew + " successfully", Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    })
                                    .setCustomTitle(titleview1)
                                    .create();
                            editdel.setCancelable(true);
                            editdel.setView(alayout);
                            editdel.show();
                            accalert.dismiss();
                        } else {
                            LayoutInflater layoutInflater1 = getLayoutInflater();
                            View titleview1 = layoutInflater1.inflate(R.layout.titleview,null);
                            TextView tv_titleview1 = (TextView) titleview1.findViewById(R.id.tv_titleview);
                            tv_titleview1.setText("Edit / Delete");
                            final AlertDialog editdel = new AlertDialog.Builder(Preferences.this)
                                    .setTitle("Edit / Delete")
                                    .setMessage("Alert! DELETE will remove all transactions in Account " + selected)
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences spacc_delete = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                            SharedPreferences.Editor spacc_delete_edit = spacc_delete.edit();
                                            CategoryManager cato = new CategoryManager(Preferences.this);
                                            try {
                                                cato.open();
                                            } catch (SQLException d) {
                                                d.printStackTrace();
                                            }
                                            cato.deleteCategory("0", "0", selected);
                                            if (selected.equals(spacc_delete.getString("default_acc", "General"))) {
                                                spacc_delete_edit.putString("default_acc", parent.getItemAtPosition(0).toString());
                                                spacc_delete_edit.apply();
                                            }
                                            Toast.makeText(Preferences.this, "Account " + selected + " Deleted successfully", Toast.LENGTH_SHORT).show();

                                            cato.close();

                                        }
                                    })
                                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            SharedPreferences sP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                            SharedPreferences.Editor sPedit = sP.edit();

                                            String selectednew = tvnew.getText().toString();
                                            if (tvnew.getText().toString().equals(""))
                                                Toast.makeText(Preferences.this, "Empty field!", Toast.LENGTH_SHORT).show();
                                            else {
                                                CategoryManager cato = new CategoryManager(Preferences.this);
                                                try {
                                                    cato.open();
                                                } catch (SQLException d) {
                                                    d.printStackTrace();
                                                }
                                                BudgetManager bato = new BudgetManager(Preferences.this);
                                                try {
                                                    bato.open();
                                                } catch (SQLException d) {
                                                    d.printStackTrace();
                                                }

                                                cato.editAcc(selected, selectednew);
                                                bato.editAcc(selected, selectednew);
                                                bato.close();
                                                cato.close();
                                                if (selected.equals(sP.getString("default_acc", "General"))) {
                                                    sPedit.putString("default_acc", selectednew);
                                                    sPedit.apply();
                                                }
                                                Toast.makeText(Preferences.this, "Account " + selected + " changed to " + selectednew + " successfully", Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    })
                                    .setCustomTitle(titleview1)

                                    .create();
                            editdel.setCancelable(true);
                            editdel.setView(alayout);
                            editdel.show();
                            accalert.dismiss();
                        }


                    }
                });

                accalert.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor spEdit = sp.edit();
                        spEdit.putString("default_acc", parent.getItemAtPosition(position).toString());
                        spEdit.commit();
                        default_acc.setText("Default Account: " + sp.getString("default_acc", "General").toString());
                        Toast.makeText(Preferences.this, "Default Account set to : " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                        accalert.dismiss();
                        return true;

                    }
                });
                accalert.setCancelable(true);
                accalert.show();

                break;

        }

    }
}




