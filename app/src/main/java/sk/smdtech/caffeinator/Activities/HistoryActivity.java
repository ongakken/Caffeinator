// Copyright 2019 SMD Technologies, s.r.o. All rights reserved.

package sk.smdtech.caffeinator.Activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import sk.smdtech.caffeinator.R;

public class HistoryActivity extends AppCompatActivity {

    // UI Data
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle drawerToggle;
    public TextView intakeLog;

    public static final String HISS = "Caffeinator%History%Save%File";
    public static final String COMM = "Caffeinator%Share%File";
    String logHistory;

    private Handler updateHandler;

    Float caffeine;
    int inputsCountBefore = 0;
    int inputsCount = 0;
    String intakeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        updateHandler = new Handler();


        // Auto Save/Load section
        final SharedPreferences loadInstance = getSharedPreferences(HISS, MODE_PRIVATE);
        logHistory = loadInstance.getString("logHistory", "\n Log initialized!");

        // UI
        drawer_layout = (DrawerLayout)findViewById(R.id.graph_activity_drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout,R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        intakeLog = findViewById(R.id.historyLog);
        drawer_layout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        intakeLog.setMovementMethod(new ScrollingMovementMethod());
        intakeLog.append(logHistory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navView = (NavigationView)findViewById(R.id.graph_activity_nv);
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
                getData();
                log(caffeine, intakeType);
                saveData();

                // Repeat this every 1500ms
                updateHandler.postDelayed(this, 250);
            }
        }, 0);

    }

    private void log(float amount, String type) {
        if (inputsCount > inputsCountBefore) {
            Date currentTime = Calendar.getInstance().getTime();
            intakeLog.append("\n | " + currentTime + " | Consumed: " + type + " " + amount + "mg");
            inputsCountBefore += 1;
        }
    }

    private void saveData() {
        SharedPreferences saveInstance = getSharedPreferences(HISS, MODE_PRIVATE);
        SharedPreferences.Editor save = saveInstance.edit();

        logHistory = intakeLog.getText().toString();
        save.putString("logHistory", logHistory);
        save.putInt("inputsCount", inputsCount);
        save.putInt("inputsCountBefore", inputsCountBefore);
        save.apply();
    }

    private void getData() {
        final SharedPreferences loadInstance = getSharedPreferences(HISS, MODE_PRIVATE);

        inputsCountBefore = loadInstance.getInt("inputsCountBefore", 0);
        inputsCount = loadInstance.getInt("inputsCount", 0);
        intakeType = loadInstance.getString("intakeType", "Hello");
        caffeine = loadInstance.getFloat("caffeine", 0);
    }

    private void switchIntent(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
}