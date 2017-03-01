package sai.developement.travelogue.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import sai.developement.travelogue.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();

        launchHome();
    }

    private void launchHome() {
        Runnable homeRunnable = new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                SplashActivity.this.finish();
            }
        };

        final Handler handler = new Handler();
        handler.postDelayed(homeRunnable, SPLASH_DELAY);
    }
}
