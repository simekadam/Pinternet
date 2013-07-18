package com.simekadam.pinternet;

import android.app.Activity;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: simekadam
 * Date: 3/25/13
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsActivity extends PreferenceActivity {

    public static final String PREFS_NAME = "PinternetPrefs";
    private static final String ADD_SHORTCUT_LABEL = "pref_addShortcutIntent";

    private String addShortcutIntentLabel;
    private SharedPreferences preferences;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preferences);
        this.loadSettings();

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }



    private void loadSettings()
    {

//        addShortcutIntentLabel = preferences.getString("addShortcutLabel", "Pin shortcut to your homescreen");

    }

    private void saveSettings()
    {


    }


    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferenceheaders, target);
    }

    public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if(key.equals(SettingsActivity.ADD_SHORTCUT_LABEL))
            {
                try {
                    ActivityInfo ai = getPackageManager().getActivityInfo(new ComponentName("com.simekadam.pinternet", "com.simekadam.pinternet.RouteActivity"), PackageManager.GET_INTENT_FILTERS);
                    Log.d("Pinternet", "ble ble"+ai);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d("Pinternet", "Component not found "+e);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }



}