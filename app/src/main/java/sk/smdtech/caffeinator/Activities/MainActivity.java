// Copyright 2019 SMD Technologies, s.r.o. All rights reserved.

package sk.smdtech.caffeinator.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Date;

import classes.CircularProgressBar;
import services.caffeineMetabolizationService;
import sk.smdtech.caffeinator.R;

public class MainActivity extends AppCompatActivity {

    // Variables
    float caffeineIntakeValue;
    float caffeineBloodValue;
    float caffeineAddValue;
    float caffeineIntakeLeft;

    int maxCaffeineIntake = 400;

    String logHistory;

    private Handler updateHandler;

    Intent startCaffeineMetabolizationService;
    private caffeineMetabolizationService mCaffeineMetabolizationService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    //GPS
    boolean gps_enabled = false;
    boolean network_enabled = false;

    LocationManager lm;

    // UI data types
    TextView bodyCaffeine;
    TextView bloodCaffeine;
    TextView safeAmount;
    TextView totalCaffeine;
    TextView waterIntake;

    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle drawerToggle;
    private CircularProgressBar circularProgressBar;

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    public static final String SAVE = "Caffeinator%Main%Save%File";
    public static final String RECEIVE_FROM_SERVICE = "Caffeinator%Share%File";

    //icon - cup of coffee HEX #FFA500 [do not remove]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Variables
        ctx = this;
        updateHandler = new Handler();

