/*
 * Copyright 2019 SMD Technologies, s.r.o. All rights reserved.
 */

package sk.smdtech.caffeinator.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import sk.smdtech.caffeinator.R;

public class WizardActivityMain extends AppCompatActivity implements DialogInterface.OnDismissListener {

    public static final String SAVE = "Caffeinator%Main%Save%File";
    public static final String RECEIVE_FROM_SERVICE = "Caffeinator%Share%File";
    Context ctx = this;
    boolean privacy_policy_accepted = false;

    String Gender;
    int Age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard_main);

        final SharedPreferences loadInstance = getSharedPreferences(SAVE, MODE_PRIVATE);
        SharedPreferences loadSecondInstance = getSharedPreferences(RECEIVE_FROM_SERVICE, MODE_PRIVATE);

        privacy_policy_accepted = loadInstance.getBoolean("privacyPolicyAccepted", privacy_policy_accepted);
        Age = loadSecondInstance.getInt("Age", 0);
        Gender = loadSecondInstance.getString("Gender", "Hello");

        if(privacy_policy_accepted && Age > 0 && Gender == "Male" || Gender == "Female") {
            switchIntent(MainActivity.class);
            finish();
        }

        showPrivacyPolicyAlert();

        SharedPreferences saveInstance = getSharedPreferences(SAVE, MODE_PRIVATE);
        SharedPreferences.Editor save = saveInstance.edit();

        save.putBoolean("privacyPolicyAccepted", privacy_policy_accepted);

        Button btn_start = findViewById(R.id.btn_submit);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchIntent(WizardActivitySecondary.class);
                finish();
            }
        });

    }

    private void showPrivacyPolicyAlert() {
        new AlertDialog.Builder(ctx)
                .setTitle("Privacy Policy")
                .setMessage(R.string.privacy_policy_dialog)
                .setCancelable(false)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        privacy_policy_accepted = true;
                    }
                })
                .setNeutralButton(R.string.show_policy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchIntent(PrivacyPolicyActivity.class);
                    }
                })
                .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        privacy_policy_accepted = false;
                        finish();
                    }
                })
                .setOnDismissListener(WizardActivityMain.this)
                .show();
    }

    private void switchIntent(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
    }

    public void onDismiss(DialogInterface dialog) {
        // Privacy Policy Dialog
        if(!privacy_policy_accepted)
            showPrivacyPolicyAlert();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
