/*
 * AboutActivity.java
 * - This file is part of the Android app RomanDigital
 *
 * Copyright 2024,2026 David Yockey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.diffengine.romandigitalclock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        // Compensate for forced edge-to-edge in SDK 35 (Android 15) and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            View containerView = findViewById(android.R.id.content);
            ViewCompat.setOnApplyWindowInsetsListener(containerView, (v, insets) -> {

                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
                v.setPadding(bars.left, 0, bars.right, bars.bottom);

                View spacer = findViewById(R.id.spacerAbout);
                spacer.getLayoutParams().height = bars.top;

                return WindowInsetsCompat.CONSUMED;
            });
        }

        TextView tv = findViewById(R.id.tvVersion);

        // BuildConfig full package name is to prevent any possible future confusion with org.acra.BuildConfig
        String appversion = net.diffengine.romandigitalclock.BuildConfig.VERSION_NAME;

        tv.setText(getString(R.string.app_version_label, appversion));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Call in onResume method of MainActivity and WidgetSettingsActivity
    // to show the About activity on app installation or update.
    public static void showAboutOnUpgrade(Context context, int buildVersion) {
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
        int versionCode = prefManager.getInt("lastVersionAboutShownOnUpgrade",0);
        if (BuildConfig.VERSION_CODE > versionCode){
            Intent showActivityIntent = new Intent(context, AboutActivity.class);
            context.startActivity(showActivityIntent);
            prefManager.edit().putInt("lastVersionAboutShownOnUpgrade", buildVersion).apply();
        }
    }

///// Retain the following as a template for similar setting clearance methods in the future...
//
//    // For debugging only : call on click of a button only shown and enabled in a DEBUG build.
//    public static void clearAboutOnUpgrade(Context context, String prefToRemove) {
//        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(context);
//        prefManager.edit().remove(prefToRemove).apply();
//        Toast.makeText(context, "Removed " + prefToRemove, Toast.LENGTH_LONG).show();
//    }
}