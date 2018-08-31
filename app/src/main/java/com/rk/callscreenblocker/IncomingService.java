package com.rk.callscreenblocker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.IBinder;
import android.util.Log;

public class IncomingService extends Service {
    private Call_Receiver callReceiver;

    public IncomingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service status", "Service started");
//        create_sound();
        register_call();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("Service status", "Service onDestroy");
        if (callReceiver != null) {
            unregisterReceiver(callReceiver);
        }
//        Alarmactivater.oneTimeAlarm(this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (callReceiver != null) {
            unregisterReceiver(callReceiver);
        }
        Log.e("Service status", "onTaskRemoved");
//        stopSelf();
//        Alarmactivater.oneTimeAlarm(this);
    }

    private void create_sound() {
        try {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register_call() {
        callReceiver = new Call_Receiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.setPriority(999);
        registerReceiver(callReceiver, intentFilter);
    }
}
