package com.simtoonsoftware.caffeinator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AddCaffeineIntakeActivity extends AppCompatActivity {

    //declarations
    static public float caffeineIntakeValue;

    TextView text_caffeineIntakeValue;
    EditText input_CaffeineIntakeValue;
    Button btn_addCaffeineIntake;
    Button btn_back;

    private AdView RandomBannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caffeine_intake);

    //definitions
        input_CaffeineIntakeValue = findViewById(R.id.input_CaffeineIntakeValue);
        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        btn_back = findViewById(R.id.btn_back);

        text_caffeineIntakeValue.setText("" + caffeineIntakeValue);

        MobileAds.initialize(this, "ca-app-pub-9086446979210331~8508547502");

        RandomBannerAd = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        RandomBannerAd.loadAd(adRequest);

        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineIntakeValue += Float.parseFloat(input_CaffeineIntakeValue.getText() + "");
                text_caffeineIntakeValue.setText(caffeineIntakeValue + "mg");
            }
            });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddCaffeineIntakeActivity.this, MainActivity.class));
            }
        });
    }
  }

