package com.simtoonsoftware.caffeinator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //definitions and declarations
    float caffeineIntakeValue = 0;
    TextView text_caffeineIntakeValue;
    Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);

    //icon - cup of coffee HEX #FFA500

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        text_caffeineIntakeValue.setText("" + caffeineIntakeValue);
        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddCaffeineIntakeActivityIntent = new Intent(getApplicationContext(), AddCaffeineIntakeActivity.class);
                startActivity(AddCaffeineIntakeActivityIntent);
            }
        });


    }}
