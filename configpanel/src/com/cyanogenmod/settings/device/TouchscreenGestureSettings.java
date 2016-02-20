/*
 * Copyright (C) 2015 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.settings.device;

import com.cyanogenmod.settings.device.utils.Constants;
import com.cyanogenmod.settings.device.utils.FileUtils;
import com.cyanogenmod.settings.device.utils.NodePreferenceActivity;

import org.mokee.internal.util.ScreenType;

import android.os.Bundle;
import android.provider.Settings;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.preference.ListPreference;

public class TouchscreenGestureSettings extends NodePreferenceActivity {
    
    private static final String KEY_HAPTIC_FEEDBACK = "touchscreen_gesture_haptic_feedback";

    private SwitchPreference mHapticFeedback;
    private SwitchPreference mKeySwap;
    
    private ListPreference mSliderTop;
    private ListPreference mSliderMiddle;
    private ListPreference mSliderBottom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.touchscreen_panel);

        SwitchPreference mKeySwap = (SwitchPreference) findPreference(Constants.KEY_SWAP_KEY);

        mHapticFeedback = (SwitchPreference) findPreference(KEY_HAPTIC_FEEDBACK);
        mHapticFeedback.setOnPreferenceChangeListener(this);
        
        mSliderTop = (ListPreference) findPreference("keycode_top_position");
        mSliderTop.setOnPreferenceChangeListener(this);

        mSliderMiddle = (ListPreference) findPreference("keycode_middle_position");
        mSliderMiddle.setOnPreferenceChangeListener(this);

        mSliderBottom = (ListPreference) findPreference("keycode_bottom_position");
        mSliderBottom.setOnPreferenceChangeListener(this);
    }

    private void setSummary(ListPreference preference, String file) {
        String keyCode;
        if ((keyCode = FileUtils.readOneLine(file)) != null) {
            preference.setValue(keyCode);
            preference.setSummary(preference.getEntry());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        if (KEY_HAPTIC_FEEDBACK.equals(key)) {
            final boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), KEY_HAPTIC_FEEDBACK, value ? 1 : 0);
            return true;
        }

        if (preference instanceof SwitchPreference) {
            return super.onPreferenceChange(preference, newValue);
        }
        
        final String file;
        if (preference == mSliderTop) {
            file = Constants.KEYCODE_SLIDER_TOP;
        } else if (preference == mSliderMiddle) {
            file = Constants.KEYCODE_SLIDER_MIDDLE;
        } else if (preference == mSliderBottom) {
            file = Constants.KEYCODE_SLIDER_BOTTOM;
        } else {
            return false;
        }

        FileUtils.writeLine(file, (String) newValue);
        setSummary((ListPreference) preference, file);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If running on a phone, remove padding around the listview
        if (!ScreenType.isTablet(this)) {
            getListView().setPadding(0, 0, 0, 0);
        }

        mHapticFeedback.setChecked(
                Settings.System.getInt(getContentResolver(), KEY_HAPTIC_FEEDBACK, 1) != 0);

        setSummary(mSliderTop, Constants.KEYCODE_SLIDER_TOP);
        setSummary(mSliderMiddle, Constants.KEYCODE_SLIDER_MIDDLE);
        setSummary(mSliderBottom, Constants.KEYCODE_SLIDER_BOTTOM);
    }
}
