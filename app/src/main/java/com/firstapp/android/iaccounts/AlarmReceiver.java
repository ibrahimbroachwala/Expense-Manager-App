package com.firstapp.android.iaccounts;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;

import java.util.logging.Handler;

/**
 * Created by Ibrahimkb on 28-12-2015.
 */
public class AlarmReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context, "Expense Manager","Click to Add Today's Expenses.","Alert");


    }
    public void createNotification(Context c,String m,String m2,String m3){

        PendingIntent p = PendingIntent.getActivity(c,0,
                new Intent(c,MainActivity.class),0);
        Bitmap bmp = BitmapFactory.decodeResource(App.getContext().getResources(),R.drawable.iconmain);
        NotificationCompat.Builder alarmb = (NotificationCompat.Builder) new NotificationCompat.Builder(c)
                .setSmallIcon(R.drawable.smallicon)
                .setLargeIcon(bmp)
                .setContentTitle(m)
                .setContentText(m2)
                .setTicker(m3);
        alarmb.setContentIntent(p);
        alarmb.setDefaults(NotificationCompat.DEFAULT_SOUND);
        alarmb.setAutoCancel(true);
        NotificationManager notiman = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        notiman.notify(1,alarmb.build());


    }


}
