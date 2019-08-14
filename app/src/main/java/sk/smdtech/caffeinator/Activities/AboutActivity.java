/*
 * Copyright 2019 SMD Technologies, s.r.o. All rights reserved.
 */

package sk.smdtech.caffeinator.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

import sk.smdtech.caffeinator.R;

public class AboutActivity extends AppCompatActivity {

    // UI Data
    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle drawerToggle;
    private Button showPrivacyPolicyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // UI
        drawer_layout = (DrawerLayout)findViewById(R.id.about_activity_drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer_layout,R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        showPrivacyPolicyButton = findViewById(R.id.privacyPolicyButton);

        drawer_layout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showPrivacyPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCaffeineIntakeActivity = new Intent(AboutActivity.this, PrivacyPolicyActivity.class);
                startActivity(addCaffeineIntakeActivity);
                finish();
            }
        });

        NavigationView navView = (NavigationView)findViewById(R.id.about_activity_nv);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.overview) {
                    switchIntent(MainActivity.class);
                } else if (id == R.id.graph) {
                    switchIntent(GraphActivity.class);
                } else if (id == R.id.about) {
                    switchIntent(AboutActivity.class);
                }
                return true;
            }
        });

    }

    private void switchIntent(Class targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
}
