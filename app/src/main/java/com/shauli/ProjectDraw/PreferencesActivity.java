package com.shauli.ProjectDraw;


import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.prefs);
        final ListPreference itemList = (ListPreference) findPreference("fill");
        final ListPreference itemList2 = (ListPreference) findPreference("width");

        if (itemList.getValue().equals("2")) {
            itemList2.setValueIndex(0);
            itemList2.setEnabled(false);
        }

        itemList.setOnPreferenceChangeListener((preference, newValue) -> {
            String val = newValue.toString();
            int index = itemList.findIndexOfValue(val);
            if (index == 1) {
                itemList2.setValueIndex(0);
                itemList2.setEnabled(false);
            } else
                itemList2.setEnabled(true);
            return true;
        });
    }
}
