/*
 * ScreenSettingsFragment.java
 * - This file is part of the Android app RomanDigital
 *   and was derived from a fragment removed from AppSettingsActivity.java
 *
 * Copyright © 2024-2026 David Yockey
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

package net.diffengine.romandigitalclock.fragment.preference;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import net.diffengine.romandigitalclock.R;

public class ScreenSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.screen_preferences, rootKey);
    }
}