        // Data resources
        Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);

        // Auto Save/Load section
        final SharedPreferences loadInstance = getSharedPreferences(SAVE, MODE_PRIVATE);

        caffeineIntakeValue = loadInstance.getFloat("caffeineIntakeValue", 0);
        caffeineBloodValue = loadInstance.getFloat("caffeineBloodValue", 0);
        logHistory = loadInstance.getString("logHistory", "\n Log initialized!");

        // Services
        startServices();

        // UI
        bodyCaffeine = findViewById(R.id.bodyCaffeine);
        bloodCaffeine = findViewById(R.id.bloodCaffeine);
        totalCaffeine = findViewById(R.id.totalCaffeine);
        circularProgressBar = findViewById(R.id.custom_progressBar);
        safeAmount = findViewById(R.id.safeAmount);
        waterIntake = findViewById(R.id.waterIntake);


        drawer_layout = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);

        drawer_layout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navView = (NavigationView) findViewById(R.id.main_activity_nv);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.overview) {
                    switchIntent(MainActivity.class);
                } else if (id == R.id.history) {
                    switchIntent(HistoryActivity.class);
                } else if (id == R.id.about) {
                    switchIntent(AboutActivity.class);
                }
                return true;
            }
        });

        updateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                receiveData();  //Log.i("exchangeData", "Data Exchange complete! "+ caffeineIntakeValue);
                updateUI(); //Log.i("updateUI", "UI Updated! ");
                saveData(); //Log.i("saveData", "Saving... ");
                // Repeat this every 250ms
                updateHandler.postDelayed(this, 250);
            }
        }, 25);

        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCaffeineIntakeActivity = new Intent(MainActivity.this, AddCaffeine.class);
                addCaffeineIntakeActivity.putExtra("caffeineIntakeValue", caffeineIntakeValue);
                addCaffeineIntakeActivity.putExtra("caffeineBloodValue", caffeineBloodValue);
                startActivityForResult(addCaffeineIntakeActivity, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check that it is the addCaffeineActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Get data from Intent and send them
                caffeineAddValue = data.getFloatExtra("caffeineAddValue", caffeineAddValue);
                Log.i("AddCaffeine ", "Caffeine received! " + caffeineAddValue + "-----------------------------------------");
                log(caffeineAddValue);
                sendData();
                receiveData();
            }
        }
    }

    private void saveData() {
        SharedPreferences saveInstance = getSharedPreferences(SAVE, MODE_PRIVATE);
        SharedPreferences.Editor save = saveInstance.edit();

        save.putString("logHistory", logHistory);
        save.putFloat("caffeineIntakeValue", caffeineIntakeValue);
        save.putFloat("caffeineBloodValue", caffeineBloodValue);
        save.apply();
    }

    private void updateUI() {
        // UI
        caffeineIntakeValue = Math.round(caffeineIntakeValue * 100.0f) / 100.0f;
        bodyCaffeine.setText("Caffeine in body: " + caffeineIntakeValue);
        caffeineBloodValue = Math.round(caffeineBloodValue * 100.0f) / 100.0f;
        bloodCaffeine.setText("Caffeine in blood: " + caffeineBloodValue);
        caffeineIntakeLeft = maxCaffeineIntake - currentCaffeineDisplayLevel();
        caffeineIntakeLeft = Math.round(caffeineIntakeLeft * 100.0f) / 100.0f;

        safeAmount.setText("You can still consume: " + caffeineIntakeLeft + "mg");

        Float caffeineDisplayTextInteger = currentCaffeineDisplayLevel();
        caffeineDisplayTextInteger = Math.round(caffeineDisplayTextInteger * 100.0f) / 100.0f;
        String totalCaffeineString = Float.toString(caffeineDisplayTextInteger);
        totalCaffeine.setText("Total Caffeine: " + totalCaffeineString);
        circularProgressBar.setProgress(currentCaffeineDisplayLevel());
        calculateWaterIntake();
    }

    private void calculateWaterIntake() {
        if(currentCaffeineDisplayLevel() >= 50 && currentCaffeineDisplayLevel() <= 100) {
            waterIntake.setText("Recommended Water Intake: 1 cup");
        } else if(currentCaffeineDisplayLevel() >= 100 && currentCaffeineDisplayLevel() <= 200) {
            waterIntake.setText("Recommended Water Intake: 2 cups");
        } else if(currentCaffeineDisplayLevel() >= 200 && currentCaffeineDisplayLevel() <= 300) {
            waterIntake.setText("Recommended Water Intake: 3 cups");
        } else if(currentCaffeineDisplayLevel() >= 300) {
            waterIntake.setText("Recommended Water Intake: 4 cups");
        }
    }

    private void receiveData() {
        SharedPreferences receiveData = getSharedPreferences(RECEIVE_FROM_SERVICE, MODE_PRIVATE);

        caffeineIntakeValue = receiveData.getFloat("caffeineIntakeValue", caffeineIntakeValue);
        caffeineBloodValue = receiveData.getFloat("caffeineBloodValue", caffeineBloodValue);
    }

    private void sendData() {
        SharedPreferences sendData = getSharedPreferences(RECEIVE_FROM_SERVICE, MODE_PRIVATE);
        SharedPreferences.Editor send = sendData.edit();

        send.putFloat("caffeineAddValue", caffeineAddValue);

        //Send to addCaffeine activity
        send.putFloat("caffeineBodyValue", caffeineIntakeValue);
        send.putFloat("caffeineBloodValue", caffeineBloodValue);

        send.apply();
        caffeineAddValue = 0; //This value has been sent and we don't need it anymore
    }

    private void switchIntent(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
    }

    public void startServices() {
        mCaffeineMetabolizationService = new caffeineMetabolizationService(getCtx());
        startCaffeineMetabolizationService = new Intent(getCtx(), caffeineMetabolizationService.class);
        if (!isMyServiceRunning(caffeineMetabolizationService.class)) {
            startService(startCaffeineMetabolizationService);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    private void log(float amount) {
        Date currentTime = Calendar.getInstance().getTime();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @SuppressLint("MissingPermission")
    private Location getGPSLocation() {
        if(!(checkLocationPermission()))
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        else return null;

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location gps_loc = new Location("GPS_PROVIDER");
        Location net_loc = new Location("NETWORK_PROVIDER");
        Location finalLoc = new Location("Caffeinator_Provider");

        if (gps_enabled && checkLocationPermission()) {
            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (network_enabled && checkLocationPermission()) {
            net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (gps_loc != null && net_loc != null) {
            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;
        } else {
            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }
        return finalLoc;
    }

    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location startingLocation = getGPSLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            default: {
                // nothing
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }


    @Override
    protected void onDestroy() {
        stopService(startCaffeineMetabolizationService);
        Log.i("Watchdog:", "Main Activity has been destroyed!");
        super.onDestroy();
    }

    private float currentCaffeineDisplayLevel(){
        float totalCaffeine = caffeineIntakeValue + caffeineBloodValue;
        return totalCaffeine;
    }
}
