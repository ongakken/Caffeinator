package com.simtoonsoftware.caffeinator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    // Declarations and Definitions
    static float caffeineIntakeValue;

    // UI
    TextView text_caffeineIntakeValue;
    EditText input_CaffeineIntakeValue;
    Button btn_addCaffeineIntake;
    Button btn_back;

    private AdView RandomBannerAd;



    public static float getCaffeineIntakeValue() {
        return caffeineIntakeValue;
    }

    public void setCaffeineIntakeValue(float caffeineIntakeValue) {
        this.caffeineIntakeValue = caffeineIntakeValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caffeine_intake);

        //Auto Saving/Loading Section

        //LOAD
        SharedPreferences loadGame = getSharedPreferences("SAVE", Context.MODE_PRIVATE); // Creates a loadGame shared preference under the string LOAD
        caffeineIntakeValue = loadGame.getFloat("caffeineIntakeValue", 0); // Loads currentCaffeineLevel from a saved preference, if no preference is found then it is set to 0
        System.out.println(caffeineIntakeValue);

        //SAVE
        SharedPreferences saveGame = getSharedPreferences("SAVE", Context.MODE_PRIVATE); // Creates a saveGame shared preference under the string SAVE
        final SharedPreferences.Editor save = saveGame.edit();


        // Ad Section
        MobileAds.initialize(this, "ca-app-pub-9086446979210331~8508547502"); // Real AD ID
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); // Testing AD ID
        RandomBannerAd = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        RandomBannerAd.loadAd(adRequest);

        // Shared Preferences - passing data between classes
        //SharedPreferences dataTunnel = PreferenceManager.getDefaultSharedPreferences(this);
        //final SharedPreferences.Editor passData = dataTunnel.edit();
        //passData.putFloat("caffeineIntakeValue", caffeineIntakeValue );
        //passData.apply(); // We use this to save all the put data // it is commented because we're doing this somewhere else

        // Resources
        input_CaffeineIntakeValue = findViewById(R.id.input_CaffeineIntakeValue);
        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        btn_back = findViewById(R.id.btn_back);

        text_caffeineIntakeValue.setText("" + caffeineIntakeValue);

        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineIntakeValue += Float.parseFloat(input_CaffeineIntakeValue.getText() + "");
                text_caffeineIntakeValue.setText(caffeineIntakeValue + "mg");
                setCaffeineIntakeValue(caffeineIntakeValue);
                save.putFloat("caffeineIntakeValue", 0);
                save.commit();
                System.out.println(caffeineIntakeValue);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddCaffeineIntakeActivity.this, MainActivity.class));
                //passData.apply();
            }
        });
    }
}

