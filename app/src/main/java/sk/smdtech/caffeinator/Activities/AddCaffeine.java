// Copyright 2019 SMD Technologies, s.r.o. All rights reserved.

package sk.smdtech.caffeinator.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import sk.smdtech.caffeinator.R;

public class AddCaffeine extends AppCompatActivity {

    // Variables
    float caffeineValueDefault;
    float caffeineValue;

    private Handler updateHandler;

    public static final String SAVE = "Caffeinator%Save%File";
    String caffeineValueText;
    String invalidCharacter = ".";

    // UI Data
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle drawerToggle;

    EditText caffeineAmount;

    TextView invalidValue;
    TextView bodyCaffeineLevel;
    TextView bloodstreamCaffeineLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caffeine);
        // Variables
        updateHandler = new Handler();

        // Resources
        final EditText caffeineAmount = findViewById(R.id.caffeineAmount);
        Button addCaffeine = findViewById(R.id.addCaffeineButton);
        final TextView invalidValue = findViewById(R.id.invalidValue);
        final TextView bodyCaffeineLevel = findViewById(R.id.bodyCaffeineLevel);
        bloodstreamCaffeineLevel = findViewById(R.id.bloodstreamCaffeineLevel);

        // Get data from MainActivity and put them in the TextView
        Intent getData = getIntent();
        caffeineValueDefault = getData.getFloatExtra("caffeineIntakeValue", caffeineValueDefault);

        // UI
        drawer_layout = (DrawerLayout)findViewById(R.id.addCaffeine_activity_drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout,R.string.Open, R.string.Close);
        drawerToggle.setDrawerIndicatorEnabled(true);

        drawer_layout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navView = (NavigationView)findViewById(R.id.addCaffeine_activity_nv);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.overview) {
                    switchIntent(MainActivity.class);
                } else if(id == R.id.graph) {
                    switchIntent(GraphActivity.class);
                }
                finish();
                return true;
            }
        });


        updateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                receiveData();
                bodyCaffeineLevel.setText("Body caffeine level: " + caffeineValue);
                bloodstreamCaffeineLevel.setText("Bloodstream caffeine level: " + "N/A");
                updateHandler.postDelayed(this, 250);
            }
        }, 25);

        addCaffeine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineValueText = caffeineAmount.getText().toString();
                if(caffeineAmount.length() == 0 || caffeineValueText.equals(invalidCharacter)) {
                    invalidValue.setText("Please enter valid value.");
                } else {
                    caffeineValue = Float.valueOf(caffeineAmount.getText().toString());
                    caffeineValueDefault += caffeineValue;
                    bodyCaffeineLevel.setText("Caffeine Amount: " + caffeineValueDefault);
                    updateHandler.removeCallbacksAndMessages(null);

                    // Pass back the data and safely finish the activity
                    Intent intent = new Intent();
                    intent.putExtra("caffeineAddValue", caffeineValue);
                    caffeineValue = 0;
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void switchIntent(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
    }

    private void receiveData() {
        SharedPreferences receiveData = getSharedPreferences(SAVE, MODE_PRIVATE);

        caffeineValue = receiveData.getFloat("caffeineMetabolizedValue", caffeineValue);
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
