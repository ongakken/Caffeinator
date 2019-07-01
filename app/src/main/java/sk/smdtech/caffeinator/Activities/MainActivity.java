// Copyright 2019 SMD Technologies, s.r.o. All rights reserved.

package sk.smdtech.caffeinator.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Date;

import services.caffeineMetabolizationService;
import sk.smdtech.caffeinator.R;

public class MainActivity extends AppCompatActivity {

    // Variables
    float caffeineIntakeValue;
    float caffeineAddValue;
    float caffeineIntakeLeft;

    int currentCaffeineLevel;
    int maxCaffeineIntake = 400;
    int getPrg_maxCaffeine_currentValue;

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
    TextView text_caffeineIntakeValue;
    TextView text_caffeineIntakeLeft;
    TextView intakeLog;
    ProgressBar prg_maxCaffeine;

    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle drawerToggle;

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    public static final String SAVE = "Caffeinator%Save%File";

    //icon - cup of coffee HEX #FFA500 [do not remove]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Variables
        ctx = this;
        updateHandler = new Handler();

        // Data resources
        prg_maxCaffeine = findViewById(R.id.prgBar_maxCaffeine);
        text_caffeineIntakeLeft = findViewById(R.id.caffeineIntakeLeftText);
        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        intakeLog = findViewById(R.id.intakeLog);

        // Auto Save/Load section
        final SharedPreferences loadInstance = getSharedPreferences(SAVE, MODE_PRIVATE);

        caffeineIntakeValue = loadInstance.getFloat("caffeineIntakeValue", 0);
        logHistory = loadInstance.getString("logHistory", "\n Log initialized!");

        // Services
        startServices();

        // GPS
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // UI
        intakeLog.setMovementMethod(new ScrollingMovementMethod());
        intakeLog.append(logHistory);
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
                } else if (id == R.id.graph) {
                    switchIntent(GraphActivity.class);
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

        logHistory = intakeLog.getText().toString();
        save.putString("logHistory", logHistory);
        save.putFloat("caffeineIntakeValue", caffeineIntakeValue);
        save.apply();
    }

    private void updateUI() {
        // UI
        text_caffeineIntakeValue.setText(caffeineIntakeValue + "mg");
        currentCaffeineLevel = (int) caffeineIntakeValue;
        caffeineIntakeLeft = maxCaffeineIntake - caffeineIntakeValue;
        caffeineIntakeLeft = Math.round(caffeineIntakeLeft * 100.0f) / 100.0f;
        text_caffeineIntakeLeft.setText(caffeineIntakeLeft + "mg");
        prg_maxCaffeine.setMax(maxCaffeineIntake);
        prg_maxCaffeine.setProgress(currentCaffeineLevel); //we have to figure out how to calculate person's max daily caffeine intake and interpret it with this progressbar
        getPrg_maxCaffeine_currentValue = prg_maxCaffeine.getProgress();
    }

    private void receiveData() {
        SharedPreferences receiveData = getSharedPreferences(SAVE, MODE_PRIVATE);

        caffeineIntakeValue = receiveData.getFloat("caffeineMetabolizedValue", caffeineIntakeValue);
    }

    private void sendData() {
        SharedPreferences sendData = getSharedPreferences(SAVE, MODE_PRIVATE);
        SharedPreferences.Editor send = sendData.edit();

        send.putFloat("caffeineAddValue", caffeineAddValue);
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
        intakeLog.append("\n | " + currentTime + " | Consumed: " + amount + "mg");

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
                    intakeLog.append("\n " + (CharSequence) startingLocation);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    intakeLog.append("\n " + "PERMISSION DENIED "+ "\n " +" Couldn't retrieve user location!");
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
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();
    }
}
