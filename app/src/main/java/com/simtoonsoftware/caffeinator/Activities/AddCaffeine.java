package com.simtoonsoftware.caffeinator.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.simtoonsoftware.caffeinator.R;

import org.w3c.dom.Text;

public class AddCaffeine extends AppCompatActivity {

    // Variables
    float caffeineValueDefault;
    float caffeineValueFinal;
    float caffeineValue;

    private Handler updateHandler;

    public static final String SAVE = "Caffeinator%Save%File";
    String caffeineValueText;
    String invalidCharacter = ".";

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
        final TextView textView = findViewById(R.id.addCaffeineText);

        // Get data from MainActivity and put them in the TextView
        Intent getData = getIntent();
        caffeineValueDefault = getData.getFloatExtra("caffeineIntakeValue", caffeineValueDefault);

        //UI
        updateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                receiveData();
                textView.setText("Caffeine Amount: " + caffeineValue);
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
                    textView.setText("Caffeine Amount: " + caffeineValueDefault);

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

    private void receiveData() {
        SharedPreferences receiveData = getSharedPreferences(SAVE, MODE_PRIVATE);

        caffeineValue = receiveData.getFloat("caffeineMetabolizedValue", caffeineValue);
    }
}
