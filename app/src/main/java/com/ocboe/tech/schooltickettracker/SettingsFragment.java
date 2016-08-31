package com.ocboe.tech.schooltickettracker;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ocboe.tech.schooltickettracker.updater.AppUpdateUtil;

public class SettingsFragment extends PreferenceFragment{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle("Settings");

        if (BuildConfig.isInternetAvailable) {
            Preference checkForUpdatePreference = getPreferenceManager().findPreference("checkForUpdate");
            checkForUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (!MainActivity.isSchoolTicketTrackerBeingUpdated(getActivity())) {
                        Snackbar.make(TechSummaryActivity.mView, getResources().getString(R.string.checking_for_update), Snackbar.LENGTH_SHORT).show();
                        //Toast.makeText(getActivity(), getResources().getString(R.string.checking_for_update), Toast.LENGTH_SHORT).show();
                        MainActivity.shouldShowUpdateDialog = false;
                        AppUpdateUtil.checkForUpdate(getActivity());
                    } else
                        Snackbar.make(TechSummaryActivity.mView, getResources().getString(R.string.ongoing_update), Snackbar.LENGTH_SHORT).show();
                        //Toast.makeText(getActivity(), getResources().getString(R.string.ongoing_update), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
        
        //getActivity().getActionBar().setTitle("Settings");
    }

}
