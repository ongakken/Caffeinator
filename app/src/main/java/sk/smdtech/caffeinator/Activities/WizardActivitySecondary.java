/*
 * Copyright 2019 SMD Technologies, s.r.o. All rights reserved.
 */

package sk.smdtech.caffeinator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import sk.smdtech.caffeinator.R;

public class WizardActivitySecondary extends AppCompatActivity{

    public static final String RECEIVE_FROM_SERVICE = "Caffeinator%Share%File";
    String Gender;
    String AgeString;

    int Age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_secondary);

        final EditText ageInput = findViewById(R.id.ageInputText);
        final Spinner genderSpinner = (Spinner) findViewById(R.id.spinner);
        Button btn_submit = findViewById(R.id.btn_submit);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Genders, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        genderSpinner.setAdapter(adapter);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int genderSpinnerPosition = genderSpinner.getSelectedItemPosition();
                if (genderSpinnerPosition == 0) {
                    Gender = "Male";
                } else {
                    Gender = "Female";
                }
                String AgeString = ageInput.getText().toString();
                Age = Integer.valueOf(AgeString);

                submitData();
                switchIntent(MainActivity.class);
                finish();
            }
        });
    }

    private void submitData() {
        SharedPreferences submitData = getSharedPreferences(RECEIVE_FROM_SERVICE, MODE_PRIVATE);
        SharedPreferences.Editor submit = submitData.edit();

        submit.putInt("Age", Age);
        submit.putString("Gender", Gender);

        submit.apply();
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String Gender = (String) parent.getItemAtPosition(pos);
        if (Gender == "Male")
                finish();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void switchIntent(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
        finish();
    }


}
