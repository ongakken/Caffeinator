package com.simtoonsoftware.caffeinator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
    btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, AddCaffeineIntakeActivity.class));
            System.out.println("Well, it works ..."); //debug text, shows only in logcat
        }
    });
    }
}
