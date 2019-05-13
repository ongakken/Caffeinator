package com.simtoonsoftware.caffeinator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
        EditText input_CaffeineIntakeValue = findViewById(R.id.input_CaffeineIntakeValue);

        input_CaffeineIntakeValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO) {
                    getApplicationContext().getResources().getInteger(R.integer.caffeineIntakeValue) = textView.getText().toString();
                    Toast.makeText(MainActivity.this, "Current caffeine level: "
                            + getApplicationContext().getResources().getInteger(R.integer.caffeineIntakeValue), Toast.LENGTH_SHORT).show();
                }
                return handled;
            }

        });
        btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, AddCaffeineIntakeActivity.class));
            System.out.println("Switched to the 'AddCaffeineIntakeActivity' Activity"); //debug text, shows only in logcat
        }});
    }
}
