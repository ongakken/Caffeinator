// Copyright 2019 SMD Technologies, s.r.o. All rights reserved.

package sk.smdtech.caffeinator.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import sk.smdtech.caffeinator.R;

public class AddCaffeine extends AppCompatActivity {

    // Variables
    float caffeineValue;
    float caffeineValueDefault;
    float caffeineBloodValue;

    int inputsCount = 0;

    private Handler updateHandler;

    public static final String COMM = "Caffeinator%Share%File";
    public static final String HISS = "Caffeinator%History%Save%File";
    String caffeineValueText;
    String invalidCharacter = ".";

    String coffee = "Coffee";
    String energyDrink = "Energy Drink";
    String tea = "Tea";
    String workoutPill = "Workout Pill";

    String currentType;

    // UI Data
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle drawerToggle;

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
        final TextView bloodstreamCaffeineLevel = findViewById(R.id.bloodstreamCaffeineLevel);
        final Spinner typeSpinner = findViewById(R.id.typeSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeSpinner.setAdapter(adapter);

        // Get data from MainActivity and put them in the TextView
        Intent getData = getIntent();
        caffeineValueDefault = getData.getFloatExtra("caffeineIntakeValue", caffeineValueDefault);
        caffeineBloodValue = getData.getFloatExtra("caffeineBloodValue", caffeineBloodValue);

        // UI
        drawer_layout = (DrawerLayout)findViewById(R.id.addCaffeine_activity_drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout,R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);

        drawer_layout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navView = (NavigationView)findViewById(R.id.addCaffeine_activity_nv);
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
                receiveData();
                if (caffeineValue > 0.1) {
                    bodyCaffeineLevel.setText("Body caffeine level: " + caffeineValue + "mg");
                } else {
                    bodyCaffeineLevel.setText("Body caffeine level: " + 0 + "mg");
                }
                if (caffeineBloodValue > 0.1) {
                    bloodstreamCaffeineLevel.setText("Body caffeine level: " + caffeineBloodValue + "mg");
                } else {
                    bloodstreamCaffeineLevel.setText("Body caffeine level: " + 0 + "mg");
                }
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
                    int genderSpinnerPosition = typeSpinner.getSelectedItemPosition();
                    if (genderSpinnerPosition == 0) {
                        currentType = coffee;
                    } else if (genderSpinnerPosition == 1) {
                        currentType = energyDrink;
                    } else if (genderSpinnerPosition == 2) {
                        currentType = tea;
                    } else {
                        currentType = workoutPill;
                    }
                    caffeineValue = Float.valueOf(caffeineAmount.getText().toString());
                    caffeineValueDefault += caffeineValue;
                    bodyCaffeineLevel.setText("Caffeine Amount: " + caffeineValueDefault);
                    updateHandler.removeCallbacksAndMessages(null);

                    // Pass back the data and safely finish the activity
                    Intent intent = new Intent();
                    intent.putExtra("caffeineAddValue", caffeineValue);
                    intakeLog();
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
        finish();
    }

    private void intakeLog() {
        SharedPreferences submitData = getSharedPreferences(HISS, MODE_PRIVATE);
        SharedPreferences.Editor submit = submitData.edit();
        inputsCount ++;

        submit.putInt("inputsCount", inputsCount);
        submit.putString("intakeType", currentType);
        submit.putFloat("caffeine", caffeineValue);

        submit.commit();
    }

    private void receiveData() {
        SharedPreferences receiveData = getSharedPreferences(COMM, MODE_PRIVATE);

        caffeineValue = receiveData.getFloat("caffeineIntakeValue", caffeineValue);
        caffeineBloodValue = receiveData.getFloat("caffeineBloodValue", caffeineValue);
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
