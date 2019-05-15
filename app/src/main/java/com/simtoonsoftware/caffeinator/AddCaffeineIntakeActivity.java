package com.simtoonsoftware.caffeinator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddCaffeineIntakeActivity extends AppCompatActivity {

    //declarations
    float caffeineIntakeValue;

    TextView text_caffeineIntakeValue;
    EditText input_CaffeineIntakeValue;
    Button btn_addCaffeineIntake;
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caffeine_intake);

    //definitions
        caffeineIntakeValue = Float.parseFloat(getResources().getString(R.string.caffeineIntakeValue));
        input_CaffeineIntakeValue = findViewById(R.id.input_CaffeineIntakeValue);
        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        btn_back = findViewById(R.id.btn_back);

        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                caffeineIntakeValue += Float.parseFloat(input_CaffeineIntakeValue.getText() + "");
                text_caffeineIntakeValue.setText("" + caffeineIntakeValue);

            }
            });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddCaffeineIntakeActivity.this, MainActivity.class));
                text_caffeineIntakeValue.setText("" + caffeineIntakeValue);
            }
        });
    }
  }

