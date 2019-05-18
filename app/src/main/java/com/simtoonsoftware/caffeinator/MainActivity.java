package com.simtoonsoftware.caffeinator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    //definitions and declarations
    float caffeineIntakeValue;
    TextView text_caffeineIntakeValue;

    private InterstitialAd RandomAd;
    private AdView RandomBannerAd;

    AddCaffeineIntakeActivity AddCaffeineIntakeActivityRef = new AddCaffeineIntakeActivity();

    //icon - cup of coffee HEX #FFA500

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this); //SharedPrefs allow you to store ints and use them between activities
        // SENDING
        //SharedPreferences.Editor shared = preferences.edit(); // An editor is only necessary when you want to save data
        //shared.putFloat("caffeineIntakeValue",caffeineIntakeValue); //sample
        //shared.apply();

        // RECEIVING
        //caffeineIntakeValue = preferences.getFloat("caffeineIntakeValue", 0); //Sample -- We're doing it in the syncthread.

        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        caffeineIntakeValue = AddCaffeineIntakeActivityRef.caffeineIntakeValue;

        MobileAds.initialize(this, "ca-app-pub-9086446979210331~8508547502");

        RandomAd = new InterstitialAd(this);
        RandomAd.setAdUnitId("ca-app-pub-3940256099942544/6300978111"); //testing
        //RandomAd.setAdUnitId("ca-app-pub-9086446979210331/2057677460"); //real deal
        RandomAd.loadAd(new AdRequest.Builder().build());
        RandomBannerAd = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        RandomBannerAd.loadAd(adRequest);

        Thread syncthread = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted()){
                    try {
                        Thread.sleep(250); //How often the GUI reloads -- Values too low cause lags
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Preferences Updater
                                caffeineIntakeValue = preferences.getFloat("caffeineIntakeValue", caffeineIntakeValue);

                                //UI Update
                                text_caffeineIntakeValue.setText("" + caffeineIntakeValue + "mg");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };syncthread.start();

        Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, AddCaffeineIntakeActivity.class));
                if (RandomAd.isLoaded()) {
                    RandomAd.show();
                } else {
                    Log.d("TAG", "The interstitial ad hasn't been loaded yet");
                }
            }
        });
    }}
