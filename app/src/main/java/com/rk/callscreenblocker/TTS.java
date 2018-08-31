package com.rk.callscreenblocker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by user1 on 27/7/18.
 */
public class TTS extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    private TextToSpeech mTts;
    private int current = 5;
    private AudioManager audioManager;
//    private String spokenText="Magesh loves chitu kuruvi";
//    private String spokenText="Goudam is a bad boy";

    @Override
    public void onCreate() {
        // This is a good place to set spokenText
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setmaxvolume();
        mTts = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        speak_time(status);

    }

    @Override
    public void onUtteranceCompleted(String uttId) {
        setCurrentvolume();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void speak_time(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                mTts.setOnUtteranceCompletedListener(this);
                mTts.setPitch(1.3f);
                mTts.setSpeechRate(0.6f);
                // start speak
                HashMap<String, String> params = new HashMap<String, String>();

                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");

                mTts.speak(get_time(), TextToSpeech.QUEUE_FLUSH, params);

            }
        }

    }

    private String get_time() {
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat df;
        /*c.set(Calendar.MINUTE,0);
        c.set(Calendar.HOUR, 11);
        if (c.get(Calendar.MINUTE) < 30)
            df = new SimpleDateFormat("h:m a");
        else*/
//        df = new SimpleDateFormat("h:m a");
        if (c.get(Calendar.MINUTE) == 0)
            df = new SimpleDateFormat("h");
        else
            df = new SimpleDateFormat("h:m");

//        int mHour = c.get(Calendar.HOUR_OF_DAY);
//        int mMinute = c.get(Calendar.MINUTE);
//        final String myTime = "The Time is " + String.valueOf(mHour)+ " " + String.valueOf(mMinute);
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

    private void setmaxvolume() {
        current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 2, 0);
    }

    private void setCurrentvolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
    }
}
