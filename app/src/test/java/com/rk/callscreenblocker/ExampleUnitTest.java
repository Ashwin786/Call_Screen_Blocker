package com.rk.callscreenblocker;

import android.util.Log;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        get_time();
    }

    private String get_time() {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 8);
        SimpleDateFormat df;
        if (c.get(Calendar.MINUTE) < 30)
            df = new SimpleDateFormat("h m a");
        else
            df = new SimpleDateFormat("h mm a");
        String myTime = "Time is " + df.format(c.getTime());
        Log.e("myTime", "" + myTime);
//        int mHour = c.get(Calendar.HOUR_OF_DAY);
//        int mMinute = c.get(Calendar.MINUTE);
//        final String myTime = "The Time is " + String.valueOf(mHour)+ " " + String.valueOf(mMinute);
        return myTime;
    }

}