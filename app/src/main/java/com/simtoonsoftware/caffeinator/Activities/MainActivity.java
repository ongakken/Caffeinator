package com.simtoonsoftware.caffeinator.Activities;

import android.app.ActivityManager;
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
import com.simtoonsoftware.caffeinator.R;

import java.util.Timer;
import java.util.TimerTask;

import broadcasters.SensorRestarterBroadcastReceiver;
import services.caffeineMetabolizationService;

public class MainActivity extends AppCompatActivity {

    // Variables
    float caffeineIntakeValue;
    float caffeineMetabolizedValue;
    float caffeineIntakeLeft;

    int currentCaffeineLevel;
    int maxCaffeineIntake = 400;
    int getPrg_maxCaffeine_currentValue;

    Intent startCaffeineMetabolizationService;
    private caffeineMetabolizationService mCaffeineMetabolizationService;
    Context ctx;
    public Context getCtx() {
        return ctx;
    }

    // Timers
    Timer autosave = new Timer();

    // UI data types
    TextView text_caffeineIntakeValue;
    TextView text_caffeineIntakeLeft;
    ProgressBar prg_maxCaffeine;
    private InterstitialAd RandomInterstitialAd;
    private AdView RandomBannerAd;

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    public static final String SAVE = "Caffeinator%Save%File";

    //icon - cup of coffee HEX #FFA500 [do not remove]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Variables
        ctx = this;
        setContentView(R.layout.activity_main);

        // Data resources
        prg_maxCaffeine = findViewById(R.id.prgBar_maxCaffeine);
        text_caffeineIntakeLeft = findViewById(R.id.text_caffeineIntakeLeft);
        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        RandomBannerAd = findViewById(R.id.adView);

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
        RandomBannerAd.loadAd(new AdRequest.Builder().build());

        //UI

        // Services
        startServices();

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

        Thread syncthread = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted()){
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exchangeData();Log.i("exchangeData", "Data Exchange complete! "+ caffeineMetabolizedValue);
                                computeData();Log.i("computeData", "Calculations complete! "+ caffeineMetabolizedValue);
                                updateUI();Log.i("updateUI", "UI Updated ");
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };syncthread.start();

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

    private void updateUI() {
        // UI
        text_caffeineIntakeValue.setText(caffeineIntakeValue + "mg");
        currentCaffeineLevel = (int)caffeineIntakeValue;
        caffeineIntakeLeft = maxCaffeineIntake - caffeineIntakeValue;
        text_caffeineIntakeLeft.setText(caffeineIntakeLeft + "mg");

        prg_maxCaffeine.setMax(maxCaffeineIntake);
        prg_maxCaffeine.setProgress(currentCaffeineLevel); //we have to figure out how to calculate person's max daily caffeine intake and interpret it with this progressbar
        getPrg_maxCaffeine_currentValue = prg_maxCaffeine.getProgress();
    }

    private void exchangeData() {
        SharedPreferences sendData = getSharedPreferences(SAVE, MODE_PRIVATE);
        SharedPreferences.Editor send = sendData.edit();
        SharedPreferences receiveData = getSharedPreferences(SAVE, MODE_PRIVATE);

        caffeineMetabolizedValue = receiveData.getFloat("caffeineMetabolizedValue", 0);
        send.putFloat("caffeineIntakeValue", caffeineIntakeValue);
    }
    private void computeData() {
        caffeineIntakeValue -= caffeineMetabolizedValue;
        caffeineMetabolizedValue = 0;
    }

    public void startServices() {
        mCaffeineMetabolizationService = new caffeineMetabolizationService(getCtx());
        startCaffeineMetabolizationService = new Intent(getCtx(), caffeineMetabolizationService.class);
        if (!isMyServiceRunning(caffeineMetabolizationService.class)) {
            startCaffeineMetabolizationService.putExtra("caffeineMetabolizeValue", caffeineIntakeValue);
            startService(startCaffeineMetabolizationService);
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(startCaffeineMetabolizationService);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}
