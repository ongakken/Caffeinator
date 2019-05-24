package com.simtoonsoftware.caffeinator.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.simtoonsoftware.caffeinator.R;

public class AddCaffeine extends AppCompatActivity {

    // Variables
    float caffeineValueDefault;
    float caffeineValueFinal;
    float caffeineValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caffeine);

        // Resources
        final EditText caffeineAmount = findViewById(R.id.caffeineAmount);
        Button addCaffeine = findViewById(R.id.addCaffeineButton);
        final TextView textView = findViewById(R.id.textView);

        // Get data from MainActivity and put them in the TextView
        Intent getData = getIntent();
        caffeineValueDefault = getData.getFloatExtra("caffeineIntakeValue", caffeineValueDefault);
        textView.setText(Float.toString(caffeineValueDefault));

        addCaffeine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caffeineValue = Float.valueOf(caffeineAmount.getText().toString());
                caffeineValue += caffeineValueDefault;
                textView.setText(Float.toString(caffeineValue));
            }
        });
    }
}
