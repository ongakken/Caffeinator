package com.simtoonsoftware.caffeinator.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.simtoonsoftware.caffeinator.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Variables
    float caffeineIntakeValue;
    float caffeineIntakeLeft;

    int currentCaffeineLevel;
    int maxCaffeineIntake;
    int getPrg_maxCaffeine_currentValue;

    public static final String SAVE = "Clicker%Game%2%Save";

    // Timers
    Timer autosave = new Timer();

    // UI data types
    TextView text_caffeineIntakeValue;
    TextView text_caffeineIntakeLeft;
    ProgressBar prg_maxCaffeine;
    private InterstitialAd RandomInterstitialAd;
    private AdView RandomBannerAd;

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    //icon - cup of coffee HEX #FFA500 [do not remove]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Auto Save/Load section
        final SharedPreferences saveInstance = getSharedPreferences(SAVE, MODE_PRIVATE);
        final SharedPreferences.Editor save = saveInstance.edit();
        final SharedPreferences loadInstance = getSharedPreferences(SAVE, MODE_PRIVATE);

        caffeineIntakeValue = loadInstance.getFloat("caffeineIntakeValue", 0);


        // Ad section
        MobileAds.initialize(this, "ca-app-pub-9086446979210331~8508547502"); // Real AD ID
            //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); // Testing AD ID
        RandomInterstitialAd = new InterstitialAd(this);
        RandomInterstitialAd.setAdUnitId("ca-app-pub-9086446979210331/2057677460"); // Real AD ID
            //RandomInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Testing AD ID
        RandomInterstitialAd.loadAd(new AdRequest.Builder().build());
        RandomBannerAd = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        RandomBannerAd.loadAd(adRequest);

        // Data resources
        prg_maxCaffeine = findViewById(R.id.prgBar_maxCaffeine);
        text_caffeineIntakeLeft = findViewById(R.id.text_caffeineIntakeLeft);
        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        maxCaffeineIntake = 400;
        prg_maxCaffeine.setMax(maxCaffeineIntake);
        prg_maxCaffeine.setProgress(currentCaffeineLevel); //we have to figure out how to calculate person's max daily caffeine intake and interpret it with this progressbar
        getPrg_maxCaffeine_currentValue = prg_maxCaffeine.getProgress();

        // UI
        text_caffeineIntakeValue.setText(caffeineIntakeValue + "mg");
        currentCaffeineLevel = (int)caffeineIntakeValue;
        caffeineIntakeLeft = maxCaffeineIntake - caffeineIntakeValue;
        text_caffeineIntakeLeft.setText(caffeineIntakeLeft + "mg");

        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCaffeineIntakeActivity = new Intent(MainActivity.this, AddCaffeine.class);
                addCaffeineIntakeActivity.putExtra("caffeineIntakeValue", caffeineIntakeValue);
                startActivityForResult(addCaffeineIntakeActivity, SECOND_ACTIVITY_REQUEST_CODE);
                if (RandomInterstitialAd.isLoaded()) {
                    RandomInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial ad hasn't been loaded yet");
                }
            }
        });

        autosave.schedule(new TimerTask() {
            @Override
            public void run() {
                save.putFloat("caffeineIntakeValue", caffeineIntakeValue);
                save.apply();
            }
        }, 2500, 2500);
    }
    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Get data from Intent
                caffeineIntakeValue = data.getFloatExtra("caffeineIntakeValue", caffeineIntakeValue);

                // Update the text view
                text_caffeineIntakeValue.setText(Float.toString(caffeineIntakeValue));

                // Update the progress bar
                caffeineIntakeLeft = 0;
                caffeineIntakeLeft = maxCaffeineIntake - caffeineIntakeValue;
                text_caffeineIntakeLeft.setText(caffeineIntakeLeft + "mg");
            }
        }
    }
}
