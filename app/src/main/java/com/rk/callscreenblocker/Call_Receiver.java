package com.rk.callscreenblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Call_Receiver extends BroadcastReceiver {
    public Call_Receiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        Log.e("incoming state", stateStr);
        if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Common.getInstance(context).cancel();
        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            Common.getInstance(context).block_ui();
        }else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            Common.getInstance(context).cancel();
        }
    }
}
