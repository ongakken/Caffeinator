package com.simtoonsoftware.caffeinator;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddCaffeineIntakeActivity extends AppCompatActivity {

    //declarations and definitions
    text_caffeineIntakeValue = (TextView) findViewById(R.id.text_caffeineIntakeValue);
    Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
    input_CaffeineIntakeValue = (EditText) findViewById(R.id.input_CaffeineIntakeValue);
    float caffeineIntakeValue;

    btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // startActivity(new Intent(MainActivity.this, AddCaffeineIntakeActivity.class));
            caffeineIntakeValue = caffeineIntakeValue + Float.parseFloat(input_CaffeineIntakeValue.getText() + "");
            text_caffeineIntakeValue.setText("" + caffeineIntakeValue);
        });
    }
}
