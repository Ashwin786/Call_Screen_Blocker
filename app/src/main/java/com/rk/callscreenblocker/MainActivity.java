package com.rk.callscreenblocker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private int OVERLAY_PERMISSION_CODE = 0;
    private Button btn, btn2;
    private SharedPreferences sp;
    private String btntext = "Activate";
    protected TextView tv_hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("SystemClock", "" + SystemClock.uptimeMillis());
        Log.e("SystemClock real", "" + SystemClock.elapsedRealtime());
    /*    if (1==1) {
//            get_displayname();

//            get_time();
            check_mode();
//            speak_time(this);
//            Alarmactivater.gettime(this);
            return;
        }*/

        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.e("androidId", androidId);
        sp = getSharedPreferences("call_blocker", MODE_PRIVATE);
        is_running();
        if (!androidId.equals(Common.my_bharathId))
            setup_ui();
//        else
//            Common.getInstance(this).register_call();
//        sp.edit().putString("status", btntext).commit();
        if (!sp.getBoolean("permission_status", false)) {
            checkANDgetpermission();
        } else if (sp.getString("status", "").equals("Activated") && !Alarmactivater.isAlarmRunning(MainActivity.this)) {
            Alarmactivater.scheduleAlarm(MainActivity.this);
//            Log.e("activated","activated");
        }


    }



    private void get_displayname() {
//            Calendar calendar = Calendar.getInstance();
//        Map<String, Integer> map = calendar.getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
//        String map = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("EEEE dd MMMM ");
        String month_name = month_date.format(cal.getTime());
        Log.e("get_displayname", month_name.toString());
    }

    private String get_time() {
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat df;
        if (c.get(Calendar.MINUTE) == 0)
            df = new SimpleDateFormat("h");
        else
            df = new SimpleDateFormat("h:m");

        int mHour = c.get(Calendar.HOUR_OF_DAY);
        Log.e("mHour", "" + mHour);
        String myTime;
        if (c.get(Calendar.HOUR_OF_DAY) == 6) {
            SimpleDateFormat month_date = new SimpleDateFormat("EEEE dd MMMM ");
            myTime = "Good Morning Ramesh . Today is " + month_date.format(c.getTime()) + " and the Time is " + df.format(c.getTime());
        } else if (c.get(Calendar.HOUR_OF_DAY) > 21)
            myTime = "Good Night Ramesh . Time is " + df.format(c.getTime());
        else
            myTime = "Time is " + df.format(c.getTime());
        Log.e("myTime", "" + myTime);
        return myTime;
    }

    private void speak_time(Context context) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        if (!Common.getInstance(context).isMyServiceRunning(TTS.class)) {
            if (hour > 6 && hour < 22) {
                context.startService(new Intent(context, TTS.class));
            }
        } else
            context.stopService(new Intent(context, TTS.class));

    }

    private void is_running() {
        Log.e("Alarm running ", "" + Alarmactivater.isAlarmRunning(MainActivity.this));
        Log.e("Service running ", "" + Common.getInstance(this).isMyServiceRunning(IncomingService.class));
    }

    private void setup_ui() {
        btn = (Button) findViewById(R.id.btn);
        tv_hint = (TextView) findViewById(R.id.tv_hint);
        tv_hint.setVisibility(View.GONE);
        btn.setVisibility(View.VISIBLE);
        btn2 = (Button) findViewById(R.id.btn2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String saved_text = sp.getString("status", "");
                if (sp.getBoolean("permission_status", false)) {
                    if (saved_text.equals("") || saved_text.equals("Deactivated")) {
                        sp.edit().putString("status", "Activated").commit();
                        long start_time = Alarmactivater.scheduleAlarm(MainActivity.this);
                        btn.setText("Deactivate");
                        tv_hint.setVisibility(View.VISIBLE);
                        tv_hint.setText("Activate on " + new SimpleDateFormat("HH:mm:ss").format(new Date(start_time)));
                    } else {
                        sp.edit().putString("status", "Deactivated").commit();
                        Alarmactivater.cancelAlarm(MainActivity.this);
                        btn.setText("Activate");
                        tv_hint.setVisibility(View.GONE);
                    }
                } else {
                    checkANDgetpermission();
                }
//                Common.getInstance(MainActivity.this).block_ui();
            }
        });
//        btn2.setVisibility(View.VISIBLE);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alarmactivater.isAlarmRunning(MainActivity.this);
//                Log.e("Is service running", "" + Common.getInstance(MainActivity.this).isMyServiceRunning(IncomingService.class));
//                Common.getInstance(MainActivity.this).clear();
            }
        });
        String saved_text = sp.getString("status", "");
        if (saved_text.equals("Activated")) {
            if (Common.alarm) {
                if (Alarmactivater.isAlarmRunning(MainActivity.this))
                    btntext = "Deactivate";
//                else
//                    sp.edit().putString("status", "Deactivated").commit();
            } else {
                if (Common.getInstance(this).isMyServiceRunning(IncomingService.class))
                    btntext = "Deactivate";
            }
        } else if (saved_text.equals("Deactivated")) {
            if (Common.alarm) {
                if (!Alarmactivater.isAlarmRunning(MainActivity.this))
                    btntext = "Activate";
                else {
                    sp.edit().putString("status", "Activated").commit();
                    btntext = "Deactivate";
                }
            } else {
                if (!Common.getInstance(this).isMyServiceRunning(IncomingService.class))
                    btntext = "Activate";
            }
        }
        btn.setText(btntext);
    }


    public boolean addOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            if (Settings.canDrawOverlays(this)) {
//               Common.getInstance(this).block_ui();
                sp.edit().putBoolean("permission_status", true).commit();
                check_autostart();
            } else {
                Toast.makeText(this, "Kindly switch on the permission button", Toast.LENGTH_SHORT).show();
                addOverlay();
            }
        }
    }

    private void check_autostart() {
        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent1 = new Intent();
            intent1.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent1);
        }
    }


    private int checkANDgetpermission() {
        String[] network = {Manifest.permission.READ_PHONE_STATE};
        ArrayList<String> list = new ArrayList();
        int j = 0;
        for (int i = 0; i < network.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, network[i]) != PackageManager.PERMISSION_GRANTED) {
                list.add(network[i]);
                j++;
            }
        }

        if (list.size() > 0) {
            String[] get = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                get[i] = list.get(i);
            }
            ActivityCompat.requestPermissions(MainActivity.this, get, 0);
        } else
            addOverlay();
        return j;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0) {
            boolean permission_granted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permission_granted = false;
                }
            }
            if (!permission_granted)
                checkANDgetpermission();
            else {
                addOverlay();
            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Common.getInstance(this).unregister();
    }
}
