package com.simtoonsoftware.caffeinator;

import android.content.Intent;
import android.os.Bundle;
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
    static public float caffeineIntakeValue;

    TextView text_caffeineIntakeValue;
    AddCaffeineIntakeActivity addCaffeineIntakeActivityRef = new AddCaffeineIntakeActivity();

    private InterstitialAd RandomAd;
    private AdView RandomBannerAd;

    //icon - cup of coffee HEX #FFA500

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        caffeineIntakeValue = addCaffeineIntakeActivityRef.caffeineIntakeValue;
        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        text_caffeineIntakeValue.setText(caffeineIntakeValue + "mg");

        MobileAds.initialize(this, "ca-app-pub-9086446979210331~8508547502");

        RandomAd = new InterstitialAd(this);
        RandomAd.setAdUnitId("ca-app-pub-9086446979210331/2057677460");
        RandomAd.loadAd(new AdRequest.Builder().build());
        RandomBannerAd = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        RandomBannerAd.loadAd(adRequest);

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
