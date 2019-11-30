// Copyright 2019 SMD Technologies, s.r.o. All rights reserved.

package sk.smdtech.caffeinator.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import sk.smdtech.caffeinator.R;

public class AddCaffeine extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private InterstitialAd mInterstitialAd;

    // Variables
    float caffeineValue;
    float caffeineValueDefault;
    float caffeineBloodValue;

    int inputsCount;

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

    // Coffee Buttons
    Button smallCoffee;
    Button mediumCoffee;
    Button largeCoffee;
    Button cafeLatte;
    Button cappuccino;
    Button espresso;
    Button clrbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caffeine);
        // Ad Section
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        // Variables
        updateHandler = new Handler();

        // Resources
        final EditText caffeineAmount = findViewById(R.id.caffeineAmount);
        // Coffee Buttons
        smallCoffee = findViewById(R.id.smallCoffee);
        mediumCoffee = findViewById(R.id.mediumCoffee);
        largeCoffee = findViewById(R.id.largeCoffee);
        cafeLatte = findViewById(R.id.cafeLatte);
        cappuccino = findViewById(R.id.cappuccino);
        espresso = findViewById(R.id.espresso);
         // Making Buttons invisible on start
        smallCoffee.setVisibility(View.INVISIBLE);
        mediumCoffee.setVisibility(View.INVISIBLE);
        largeCoffee.setVisibility(View.INVISIBLE);
        cafeLatte.setVisibility(View.INVISIBLE);
        cappuccino.setVisibility(View.INVISIBLE);
        espresso.setVisibility(View.INVISIBLE);
         // Tea Buttons

        // Other Resources
        Button addCaffeine = findViewById(R.id.addCaffeineButton);
        Button clrbtn = findViewById(R.id.clrbtn);
        final TextView invalidValue = findViewById(R.id.invalidValue);
        final TextView bodyCaffeineLevel = findViewById(R.id.bodyCaffeineLevel);
        final TextView bloodstreamCaffeineLevel = findViewById(R.id.bloodstreamCaffeineLevel);
        final Spinner typeSpinner = findViewById(R.id.typeSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(this);

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
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    finish();
                }
            }
        });
        clrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineAmount.setText("");
            }
        });
        smallCoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineAmount.setText("70");
            }
        });
        mediumCoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineAmount.setText("110");
            }
        });
        largeCoffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineAmount.setText("160");
            }
        });
        cafeLatte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineAmount.setText("31.70");
            }
        });
        cappuccino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineAmount.setText("43.39");
            }
        });
        espresso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineAmount.setText("63.6");
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        if (selectedItem.equals(coffee)) {
            currentType = coffee;
            setCoffeeVisible();
        } else if (selectedItem.equals(energyDrink)) {
            currentType = energyDrink;
            setCoffeeInvisible();
        } else if (selectedItem.equals(tea)) {
            currentType = tea;
            setCoffeeInvisible();
        } else if (selectedItem.equals(workoutPill)){
            currentType = workoutPill;
            setCoffeeInvisible();
        }
        Toast.makeText(parent.getContext(), selectedItem, Toast.LENGTH_SHORT).show();
        Log.i("AddCaffeine ", selectedItem + "WAS SELECTED!");
    }

    private void setCoffeeVisible() {
        smallCoffee.setVisibility(View.VISIBLE);
        mediumCoffee.setVisibility(View.VISIBLE);
        largeCoffee.setVisibility(View.VISIBLE);
        cafeLatte.setVisibility(View.VISIBLE);
        cappuccino.setVisibility(View.VISIBLE);
        espresso.setVisibility(View.VISIBLE);
        Log.i("AddCaffeine ", "Coffee Buttons went visible!");
    }

    private void setCoffeeInvisible() {
        smallCoffee.setVisibility(View.INVISIBLE);
        mediumCoffee.setVisibility(View.INVISIBLE);
        largeCoffee.setVisibility(View.INVISIBLE);
        cafeLatte.setVisibility(View.INVISIBLE);
        cappuccino.setVisibility(View.INVISIBLE);
        espresso.setVisibility(View.INVISIBLE);
        Log.i("AddCaffeine ", "Coffee Buttons went invisible!");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void switchIntent(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
        finish();
    }

    private void intakeLog() {
        final SharedPreferences loadInstance = getSharedPreferences(HISS, MODE_PRIVATE);
        inputsCount = loadInstance.getInt("inputsCount", 0);

        SharedPreferences submitData = getSharedPreferences(HISS, MODE_PRIVATE);
        SharedPreferences.Editor submit = submitData.edit();
        inputsCount += 1;

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

}
