package com.simtoonsoftware.caffeinator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //definitions and declarations
    float caffeineIntakeValue;
    TextView text_caffeineIntakeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_caffeineIntakeValue.setText("" + caffeineIntakeValue);
        //System.out.println("Switched to the 'AddCaffeineIntakeActivity' Activity" + caffeineIntakeValue); //debug text, shows only in logcat
    }}
