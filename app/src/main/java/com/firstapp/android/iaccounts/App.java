package com.firstapp.android.iaccounts;

import android.app.Application;
import android.content.Context;

/**
 * Created by Ibrahimkb on 01-01-2016.
 */
public class App extends Application {


    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
