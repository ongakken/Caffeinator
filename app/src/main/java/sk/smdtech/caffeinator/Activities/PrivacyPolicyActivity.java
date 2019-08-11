/*
 * Copyright 2019 SMD Technologies, s.r.o. All rights reserved.
 */

package sk.smdtech.caffeinator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import sk.smdtech.caffeinator.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    TextView privacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        privacyPolicy = findViewById(R.id.textView3);
        privacyPolicy.setMovementMethod(new ScrollingMovementMethod());
    }
}
