package com.rk.callscreenblocker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by user1 on 10/11/16.
 */
public class Alarmactivater {
    public static final int REQUEST_CODE = 1001;
    private static final String ACTION_ALARM_RECEIVER = "call_blocker_alarm";
    private static SharedPreferences sp;
    protected static boolean onetime = true;

    public static long scheduleAlarm(Context context) {
//        if (Common.getInstance(context).checkIsMyPhone()) {
        long start_time = 0;
        if (Common.alarm) {
            sp = context.getSharedPreferences("call_blocker", context.MODE_PRIVATE);
            String status = sp.getString("status", "");
            Log.e("Alarm status", status);
            if (!isAlarmRunning(context) && status.equals("Activated")) {
                if (onetime) {
                    start_time = oneTimeAlarm(context);
                } else {
                    start_time = gettime(context);
                    Intent intent = new Intent(context, AlarmReceiver.class);
                    intent.setAction(ACTION_ALARM_RECEIVER);
                    final PendingIntent pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarm.setRepeating(AlarmManager.RTC_WAKEUP, start_time, Common._interval_time, pIntent);
                    set_notification(context, "" + start_time);
                }
            } else {
                set_notification(context, "AAR or status" + status);
                Log.e("alarm", "scheduleAlarm");
            }
        } else {
            if (!Common.getInstance(context).isMyServiceRunning(IncomingService.class))
                context.startService(new Intent(context, IncomingService.class));
        }
        return start_time;
    }

    private static void set_notification(Context context, String start_time) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.lock)
                .setContentTitle("Alarm Activation")
                .setContentText(start_time)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(101, mBuilder.build());

    }

    protected static long gettime(Context context) {
        long start_time = 0;
        if (Common.getInstance(context).checkIsMyPhone()) {
            Calendar c = Calendar.getInstance();
            int hrs = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            if (min < 30) {
                c.set(Calendar.MINUTE, 30);
                c.set(Calendar.SECOND, 0);
            } else {
                c.set(Calendar.HOUR_OF_DAY, hrs + 1);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
            }

//            Log.e("Current day of year", "" + c.get(Calendar.DAY_OF_YEAR));
//            Log.e("Current month", "" + (c.get(Calendar.MONTH) + 1));
//            Log.e("Current day", "" + c.get(Calendar.DAY_OF_MONTH));
            if (hrs >= 22 && hrs < 6) {
                c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
                c.set(Calendar.HOUR_OF_DAY, 6);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
            }
            start_time = c.getTimeInMillis();

        } else {
            start_time = System.currentTimeMillis() + 4000;
        }
//        start_time = System.currentTimeMillis() + 30000;
        Log.e("alarm", "started at" + start_time);
        return start_time;
    }


    public static void cancelAlarm(Context context) {
//        if (Common.getInstance(context).checkIsMyPhone()) {
        if (Common.alarm) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction(ACTION_ALARM_RECEIVER);
            final PendingIntent pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pIntent);
            pIntent.cancel();//important
            if (Common.getInstance(context).isMyServiceRunning(IncomingService.class))
                context.stopService(new Intent(context, IncomingService.class));
            Log.e("alarm", "cancelled");
        } else {
            if (Common.getInstance(context).isMyServiceRunning(IncomingService.class)) {
                context.stopService(new Intent(context, IncomingService.class));
                Log.e("service status", "stopped");
            } else
                Log.e("service status", "not running");
        }
    }

    public static boolean isAlarmRunning(Context context) {
        //checking if alarm is working with pendingIntent
        Intent intent = new Intent(context, AlarmReceiver.class);//the same as up
        intent.setAction(ACTION_ALARM_RECEIVER);//the same as up
        boolean isWorking = (PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE) != null);//just changed the flag
        Log.e(ACTION_ALARM_RECEIVER, "alarm is " + (isWorking ? "" : "not") + " working...");
        return isWorking;
    }

    public static long oneTimeAlarm(Context context) {

//        if (Common.getInstance(context).isMyServiceRunning(IncomingService.class))
//            context.stopService(new Intent(context, IncomingService.class));
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_ALARM_RECEIVER);
        Log.e("Alarm", "onetime");
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long start_time = gettime(context);
//        long start_time = System.currentTimeMillis() + 6000;
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, start_time, pIntent);
//        alarm.set(AlarmManager.RTC_WAKEUP, start_time, pIntent);
//        set_notification(context, "" + new SimpleDateFormat("HH:mm").format(new Date(start_time)));
        return start_time;
    }

}
