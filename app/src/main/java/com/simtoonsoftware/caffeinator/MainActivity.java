package com.simtoonsoftware.caffeinator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //definitions and declarations
    float caffeineIntakeValue;
    text_caffeineIntakeValue = (TextView) findViewById(R.id.text_caffeineIntakeValue);

    EditText input_CaffeineIntakeValue;
    TextView text_caffeineIntakeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_caffeineIntakeValue.setText("" + caffeineIntakeValue);
            //System.out.println("Switched to the 'AddCaffeineIntakeActivity' Activity" + caffeineIntakeValue); //debug text, shows only in logcat
    }}
