package com.ple2020pi.memoranki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (myPreferences.getBoolean("myPreferences_darkmode",true)){
            setTheme(R.style.LightTheme);
        }
        else {
            setTheme(R.style.DarkTheme);
        }
        SharedPreferences.OnSharedPreferenceChangeListener myPreferencesChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences myPreferences, String key) {
                if (key.equals("myPreferences_darkmode")){
                    recreate();
                }
            }
        };
        myPreferences.registerOnSharedPreferenceChangeListener(myPreferencesChangeListener);

        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();
            if(key.equals("myPreferences_about")){
                Intent intent = new Intent(getActivity().getApplication(), AboutActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent,2);
    }
}