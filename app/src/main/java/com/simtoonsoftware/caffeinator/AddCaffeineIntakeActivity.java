package com.simtoonsoftware.caffeinator;

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
    Button btn_addCaffeineIntake;
    EditText input_CaffeineIntakeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caffeine_intake);

    //definitions
        text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
        btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        input_CaffeineIntakeValue = findViewById(R.id.input_CaffeineIntakeValue);


        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                caffeineIntakeValue += Float.parseFloat(input_CaffeineIntakeValue.getText() + "");
                text_caffeineIntakeValue.setText("" + caffeineIntakeValue);

            }
            });
    }
  }

