package com.rk.callscreenblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Restart_Receiver extends BroadcastReceiver {
    public Restart_Receiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e("Restart_Receiver","Restart_Receiver");
        Intent startIntent = new Intent(context, IncomingService.class);
        context.startService(startIntent);
    }
}
