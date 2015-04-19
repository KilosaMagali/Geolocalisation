package com.manhattanproject.geolocalisation;

/**
 * Created by vince_000 on 19/04/2015.
 */
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

public class Timer extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //5000 is the starting number (in milliseconds)
        //1000 is the number to count down each time (in milliseconds)
        MyCount counter = new MyCount(5000,1000);

        counter.start();

    }

    //countdowntimer is an abstract class, so extend it and fill in methods
    public class MyCount extends CountDownTimer{

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Toast.makeText(getApplicationContext(), "tada"+millisUntilFinished, Toast.LENGTH_LONG).show();

        }

    }
}
