package romang.montejo.moya.Fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import romang.montejo.moya.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        // TODO: 11/1/2022 Hacer preferencias
    }
}