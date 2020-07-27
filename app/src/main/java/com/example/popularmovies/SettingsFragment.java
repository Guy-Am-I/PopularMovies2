package com.example.popularmovies;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    /**
     * Bind the preference layout to our settings fragment to display in UI
     * @param savedInstanceState
     * @param rootKey
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_fragment, rootKey);

        Preference notifPref = findPreference(getString(R.string.pref_notifications_key));
        notifPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SettingsActivity.openNotificationSettingsForApp(getContext());
                return true;
            }
        });

    }




}
