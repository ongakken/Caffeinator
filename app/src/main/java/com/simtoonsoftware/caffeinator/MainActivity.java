package com.simtoonsoftware.caffeinator;

import android.content.Context;
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

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    //Method overrides (except onCreate())
    @Override
    protected void onPause() {
        super.onPause();
    }

    //Floats
    float caffeineIntakeValue;
    float caffeineIntakeLeft;

    //Integers
    int currentCaffeineLevel;
    int maxCaffeineIntake;
    int prg_maxCaffeine_maxValue;
    int getPrg_maxCaffeine_currentValue;

    //Timers
    Timer autosave = new Timer();

    //UI data types
    TextView text_caffeineIntakeValue;
    TextView text_caffeineIntakeLeft;
    ProgressBar prg_maxCaffeine;
    private InterstitialAd RandomInterstitialAd;
    private AdView RandomBannerAd;

    //icon - cup of coffee HEX #FFA500 [do not remove]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Saving and Loading Section

        //LOAD
        SharedPreferences loadGame = getSharedPreferences("SAVE", Context.MODE_PRIVATE); // Creates a loadGame shared preference under the string LOAD
        caffeineIntakeValue = loadGame.getFloat("caffeineIntakeValue", 0); // Loads currentCaffeineLevel from a saved preference, if no preference is found then it is set to 0
        System.out.println(caffeineIntakeValue);

        //SAVE
        SharedPreferences saveGame = getSharedPreferences("SAVE", Context.MODE_PRIVATE); // Creates a saveGame shared preference under the string SAVE
        final SharedPreferences.Editor save = saveGame.edit();

        // Shared Preferences - passing data between classes
        //SharedPreferences dataTunnel = PreferenceManager.getDefaultSharedPreferences(this);
        //caffeineIntakeValue = dataTunnel.getFloat("caffeineIntakeValue", 0);

        //Ad section
        MobileAds.initialize(this, "ca-app-pub-9086446979210331~8508547502"); // Real AD ID
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); // Testing AD ID
        RandomInterstitialAd = new InterstitialAd(this);
        RandomInterstitialAd.setAdUnitId("ca-app-pub-9086446979210331/2057677460"); // Real AD ID
        //RandomInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Testing AD ID
        RandomInterstitialAd.loadAd(new AdRequest.Builder().build());
        RandomBannerAd = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        RandomBannerAd.loadAd(adRequest);

        //Data resources
        prg_maxCaffeine = findViewById(R.id.prgBar_maxCaffeine);
        text_caffeineIntakeLeft = findViewById(R.id.text_caffeineIntakeLeft);
        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        maxCaffeineIntake = 400;
        prg_maxCaffeine.setMax(maxCaffeineIntake);
        prg_maxCaffeine.setProgress(currentCaffeineLevel); //we have to figure out how to calculate person's max daily caffeine intake and interpret it with this progressbar
        getPrg_maxCaffeine_currentValue = prg_maxCaffeine.getProgress();
        caffeineIntakeValue = AddCaffeineIntakeActivity.getCaffeineIntakeValue();

        save.putFloat("caffeineIntakeValue", 0);
        save.commit();
        System.out.println(caffeineIntakeValue);

        text_caffeineIntakeValue.setText(caffeineIntakeValue + "mg");
        currentCaffeineLevel = (int)caffeineIntakeValue;
        caffeineIntakeLeft = maxCaffeineIntake - caffeineIntakeValue;
        text_caffeineIntakeLeft.setText(caffeineIntakeLeft + "mg");

        Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, AddCaffeineIntakeActivity.class));
                if (RandomInterstitialAd.isLoaded()) {
                    RandomInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial ad hasn't been loaded yet");
                }
            }
        });
    }

}
