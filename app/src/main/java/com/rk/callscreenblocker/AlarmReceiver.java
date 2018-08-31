package com.rk.callscreenblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {


    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e("alarm intent", "received");

        if (Common.getInstance(context).checkIsMyPhone()) {
            speak_time(context);
            if (Alarmactivater.onetime)
                Alarmactivater.oneTimeAlarm(context);
            else
                Alarmactivater.scheduleAlarm(context);
        } else {
//            create_sound();
            if (!Common.getInstance(context).isMyServiceRunning(IncomingService.class)) {
                context.startService(new Intent(context, IncomingService.class));
            } else
                Log.e("service", "running");
        }

    }


    private void create_sound() {
        try {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void speak_time(Context context) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        if (!Common.getInstance(context).isMyServiceRunning(TTS.class)) {
            if (hour > 5 && hour < 22) {
                if (check_mode(context) == AudioManager.RINGER_MODE_NORMAL)
                    context.startService(new Intent(context, TTS.class));
            }
        } else
            context.stopService(new Intent(context, TTS.class));

    }

    private int check_mode(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode();
    }
}
